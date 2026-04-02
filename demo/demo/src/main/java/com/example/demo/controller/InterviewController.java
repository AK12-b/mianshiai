package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AnswerRequest;
import com.example.demo.dto.InterviewConfigRequest;
import com.example.demo.entity.*;
import com.example.demo.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/create")
    public ApiResponse<MockInterview> createInterview(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        InterviewConfigRequest config = new InterviewConfigRequest();
        config.setPostId(Long.valueOf(request.get("postId").toString()));
        config.setInterviewMode(Integer.valueOf(request.get("interviewMode").toString()));
        config.setInterviewModule((String) request.get("interviewModule"));
        config.setAiCharacter(Integer.valueOf(request.get("aiCharacter").toString()));
        config.setAiGender(Integer.valueOf(request.get("aiGender").toString()));
        config.setInterviewDuration(Integer.valueOf(request.get("interviewDuration").toString()));
        config.setInputType(Integer.valueOf(request.get("inputType").toString()));

        MockInterview interview = interviewService.createInterview(userId, config);
        return ApiResponse.success(interview);
    }

    @PostMapping("/{interviewId}/start")
    public ApiResponse<InterviewDialog> startInterview(@PathVariable Long interviewId) {
        InterviewDialog dialog = interviewService.generateFirstQuestion(interviewId);
        if (dialog != null) {
            return ApiResponse.success(dialog);
        }
        return ApiResponse.error("生成题目失败");
    }

    @PostMapping("/submit-answer")
    public ApiResponse<InterviewDialog> submitAnswer(@RequestBody AnswerRequest request) {
        InterviewDialog dialog = interviewService.submitAnswer(request);
        return ApiResponse.success(dialog);
    }

    @PostMapping("/{interviewId}/follow-up")
    public ApiResponse<InterviewDialog> generateFollowUp(@PathVariable Long interviewId, @RequestBody Map<String, String> request) {
        String userAnswer = request.get("userAnswer");
        InterviewDialog dialog = interviewService.generateFollowUpQuestion(interviewId, userAnswer);
        if (dialog != null) {
            return ApiResponse.success(dialog);
        }
        return ApiResponse.error("生成追问失败");
    }

    @GetMapping("/{interviewId}")
    public ApiResponse<MockInterview> getInterview(@PathVariable Long interviewId) {
        Optional<MockInterview> interview = interviewService.getInterviewById(interviewId);
        return interview.map(ApiResponse::success).orElseGet(() -> ApiResponse.error("面试不存在"));
    }

    @PostMapping("/{interviewId}/end")
    public ApiResponse<MockInterview> endInterview(
            @PathVariable Long interviewId,
            @RequestBody(required = false) Map<String, Object> body) {
        boolean timeout = body != null && Boolean.TRUE.equals(body.get("timeout"));
        MockInterview interview = interviewService.endInterview(interviewId, timeout);
        interviewService.evaluateInterview(interviewId);
        return ApiResponse.success(interview);
    }

    @PostMapping("/{interviewId}/pause")
    public ApiResponse<MockInterview> pauseInterview(@PathVariable Long interviewId) {
        MockInterview interview = interviewService.pauseInterview(interviewId);
        if (interview != null) {
            return ApiResponse.success(interview);
        }
        return ApiResponse.error("暂停失败");
    }

    @PostMapping("/{interviewId}/terminate")
    public ApiResponse<MockInterview> terminateInterview(@PathVariable Long interviewId) {
        MockInterview interview = interviewService.terminateInterview(interviewId);
        if (interview != null) {
            return ApiResponse.success(interview);
        }
        return ApiResponse.error("终止失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<MockInterview>> getUserInterviews(@PathVariable Long userId) {
        List<MockInterview> interviews = interviewService.getUserInterviews(userId);
        return ApiResponse.success(interviews);
    }

    @GetMapping("/user/{userId}/post/{postId}")
    public ApiResponse<List<MockInterview>> getUserInterviewsByPostId(@PathVariable Long userId, @PathVariable Long postId) {
        List<MockInterview> interviews = interviewService.getUserInterviewsByPostId(userId, postId);
        return ApiResponse.success(interviews);
    }

    @GetMapping("/user/{userId}/mode/{interviewMode}")
    public ApiResponse<List<MockInterview>> getUserInterviewsByMode(@PathVariable Long userId, @PathVariable Integer interviewMode) {
        List<MockInterview> interviews = interviewService.getUserInterviewsByMode(userId, interviewMode);
        return ApiResponse.success(interviews);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ApiResponse<List<MockInterview>> getUserInterviewsByStatus(@PathVariable Long userId, @PathVariable Integer status) {
        List<MockInterview> interviews = interviewService.getUserInterviewsByStatus(userId, status);
        return ApiResponse.success(interviews);
    }

    @GetMapping("/{interviewId}/dialogs")
    public ApiResponse<List<InterviewDialog>> getInterviewDialogs(@PathVariable Long interviewId) {
        List<InterviewDialog> dialogs = interviewService.getInterviewDialogs(interviewId);
        return ApiResponse.success(dialogs);
    }

    @GetMapping("/{interviewId}/evaluate")
    public ApiResponse<InterviewEvaluate> getInterviewEvaluate(@PathVariable Long interviewId) {
        Optional<InterviewEvaluate> evaluate = interviewService.getInterviewEvaluate(interviewId);
        return evaluate.map(ApiResponse::success).orElseGet(() -> ApiResponse.error("评估报告不存在"));
    }
}