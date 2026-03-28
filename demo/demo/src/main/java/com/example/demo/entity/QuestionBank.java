package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "question_bank")
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private Long postId;

    private Integer questionType;

    private Integer questionLevel;

    private String questionTitle;

    private String questionAnswer;

    private String knowledgePoint;

    private LocalDateTime createTime;

    private Integer isDelete;

    private Integer isAiGenerate;
}