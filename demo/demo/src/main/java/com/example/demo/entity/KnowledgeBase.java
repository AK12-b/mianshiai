package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "knowledge_base")
public class KnowledgeBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long knowledgeId;

    private Long postId;

    /** 与库表 category_id 对齐；改库后多为 NOT NULL，面试自动沉淀题目时写入默认分类 */
    @Column(name = "category_id")
    private Long categoryId;

    private String knowledgeName;

    private String knowledgeContent;

    private String vectorId;

    private LocalDateTime updateTime;

    /** 部分库表新增 create_time NOT NULL，与 update_time 一并写入 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    private Integer isDelete;
}