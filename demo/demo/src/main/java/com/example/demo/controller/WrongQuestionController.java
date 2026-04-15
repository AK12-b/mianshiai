package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.InterviewWrongQuestion;
import com.example.demo.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wrong-question")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    @PostMapping("/add")
    public ApiResponse<InterviewWrongQuestion> addWrongQuestion(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long questionId = Long.valueOf(request.get("questionId").toString());
        Long interviewId = request.get("interviewId") != null ? 
            Long.valueOf(request.get("interviewId").toString()) : null;
        Long practiceId = request.get("practiceId") != null ? 
            Long.valueOf(request.get("practiceId").toString()) : null;
        Integer wrongReason = request.get("wrongReason") != null ?
            Integer.valueOf(request.get("wrongReason").toString()) : null;
        String knowledgePoint = (String) request.get("knowledgePoint");

        InterviewWrongQuestion wrongQuestion = wrongQuestionService.addWrongQuestion(userId, questionId, 
            interviewId, practiceId, wrongReason, knowledgePoint);
        return ApiResponse.success(wrongQuestion);
    }

    @PutMapping("/{wrongId}/reason")
    public ApiResponse<InterviewWrongQuestion> updateWrongReason(@PathVariable Long wrongId, @RequestBody Map<String, Object> request) {
        if (request.get("wrongReason") == null) {
            return ApiResponse.error("wrongReason 不能为空");
        }
        Integer wrongReason = Integer.valueOf(request.get("wrongReason").toString());
        InterviewWrongQuestion wrongQuestion = wrongQuestionService.updateWrongReason(wrongId, wrongReason);
        if (wrongQuestion != null) {
            return ApiResponse.success(wrongQuestion);
        }
        return ApiResponse.error("更新失败");
    }

    @PostMapping("/{wrongId}/toggle-collect")
    public ApiResponse<InterviewWrongQuestion> toggleCollect(@PathVariable Long wrongId) {
        InterviewWrongQuestion wrongQuestion = wrongQuestionService.toggleCollect(wrongId);
        if (wrongQuestion != null) {
            return ApiResponse.success(wrongQuestion);
        }
        return ApiResponse.error("操作失败");
    }

    @DeleteMapping("/{wrongId}")
    public ApiResponse<String> deleteWrongQuestion(@PathVariable Long wrongId) {
        boolean deleted = wrongQuestionService.deleteWrongQuestion(wrongId);
        if (deleted) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<InterviewWrongQuestion>> getUserWrongQuestions(@PathVariable Long userId) {
        List<InterviewWrongQuestion> wrongQuestions = wrongQuestionService.getUserWrongQuestions(userId);
        return ApiResponse.success(wrongQuestions);
    }

    @GetMapping("/user/{userId}/titles")
    public ApiResponse<List<String>> getUserWrongQuestionTitles(@PathVariable Long userId,
                                                                @RequestParam(value = "cycle", required = false) Integer cycle,
                                                                @RequestParam(value = "postId", required = false) Long postId,
                                                                @RequestParam(value = "limit", required = false) Integer limit) {
        int lim = (limit == null ? 12 : Math.max(1, Math.min(50, limit)));
        List<String> titles = wrongQuestionService.getWrongQuestionTitlesByCycle(userId, cycle, postId, lim);
        return ApiResponse.success(titles);
    }

    @GetMapping("/user/{userId}/knowledge-points")
    public ApiResponse<List<String>> getUserWrongKnowledgePoints(@PathVariable Long userId,
                                                                 @RequestParam(value = "cycle", required = false) Integer cycle,
                                                                 @RequestParam(value = "postId", required = false) Long postId,
                                                                 @RequestParam(value = "limit", required = false) Integer limit) {
        int lim = (limit == null ? 12 : Math.max(1, Math.min(50, limit)));
        List<String> labels = wrongQuestionService.getWrongKnowledgePointsByCycle(userId, cycle, postId, lim);
        return ApiResponse.success(labels);
    }

    @GetMapping("/user/{userId}/knowledge-point-stats")
    public ApiResponse<List<Map<String, Object>>> getUserWrongKnowledgePointStats(@PathVariable Long userId,
                                                                                  @RequestParam(value = "cycle", required = false) Integer cycle,
                                                                                  @RequestParam(value = "postId", required = false) Long postId,
                                                                                  @RequestParam(value = "limit", required = false) Integer limit) {
        int lim = (limit == null ? 18 : Math.max(1, Math.min(50, limit)));
        List<Map<String, Object>> list = wrongQuestionService.getWrongKnowledgePointStatsByCycle(userId, cycle, postId, lim);
        return ApiResponse.success(list);
    }

    /** 数据修复：从已完成面试回填错题本（表为空时可用） */
    @PostMapping("/user/{userId}/backfill")
    public ApiResponse<Map<String, Object>> backfill(@PathVariable Long userId) {
        int inserted = wrongQuestionService.backfillFromCompletedInterviews(userId, 30);
        return ApiResponse.success(Map.of("inserted", inserted));
    }

    @GetMapping("/collected/user/{userId}")
    public ApiResponse<List<InterviewWrongQuestion>> getUserCollectedQuestions(@PathVariable Long userId) {
        List<InterviewWrongQuestion> wrongQuestions = wrongQuestionService.getUserCollectedQuestions(userId);
        return ApiResponse.success(wrongQuestions);
    }
}