package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "practice_single_task")
public class PracticeSingleTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long singleId;

    private Long masterId;

    private Long userId;

    private Long postId;

    private Integer singleStatus;

    private Integer totalDuration;

    private BigDecimal totalScore;

    private Integer skipQuantity;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private LocalDateTime createTime;

    private Integer isDelete;
}