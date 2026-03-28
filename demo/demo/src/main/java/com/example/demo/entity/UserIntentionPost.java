package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_intention_post")
public class UserIntentionPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uipId;

    private Long userId;

    private Long postId;

    private LocalDateTime createTime;

    private Integer isDelete;
}