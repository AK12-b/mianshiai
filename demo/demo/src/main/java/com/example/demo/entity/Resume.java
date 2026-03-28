package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    private Long userId;

    private Long postId;

    private String originalUrl;

    private Long resumeSize;

    private String resumeFormat;

    private String diagnoseResult;

    private String optimizeUrl;

    private Integer isOptimizeApply;

    private LocalDateTime optimizeTime;

    private LocalDateTime createTime;

    private Integer isDelete;
}