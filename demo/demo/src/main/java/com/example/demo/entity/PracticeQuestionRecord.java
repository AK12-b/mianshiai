package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "practice_question_record")
public class PracticeQuestionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    private Long singleId;

    private Long userId;

    private Long questionId;

    private String userAnswer;

    private Integer inputType;

    private Integer answerDuration;

    private BigDecimal techScore;

    private BigDecimal knowledgeDepthScore;

    private BigDecimal logicScore;

    private BigDecimal expressScore;

    private BigDecimal matchScore;

    private Integer errorType;

    private LocalDateTime createTime;

    private Integer isDelete;
}