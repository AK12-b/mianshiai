package com.example.demo.repository;

import com.example.demo.entity.InterviewWrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewWrongQuestionRepository extends JpaRepository<InterviewWrongQuestion, Long> {
    List<InterviewWrongQuestion> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    List<InterviewWrongQuestion> findByUserIdAndIsCollectAndIsDelete(Long userId, Integer isCollect, Integer isDelete);
}