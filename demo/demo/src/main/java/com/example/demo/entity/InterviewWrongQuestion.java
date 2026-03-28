package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_wrong_question")
public class InterviewWrongQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wrongId;

    private Long userId;

    private Long questionId;

    private Long interviewId;

    private Long practiceId;

    private Integer wrongReason;

    private String knowledgePoint;

    private Integer isCollect;

    private LocalDateTime createTime;

    private Integer isDelete;
}