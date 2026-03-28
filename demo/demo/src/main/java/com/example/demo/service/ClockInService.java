package com.example.demo.service;

import com.example.demo.entity.ClockInRecord;
import com.example.demo.repository.ClockInRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClockInService {

    private final ClockInRecordRepository clockInRecordRepository;

    @Transactional
    public ClockInRecord clockIn(Long userId, Long taskId, Integer taskFinishStatus, Integer trainDuration) {
        ClockInRecord record = new ClockInRecord();
        record.setUserId(userId);
        record.setTaskId(taskId);
        record.setClockInTime(LocalDateTime.now());
        record.setTaskFinishStatus(taskFinishStatus);
        record.setTrainDuration(trainDuration);
        record.setCreateTime(LocalDateTime.now());
        record.setIsDelete(0);

        return clockInRecordRepository.save(record);
    }

    public List<ClockInRecord> getUserClockInRecords(Long userId) {
        return clockInRecordRepository.findByUserIdAndIsDeleteOrderByClockInTimeDesc(userId, 0);
    }
}