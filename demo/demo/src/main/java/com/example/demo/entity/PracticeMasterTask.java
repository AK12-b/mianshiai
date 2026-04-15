package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "practice_master_task")
public class PracticeMasterTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long masterId;

    private Long userId;

    private Long postId;

    private Long suggestionId;

    private String knowledgePoint;

    @Column(name = "module_type")
    private Integer moduleType;

    private Integer practiceLevel;

    private Integer taskCycle;

    private Integer taskQuantity;

    private LocalDateTime planStartTime;

    private LocalDateTime planEndTime;

    private Integer masterStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDelete;
}