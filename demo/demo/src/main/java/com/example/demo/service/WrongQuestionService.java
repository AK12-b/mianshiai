package com.example.demo.service;

import com.example.demo.entity.InterviewWrongQuestion;
import com.example.demo.entity.InterviewDialog;
import com.example.demo.entity.MockInterview;
import com.example.demo.entity.QuestionBank;
import com.example.demo.repository.KnowledgeCategoryRepository;
import com.example.demo.repository.InterviewWrongQuestionRepository;
import com.example.demo.repository.InterviewDialogRepository;
import com.example.demo.repository.MockInterviewRepository;
import com.example.demo.repository.QuestionBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WrongQuestionService {

    private final InterviewWrongQuestionRepository interviewWrongQuestionRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewDialogRepository interviewDialogRepository;
    private final QuestionBankRepository questionBankRepository;
    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    @Transactional
    public InterviewWrongQuestion addWrongQuestion(Long userId, Long questionId, Long interviewId, 
                                              Long practiceId, Integer wrongReason, String knowledgePoint) {
        boolean exists = interviewWrongQuestionRepository.existsByUserIdAndQuestionIdAndIsDelete(userId, questionId, 0);
        if (exists) {
            List<InterviewWrongQuestion> existing = interviewWrongQuestionRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
            for (InterviewWrongQuestion row : existing) {
                if (row != null && questionId != null && questionId.equals(row.getQuestionId())) {
                    return row;
                }
            }
        }
        InterviewWrongQuestion wrongQuestion = new InterviewWrongQuestion();
        wrongQuestion.setUserId(userId);
        wrongQuestion.setQuestionId(questionId);
        wrongQuestion.setInterviewId(interviewId);
        wrongQuestion.setPracticeId(practiceId);
        wrongQuestion.setWrongReason(wrongReason);
        wrongQuestion.setKnowledgePoint(knowledgePoint);
        wrongQuestion.setIsCollect(0);
        wrongQuestion.setCreateTime(LocalDateTime.now());
        wrongQuestion.setIsDelete(0);

        return interviewWrongQuestionRepository.save(wrongQuestion);
    }

    @Transactional
    public InterviewWrongQuestion updateWrongReason(Long wrongId, Integer wrongReason) {
        InterviewWrongQuestion wrongQuestion = interviewWrongQuestionRepository.findById(wrongId).orElse(null);
        if (wrongQuestion == null) {
            return null;
        }
        wrongQuestion.setWrongReason(wrongReason);
        return interviewWrongQuestionRepository.save(wrongQuestion);
    }

    @Transactional
    public InterviewWrongQuestion toggleCollect(Long wrongId) {
        InterviewWrongQuestion wrongQuestion = interviewWrongQuestionRepository.findById(wrongId).orElse(null);
        if (wrongQuestion != null) {
            wrongQuestion.setIsCollect(wrongQuestion.getIsCollect() == 0 ? 1 : 0);
            return interviewWrongQuestionRepository.save(wrongQuestion);
        }
        return null;
    }

    @Transactional
    public boolean deleteWrongQuestion(Long wrongId) {
        InterviewWrongQuestion wrongQuestion = interviewWrongQuestionRepository.findById(wrongId).orElse(null);
        if (wrongQuestion != null) {
            wrongQuestion.setIsDelete(1);
            interviewWrongQuestionRepository.save(wrongQuestion);
            return true;
        }
        return false;
    }

    public List<InterviewWrongQuestion> getUserWrongQuestions(Long userId) {
        List<InterviewWrongQuestion> rows = interviewWrongQuestionRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        return dedupeByQuestion(rows);
    }

    public List<InterviewWrongQuestion> getUserCollectedQuestions(Long userId) {
        List<InterviewWrongQuestion> rows = interviewWrongQuestionRepository.findByUserIdAndIsCollectAndIsDelete(userId, 1, 0);
        return dedupeByQuestion(rows);
    }

    /**
     * 成长数据页「错题统计」专用：按周期/岗位返回错题题目文本（不带次数）。
     * cycle: 1=最近7天，2=当月，3=当年；postId 可选（0/空=全部岗位）
     */
    public List<String> getWrongQuestionTitlesByCycle(Long userId, Integer cycle, Long postId, int limit) {
        if (userId == null || userId <= 0) return List.of();
        int c = cycle == null ? 1 : cycle;
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;
        if (c == 3) {
            start = LocalDate.of(today.getYear(), 1, 1);
        } else if (c == 2) {
            start = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        } else {
            start = today.minusDays(6);
            c = 1;
        }

        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay(); // [start, end+1)
        List<InterviewWrongQuestion> rows = interviewWrongQuestionRepository.findByUserIdAndCreateTimeRange(userId, startTime, endTime);
        if (rows == null || rows.isEmpty()) return List.of();

        // 按 questionId 去重（保留最新），并保持顺序
        LinkedHashMap<Long, InterviewWrongQuestion> deduped = new LinkedHashMap<>();
        for (InterviewWrongQuestion w : rows) {
            if (w == null || w.getQuestionId() == null) continue;
            deduped.putIfAbsent(w.getQuestionId(), w);
            if (deduped.size() >= Math.max(1, limit) * 2) {
                // 提前截断避免无意义加载（后续还要按岗位过滤）
                break;
            }
        }
        if (deduped.isEmpty()) return List.of();

        List<Long> qids = new ArrayList<>(deduped.keySet());
        Map<Long, QuestionBank> qMap = questionBankRepository.findAllById(qids).stream()
                .filter(q -> q != null && Integer.valueOf(0).equals(q.getIsDelete()))
                .collect(Collectors.toMap(QuestionBank::getQuestionId, q -> q, (a, b) -> a));

        List<String> titles = new ArrayList<>();
        for (Long qid : qids) {
            QuestionBank qb = qMap.get(qid);
            if (qb == null) continue;
            if (postId != null && postId > 0) {
                if (qb.getPostId() == null || !postId.equals(qb.getPostId())) continue;
            }
            String text = qb.getQuestionTitle() != null && !qb.getQuestionTitle().trim().isBlank()
                    ? qb.getQuestionTitle().trim()
                    : (qb.getKnowledgePoint() != null && !qb.getKnowledgePoint().trim().isBlank()
                        ? qb.getKnowledgePoint().trim()
                        : ("题目#" + qid));
            titles.add(text);
            if (titles.size() >= Math.max(1, limit)) break;
        }
        return titles;
    }

    /**
     * 成长数据页「错题统计」专用：按周期/岗位返回错题“知识点/题目类型”标签（不带次数）。
     * 优先使用错题本行的 knowledgePoint；否则使用题库的 knowledgePoint；再退化为题目标题。
     * cycle: 1=最近7天，2=当月，3=当年；postId 可选（0/空=全部岗位）
     */
    public List<String> getWrongKnowledgePointsByCycle(Long userId, Integer cycle, Long postId, int limit) {
        if (userId == null || userId <= 0) return List.of();
        int c = cycle == null ? 1 : cycle;
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;
        if (c == 3) {
            start = LocalDate.of(today.getYear(), 1, 1);
        } else if (c == 2) {
            start = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        } else {
            start = today.minusDays(6);
        }

        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<InterviewWrongQuestion> rows = interviewWrongQuestionRepository.findByUserIdAndCreateTimeRange(userId, startTime, endTime);
        if (rows == null || rows.isEmpty()) return List.of();

        // 先按 questionId 去重（保留最新），保持顺序
        LinkedHashMap<Long, InterviewWrongQuestion> deduped = new LinkedHashMap<>();
        for (InterviewWrongQuestion w : rows) {
            if (w == null || w.getQuestionId() == null) continue;
            deduped.putIfAbsent(w.getQuestionId(), w);
            if (deduped.size() >= Math.max(1, limit) * 3) break;
        }
        if (deduped.isEmpty()) return List.of();

        List<Long> qids = new ArrayList<>(deduped.keySet());
        Map<Long, QuestionBank> qMap = questionBankRepository.findAllById(qids).stream()
                .filter(q -> q != null && Integer.valueOf(0).equals(q.getIsDelete()))
                .collect(Collectors.toMap(QuestionBank::getQuestionId, q -> q, (a, b) -> a));

        // 标签去重（同一知识点只展示一次），并保持顺序
        LinkedHashMap<String, Boolean> labelDedup = new LinkedHashMap<>();
        for (Long qid : qids) {
            InterviewWrongQuestion w = deduped.get(qid);
            QuestionBank qb = qMap.get(qid);
            if (qb == null) continue;
            if (postId != null && postId > 0) {
                if (qb.getPostId() == null || !postId.equals(qb.getPostId())) continue;
            }

            String kp = (w != null && w.getKnowledgePoint() != null) ? w.getKnowledgePoint().trim() : "";
            if (kp.isBlank()) kp = (qb.getKnowledgePoint() == null ? "" : qb.getKnowledgePoint().trim());
            if (kp.isBlank()) kp = (qb.getQuestionTitle() == null ? "" : qb.getQuestionTitle().trim());
            if (kp.isBlank()) continue;

            labelDedup.putIfAbsent(kp, true);
            if (labelDedup.size() >= Math.max(1, limit)) break;
        }
        return new ArrayList<>(labelDedup.keySet());
    }

    /**
     * 成长数据页「错题统计」专用：按周期/岗位聚合知识点错题次数（TopN），用于词云按次数缩放字号。
     * 返回按 count desc 排序的 {name, count} 列表。
     */
    public List<Map<String, Object>> getWrongKnowledgePointStatsByCycle(Long userId, Integer cycle, Long postId, int limit) {
        if (userId == null || userId <= 0) return List.of();
        int c = cycle == null ? 1 : cycle;
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;
        if (c == 3) {
            start = LocalDate.of(today.getYear(), 1, 1);
        } else if (c == 2) {
            start = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        } else {
            start = today.minusDays(6);
        }

        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<InterviewWrongQuestion> rows = interviewWrongQuestionRepository.findByUserIdAndCreateTimeRange(userId, startTime, endTime);
        if (rows == null || rows.isEmpty()) return List.of();

        // 先取出 questionId -> QuestionBank（用于岗位过滤与 knowledgePoint 兜底）
        List<Long> qids = rows.stream()
                .filter(x -> x != null && x.getQuestionId() != null)
                .map(InterviewWrongQuestion::getQuestionId)
                .distinct()
                .limit(2000)
                .toList();
        Map<Long, QuestionBank> qMap = questionBankRepository.findAllById(qids).stream()
                .filter(q -> q != null && Integer.valueOf(0).equals(q.getIsDelete()))
                .collect(Collectors.toMap(QuestionBank::getQuestionId, q -> q, (a, b) -> a));

        Map<String, Integer> counter = new LinkedHashMap<>();
        for (InterviewWrongQuestion w : rows) {
            if (w == null || w.getQuestionId() == null) continue;
            QuestionBank qb = qMap.get(w.getQuestionId());
            if (qb == null) continue;
            if (postId != null && postId > 0) {
                if (qb.getPostId() == null || !postId.equals(qb.getPostId())) continue;
            }

            String name = (w.getKnowledgePoint() == null ? "" : w.getKnowledgePoint().trim());
            if (name.isBlank()) name = (qb.getKnowledgePoint() == null ? "" : qb.getKnowledgePoint().trim());
            if (name.isBlank()) name = (qb.getQuestionTitle() == null ? "" : qb.getQuestionTitle().trim());
            if (name.isBlank()) continue;

            counter.put(name, (counter.getOrDefault(name, 0) + 1));
        }

        return counter.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(Math.max(1, limit))
                .map(e -> Map.<String, Object>of("name", e.getKey(), "count", e.getValue()))
                .toList();
    }

    /**
     * 数据修复：错题本表为空时，从「已完成面试」中回填错题卡片。
     * 规则：回填每场面试中 speaker=1 的题目，错误原因留空，交由用户后续手动选择。
     */
    @Transactional
    public int backfillFromCompletedInterviews(Long userId, int maxInterviews) {
        if (userId == null || userId <= 0) return 0;
        List<MockInterview> completed =
                mockInterviewRepository.findByUserIdAndInterviewStatusAndIsDelete(userId, 2, 0);
        if (completed.isEmpty()) return 0;
        int limit = Math.max(1, Math.min(maxInterviews, completed.size()));
        int inserted = 0;
        Set<String> dedupe = new HashSet<>();

        for (int i = 0; i < limit; i++) {
            MockInterview it = completed.get(i);
            if (it == null || it.getInterviewId() == null) continue;
            Long interviewId = it.getInterviewId();

            List<InterviewDialog> dialogs =
                    interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
            if (dialogs.isEmpty()) continue;

            for (InterviewDialog d : dialogs) {
                if (d == null || !Integer.valueOf(1).equals(d.getSpeaker())) continue;
                String qText = d.getContentText() == null ? "" : d.getContentText().trim();
                if (qText.isBlank()) continue;

                Long postId = it.getPostId();
                QuestionBank qb = null;
                if (postId != null) {
                    qb = questionBankRepository.findFirstByPostIdAndQuestionTitleAndIsDelete(postId, qText, 0).orElse(null);
                }
                if (qb == null) {
                    qb = new QuestionBank();
                    qb.setPostId(postId == null ? 1L : postId);
                    qb.setCategoryId(resolveCategoryId(it.getInterviewModule()));
                    qb.setQuestionType(1);
                    qb.setQuestionLevel(2);
                    qb.setQuestionTitle(qText);
                    qb.setQuestionAnswer("");
                    qb.setKnowledgePoint(it.getInterviewModule() == null ? "" : it.getInterviewModule());
                    qb.setCreateTime(LocalDateTime.now());
                    qb.setIsDelete(0);
                    qb.setIsAiGenerate(1);
                    qb = questionBankRepository.save(qb);
                }

                String key = userId + "|" + qb.getQuestionId();
                if (dedupe.contains(key)) continue;
                dedupe.add(key);
                boolean exists = interviewWrongQuestionRepository.existsByUserIdAndQuestionIdAndIsDelete(
                        userId, qb.getQuestionId(), 0
                );
                if (exists) continue;

                InterviewWrongQuestion w = new InterviewWrongQuestion();
                w.setUserId(userId);
                w.setQuestionId(qb.getQuestionId());
                w.setInterviewId(interviewId);
                w.setPracticeId(null);
                w.setWrongReason(null);
                w.setKnowledgePoint(qb.getKnowledgePoint());
                w.setIsCollect(0);
                w.setCreateTime(LocalDateTime.now());
                w.setIsDelete(0);
                interviewWrongQuestionRepository.save(w);
                inserted++;
            }
        }
        return inserted;
    }

    private long resolveCategoryId(String knowledgePoint) {
        String raw = knowledgePoint == null ? "" : knowledgePoint.trim();
        // 这里优先按库内 category_name 精确匹配；找不到就落到「基础编程练习」(7)
        Long id = knowledgeCategoryRepository.findFirstByCategoryName(raw).map(x -> x.getCategoryId()).orElse(null);
        return id != null ? id : 7L;
    }

    private List<InterviewWrongQuestion> dedupeByQuestion(List<InterviewWrongQuestion> rows) {
        if (rows == null || rows.isEmpty()) return rows;
        Map<Long, InterviewWrongQuestion> map = new LinkedHashMap<>();
        List<InterviewWrongQuestion> noQuestionRows = new ArrayList<>();
        for (InterviewWrongQuestion row : rows) {
            if (row == null) continue;
            Long qid = row.getQuestionId();
            if (qid == null) {
                noQuestionRows.add(row);
                continue;
            }
            map.putIfAbsent(qid, row);
        }
        List<InterviewWrongQuestion> result = new ArrayList<>(map.values());
        result.addAll(noQuestionRows);
        return result;
    }
}