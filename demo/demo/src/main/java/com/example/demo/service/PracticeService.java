package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeMasterTaskRepository practiceMasterTaskRepository;
    private final PracticeSingleTaskRepository practiceSingleTaskRepository;
    private final PracticeQuestionRecordRepository practiceQuestionRecordRepository;
    private final PracticeSingleReportRepository practiceSingleReportRepository;
    private final QwenService qwenService;

    @Transactional
    public PracticeMasterTask createMasterTask(Long userId, Long postId, String knowledgePoint, 
                                            Integer practiceLevel, Integer taskCycle, Integer taskQuantity,
                                            LocalDateTime planStartTime, LocalDateTime planEndTime) {
        PracticeMasterTask masterTask = new PracticeMasterTask();
        masterTask.setUserId(userId);
        masterTask.setPostId(postId);
        masterTask.setKnowledgePoint(knowledgePoint);
        masterTask.setPracticeLevel(practiceLevel);
        masterTask.setTaskCycle(taskCycle);
        masterTask.setTaskQuantity(taskQuantity);
        masterTask.setPlanStartTime(planStartTime);
        masterTask.setPlanEndTime(planEndTime);
        masterTask.setMasterStatus(0);
        masterTask.setCreateTime(LocalDateTime.now());
        masterTask.setUpdateTime(LocalDateTime.now());
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
        
        String evaluation = qwenService.evaluateAnswer("", userAnswer, "");
        if (evaluation != null && !evaluation.isEmpty()) {
            record.setTechScore(new java.math.BigDecimal("75.00"));
            record.setKnowledgeDepthScore(new java.math.BigDecimal("75.00"));
            record.setLogicScore(new java.math.BigDecimal("75.00"));
            record.setExpressScore(new java.math.BigDecimal("75.00"));
            record.setMatchScore(new java.math.BigDecimal("75.00"));
            record.setErrorType(0);
        } else {
            record.setTechScore(java.math.BigDecimal.ZERO);
            record.setKnowledgeDepthScore(java.math.BigDecimal.ZERO);
            record.setLogicScore(java.math.BigDecimal.ZERO);
            record.setExpressScore(java.math.BigDecimal.ZERO);
            record.setMatchScore(java.math.BigDecimal.ZERO);
            record.setErrorType(1);
        }
        
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
        return practiceMasterTaskRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }

    public List<PracticeMasterTask> getUserMasterTasksByStatus(Long userId, Integer masterStatus) {
        return practiceMasterTaskRepository.findByUserIdAndMasterStatusAndIsDeleteOrderByCreateTimeDesc(userId, masterStatus, 0);
    }

    public List<PracticeMasterTask> getUserMasterTasksByPostId(Long userId, Long postId) {
        return practiceMasterTaskRepository.findByUserIdAndPostIdAndIsDeleteOrderByCreateTimeDesc(userId, postId, 0);
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
}