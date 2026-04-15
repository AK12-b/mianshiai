package com.example.demo.service;

import com.example.demo.entity.QuestionBank;
import com.example.demo.entity.Post;
import com.example.demo.repository.KnowledgeCategoryRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.QuestionBankRepository;
import com.example.demo.util.KnowledgePointUiMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final PostRepository postRepository;
    private final QwenService qwenService;
    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    @Transactional
    public QuestionBank addQuestion(Long postId, Integer questionType, Integer questionLevel,
                                   String questionTitle, String questionAnswer, String knowledgePoint,
                                   Integer isAiGenerate) {
        QuestionBank question = new QuestionBank();
        question.setPostId(postId);
        question.setQuestionType(questionType);
        question.setQuestionLevel(questionLevel);
        question.setQuestionTitle(questionTitle);
        question.setQuestionAnswer(questionAnswer);
        question.setKnowledgePoint(knowledgePoint);
        question.setCategoryId(resolveCategoryId(knowledgePoint));
        question.setIsAiGenerate(isAiGenerate != null ? isAiGenerate : 0);
        question.setCreateTime(LocalDateTime.now());
        question.setIsDelete(0);

        QuestionBank saved = questionBankRepository.save(question);
        enrichUiKnowledgePoint(saved);
        return saved;
    }

    @Transactional
    public QuestionBank updateQuestion(Long questionId, Integer questionType, Integer questionLevel,
                                     String questionTitle, String questionAnswer, String knowledgePoint) {
        QuestionBank question = questionBankRepository.findById(questionId).orElse(null);
        if (question != null) {
            question.setQuestionType(questionType);
            question.setQuestionLevel(questionLevel);
            question.setQuestionTitle(questionTitle);
            question.setQuestionAnswer(questionAnswer);
            question.setKnowledgePoint(knowledgePoint);
            question.setCategoryId(resolveCategoryId(knowledgePoint));
            questionBankRepository.save(question);
            enrichUiKnowledgePoint(question);
            return question;
        }
        return null;
    }

    @Transactional
    public boolean deleteQuestion(Long questionId) {
        QuestionBank question = questionBankRepository.findById(questionId).orElse(null);
        if (question != null) {
            question.setIsDelete(1);
            questionBankRepository.save(question);
            return true;
        }
        return false;
    }

    public List<QuestionBank> getQuestionsByPostId(Long postId) {
        List<QuestionBank> list = questionBankRepository.findByPostIdAndIsDelete(postId, 0);
        enrichUiKnowledgePoint(list);
        return list;
    }

    public List<QuestionBank> getQuestionsByPostIdAndType(Long postId, Integer questionType) {
        List<QuestionBank> list =
                questionBankRepository.findByPostIdAndQuestionTypeAndIsDelete(postId, questionType, 0);
        enrichUiKnowledgePoint(list);
        return list;
    }

    public QuestionBank getQuestionById(Long questionId) {
        QuestionBank q = questionBankRepository.findById(questionId).orElse(null);
        enrichUiKnowledgePoint(q);
        return q;
    }

    public List<QuestionBank> getAllQuestions() {
        List<QuestionBank> list = questionBankRepository.findByIsDelete(0);
        enrichUiKnowledgePoint(list);
        return list;
    }

    private static void enrichUiKnowledgePoint(List<QuestionBank> list) {
        if (list == null) {
            return;
        }
        for (QuestionBank q : list) {
            enrichUiKnowledgePoint(q);
        }
    }

    private static void enrichUiKnowledgePoint(QuestionBank q) {
        if (q == null) {
            return;
        }
        q.setUiKnowledgePoint(KnowledgePointUiMapper.toUiLabel(q.getKnowledgePoint()));
    }

    private long resolveCategoryId(String knowledgePoint) {
        String raw = knowledgePoint == null ? "" : knowledgePoint.trim();
        String ui = KnowledgePointUiMapper.toUiLabel(raw);
        // 先按规范名查，再按原始名查
        Long id =
                knowledgeCategoryRepository.findFirstByCategoryName(ui).map(k -> k.getCategoryId()).orElse(null);
        if (id == null && !raw.isBlank()) {
            id = knowledgeCategoryRepository.findFirstByCategoryName(raw).map(k -> k.getCategoryId()).orElse(null);
        }
        // 默认落到「基础编程练习」(7)；确保 FK 合法
        return id != null ? id : 7L;
    }

    @Transactional
    public List<QuestionBank> ensureAnswersByKnowledgePoint(String knowledgePoint) {
        String kp = knowledgePoint == null ? "" : knowledgePoint.trim();
        if (kp.isBlank()) {
            return List.of();
        }
        Long parsedCategoryId = null;
        try {
            parsedCategoryId = Long.valueOf(kp);
        } catch (Exception ignored) {
            // 非数字时仍按 knowledge_point 分组
        }
        final Long categoryId = parsedCategoryId;
        List<QuestionBank> all = questionBankRepository.findByIsDelete(0);
        List<QuestionBank> list = all.stream()
                .filter(q -> {
                    if (q == null) return false;
                    if (categoryId != null && categoryId > 0) {
                        return categoryId.equals(q.getCategoryId());
                    }
                    return KnowledgePointUiMapper.sameUiGroup(q.getKnowledgePoint(), kp);
                })
                .toList();
        for (QuestionBank q : list) {
            if (q == null) continue;
            String ans = q.getQuestionAnswer() == null ? "" : q.getQuestionAnswer().trim();
            if (!isMissingAnswer(ans)) continue;

            String postName = "技术";
            if (q.getPostId() != null) {
                Post p = postRepository.findById(q.getPostId()).orElse(null);
                if (p != null && p.getPostName() != null && !p.getPostName().isBlank()) {
                    postName = p.getPostName();
                }
            }
            String generated = qwenService.generateReferenceAnswerForQuestionBank(q.getQuestionTitle(), postName);
            if (generated != null && !generated.trim().isBlank()) {
                q.setQuestionAnswer(fitQuestionAnswer(generated));
                questionBankRepository.save(q);
            }
        }
        List<QuestionBank> refreshedAll = questionBankRepository.findByIsDelete(0);
        List<QuestionBank> out =
                refreshedAll.stream()
                        .filter(q -> {
                            if (q == null) return false;
                            if (categoryId != null && categoryId > 0) {
                                return categoryId.equals(q.getCategoryId());
                            }
                            return KnowledgePointUiMapper.sameUiGroup(q.getKnowledgePoint(), kp);
                        })
                        .toList();
        enrichUiKnowledgePoint(out);
        return out;
    }

    /** 库字段 question_answer 目前为 varchar(255)，防止 AI 长答案写库失败 */
    private String fitQuestionAnswer(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        final int maxLen = 255;
        if (t.length() <= maxLen) return t;
        log.warn("question_answer 超长，已截断: {} -> {}", t.length(), maxLen);
        return t.substring(0, maxLen);
    }

    private boolean isMissingAnswer(String answer) {
        if (answer == null) return true;
        String a = answer.trim();
        if (a.isBlank()) return true;
        // 避免把“分类 JSON”误写为答案后无法再次生成
        String lower = a.toLowerCase();
        if (a.startsWith("{") && a.endsWith("}") && (lower.contains("category_id") || lower.contains("category_name") || lower.contains("categoryid"))) {
            return true;
        }
        return "--".equals(a)
                || "-".equals(a)
                || "暂无答案".equals(a)
                || "无".equals(a)
                || "N/A".equalsIgnoreCase(a)
                || "NA".equalsIgnoreCase(a);
    }

}
