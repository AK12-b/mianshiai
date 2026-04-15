package com.example.demo.service;

import com.example.demo.entity.ClockInRecord;
import com.example.demo.entity.InterviewEvaluate;
import com.example.demo.entity.MessageNotice;
import com.example.demo.entity.MockInterview;
import com.example.demo.entity.PracticeQuestionRecord;
import com.example.demo.repository.ClockInRecordRepository;
import com.example.demo.repository.InterviewEvaluateRepository;
import com.example.demo.repository.MessageNoticeRepository;
import com.example.demo.repository.MockInterviewRepository;
import com.example.demo.repository.PracticeQuestionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewEvaluateRepository interviewEvaluateRepository;
    private final PracticeQuestionRecordRepository practiceQuestionRecordRepository;
    private final ClockInRecordRepository clockInRecordRepository;
    private final MessageNoticeRepository messageNoticeRepository;

    @Transactional
    public Map<String, Object> getDashboardData(Long userId) {
        ensureDefaultNoticeAndTodo(userId);

        List<MockInterview> interviews = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        int interviewCount = interviews.size();
        Set<Long> interviewIds = new HashSet<>();
        for (MockInterview it : interviews) {
            if (it != null && it.getInterviewId() != null) {
                interviewIds.add(it.getInterviewId());
            }
        }

        List<InterviewEvaluate> allEvaluations = interviewEvaluateRepository.findByIsDelete(0);
        List<InterviewEvaluate> userEvaluations = allEvaluations.stream()
                .filter(e -> e != null && e.getInterviewId() != null && interviewIds.contains(e.getInterviewId()))
                .toList();

        BigDecimal highestScore = userEvaluations.stream()
                .map(InterviewEvaluate::getTotalScore)
                .filter(v -> v != null)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        List<PracticeQuestionRecord> practiceRecords = practiceQuestionRecordRepository.findByUserIdAndIsDelete(userId, 0);
        int practiceQuestionCount = practiceRecords.size();
        long passed = practiceRecords.stream()
                .filter(r -> r != null && Integer.valueOf(0).equals(r.getErrorType()))
                .count();
        int practiceAccuracy = practiceQuestionCount <= 0 ? 0 : (int) Math.round((passed * 100.0) / practiceQuestionCount);

        List<ClockInRecord> clockRecords = clockInRecordRepository.findByUserIdAndIsDeleteOrderByClockInTimeDesc(userId, 0);
        Set<LocalDate> clockDays = new HashSet<>();
        for (ClockInRecord r : clockRecords) {
            if (r == null || r.getClockInTime() == null) continue;
            clockDays.add(r.getClockInTime().toLocalDate());
        }
        int clockInDays = clockDays.size();
        int streakDays = computeStreak(clockDays);

        List<MessageNotice> messageRows = messageNoticeRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        List<Map<String, Object>> notices = new ArrayList<>();
        List<Map<String, Object>> todos = new ArrayList<>();
        for (MessageNotice row : messageRows) {
            if (row == null || row.getNoticeContent() == null || row.getNoticeContent().isBlank()) continue;
            if (Integer.valueOf(1).equals(row.getIsClose())) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("text", row.getNoticeContent());
            item.put("path", inferPath(row.getNoticeContent()));
            if (row.getNoticeType() != null && row.getNoticeType() == 3) {
                if (todos.size() < 4) todos.add(item);
            } else {
                if (notices.size() < 3) notices.add(item);
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("interviewCount", interviewCount);
        stats.put("highestScore", highestScore.setScale(0, RoundingMode.HALF_UP).intValue());
        stats.put("practiceQuestionCount", practiceQuestionCount);
        stats.put("practiceAccuracy", practiceAccuracy);
        stats.put("clockInDays", clockInDays);
        stats.put("streakDays", streakDays);

        Map<String, Object> resp = new HashMap<>();
        resp.put("stats", stats);
        resp.put("notices", notices);
        resp.put("todos", todos);
        return resp;
    }

    private int computeStreak(Set<LocalDate> days) {
        if (days.isEmpty()) return 0;
        int streak = 0;
        LocalDate cur = LocalDate.now();
        while (days.contains(cur)) {
            streak++;
            cur = cur.minusDays(1);
        }
        return streak;
    }

    private String inferPath(String text) {
        String t = text == null ? "" : text;
        if (t.contains("简历") || t.contains("项目经历")) return "/app/resume";
        if (t.contains("错题")) return "/app/wrong-book";
        if (t.contains("练习")) return "/app/practice";
        if (t.contains("报告") || t.contains("面试")) return "/app/interview-reports";
        return "/app/dashboard";
    }

    private void ensureDefaultNoticeAndTodo(Long userId) {
        List<MessageNotice> rows = messageNoticeRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
        if (!rows.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        seedMessage(userId, 2, "模拟面试评分模型已更新", now.minusHours(2));
        seedMessage(userId, 2, "你的简历优化结果可查看", now.minusHours(4));
        seedMessage(userId, 1, "本周练习目标还差 6 题", now.minusHours(8));
        seedMessage(userId, 3, "完成 Java 后端专项练习 10 题", now.minusHours(1));
        seedMessage(userId, 3, "复盘 3 道高频错题", now.minusHours(3));
        seedMessage(userId, 3, "查看最新面试报告", now.minusHours(5));
        seedMessage(userId, 3, "更新项目经历中的优化指标", now.minusHours(7));
    }

    private void seedMessage(Long userId, int noticeType, String content, LocalDateTime time) {
        MessageNotice row = new MessageNotice();
        row.setUserId(userId);
        row.setNoticeType(noticeType);
        row.setNoticeContent(content);
        row.setSendTime(time);
        row.setRemindCycle(0);
        row.setIsRead(0);
        row.setIsClose(0);
        row.setCreateTime(time);
        row.setIsDelete(0);
        messageNoticeRepository.save(row);
    }
}

