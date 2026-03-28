package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ability_growth")
public class AbilityGrowth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long growthId;

    private Long userId;

    private Long postId;

    private Integer statisticsCycle;

    private String cycleTime;

    private Integer interviewCount;

    private BigDecimal avgScore;

    private Integer practiceQuestionCount;

    private Integer totalTrainDuration;

    private Integer wrongQuestionCount;

    private String weakPoint;

    private BigDecimal weakPointImproveRate;

    private String abilityImprove;

    private String growthSuggest;

    private LocalDateTime createTime;

    private Integer isDelete;
}