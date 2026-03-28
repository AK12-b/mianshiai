package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_dialog")
public class InterviewDialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dialogId;

    private Long interviewId;

    private Integer speaker;

    private String contentText;

    private Long questionId;

    private Integer dialogueRound;

    private String voiceUrl;

    private Integer voiceDuration;

    private Integer singleQuestionTimeout;

    private LocalDateTime createTime;

    private Integer isDelete;

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Integer speaker) {
        this.speaker = speaker;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getDialogueRound() {
        return dialogueRound;
    }

    public void setDialogueRound(Integer dialogueRound) {
        this.dialogueRound = dialogueRound;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public Integer getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(Integer voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public Integer getSingleQuestionTimeout() {
        return singleQuestionTimeout;
    }

    public void setSingleQuestionTimeout(Integer singleQuestionTimeout) {
        this.singleQuestionTimeout = singleQuestionTimeout;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
