package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_evaluate")
public class InterviewEvaluate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluateId;

    private Long interviewId;

    private Long postId;

    private BigDecimal totalScore;

    private BigDecimal techScore;

    private BigDecimal knowledgeDepthScore;

    private BigDecimal logicScore;

    private BigDecimal expressScore;

    private BigDecimal matchScore;

    private String brightPoint;

    private String problemAnalysis;

    private String gapAnalysis;

    private String suggestGuide;

    private String techAnalysis;

    private String knowledgeDepthAnalysis;

    private String logicAnalysis;

    private String expressAnalysis;

    private String matchAnalysis;

    private LocalDateTime createTime;

    private Integer isDelete;

    public Long getEvaluateId() {
        return evaluateId;
    }

    public void setEvaluateId(Long evaluateId) {
        this.evaluateId = evaluateId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getTechScore() {
        return techScore;
    }

    public void setTechScore(BigDecimal techScore) {
        this.techScore = techScore;
    }

    public BigDecimal getKnowledgeDepthScore() {
        return knowledgeDepthScore;
    }

    public void setKnowledgeDepthScore(BigDecimal knowledgeDepthScore) {
        this.knowledgeDepthScore = knowledgeDepthScore;
    }

    public BigDecimal getLogicScore() {
        return logicScore;
    }

    public void setLogicScore(BigDecimal logicScore) {
        this.logicScore = logicScore;
    }

    public BigDecimal getExpressScore() {
        return expressScore;
    }

    public void setExpressScore(BigDecimal expressScore) {
        this.expressScore = expressScore;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public String getBrightPoint() {
        return brightPoint;
    }

    public void setBrightPoint(String brightPoint) {
        this.brightPoint = brightPoint;
    }

    public String getProblemAnalysis() {
        return problemAnalysis;
    }

    public void setProblemAnalysis(String problemAnalysis) {
        this.problemAnalysis = problemAnalysis;
    }

    public String getGapAnalysis() {
        return gapAnalysis;
    }

    public void setGapAnalysis(String gapAnalysis) {
        this.gapAnalysis = gapAnalysis;
    }

    public String getSuggestGuide() {
        return suggestGuide;
    }

    public void setSuggestGuide(String suggestGuide) {
        this.suggestGuide = suggestGuide;
    }

    public String getTechAnalysis() {
        return techAnalysis;
    }

    public void setTechAnalysis(String techAnalysis) {
        this.techAnalysis = techAnalysis;
    }

    public String getKnowledgeDepthAnalysis() {
        return knowledgeDepthAnalysis;
    }

    public void setKnowledgeDepthAnalysis(String knowledgeDepthAnalysis) {
        this.knowledgeDepthAnalysis = knowledgeDepthAnalysis;
    }

    public String getLogicAnalysis() {
        return logicAnalysis;
    }

    public void setLogicAnalysis(String logicAnalysis) {
        this.logicAnalysis = logicAnalysis;
    }

    public String getExpressAnalysis() {
        return expressAnalysis;
    }

    public void setExpressAnalysis(String expressAnalysis) {
        this.expressAnalysis = expressAnalysis;
    }

    public String getMatchAnalysis() {
        return matchAnalysis;
    }

    public void setMatchAnalysis(String matchAnalysis) {
        this.matchAnalysis = matchAnalysis;
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
