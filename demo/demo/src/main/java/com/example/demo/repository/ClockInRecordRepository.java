package com.example.demo.repository;

import com.example.demo.entity.ClockInRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClockInRecordRepository extends JpaRepository<ClockInRecord, Long> {
    List<ClockInRecord> findByUserIdAndIsDeleteOrderByClockInTimeDesc(Long userId, Integer isDelete);
}