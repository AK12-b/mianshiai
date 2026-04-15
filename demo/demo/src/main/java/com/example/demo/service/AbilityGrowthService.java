package com.example.demo.service;

import com.example.demo.entity.AbilityGrowth;
import com.example.demo.entity.InterviewEvaluate;
import com.example.demo.entity.MockInterview;
import com.example.demo.entity.PracticeQuestionRecord;
import com.example.demo.entity.PracticeSingleTask;
import com.example.demo.repository.AbilityGrowthRepository;
import com.example.demo.repository.InterviewEvaluateRepository;
import com.example.demo.repository.MockInterviewRepository;
import com.example.demo.repository.PracticeQuestionRecordRepository;
import com.example.demo.repository.PracticeSingleTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbilityGrowthService {

    private final AbilityGrowthRepository abilityGrowthRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewEvaluateRepository interviewEvaluateRepository;
    private final PracticeSingleTaskRepository practiceSingleTaskRepository;
    private final PracticeQuestionRecordRepository practiceQuestionRecordRepository;
    private final QwenService qwenService;

    @Transactional
    public AbilityGrowth createGrowthRecord(Long userId, Long postId, Integer statisticsCycle, 
                                           String cycleTime, Integer interviewCount, BigDecimal avgScore,
                                           Integer practiceQuestionCount, Integer totalTrainDuration,
                                           Integer wrongQuestionCount, String weakPoint,
                                           BigDecimal weakPointImproveRate, String abilityImprove, String growthSuggest) {
        AbilityGrowth growth = new AbilityGrowth();
        growth.setUserId(userId);
        growth.setPostId(postId);
        growth.setStatisticsCycle(statisticsCycle);
        growth.setCycleTime(cycleTime);
        growth.setInterviewCount(interviewCount);
        growth.setAvgScore(avgScore);
        growth.setPracticeQuestionCount(practiceQuestionCount);
        growth.setTotalTrainDuration(totalTrainDuration);
        growth.setWrongQuestionCount(wrongQuestionCount);
        growth.setWeakPoint(weakPoint);
        growth.setWeakPointImproveRate(weakPointImproveRate);
        growth.setAbilityImprove(abilityImprove);
        growth.setGrowthSuggest(growthSuggest);
        growth.setCreateTime(LocalDateTime.now());
        growth.setIsDelete(0);

        return abilityGrowthRepository.save(growth);
    }

    public List<AbilityGrowth> getUserGrowthRecords(Long userId) {
        return buildFromSourceTables(userId);
    }

    public List<Map<String, Object>> getUserDimensionTrend(Long userId) {
        List<MockInterview> interviews = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        if (interviews.isEmpty()) return List.of();

        List<MockInterview> finished = interviews.stream()
                .filter(it -> it != null && (it.getEndTime() != null || Integer.valueOf(2).equals(it.getInterviewStatus())))
                .toList();
        if (finished.isEmpty()) return List.of();

        Set<Long> interviewIds = finished.stream().map(MockInterview::getInterviewId).collect(Collectors.toSet());
        List<InterviewEvaluate> evaluations = interviewEvaluateRepository.findByIsDelete(0).stream()
                .filter(e -> e != null && e.getInterviewId() != null && interviewIds.contains(e.getInterviewId()))
                .toList();
        Map<Long, InterviewEvaluate> evalMap = evaluations.stream()
                .collect(Collectors.toMap(InterviewEvaluate::getInterviewId, e -> e, (a, b) -> a));

        Map<String, StatBucket> buckets = new HashMap<>();
        for (MockInterview it : finished) {
            InterviewEvaluate ev = evalMap.get(it.getInterviewId());
            if (ev == null) continue;
            LocalDateTime t = it.getEndTime() != null ? it.getEndTime() : it.getStartTime();
            if (t == null) t = LocalDateTime.now();
            LocalDate d = t.toLocalDate();
            addToBucket(buckets, 1, cycleWeek(d), it, ev);
            addToBucket(buckets, 2, cycleMonth(d), it, ev);
            addToBucket(buckets, 3, cycleYear(d), it, ev);
        }

        return buckets.values().stream()
                .sorted(Comparator.comparing((StatBucket b) -> b.cycleTime).reversed())
                .map(b -> {
                    Map<String, Object> m = new HashMap<>();
                    BigDecimal avgScore = avg(b.totalScore, b.scoreCount);
                    BigDecimal tech = avg(b.tech, b.scoreCount);
                    BigDecimal depth = avg(b.depth, b.scoreCount);
                    BigDecimal logic = avg(b.logic, b.scoreCount);
                    BigDecimal express = avg(b.express, b.scoreCount);
                    BigDecimal match = avg(b.match, b.scoreCount);
                    m.put("statisticsCycle", b.cycle);
                    m.put("cycleTime", b.cycleTime);
                    m.put("postId", b.postId);
                    m.put("interviewCount", b.interviewCount);
                    m.put("avgScore", avgScore);
                    m.put("techScore", tech);
                    m.put("knowledgeDepthScore", depth);
                    m.put("logicScore", logic);
                    m.put("expressScore", express);
                    m.put("matchScore", match);

                    String weak = "技术正确性";
                    BigDecimal min = tech;
                    if (depth.compareTo(min) < 0) { min = depth; weak = "知识深度"; }
                    if (logic.compareTo(min) < 0) { min = logic; weak = "逻辑严谨性"; }
                    if (express.compareTo(min) < 0) { min = express; weak = "表达能力"; }
                    if (match.compareTo(min) < 0) { weak = "岗位匹配度"; }
                    m.put("weakPoint", weak);
                    m.put("growthSuggest", (b.growthSuggest == null || b.growthSuggest.isBlank()) ? "建议围绕薄弱维度做专项复盘训练。" : b.growthSuggest);
                    m.put("abilityImprove", "持续训练中");
                    return m;
                })
                .toList();
    }

    /**
     * 按周展示：计算「最近7天（含今天）」的每日平均分/维度分（滚动窗口）。
     * 返回仅包含有评估数据的日期；前端负责把 7 天补齐并将无数据日期显示为空。
     */
    public List<Map<String, Object>> getUserDailyTrendForCurrentWeek(Long userId, Long postId) {
        List<MockInterview> interviews = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        if (interviews.isEmpty()) return List.of();

        List<MockInterview> finished = interviews.stream()
                .filter(it -> it != null && (it.getEndTime() != null || Integer.valueOf(2).equals(it.getInterviewStatus())))
                .filter(it -> postId == null || postId <= 0 || (it.getPostId() != null && it.getPostId().equals(postId)))
                .toList();
        if (finished.isEmpty()) return List.of();

        Set<Long> interviewIds = finished.stream().map(MockInterview::getInterviewId).collect(Collectors.toSet());
        List<InterviewEvaluate> evaluations = interviewEvaluateRepository.findByIsDelete(0).stream()
                .filter(e -> e != null && e.getInterviewId() != null && interviewIds.contains(e.getInterviewId()))
                .toList();
        Map<Long, InterviewEvaluate> evalMap = evaluations.stream()
                .collect(Collectors.toMap(InterviewEvaluate::getInterviewId, e -> e, (a, b) -> a));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate weekEnd = today;

        Map<String, StatBucket> buckets = new HashMap<>();
        for (MockInterview it : finished) {
            InterviewEvaluate ev = evalMap.get(it.getInterviewId());
            if (ev == null) continue;
            LocalDateTime t = it.getEndTime() != null ? it.getEndTime() : it.getStartTime();
            if (t == null) continue;
            LocalDate d = t.toLocalDate();
            if (d.isBefore(weekStart) || d.isAfter(weekEnd) || d.isAfter(today)) continue;
            addToBucket(buckets, 1, d.toString(), it, ev);
        }

        return buckets.values().stream()
                .sorted(Comparator.comparing((StatBucket b) -> b.cycleTime).reversed())
                .map(b -> {
                    Map<String, Object> m = new HashMap<>();
                    BigDecimal avgScore = avg(b.totalScore, b.scoreCount);
                    BigDecimal tech = avg(b.tech, b.scoreCount);
                    BigDecimal depth = avg(b.depth, b.scoreCount);
                    BigDecimal logic = avg(b.logic, b.scoreCount);
                    BigDecimal express = avg(b.express, b.scoreCount);
                    BigDecimal match = avg(b.match, b.scoreCount);
                    m.put("date", b.cycleTime); // yyyy-MM-dd
                    m.put("interviewCount", b.interviewCount);
                    m.put("avgScore", avgScore);
                    m.put("techScore", tech);
                    m.put("knowledgeDepthScore", depth);
                    m.put("logicScore", logic);
                    m.put("expressScore", express);
                    m.put("matchScore", match);
                    return m;
                })
                .toList();
    }

    /**
     * 雷达图专用：按前端选中的周期，从面试主表+评估表实时聚合维度分。
     * cycle: 1=最近7天，2=当月，3=当年；postId 可选（0/空=全部岗位）
     */
    public Map<String, Object> getRadarMetricByCycle(Long userId, Integer cycle, Long postId) {
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

        List<MockInterview> interviews = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        if (interviews.isEmpty()) return emptyRadarMetric(c, postId);

        List<MockInterview> filtered = interviews.stream()
                .filter(it -> it != null && (it.getEndTime() != null || Integer.valueOf(2).equals(it.getInterviewStatus())))
                .filter(it -> postId == null || postId <= 0 || (it.getPostId() != null && it.getPostId().equals(postId)))
                .filter(it -> {
                    LocalDateTime t = it.getEndTime() != null ? it.getEndTime() : it.getStartTime();
                    if (t == null) return false;
                    LocalDate d = t.toLocalDate();
                    return !d.isBefore(start) && !d.isAfter(end);
                })
                .toList();
        if (filtered.isEmpty()) return emptyRadarMetric(c, postId);

        Set<Long> interviewIds = filtered.stream().map(MockInterview::getInterviewId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<InterviewEvaluate> evaluations = interviewEvaluateRepository.findByIsDelete(0).stream()
                .filter(e -> e != null && e.getInterviewId() != null && interviewIds.contains(e.getInterviewId()))
                .toList();
        if (evaluations.isEmpty()) return emptyRadarMetric(c, postId);

        BigDecimal tech = BigDecimal.ZERO;
        BigDecimal depth = BigDecimal.ZERO;
        BigDecimal logic = BigDecimal.ZERO;
        BigDecimal express = BigDecimal.ZERO;
        BigDecimal match = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        int n = 0;
        for (InterviewEvaluate e : evaluations) {
            if (e == null) continue;
            tech = tech.add(z(e.getTechScore()));
            depth = depth.add(z(e.getKnowledgeDepthScore()));
            logic = logic.add(z(e.getLogicScore()));
            express = express.add(z(e.getExpressScore()));
            match = match.add(z(e.getMatchScore()));
            total = total.add(z(e.getTotalScore()));
            n++;
        }
        if (n <= 0) return emptyRadarMetric(c, postId);

        Map<String, Object> m = new HashMap<>();
        m.put("statisticsCycle", c);
        m.put("postId", postId == null ? 0L : postId);
        m.put("interviewCount", filtered.size());
        m.put("avgScore", avg(total, n));
        m.put("techScore", avg(tech, n));
        m.put("knowledgeDepthScore", avg(depth, n));
        m.put("logicScore", avg(logic, n));
        m.put("expressScore", avg(express, n));
        m.put("matchScore", avg(match, n));
        m.put("windowStart", start.toString());
        m.put("windowEnd", end.toString());
        return m;
    }

    private List<AbilityGrowth> buildFromSourceTables(Long userId) {
        List<MockInterview> interviews = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        List<MockInterview> finishedInterviews = interviews.stream()
                .filter(it -> it != null && (it.getEndTime() != null || Integer.valueOf(2).equals(it.getInterviewStatus())))
                .toList();
        Set<Long> interviewIds = finishedInterviews.stream()
                .map(MockInterview::getInterviewId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<InterviewEvaluate> evaluations = interviewEvaluateRepository.findByIsDelete(0).stream()
                .filter(e -> e != null && e.getInterviewId() != null && interviewIds.contains(e.getInterviewId()))
                .toList();
        Map<Long, InterviewEvaluate> evalMap = evaluations.stream()
                .collect(Collectors.toMap(InterviewEvaluate::getInterviewId, e -> e, (a, b) -> a));

        List<PracticeSingleTask> singleTasks = practiceSingleTaskRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        List<PracticeQuestionRecord> questionRecords = practiceQuestionRecordRepository.findByUserIdAndIsDelete(userId, 0);
        Map<Long, List<PracticeQuestionRecord>> recordsBySingle = questionRecords.stream()
                .filter(r -> r != null && r.getSingleId() != null)
                .collect(Collectors.groupingBy(PracticeQuestionRecord::getSingleId));

        Map<String, StatBucket> buckets = new HashMap<>();
        for (MockInterview it : finishedInterviews) {
            LocalDateTime t = it.getEndTime() != null ? it.getEndTime() : it.getStartTime();
            if (t == null) t = LocalDateTime.now();
            LocalDate d = t.toLocalDate();
            addInterviewToBucket(buckets, 1, cycleDay(d), it, evalMap.get(it.getInterviewId()));
            addToBucket(buckets, 2, cycleMonth(d), it, evalMap.get(it.getInterviewId()));
            addToBucket(buckets, 3, cycleYear(d), it, evalMap.get(it.getInterviewId()));
        }
        for (PracticeSingleTask st : singleTasks) {
            if (st == null) continue;
            LocalDateTime t = st.getFinishTime() != null ? st.getFinishTime() : (st.getStartTime() != null ? st.getStartTime() : st.getCreateTime());
            if (t == null) continue;
            LocalDate d = t.toLocalDate();
            List<PracticeQuestionRecord> rs = recordsBySingle.getOrDefault(st.getSingleId(), List.of());
            addPracticeToBucket(buckets, 1, cycleDay(d), st, rs);
            addPracticeToBucket(buckets, 2, cycleMonth(d), st, rs);
            addPracticeToBucket(buckets, 3, cycleYear(d), st, rs);
        }

        List<AbilityGrowth> out = buckets.values().stream()
                .map(b -> toGrowth(userId, b))
                .sorted(Comparator.comparing(AbilityGrowth::getCreateTime).reversed())
                .toList();
        attachPersonalizedSuggestion(out, userId);
        return out;
    }

    private void attachPersonalizedSuggestion(List<AbilityGrowth> rows, Long userId) {
        if (rows == null || rows.isEmpty()) return;
        AbilityGrowth latest = rows.get(0);
        if (latest == null) return;
        String prompt = """
                你是个性化成长分析智能体。请根据用户本周期训练数据生成一句简短建议（不超过40字）。
                输出纯文本，不要JSON。
                数据：
                - 用户ID：%s
                - 周期：%s
                - 时间：%s
                - 面试次数：%d
                - 面试平均分：%s
                - 练习题数：%d
                - 累计训练时长（分钟）：%d
                - 当前短板：%s
                """
                .formatted(
                        String.valueOf(userId),
                        String.valueOf(latest.getStatisticsCycle()),
                        String.valueOf(latest.getCycleTime()),
                        latest.getInterviewCount() == null ? 0 : latest.getInterviewCount(),
                        latest.getAvgScore() == null ? "0" : latest.getAvgScore().toPlainString(),
                        latest.getPracticeQuestionCount() == null ? 0 : latest.getPracticeQuestionCount(),
                        latest.getTotalTrainDuration() == null ? 0 : latest.getTotalTrainDuration(),
                        latest.getWeakPoint() == null ? "技术正确性" : latest.getWeakPoint()
                );
        try {
            String text = qwenService.analyzeGrowth(prompt);
            if (text != null && !text.isBlank()) {
                latest.setGrowthSuggest(text.trim());
            }
        } catch (Exception ignored) {
            // keep fallback suggestion
        }
    }

    private void addInterviewToBucket(Map<String, StatBucket> buckets, int cycle, String cycleTime, MockInterview it, InterviewEvaluate ev) {
        addToBucket(buckets, cycle, cycleTime, it, ev);
    }

    private void addPracticeToBucket(Map<String, StatBucket> buckets, int cycle, String cycleTime, PracticeSingleTask st, List<PracticeQuestionRecord> rs) {
        Long postId = st.getPostId() == null ? 0L : st.getPostId();
        String key = cycle + "|" + cycleTime + "|" + postId;
        StatBucket b = buckets.computeIfAbsent(key, k -> new StatBucket(cycle, cycleTime, postId));
        int duration = st.getTotalDuration() == null ? 0 : Math.max(0, st.getTotalDuration());
        if (duration <= 0) {
            duration = rs.stream().mapToInt(r -> r.getAnswerDuration() == null ? 0 : Math.max(0, r.getAnswerDuration())).sum();
        }
        b.totalTrainDuration += duration;
        b.practiceQuestionCount += rs.size();
        int wrong = (int) rs.stream().filter(r -> r != null && !Integer.valueOf(0).equals(r.getErrorType())).count();
        b.wrongQuestionCount += wrong;
        b.createTime = LocalDateTime.now();
    }

    private void addToBucket(Map<String, StatBucket> buckets, int cycle, String cycleTime, MockInterview it, InterviewEvaluate ev) {
        Long postId = it.getPostId() == null ? 0L : it.getPostId();
        String key = cycle + "|" + cycleTime + "|" + postId;
        StatBucket b = buckets.computeIfAbsent(key, k -> new StatBucket(cycle, cycleTime, postId));
        b.interviewCount += 1;
        b.totalTrainDuration += it.getInterviewDuration() == null ? 0 : it.getInterviewDuration();
        if (ev != null) {
            b.scoreCount += 1;
            b.totalScore = b.totalScore.add(z(ev.getTotalScore()));
            b.tech = b.tech.add(z(ev.getTechScore()));
            b.depth = b.depth.add(z(ev.getKnowledgeDepthScore()));
            b.logic = b.logic.add(z(ev.getLogicScore()));
            b.express = b.express.add(z(ev.getExpressScore()));
            b.match = b.match.add(z(ev.getMatchScore()));
            if (b.growthSuggest == null || b.growthSuggest.isBlank()) b.growthSuggest = ev.getSuggestGuide();
        }
        b.createTime = LocalDateTime.now();
    }

    private AbilityGrowth toGrowth(Long userId, StatBucket b) {
        AbilityGrowth g = new AbilityGrowth();
        g.setUserId(userId);
        g.setPostId(b.postId);
        g.setStatisticsCycle(b.cycle);
        g.setCycleTime(b.cycleTime);
        g.setInterviewCount(b.interviewCount);
        g.setPracticeQuestionCount(b.practiceQuestionCount);
        g.setWrongQuestionCount(b.wrongQuestionCount);
        g.setTotalTrainDuration(b.totalTrainDuration);
        g.setAvgScore(avg(b.totalScore, b.scoreCount));

        BigDecimal techAvg = avg(b.tech, b.scoreCount);
        BigDecimal logicAvg = avg(b.logic, b.scoreCount);
        BigDecimal expressAvg = avg(b.express, b.scoreCount);
        BigDecimal matchAvg = avg(b.match, b.scoreCount);

        String weak = "技术正确性";
        BigDecimal min = techAvg;
        if (logicAvg.compareTo(min) < 0) { min = logicAvg; weak = "逻辑严谨性"; }
        if (expressAvg.compareTo(min) < 0) { min = expressAvg; weak = "表达能力"; }
        if (matchAvg.compareTo(min) < 0) { weak = "岗位匹配度"; }
        g.setWeakPoint(weak);
        g.setWeakPointImproveRate(BigDecimal.ZERO);
        g.setAbilityImprove("持续训练中");
        g.setGrowthSuggest((b.growthSuggest == null || b.growthSuggest.isBlank()) ? "建议围绕薄弱维度进行专项练习，并复盘近期面试表现。" : b.growthSuggest);
        g.setCreateTime(b.createTime);
        g.setIsDelete(0);
        return g;
    }

    private BigDecimal z(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal avg(BigDecimal sum, int count) {
        if (count <= 0) return BigDecimal.ZERO;
        return sum.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
    }

    private String cycleMonth(LocalDate d) {
        return String.format("%d-%02d", d.getYear(), d.getMonthValue());
    }

    private String cycleYear(LocalDate d) {
        return String.valueOf(d.getYear());
    }

    private String cycleDay(LocalDate d) {
        return d.toString(); // yyyy-MM-dd（滚动周结束日期）
    }

    private String cycleWeek(LocalDate d) {
        WeekFields wf = WeekFields.of(Locale.CHINA);
        int week = d.get(wf.weekOfWeekBasedYear());
        int year = d.get(wf.weekBasedYear());
        return String.format("%d-W%02d", year, week);
    }

    private Map<String, Object> emptyRadarMetric(int cycle, Long postId) {
        Map<String, Object> m = new HashMap<>();
        m.put("statisticsCycle", cycle);
        m.put("postId", postId == null ? 0L : postId);
        m.put("interviewCount", 0);
        m.put("avgScore", BigDecimal.ZERO);
        m.put("techScore", BigDecimal.ZERO);
        m.put("knowledgeDepthScore", BigDecimal.ZERO);
        m.put("logicScore", BigDecimal.ZERO);
        m.put("expressScore", BigDecimal.ZERO);
        m.put("matchScore", BigDecimal.ZERO);
        return m;
    }

    private static class StatBucket {
        final int cycle;
        final String cycleTime;
        final Long postId;
        int interviewCount = 0;
        int scoreCount = 0;
        int practiceQuestionCount = 0;
        int wrongQuestionCount = 0;
        int totalTrainDuration = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal tech = BigDecimal.ZERO;
        BigDecimal depth = BigDecimal.ZERO;
        BigDecimal logic = BigDecimal.ZERO;
        BigDecimal express = BigDecimal.ZERO;
        BigDecimal match = BigDecimal.ZERO;
        String growthSuggest = "";
        LocalDateTime createTime = LocalDateTime.now();

        StatBucket(int cycle, String cycleTime, Long postId) {
            this.cycle = cycle;
            this.cycleTime = cycleTime;
            this.postId = postId;
        }
    }
}