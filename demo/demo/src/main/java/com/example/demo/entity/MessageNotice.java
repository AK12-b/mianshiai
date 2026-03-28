package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message_notice")
public class MessageNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private Long userId;

    private Integer noticeType;

    private String noticeContent;

    private LocalDateTime sendTime;

    private Integer remindCycle;

    private Integer isRead;

    private Integer isClose;

    private LocalDateTime createTime;

    private Integer isDelete;
}