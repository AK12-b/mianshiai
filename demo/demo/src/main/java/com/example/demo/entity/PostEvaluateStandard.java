package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_evaluate_standard")
public class PostEvaluateStandard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long standardId;

    private Long postId;

    private Integer techWeight;

    private Integer knowledgeDepthWeight;

    private Integer logicWeight;

    private Integer expressWeight;

    private Integer matchWeight;

    private String scoreRule;

    private Integer isDelete;
}