package com.example.demo.dto;

public class AnswerRequest {
    private Long interviewId;
    private Long questionId;
    private String answerText;
    private String voiceUrl;
    private Integer voiceDuration;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
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
}
