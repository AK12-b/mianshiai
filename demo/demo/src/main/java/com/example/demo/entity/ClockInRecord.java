package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "clock_in_record")
public class ClockInRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clockInId;

    private Long userId;

    private Long taskId;

    private LocalDateTime clockInTime;

    private Integer taskFinishStatus;

    private Integer trainDuration;

    private LocalDateTime createTime;

    private Integer isDelete;
}