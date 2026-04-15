package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeMasterTaskRepository practiceMasterTaskRepository;
    private final PracticeSingleTaskRepository practiceSingleTaskRepository;
    private final PracticeQuestionRecordRepository practiceQuestionRecordRepository;
    private final PracticeSingleReportRepository practiceSingleReportRepository;
    private final QuestionBankRepository questionBankRepository;
    private final QwenService qwenService;

    @Transactional
    public PracticeMasterTask createMasterTask(Long userId, Long postId, String knowledgePoint, 
                                            Integer practiceLevel, Integer taskCycle, Integer taskQuantity,
                                            LocalDateTime planStartTime, LocalDateTime planEndTime) {
        PracticeMasterTask masterTask = new PracticeMasterTask();
        masterTask.setUserId(userId);
        masterTask.setPostId(postId);
        masterTask.setKnowledgePoint(knowledgePoint);
        masterTask.setModuleType(resolveQuestionTypeFromModule(knowledgePoint));
        masterTask.setPracticeLevel(practiceLevel);
        masterTask.setTaskCycle(taskCycle);
        masterTask.setTaskQuantity(taskQuantity);
        masterTask.setPlanStartTime(planStartTime);
        masterTask.setPlanEndTime(planEndTime);
        LocalDateTime now = LocalDateTime.now();
        masterTask.setMasterStatus(resolveMasterStatus(planStartTime, planEndTime, now));
        masterTask.setCreateTime(now);
        masterTask.setUpdateTime(now);
        masterTask.setIsDelete(0);

        return practiceMasterTaskRepository.save(masterTask);
    }

    @Transactional
    public PracticeMasterTask updateMasterTask(Long masterId, Integer masterStatus) {
        PracticeMasterTask masterTask = practiceMasterTaskRepository.findById(masterId).orElse(null);
        if (masterTask != null) {
            masterTask.setMasterStatus(masterStatus);
            masterTask.setUpdateTime(LocalDateTime.now());
            return practiceMasterTaskRepository.save(masterTask);
        }
        return null;
    }

    @Transactional
    public PracticeSingleTask createSingleTask(Long masterId, Long userId, Long postId) {
        PracticeSingleTask singleTask = new PracticeSingleTask();
        singleTask.setMasterId(masterId);
        singleTask.setUserId(userId);
        singleTask.setPostId(postId);
        singleTask.setSingleStatus(0);
        singleTask.setTotalDuration(0);
        singleTask.setTotalScore(BigDecimal.ZERO);
        singleTask.setSkipQuantity(0);
        singleTask.setCreateTime(LocalDateTime.now());
        singleTask.setIsDelete(0);

        return practiceSingleTaskRepository.save(singleTask);
    }

    @Transactional
    public PracticeSingleTask startSingleTask(Long singleId) {
        PracticeSingleTask singleTask = practiceSingleTaskRepository.findById(singleId).orElse(null);
        if (singleTask != null) {
            singleTask.setSingleStatus(1);
            singleTask.setStartTime(LocalDateTime.now());
            return practiceSingleTaskRepository.save(singleTask);
        }
        return null;
    }

    @Transactional
    public PracticeQuestionRecord submitAnswer(Long singleId, Long userId, Long questionId, 
                                         String userAnswer, Integer inputType, Integer answerDuration) {
        PracticeQuestionRecord record = new PracticeQuestionRecord();
        record.setSingleId(singleId);
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        record.setInputType(inputType);
        record.setAnswerDuration(answerDuration);
        
        String questionTitle = questionBankRepository.findById(questionId)
                .map(QuestionBank::getQuestionTitle)
                .orElse("");
        String questionAnswer = questionBankRepository.findById(questionId)
                .map(QuestionBank::getQuestionAnswer)
                .orElse("");
        List<Long> categoryIds = resolveCurrentCategoryIds(singleId, userId);

        // 调用「专项练习导师智能体」进行结构化评测
        String prompt = "你是专项练习导师，请对用户作答进行点评与打分，严格返回JSON：\n" +
                "{\n" +
                "  \"tech_score\": 分数,\n" +
                "  \"knowledge_depth_score\": 分数,\n" +
                "  \"logic_score\": 分数,\n" +
                "  \"express_score\": 分数,\n" +
                "  \"match_score\": 分数,\n" +
                "  \"suggest_guide\": \"改进建议\",\n" +
                "  \"problem_analysis\": \"主要问题\"\n" +
                "}\n\n" +
                "当前岗位+当前模块对应的category_id列表（用于判定知识范围）：\n" + categoryIds + "\n\n" +
                "题目：\n" + questionTitle + "\n\n" +
                (questionAnswer == null || questionAnswer.isBlank() ? "" : ("参考答案：\n" + questionAnswer + "\n\n")) +
                "用户作答：\n" + userAnswer;
        String evaluation = qwenService.evaluatePracticeAnswer(prompt);
        applyAiEvaluation(record, evaluation);
        
        record.setCreateTime(LocalDateTime.now());
        record.setIsDelete(0);

        return practiceQuestionRecordRepository.save(record);
    }

    @Transactional
    public PracticeSingleTask finishSingleTask(Long singleId, Integer totalDuration, BigDecimal totalScore, 
                                           Integer skipQuantity, LocalDateTime finishTime) {
        PracticeSingleTask singleTask = practiceSingleTaskRepository.findById(singleId).orElse(null);
        if (singleTask != null) {
            singleTask.setTotalDuration(totalDuration);
            singleTask.setTotalScore(totalScore);
            singleTask.setSkipQuantity(skipQuantity);
            singleTask.setFinishTime(finishTime);
            singleTask.setSingleStatus(2);
            return practiceSingleTaskRepository.save(singleTask);
        }
        return null;
    }

    @Transactional
    public PracticeSingleReport generateReport(Long singleId, Long userId, Long masterId, 
                                          String scoreAnalysis, String improveSuggest) {
        PracticeSingleReport report = new PracticeSingleReport();
        report.setSingleId(singleId);
        report.setUserId(userId);
        report.setMasterId(masterId);
        report.setScoreAnalysis(scoreAnalysis);
        report.setImproveSuggest(improveSuggest);
        report.setCreateTime(LocalDateTime.now());
        report.setIsDelete(0);

        return practiceSingleReportRepository.save(report);
    }

    public List<PracticeMasterTask> getUserMasterTasks(Long userId) {
        List<PracticeMasterTask> list = practiceMasterTaskRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        syncMasterStatusByTime(list);
        return list;
    }

    public List<PracticeMasterTask> getUserMasterTasksByStatus(Long userId, Integer masterStatus) {
        // 先做一次全量状态校准，再按状态筛选，避免“应该进行中却仍是待开始”
        List<PracticeMasterTask> all = practiceMasterTaskRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        syncMasterStatusByTime(all);
        return all.stream().filter(t -> Integer.valueOf(masterStatus).equals(t.getMasterStatus())).toList();
    }

    public List<PracticeMasterTask> getUserMasterTasksByPostId(Long userId, Long postId) {
        List<PracticeMasterTask> list = practiceMasterTaskRepository.findByUserIdAndPostIdAndIsDeleteOrderByCreateTimeDesc(userId, postId, 0);
        syncMasterStatusByTime(list);
        return list;
    }

    public List<PracticeSingleTask> getSingleTasksByMasterId(Long masterId) {
        return practiceSingleTaskRepository.findByMasterIdAndIsDeleteOrderByCreateTimeDesc(masterId, 0);
    }

    public List<PracticeQuestionRecord> getQuestionRecordsBySingleId(Long singleId) {
        return practiceQuestionRecordRepository.findBySingleIdAndIsDelete(singleId, 0);
    }

    public Optional<PracticeSingleReport> getReportBySingleId(Long singleId) {
        return practiceSingleReportRepository.findBySingleIdAndIsDelete(singleId, 0);
    }

    private Integer resolveMasterStatus(LocalDateTime planStartTime, LocalDateTime planEndTime, LocalDateTime now) {
        if (planStartTime == null || planEndTime == null) return 0;
        if (now.isBefore(planStartTime)) return 0; // 待开始
        if (!now.isAfter(planEndTime)) return 1;   // 进行中（含等于结束时间）
        return 2;                                  // 已完成
    }

    private void syncMasterStatusByTime(List<PracticeMasterTask> tasks) {
        if (tasks == null || tasks.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        for (PracticeMasterTask task : tasks) {
            Integer expectStatus = resolveMasterStatus(task.getPlanStartTime(), task.getPlanEndTime(), now);
            Integer currentStatus = task.getMasterStatus() == null ? 0 : task.getMasterStatus();
            if (!expectStatus.equals(currentStatus)) {
                task.setMasterStatus(expectStatus);
                task.setUpdateTime(now);
                practiceMasterTaskRepository.save(task);
            }
        }
    }

    private void applyAiEvaluation(PracticeQuestionRecord record, String raw) {
        JSONObject obj = tryParseJsonObject(raw);
        BigDecimal tech = readScore(obj, "tech_score");
        BigDecimal depth = readScore(obj, "knowledge_depth_score", "knowledge_score");
        BigDecimal logic = readScore(obj, "logic_score");
        BigDecimal express = readScore(obj, "express_score");
        BigDecimal match = readScore(obj, "match_score");

        // 智能体没按 JSON 返回时的保底：避免固定 75，改为中位保底分并标记需复盘
        if (tech == null) tech = new BigDecimal("60.00");
        if (depth == null) depth = new BigDecimal("60.00");
        if (logic == null) logic = new BigDecimal("60.00");
        if (express == null) express = new BigDecimal("60.00");
        if (match == null) match = new BigDecimal("60.00");

        record.setTechScore(tech.setScale(2, RoundingMode.HALF_UP));
        record.setKnowledgeDepthScore(depth.setScale(2, RoundingMode.HALF_UP));
        record.setLogicScore(logic.setScale(2, RoundingMode.HALF_UP));
        record.setExpressScore(express.setScale(2, RoundingMode.HALF_UP));
        record.setMatchScore(match.setScale(2, RoundingMode.HALF_UP));

        BigDecimal avg = averageOf(tech, depth, logic, express, match);
        // errorType: 0=通过；1=概念问题；2=逻辑问题；3=遗漏要点（简单映射）
        if (avg.compareTo(new BigDecimal("70")) >= 0) {
            record.setErrorType(0);
        } else if (logic.compareTo(new BigDecimal("60")) < 0) {
            record.setErrorType(2);
        } else if (depth.compareTo(new BigDecimal("60")) < 0 || tech.compareTo(new BigDecimal("60")) < 0) {
            record.setErrorType(1);
        } else {
            record.setErrorType(3);
        }
    }

    private JSONObject tryParseJsonObject(String text) {
        try {
            if (text == null || text.isBlank()) return null;
            return JSON.parseObject(text);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal readScore(JSONObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            Object v = obj.get(k);
            BigDecimal bd = toBigDecimal(v);
            if (bd != null) return clamp0to100(bd);
        }
        for (String k : keys) {
            Object v = obj.get(k == null ? null : k.toLowerCase(Locale.ROOT));
            BigDecimal bd = toBigDecimal(v);
            if (bd != null) return clamp0to100(bd);
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object v) {
        try {
            if (v == null) return null;
            if (v instanceof Number) return new BigDecimal(v.toString());
            String s = String.valueOf(v).trim();
            if (s.isBlank() || "null".equalsIgnoreCase(s)) return null;
            s = s.replaceAll("[^0-9.\\-]", "");
            if (s.isBlank()) return null;
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal clamp0to100(BigDecimal v) {
        if (v == null) return null;
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
        if (c == 0) return BigDecimal.ZERO;
        return sum.divide(new BigDecimal(c), 2, RoundingMode.HALF_UP);
    }

    private List<Long> resolveCurrentCategoryIds(Long singleId, Long userId) {
        PracticeSingleTask singleTask = practiceSingleTaskRepository.findById(singleId).orElse(null);
        if (singleTask == null) return Collections.emptyList();
        if (userId != null && singleTask.getUserId() != null && !userId.equals(singleTask.getUserId())) {
            return Collections.emptyList();
        }
        Long masterId = singleTask.getMasterId();
        if (masterId == null) return Collections.emptyList();

        PracticeMasterTask masterTask = practiceMasterTaskRepository.findById(masterId).orElse(null);
        if (masterTask == null || masterTask.getPostId() == null) return Collections.emptyList();

        Long postId = masterTask.getPostId();
        String module = masterTask.getKnowledgePoint();
        List<Long> byModule = (module == null || module.isBlank())
                ? Collections.emptyList()
                : questionBankRepository.findDistinctCategoryIdsByPostAndKnowledgePoint(postId, module);
        if (!byModule.isEmpty()) return byModule;

        Integer questionType = resolveQuestionTypeFromModule(module);
        if (questionType == null) return Collections.emptyList();
        return questionBankRepository.findDistinctCategoryIdsByPostAndQuestionType(postId, questionType);
    }

    private Integer resolveQuestionTypeFromModule(String module) {
        if (module == null || module.isBlank()) return 1;
        String m = module.trim();
        if ("技术知识".equals(m) || "专业基础".equals(m)) return 1;
        if ("项目深挖".equals(m) || "项目经验".equals(m)) return 2;
        if ("场景题".equals(m) || "场景模拟题".equals(m)) return 3;
        if ("行为题".equals(m) || "行为面试题".equals(m)) return 4;
        return 1;
    }
}