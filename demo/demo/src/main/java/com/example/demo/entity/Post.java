package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String postName;

    private String postStack;

    private String postFocus;

    private LocalDateTime createTime;

    private Integer isDelete;

    private String postIntro;
}