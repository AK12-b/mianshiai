package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.demo.dto.AnswerRequest;
import com.example.demo.dto.InterviewConfigRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.util.KnowledgePointUiMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewDialogRepository interviewDialogRepository;
    private final InterviewEvaluateRepository interviewEvaluateRepository;
    private final PostRepository postRepository;
    private final QuestionBankRepository questionBankRepository;
    private final KnowledgeCategoryRepository knowledgeCategoryRepository;
    private final KnowledgeBaseService knowledgeBaseService;
    private final InterviewWrongQuestionRepository interviewWrongQuestionRepository;
    private final QwenService qwenService;

    @Transactional
    public MockInterview createInterview(Long userId, InterviewConfigRequest config) {
        MockInterview interview = new MockInterview();
        interview.setUserId(userId);
        interview.setPostId(config.getPostId());
        interview.setInterviewMode(config.getInterviewMode());
        interview.setInterviewModule(config.getInterviewModule());
        interview.setAiCharacter(config.getAiCharacter());
        interview.setAiGender(config.getAiGender());
        interview.setInterviewDuration(config.getInterviewDuration());
        interview.setSingleQuestionTimeLimit(120);
        interview.setRemindBeforeTimeout(10);
        interview.setInputType(config.getInputType());
        /** 库表多为 start_time NOT NULL；创建时写入当前时间，真正计时时点在首次出题时再刷新 */
        interview.setStartTime(LocalDateTime.now());
        interview.setInterviewStatus(1);
        interview.setIsTimeout(0);
        interview.setIsDelete(0);
        interview.setPausedRemainingSec(0);

        return mockInterviewRepository.save(interview);
    }

    @Transactional
    public InterviewDialog generateFirstQuestion(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        List<InterviewDialog> existingDialogs =
                interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        if (existingDialogs != null) {
            for (InterviewDialog d : existingDialogs) {
                if (d != null && Objects.equals(d.getSpeaker(), 1) && Objects.equals(d.getDialogueRound(), 1)) {
                    return d;
                }
            }
        }
        applyMockInterviewColumnDefaults(interview);
        if (existingDialogs == null || existingDialogs.isEmpty()) {
            interview.setStartTime(LocalDateTime.now());
            mockInterviewRepository.save(interview);
        }

        if (interview.getPostId() == null) {
            log.warn("generateFirstQuestion: 面试未关联岗位 postId 为空 interviewId={}", interviewId);
            return null;
        }
        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        String questionType = "技术基础";
        if (Objects.equals(interview.getInterviewMode(), 2)) {
            String mod = interview.getInterviewModule();
            questionType = (mod != null && !mod.isBlank()) ? mod : "技术基础";
        }

        String questionContent =
                qwenService.generateInterviewQuestion(interview, post, questionType, null, false, null, null);
        if (questionContent == null || questionContent.isBlank()) {
            log.warn("generateFirstQuestion: 模型返回空题目 interviewId={}", interviewId);
            return null;
        }
        QuestionBank qb = upsertAiQuestionAndKnowledge(interview.getPostId(), questionType, questionContent);

        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(interviewId);
        dialog.setSpeaker(1);
        dialog.setContentText(questionContent);
        if (qb != null) {
            dialog.setQuestionId(qb.getQuestionId());
        }
        dialog.setDialogueRound(1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    @Transactional
    public InterviewDialog generateFollowUpQuestion(Long interviewId, String userAnswer) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        if (interview.getPostId() == null) {
            log.warn("generateFollowUpQuestion: 面试未关联岗位 postId 为空 interviewId={}", interviewId);
            return null;
        }

        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        if (dialogs.isEmpty()) {
            return null;
        }

        InterviewDialog lastQuestion = dialogs.get(dialogs.size() - 1);
        String module = interview.getInterviewModule();
        if (module == null || module.isBlank()) {
            module = "技术基础";
        }
        String followUp =
                qwenService.generateInterviewQuestion(
                        interview,
                        post,
                        module,
                        userAnswer,
                        true,
                        lastQuestion.getContentText(),
                        userAnswer);
        if (followUp == null || followUp.isBlank()) {
            log.warn("generateFollowUpQuestion: 模型返回空追问 interviewId={}", interviewId);
            return null;
        }
        QuestionBank qb = upsertAiQuestionAndKnowledge(interview.getPostId(), module, followUp);

        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(interviewId);
        dialog.setSpeaker(1);
        dialog.setContentText(followUp);
        if (qb != null) {
            dialog.setQuestionId(qb.getQuestionId());
        }
        dialog.setDialogueRound(dialogs.size() + 1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    private QuestionBank upsertAiQuestionAndKnowledge(Long postId, String questionTypeName, String questionText) {
        if (postId == null) return null;
        String title = normalizeText(questionText);
        if (title.isBlank()) return null;

        // 题库去重：先精确匹配，再做一次归一化扫描（避免空格/换行差异导致重复）
        QuestionBank existing = questionBankRepository
                .findFirstByPostIdAndQuestionTitleAndIsDelete(postId, questionText, 0)
                .orElse(null);
        if (existing == null) {
            List<QuestionBank> list = questionBankRepository.findByPostIdAndIsDelete(postId, 0);
            for (QuestionBank q : list) {
                if (q == null || q.getQuestionTitle() == null) continue;
                if (normalizeText(q.getQuestionTitle()).equals(title)) {
                    existing = q;
                    break;
                }
            }
        }

        QuestionBank qb = existing;
        if (qb == null) {
            qb = new QuestionBank();
            qb.setPostId(postId);
            qb.setCategoryId(resolveCategoryId(questionTypeName));
            qb.setQuestionType(mapQuestionType(questionTypeName));
            qb.setQuestionLevel(2); // 默认中等
            qb.setQuestionTitle(questionText);
            qb.setQuestionAnswer(""); // 当前为出题，不生成标准答案
            qb.setKnowledgePoint(truncateDbVarchar(questionTypeName != null ? questionTypeName : "", 255));
            qb.setCreateTime(LocalDateTime.now());
            qb.setIsDelete(0);
            qb.setIsAiGenerate(1);
            qb = questionBankRepository.save(qb);
        }

        // 独立事务沉淀知识库；内层提交失败时仍可能向外抛 UnexpectedRollbackException，此处吞掉以保住面试主事务
        try {
            knowledgeBaseService.upsertFromInterviewQuestion(postId, questionTypeName, questionText);
        } catch (Exception e) {
            log.warn("知识库沉淀调用失败（已忽略） postId={} : {}", postId, e.getMessage());
        }
        return qb;
    }

    private long resolveCategoryId(String knowledgePoint) {
        String raw = knowledgePoint == null ? "" : knowledgePoint.trim();
        String ui = KnowledgePointUiMapper.toUiLabel(raw);
        Long id = knowledgeCategoryRepository.findFirstByCategoryName(ui).map(KnowledgeCategory::getCategoryId).orElse(null);
        if (id == null && !raw.isBlank()) {
            id = knowledgeCategoryRepository.findFirstByCategoryName(raw).map(KnowledgeCategory::getCategoryId).orElse(null);
        }
        return id != null ? id : 7L;
    }

    private int mapQuestionType(String questionTypeName) {
        if (questionTypeName == null) return 1;
        String t = questionTypeName.trim();
        if (t.contains("技术")) return 1;
        if (t.contains("项目")) return 2;
        if (t.contains("场景")) return 3;
        if (t.contains("行为")) return 4;
        return 1;
    }

    private String normalizeText(String s) {
        if (s == null) return "";
        // 去除多余空白与常见标点差异，降低重复写入概率
        return s.replaceAll("\\s+", " ")
                .replace("：", ":")
                .replace("，", ",")
                .replace("。", ".")
                .trim();
    }

    /** 与库表 varchar(255) 对齐，避免专项模块名过长导致写入失败 */
    private static String truncateDbVarchar(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    /**
     * 手工改库或历史数据可能出现 NULL，与表 NOT NULL 冲突时 UPDATE 会 500；出题前补齐默认值。
     */
    private void applyMockInterviewColumnDefaults(MockInterview interview) {
        if (interview == null) return;
        if (interview.getInterviewMode() == null) interview.setInterviewMode(1);
        if (interview.getAiCharacter() == null) interview.setAiCharacter(3);
        if (interview.getAiGender() == null) interview.setAiGender(2);
        if (interview.getInterviewDuration() == null) interview.setInterviewDuration(15);
        if (interview.getSingleQuestionTimeLimit() == null) interview.setSingleQuestionTimeLimit(120);
        if (interview.getRemindBeforeTimeout() == null) interview.setRemindBeforeTimeout(10);
        if (interview.getInputType() == null) interview.setInputType(2);
        if (interview.getInterviewStatus() == null) interview.setInterviewStatus(1);
        if (interview.getPausedRemainingSec() == null) interview.setPausedRemainingSec(0);
        if (interview.getIsTimeout() == null) interview.setIsTimeout(0);
        if (interview.getIsDelete() == null) interview.setIsDelete(0);
        if (interview.getStartTime() == null) interview.setStartTime(LocalDateTime.now());
    }

    @Transactional
    public InterviewDialog submitAnswer(AnswerRequest request) {
        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(request.getInterviewId());
        dialog.setSpeaker(2);
        dialog.setContentText(request.getAnswerText());
        dialog.setQuestionId(request.getQuestionId());
        dialog.setVoiceUrl(request.getVoiceUrl());
        dialog.setVoiceDuration(request.getVoiceDuration());

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(request.getInterviewId(), 0);
        dialog.setDialogueRound(dialogs.size() + 1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    @Transactional
    public InterviewEvaluate evaluateInterview(Long interviewId) {
        // 幂等保障：同一场面试仅首次调用智能体，后续直接复用历史结果（不再消耗额度）
        InterviewEvaluate existing = interviewEvaluateRepository.findByInterviewIdAndIsDelete(interviewId, 0).orElse(null);
        if (existing != null) {
            fillMissingEvaluationFields(existing, null);
            return interviewEvaluateRepository.save(existing);
        }

        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        if (dialogs.isEmpty()) {
            return null;
        }

        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        StringBuilder allAnswers = new StringBuilder();
        for (InterviewDialog dialog : dialogs) {
            if (dialog.getSpeaker() == 2) {
                allAnswers.append(dialog.getContentText()).append("\n");
            }
        }

        String evaluation = qwenService.evaluateAnswer("面试综合评估", allAnswers.toString(), "标准答案");

        InterviewEvaluate evaluate = new InterviewEvaluate();
        evaluate.setInterviewId(interviewId);
        evaluate.setPostId(interview.getPostId());

        // 将 AI 输出解析为结构化字段写入数据库（优先解析 JSON，其次兜底文本）
        applyAiEvaluation(evaluate, evaluation);
        fillMissingEvaluationFields(evaluate, evaluation);
        
        evaluate.setCreateTime(LocalDateTime.now());
        evaluate.setIsDelete(0);

        return interviewEvaluateRepository.save(evaluate);
    }

    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{[\\s\\S]*\\}");

    private void applyAiEvaluation(InterviewEvaluate evaluate, String raw) {
        if (raw == null || raw.isBlank()) {
            applyFallbackEvaluation(evaluate, null);
            return;
        }

        JSONObject obj = tryParseJsonObject(raw);
        if (obj == null) {
            // 有些模型会把 JSON 包在说明文字里，尝试提取第一个 {...}
            Matcher m = JSON_OBJECT_PATTERN.matcher(raw);
            if (m.find()) {
                obj = tryParseJsonObject(m.group());
            }
        }

        if (obj == null) {
            applyFallbackEvaluation(evaluate, raw);
            return;
        }

        // 分数（兼容 tech_score / techScore 等）
        evaluate.setTechScore(readScore(obj, "tech_score", "techScore", "技术正确性", "技术得分"));
        evaluate.setKnowledgeDepthScore(readScore(obj, "knowledge_depth_score", "knowledgeDepthScore", "knowledge_score", "knowledgeScore", "知识深度", "知识深度得分"));
        evaluate.setLogicScore(readScore(obj, "logic_score", "logicScore", "逻辑严谨性", "逻辑得分"));
        evaluate.setExpressScore(readScore(obj, "express_score", "expressScore", "表达能力", "表达得分"));
        evaluate.setMatchScore(readScore(obj, "match_score", "matchScore", "岗位匹配度", "匹配得分"));
        BigDecimal total = readScore(obj, "total_score", "totalScore");
        if (total == null) {
            total = averageOf(
                    evaluate.getTechScore(),
                    evaluate.getKnowledgeDepthScore(),
                    evaluate.getLogicScore(),
                    evaluate.getExpressScore(),
                    evaluate.getMatchScore()
            );
        }
        evaluate.setTotalScore(total);

        // 文本分析（兼容不同 key）
        evaluate.setBrightPoint(readText(obj, "bright_point", "brightPoint", "highlight", "highlights", "亮点分析", "亮点"));
        evaluate.setProblemAnalysis(readText(obj, "problem_analysis", "problemAnalysis", "weakness", "weaknesses", "问题分析"));
        evaluate.setGapAnalysis(readText(obj, "gap_analysis", "gapAnalysis", "差距分析"));
        evaluate.setSuggestGuide(readText(obj, "suggest_guide", "suggestGuide", "suggestion", "suggestions", "improve_suggest", "建议"));

        evaluate.setTechAnalysis(readText(obj, "tech_analysis", "techAnalysis", "技术分析"));
        evaluate.setKnowledgeDepthAnalysis(readText(obj, "knowledge_depth_analysis", "knowledgeDepthAnalysis", "knowledge_analysis", "knowledgeAnalysis", "知识深度分析"));
        evaluate.setLogicAnalysis(readText(obj, "logic_analysis", "logicAnalysis", "逻辑严谨性分析"));
        evaluate.setExpressAnalysis(readText(obj, "express_analysis", "expressAnalysis", "表达能力分析"));
        evaluate.setMatchAnalysis(readText(obj, "match_analysis", "matchAnalysis", "岗位匹配度分析"));

        if (evaluate.getTotalScore() == null) applyFallbackEvaluation(evaluate, raw);
    }

    private void applyFallbackEvaluation(InterviewEvaluate evaluate, String raw) {
        // 保底：即使 AI 没返回结构化 JSON，也保证报告可生成可展示
        evaluate.setTotalScore(new BigDecimal("60.00"));
        evaluate.setTechScore(new BigDecimal("60.00"));
        evaluate.setKnowledgeDepthScore(new BigDecimal("60.00"));
        evaluate.setLogicScore(new BigDecimal("60.00"));
        evaluate.setExpressScore(new BigDecimal("60.00"));
        evaluate.setMatchScore(new BigDecimal("60.00"));
        evaluate.setBrightPoint("评估生成成功");
        evaluate.setProblemAnalysis(raw != null && !raw.isBlank() ? raw : "AI 未返回结构化评估结果，请稍后重试。");
        evaluate.setGapAnalysis("与岗位要求存在提升空间");
        evaluate.setSuggestGuide("建议按模块复盘：技术正确性、知识深度、逻辑表达与岗位匹配度。");
        evaluate.setTechAnalysis("待进一步分析");
        evaluate.setKnowledgeDepthAnalysis("待进一步分析");
        evaluate.setLogicAnalysis("待进一步分析");
        evaluate.setExpressAnalysis("待进一步分析");
        evaluate.setMatchAnalysis("待进一步分析");
    }

    private JSONObject tryParseJsonObject(String text) {
        try {
            return JSON.parseObject(text);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal readScore(JSONObject obj, String... keys) {
        for (String k : keys) {
            if (k == null) continue;
            Object v = obj.get(k);
            if (v == null) continue;
            BigDecimal bd = toBigDecimal(v);
            if (bd != null) return clamp0to100(bd);
        }
        // 兼容 key 大小写差异
        for (String k : keys) {
            if (k == null) continue;
            String alt = k.toLowerCase(Locale.ROOT);
            Object v = obj.get(alt);
            if (v == null) continue;
            BigDecimal bd = toBigDecimal(v);
            if (bd != null) return clamp0to100(bd);
        }
        return null;
    }

    private String readText(JSONObject obj, String... keys) {
        for (String k : keys) {
            if (k == null) continue;
            Object v = obj.get(k);
            if (v == null) continue;
            String s = String.valueOf(v).trim();
            if (!s.isBlank() && !"null".equalsIgnoreCase(s)) return s;
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object v) {
        try {
            if (v instanceof Number) return new BigDecimal(v.toString());
            String s = String.valueOf(v).trim();
            if (s.isBlank() || "null".equalsIgnoreCase(s)) return null;
            // 允许 "75分" 这类
            s = s.replaceAll("[^0-9.\\-]", "");
            if (s.isBlank()) return null;
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal clamp0to100(BigDecimal v) {
        if (v.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        BigDecimal max = new BigDecimal("100");
        if (v.compareTo(max) > 0) return max;
        return v;
    }

    private BigDecimal averageOf(BigDecimal... vals) {
        BigDecimal sum = BigDecimal.ZERO;
        int c = 0;
        for (BigDecimal v : vals) {
            if (v == null) continue;
            sum = sum.add(v);
            c++;
        }
        if (c == 0) return null;
        return sum.divide(new BigDecimal(c), 2, RoundingMode.HALF_UP);
    }

    private void fillMissingEvaluationFields(InterviewEvaluate evaluate, String rawText) {
        if (evaluate.getTotalScore() == null) {
            BigDecimal avg = averageOf(
                    evaluate.getTechScore(),
                    evaluate.getKnowledgeDepthScore(),
                    evaluate.getLogicScore(),
                    evaluate.getExpressScore(),
                    evaluate.getMatchScore()
            );
            evaluate.setTotalScore(avg == null ? new BigDecimal("60.00") : avg);
        }
        if (evaluate.getTechScore() == null) evaluate.setTechScore(evaluate.getTotalScore());
        if (evaluate.getKnowledgeDepthScore() == null) evaluate.setKnowledgeDepthScore(evaluate.getTotalScore());
        if (evaluate.getLogicScore() == null) evaluate.setLogicScore(evaluate.getTotalScore());
        if (evaluate.getExpressScore() == null) evaluate.setExpressScore(evaluate.getTotalScore());
        if (evaluate.getMatchScore() == null) evaluate.setMatchScore(evaluate.getTotalScore());

        if (isBlank(evaluate.getBrightPoint())) evaluate.setBrightPoint("已完成面试评估，整体表现可继续提升。");
        if (isBlank(evaluate.getProblemAnalysis())) evaluate.setProblemAnalysis(nonBlank(rawText, "报告已生成，问题分析待补充。"));
        if (isBlank(evaluate.getGapAnalysis())) evaluate.setGapAnalysis("与目标岗位要求仍有一定差距。");
        if (isBlank(evaluate.getSuggestGuide())) evaluate.setSuggestGuide("建议围绕薄弱维度持续训练，并进行复盘。");
        if (isBlank(evaluate.getTechAnalysis())) evaluate.setTechAnalysis("技术正确性需持续巩固。");
        if (isBlank(evaluate.getKnowledgeDepthAnalysis())) evaluate.setKnowledgeDepthAnalysis("知识深度建议结合项目场景进一步扩展。");
        if (isBlank(evaluate.getLogicAnalysis())) evaluate.setLogicAnalysis("回答结构可进一步增强逻辑性与条理性。");
        if (isBlank(evaluate.getExpressAnalysis())) evaluate.setExpressAnalysis("表达建议更简洁明确并突出重点。");
        if (isBlank(evaluate.getMatchAnalysis())) evaluate.setMatchAnalysis("岗位匹配度有提升空间，建议结合岗位关键词强化准备。");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isBlank() || "--".equals(s.trim());
    }

    private String nonBlank(String first, String fallback) {
        return isBlank(first) ? fallback : first;
    }

    public Optional<MockInterview> getInterviewById(Long interviewId) {
        return mockInterviewRepository.findById(interviewId);
    }

    @Transactional
    public MockInterview endInterview(Long interviewId) {
        return endInterview(interviewId, false);
    }

    @Transactional
    public MockInterview endInterview(Long interviewId, boolean timeout) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        Integer st = interview.getInterviewStatus();
        if (!Integer.valueOf(1).equals(st) && !Integer.valueOf(4).equals(st)) {
            return null;
        }
        appendInterviewQuestionsToWrongBook(interview);
        interview.setEndTime(LocalDateTime.now());
        interview.setInterviewStatus(2);
        interview.setPausedRemainingSec(null);
        if (timeout) {
            interview.setIsTimeout(1);
        }
        return mockInterviewRepository.save(interview);
    }

    /** 距面试结束剩余秒数（进行中/暂停前计算）；尚未开始计时时视为整段时长未消耗 */
    private int computeRemainingSeconds(MockInterview interview) {
        if (interview.getInterviewDuration() == null) {
            return 0;
        }
        int fullSec = interview.getInterviewDuration() * 60;
        if (interview.getStartTime() == null) {
            return fullSec;
        }
        LocalDateTime end = interview.getStartTime().plusMinutes(interview.getInterviewDuration());
        long sec = ChronoUnit.SECONDS.between(LocalDateTime.now(), end);
        return (int) Math.max(0, sec);
    }

    /**
     * 面试结束/终止时自动把本次面试题目加入错题本：
     * - 收集 AI 提问（speaker=1）；优先用对话上的 questionId，缺失时按题干在题库 upsert 得到题目 ID
     * - 错误类型先留空，交由用户后续在错题本自行选择
     * - 按 userId + questionId 全局去重，避免跨场面试重复写入同一题
     */
    private void appendInterviewQuestionsToWrongBook(MockInterview interview) {
        if (interview == null || interview.getInterviewId() == null || interview.getUserId() == null) {
            return;
        }
        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interview.getInterviewId(), 0);
        if (dialogs == null || dialogs.isEmpty()) {
            return;
        }

        for (InterviewDialog d : dialogs) {
            if (d == null || d.getSpeaker() == null || d.getSpeaker() != 1) {
                continue;
            }
            Long effectiveQuestionId = resolveQuestionIdForWrongBook(interview, d);
            if (effectiveQuestionId == null) {
                continue;
            }
            boolean exists = interviewWrongQuestionRepository.existsByUserIdAndQuestionIdAndIsDelete(
                    interview.getUserId(), effectiveQuestionId, 0
            );
            if (exists) {
                continue;
            }

            String knowledgePoint = "";
            QuestionBank qb = questionBankRepository.findById(effectiveQuestionId).orElse(null);
            if (qb != null && qb.getKnowledgePoint() != null) {
                knowledgePoint = qb.getKnowledgePoint();
            }

            InterviewWrongQuestion wrong = new InterviewWrongQuestion();
            wrong.setUserId(interview.getUserId());
            wrong.setInterviewId(interview.getInterviewId());
            wrong.setPracticeId(null);
            wrong.setQuestionId(effectiveQuestionId);
            wrong.setWrongReason(null);
            wrong.setKnowledgePoint(knowledgePoint);
            wrong.setIsCollect(0);
            wrong.setCreateTime(LocalDateTime.now());
            wrong.setIsDelete(0);
            interviewWrongQuestionRepository.save(wrong);
        }
    }

    /**
     * 优先使用对话上已绑定的题库 ID；若历史/异常导致 AI 题干未写入 questionId，则按题干与岗位做一次题库 upsert 后得到 ID，
     * 避免「结束面试后错题本一条都没有」。
     */
    private Long resolveQuestionIdForWrongBook(MockInterview interview, InterviewDialog d) {
        if (d.getQuestionId() != null) {
            return d.getQuestionId();
        }
        String text = d.getContentText();
        if (text == null || text.isBlank()) {
            return null;
        }
        String t = text.trim();
        if (t.contains("AI 正在生成") || t.contains("追问生成失败")) {
            return null;
        }
        if (interview.getPostId() == null) {
            return null;
        }
        String module = interview.getInterviewModule();
        if (module == null || module.isBlank()) {
            module = "技术基础";
        }
        QuestionBank qb = upsertAiQuestionAndKnowledge(interview.getPostId(), module, t);
        return qb != null ? qb.getQuestionId() : null;
    }

    /**
     * 暂停：仅「进行中(1)」可暂停，写入剩余秒数，状态改为已暂停(4)。约定：1=进行中，2=已完成，3=已终止，4=已暂停。
     */
    @Transactional
    public MockInterview pauseInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        if (!Integer.valueOf(1).equals(interview.getInterviewStatus())) {
            return null;
        }
        interview.setPausedRemainingSec(computeRemainingSeconds(interview));
        interview.setInterviewStatus(4);
        return mockInterviewRepository.save(interview);
    }

    /**
     * 恢复：已暂停(4) → 进行中(1)，按剩余秒数回推 startTime，使倒计时从暂停点继续。
     */
    @Transactional
    public MockInterview resumeInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        if (Integer.valueOf(1).equals(interview.getInterviewStatus())) {
            return interview;
        }
        if (!Integer.valueOf(4).equals(interview.getInterviewStatus())) {
            return null;
        }
        int durationMin = interview.getInterviewDuration() == null ? 0 : interview.getInterviewDuration();
        long totalSec = durationMin * 60L;
        Integer r = interview.getPausedRemainingSec();
        if (r == null) {
            r = (int) Math.min(totalSec, Integer.MAX_VALUE);
        }
        long elapsedSec = Math.max(0, totalSec - r);
        interview.setStartTime(LocalDateTime.now().minusSeconds(elapsedSec));
        interview.setPausedRemainingSec(null);
        interview.setInterviewStatus(1);
        return mockInterviewRepository.save(interview);
    }

    @Transactional
    public MockInterview terminateInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }
        Integer st = interview.getInterviewStatus();
        if (!Integer.valueOf(1).equals(st) && !Integer.valueOf(4).equals(st)) {
            return null;
        }
        appendInterviewQuestionsToWrongBook(interview);
        interview.setEndTime(LocalDateTime.now());
        interview.setInterviewStatus(3);
        interview.setPausedRemainingSec(null);
        return mockInterviewRepository.save(interview);
    }

    public List<MockInterview> getUserInterviews(Long userId) {
        return mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
    }

    public List<MockInterview> getUserInterviewsByPostId(Long userId, Long postId) {
        return mockInterviewRepository.findByUserIdAndPostIdAndIsDeleteOrderByStartTimeDesc(userId, postId, 0);
    }

    public List<MockInterview> getUserInterviewsByMode(Long userId, Integer interviewMode) {
        return mockInterviewRepository.findByUserIdAndInterviewModeAndIsDeleteOrderByStartTimeDesc(userId, interviewMode, 0);
    }

    public List<MockInterview> getUserInterviewsByStatus(Long userId, Integer status) {
        return mockInterviewRepository.findByUserIdAndInterviewStatusAndIsDelete(userId, status, 0);
    }

    public List<InterviewDialog> getInterviewDialogs(Long interviewId) {
        return interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
    }

    public Optional<InterviewEvaluate> getInterviewEvaluate(Long interviewId) {
        Optional<InterviewEvaluate> opt = interviewEvaluateRepository.findByInterviewIdAndIsDelete(interviewId, 0);
        if (opt.isEmpty()) return opt;
        InterviewEvaluate existing = opt.get();
        fillMissingEvaluationFields(existing, null);
        InterviewEvaluate saved = interviewEvaluateRepository.save(existing);
        return Optional.of(saved);
    }

    public List<Map<String, Object>> getReportQaDetail(Long interviewId) {
        List<Map<String, Object>> result = new ArrayList<>();
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) return result;
        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        String postName = post != null ? post.getPostName() : "技术";

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        for (int i = 0; i < dialogs.size(); i++) {
            InterviewDialog d = dialogs.get(i);
            if (d == null || d.getSpeaker() == null || d.getSpeaker() != 1) continue; // 只处理AI提问
            String question = d.getContentText();
            if (question == null || question.isBlank()) continue;

            String userAnswer = "";
            for (int j = i + 1; j < dialogs.size(); j++) {
                InterviewDialog nd = dialogs.get(j);
                if (nd == null || nd.getSpeaker() == null) continue;
                if (nd.getSpeaker() == 1) break; // 下一题开始了
                if (nd.getSpeaker() == 2) {
                    userAnswer = nd.getContentText() == null ? "" : nd.getContentText();
                    break;
                }
            }

            String referenceAnswer = "";
            QuestionBank qb = null;
            if (d.getQuestionId() != null) {
                qb = questionBankRepository.findById(d.getQuestionId()).orElse(null);
                if (qb != null && qb.getQuestionAnswer() != null && !qb.getQuestionAnswer().isBlank()) {
                    referenceAnswer = qb.getQuestionAnswer();
                }
            }

            // 首次补全参考答案并回写题库，后续直接复用，避免重复调用AI
            if (referenceAnswer.isBlank()) {
                String generated = qwenService.generateReferenceAnswer(question, postName);
                if (generated != null && !generated.isBlank()) {
                    referenceAnswer = generated;
                }
            }
            if (referenceAnswer.isBlank()) {
                referenceAnswer = "暂未生成参考回答";
            }

            Map<String, Object> item = new HashMap<>();
            item.put("dialogId", d.getDialogId());
            item.put("question", question);
            item.put("userAnswer", userAnswer);
            item.put("referenceAnswer", referenceAnswer);
            result.add(item);
        }
        return result;
    }
}