package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_login_log")
public class UserLoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private Long userId;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    private Integer isDelete;
}