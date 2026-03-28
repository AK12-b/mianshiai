package com.example.demo.repository;

import com.example.demo.entity.InterviewEvaluate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewEvaluateRepository extends JpaRepository<InterviewEvaluate, Long> {
    Optional<InterviewEvaluate> findByInterviewIdAndIsDelete(Long interviewId, Integer isDelete);
}