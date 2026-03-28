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

    private String knowledgeName;

    private String knowledgeContent;

    private String vectorId;

    private LocalDateTime updateTime;

    private Integer isDelete;
}