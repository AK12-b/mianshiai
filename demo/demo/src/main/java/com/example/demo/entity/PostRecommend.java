package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_recommend")
public class PostRecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    private Long userId;

    private Long originalPostId;

    private Long recommendPostId;

    private BigDecimal matchRate;

    private BigDecimal skillMatchRate;

    private BigDecimal projectMatchRate;

    private BigDecimal interviewMatchRate;

    private String matchHighlight;

    private String abilityGap;

    private String industryProspect;

    private LocalDateTime createTime;

    private Integer isDelete;
}