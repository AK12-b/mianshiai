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
        Integer wrongReason = Integer.valueOf(request.get("wrongReason").toString());
        String knowledgePoint = (String) request.get("knowledgePoint");

        InterviewWrongQuestion wrongQuestion = wrongQuestionService.addWrongQuestion(userId, questionId, 
            interviewId, practiceId, wrongReason, knowledgePoint);
        return ApiResponse.success(wrongQuestion);
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

    @GetMapping("/collected/user/{userId}")
    public ApiResponse<List<InterviewWrongQuestion>> getUserCollectedQuestions(@PathVariable Long userId) {
        List<InterviewWrongQuestion> wrongQuestions = wrongQuestionService.getUserCollectedQuestions(userId);
        return ApiResponse.success(wrongQuestions);
    }
}