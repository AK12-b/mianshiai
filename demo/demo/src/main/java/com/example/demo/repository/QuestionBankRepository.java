package com.example.demo.repository;

import com.example.demo.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByPostIdAndIsDelete(Long postId, Integer isDelete);
    List<QuestionBank> findByPostIdAndQuestionTypeAndIsDelete(Long postId, Integer questionType, Integer isDelete);
    List<QuestionBank> findByIsDelete(Integer isDelete);
}