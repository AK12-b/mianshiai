package com.example.demo.dto;

import lombok.Data;

@Data
public class InterviewConfigRequest {
    private Long postId;
    private Integer interviewMode;
    private String interviewModule;
    private Integer aiCharacter;
    private Integer aiGender;
    private Integer interviewDuration;
    private Integer inputType;
}