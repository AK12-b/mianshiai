package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interview")
public class MockInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewId;

    private Long userId;

    private Long postId;

    private Integer interviewMode;

    private String interviewModule;

    private Integer aiCharacter;

    private Integer aiGender;

    private Integer interviewDuration;

    private Integer singleQuestionTimeLimit;

    private Integer remindBeforeTimeout;

    private Integer inputType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer interviewStatus;

    private Integer isTimeout;

    private Integer isDelete;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(Integer interviewMode) {
        this.interviewMode = interviewMode;
    }

    public String getInterviewModule() {
        return interviewModule;
    }

    public void setInterviewModule(String interviewModule) {
        this.interviewModule = interviewModule;
    }

    public Integer getAiCharacter() {
        return aiCharacter;
    }

    public void setAiCharacter(Integer aiCharacter) {
        this.aiCharacter = aiCharacter;
    }

    public Integer getAiGender() {
        return aiGender;
    }

    public void setAiGender(Integer aiGender) {
        this.aiGender = aiGender;
    }

    public Integer getInterviewDuration() {
        return interviewDuration;
    }

    public void setInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
    }

    public Integer getSingleQuestionTimeLimit() {
        return singleQuestionTimeLimit;
    }

    public void setSingleQuestionTimeLimit(Integer singleQuestionTimeLimit) {
        this.singleQuestionTimeLimit = singleQuestionTimeLimit;
    }

    public Integer getRemindBeforeTimeout() {
        return remindBeforeTimeout;
    }

    public void setRemindBeforeTimeout(Integer remindBeforeTimeout) {
        this.remindBeforeTimeout = remindBeforeTimeout;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(Integer interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public Integer getIsTimeout() {
        return isTimeout;
    }

    public void setIsTimeout(Integer isTimeout) {
        this.isTimeout = isTimeout;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
