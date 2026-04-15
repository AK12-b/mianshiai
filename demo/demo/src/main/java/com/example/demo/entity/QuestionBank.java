package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "question_bank")
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "post_id")
    private Long postId;

    /** 关联 knowledge_category.category_id（库里为 NOT NULL 且有外键约束） */
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "question_type")
    private Integer questionType;

    @Column(name = "question_level")
    private Integer questionLevel;

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "question_answer")
    private String questionAnswer;

    /** 与库列 {@code knowledge_point} 一致；页面「知识点」统计读此字段 */
    @Column(name = "knowledge_point")
    private String knowledgePoint;

    /** 映射后的规范知识点（与前端学习资源卡片一致）；不入库，仅接口序列化 */
    @Transient
    @JsonProperty("uiKnowledgePoint")
    private String uiKnowledgePoint;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "is_ai_generate")
    private Integer isAiGenerate;
}