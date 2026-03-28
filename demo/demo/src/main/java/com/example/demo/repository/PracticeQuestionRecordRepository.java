package com.example.demo.repository;

import com.example.demo.entity.PracticeQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeQuestionRecordRepository extends JpaRepository<PracticeQuestionRecord, Long> {
    List<PracticeQuestionRecord> findBySingleIdAndIsDelete(Long singleId, Integer isDelete);
}