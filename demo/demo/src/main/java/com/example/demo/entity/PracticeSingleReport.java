package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "practice_single_report")
public class PracticeSingleReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long singleId;

    private Long userId;

    private Long masterId;

    private String scoreAnalysis;

    private String improveSuggest;

    private LocalDateTime createTime;

    private Integer isDelete;
}