package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private Integer gender;

    private String education;

    private String grade;

    private String projectExp;

    private String skillTag;

    private String skillProficiency;

    private String resumeUrl;

    private Integer themeMode;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDelete;

    private String userName;

    private String competitionAward;

    private String internExp;
}