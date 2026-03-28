package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.*;
import com.example.demo.service.PracticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping("/master-task/create")
    public ApiResponse<PracticeMasterTask> createMasterTask(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long postId = Long.valueOf(request.get("postId").toString());
        String knowledgePoint = (String) request.get("knowledgePoint");
        Integer practiceLevel = Integer.valueOf(request.get("practiceLevel").toString());
        Integer taskCycle = Integer.valueOf(request.get("taskCycle").toString());
        Integer taskQuantity = Integer.valueOf(request.get("taskQuantity").toString());
        LocalDateTime planStartTime = LocalDateTime.parse(request.get("planStartTime").toString());
        LocalDateTime planEndTime = LocalDateTime.parse(request.get("planEndTime").toString());

        PracticeMasterTask masterTask = practiceService.createMasterTask(userId, postId, knowledgePoint, 
            practiceLevel, taskCycle, taskQuantity, planStartTime, planEndTime);
        return ApiResponse.success(masterTask);
    }

    @PostMapping("/single-task/create")
    public ApiResponse<PracticeSingleTask> createSingleTask(@RequestBody Map<String, Object> request) {
        Long masterId = Long.valueOf(request.get("masterId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Long postId = Long.valueOf(request.get("postId").toString());

        PracticeSingleTask singleTask = practiceService.createSingleTask(masterId, userId, postId);
        return ApiResponse.success(singleTask);
    }

    @PostMapping("/single-task/{singleId}/start")
    public ApiResponse<PracticeSingleTask> startSingleTask(@PathVariable Long singleId) {
        PracticeSingleTask singleTask = practiceService.startSingleTask(singleId);
        if (singleTask != null) {
            return ApiResponse.success(singleTask);
        }
        return ApiResponse.error("启动失败");
    }

    @PostMapping("/submit-answer")
    public ApiResponse<PracticeQuestionRecord> submitAnswer(@RequestBody Map<String, Object> request) {
        Long singleId = Long.valueOf(request.get("singleId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Long questionId = Long.valueOf(request.get("questionId").toString());
        String userAnswer = (String) request.get("userAnswer");
        Integer inputType = Integer.valueOf(request.get("inputType").toString());
        Integer answerDuration = Integer.valueOf(request.get("answerDuration").toString());

        PracticeQuestionRecord record = practiceService.submitAnswer(singleId, userId, questionId, 
            userAnswer, inputType, answerDuration);
        return ApiResponse.success(record);
    }

    @PostMapping("/single-task/finish")
    public ApiResponse<PracticeSingleTask> finishSingleTask(@RequestBody Map<String, Object> request) {
        Long singleId = Long.valueOf(request.get("singleId").toString());
        Integer totalDuration = Integer.valueOf(request.get("totalDuration").toString());
        BigDecimal totalScore = new BigDecimal(request.get("totalScore").toString());
        Integer skipQuantity = Integer.valueOf(request.get("skipQuantity").toString());
        LocalDateTime finishTime = LocalDateTime.parse(request.get("finishTime").toString());

        PracticeSingleTask singleTask = practiceService.finishSingleTask(singleId, totalDuration, 
            totalScore, skipQuantity, finishTime);
        return ApiResponse.success(singleTask);
    }

    @PostMapping("/report/generate")
    public ApiResponse<PracticeSingleReport> generateReport(@RequestBody Map<String, Object> request) {
        Long singleId = Long.valueOf(request.get("singleId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Long masterId = Long.valueOf(request.get("masterId").toString());
        String scoreAnalysis = (String) request.get("scoreAnalysis");
        String improveSuggest = (String) request.get("improveSuggest");

        PracticeSingleReport report = practiceService.generateReport(singleId, userId, masterId, 
            scoreAnalysis, improveSuggest);
        return ApiResponse.success(report);
    }

    @GetMapping("/master-task/user/{userId}")
    public ApiResponse<List<PracticeMasterTask>> getUserMasterTasks(@PathVariable Long userId) {
        List<PracticeMasterTask> tasks = practiceService.getUserMasterTasks(userId);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/master-task/user/{userId}/status/{masterStatus}")
    public ApiResponse<List<PracticeMasterTask>> getUserMasterTasksByStatus(@PathVariable Long userId, @PathVariable Integer masterStatus) {
        List<PracticeMasterTask> tasks = practiceService.getUserMasterTasksByStatus(userId, masterStatus);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/master-task/user/{userId}/post/{postId}")
    public ApiResponse<List<PracticeMasterTask>> getUserMasterTasksByPostId(@PathVariable Long userId, @PathVariable Long postId) {
        List<PracticeMasterTask> tasks = practiceService.getUserMasterTasksByPostId(userId, postId);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/single-task/master/{masterId}")
    public ApiResponse<List<PracticeSingleTask>> getSingleTasksByMasterId(@PathVariable Long masterId) {
        List<PracticeSingleTask> tasks = practiceService.getSingleTasksByMasterId(masterId);
        return ApiResponse.success(tasks);
    }

    @GetMapping("/question-records/single/{singleId}")
    public ApiResponse<List<PracticeQuestionRecord>> getQuestionRecordsBySingleId(@PathVariable Long singleId) {
        List<PracticeQuestionRecord> records = practiceService.getQuestionRecordsBySingleId(singleId);
        return ApiResponse.success(records);
    }

    @GetMapping("/report/single/{singleId}")
    public ApiResponse<PracticeSingleReport> getReportBySingleId(@PathVariable Long singleId) {
        return practiceService.getReportBySingleId(singleId)
            .map(ApiResponse::success)
            .orElseGet(() -> ApiResponse.error("报告不存在"));
    }
}