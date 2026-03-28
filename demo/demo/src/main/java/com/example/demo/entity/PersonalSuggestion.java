package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "personal_suggestion")
public class PersonalSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suggestionId;

    private Long userId;

    private Long postId;

    private String techSuggest;

    private String knowledgeDepthSuggest;

    private String logicSuggest;

    private String expressSuggest;

    private String postSuggest;

    private LocalDateTime createTime;

    private Integer isDelete;
}