package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AnswerRequest;
import com.example.demo.dto.InterviewConfigRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.InterviewInterviewerPromptBuilder;
import com.example.demo.service.InterviewService;
import com.example.demo.service.OmniRealtimeTtsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;
    private final PostRepository postRepository;
    private final OmniRealtimeTtsService omniRealtimeTtsService;

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
        if (interview == null) {
            return ApiResponse.error("仅「进行中」或「已暂停」的面试可结束并生成报告");
        }
        interviewService.evaluateInterview(interviewId);
        return ApiResponse.success(interview);
    }

    @PostMapping("/{interviewId}/pause")
    public ApiResponse<MockInterview> pauseInterview(@PathVariable Long interviewId) {
        MockInterview interview = interviewService.pauseInterview(interviewId);
        if (interview != null) {
            return ApiResponse.success(interview);
        }
        return ApiResponse.error("仅「进行中」的面试可暂停");
    }

    @PostMapping("/{interviewId}/resume")
    public ApiResponse<MockInterview> resumeInterview(@PathVariable Long interviewId) {
        MockInterview interview = interviewService.resumeInterview(interviewId);
        if (interview != null) {
            return ApiResponse.success(interview);
        }
        return ApiResponse.error("仅「已暂停」的面试可继续");
    }

    @PostMapping("/{interviewId}/terminate")
    public ApiResponse<MockInterview> terminateInterview(@PathVariable Long interviewId) {
        MockInterview interview = interviewService.terminateInterview(interviewId);
        if (interview != null) {
            return ApiResponse.success(interview);
        }
        return ApiResponse.error("仅「进行中」或「已暂停」的面试可终止");
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

    /**
     * 语音面试：使用 CosyVoice（默认 cosyvoice-v3-flash，SpeechSynthesizer）将面试官台词合成为 WAV，仅服务端持有 API Key。
     */
    @PostMapping("/{interviewId}/realtime-tts")
    public ResponseEntity<byte[]> realtimeTts(@PathVariable Long interviewId, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String text = body.get("text") != null ? body.get("text").toString().trim() : "";
        if (text.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "text 为空");
        }
        MockInterview interview =
                interviewService
                        .getInterviewById(interviewId)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "面试不存在"));
        if (!userId.equals(interview.getUserId())) {
            throw new ResponseStatusException(FORBIDDEN, "无权访问该面试");
        }
        Post post = postRepository.findById(interview.getPostId()).orElseGet(Post::new);
        String instructions = InterviewInterviewerPromptBuilder.buildInstructions(interview, post);
        try {
            byte[] wav =
                    omniRealtimeTtsService.synthesizeInterviewerLineWav(
                            instructions, text, interview.getAiCharacter(), interview.getAiGender());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .body(wav);
        } catch (Exception e) {
            log.warn("Realtime TTS 失败 interviewId={}", interviewId, e);
            throw new ResponseStatusException(BAD_GATEWAY, e.getMessage() != null ? e.getMessage() : "TTS 失败");
        }
    }

    @GetMapping("/{interviewId}/evaluate")
    public ApiResponse<InterviewEvaluate> getInterviewEvaluate(@PathVariable Long interviewId) {
        Optional<InterviewEvaluate> evaluate = interviewService.getInterviewEvaluate(interviewId);
        return evaluate.map(ApiResponse::success).orElseGet(() -> ApiResponse.error("评估报告不存在"));
    }

    @GetMapping("/{interviewId}/report-detail")
    public ApiResponse<List<Map<String, Object>>> getReportDetail(@PathVariable Long interviewId) {
        List<Map<String, Object>> detail = interviewService.getReportQaDetail(interviewId);
        return ApiResponse.success(detail);
    }
}