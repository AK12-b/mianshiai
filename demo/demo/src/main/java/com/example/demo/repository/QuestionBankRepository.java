package com.example.demo.repository;

import com.example.demo.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByPostIdAndIsDelete(Long postId, Integer isDelete);
    List<QuestionBank> findByPostIdAndQuestionTypeAndIsDelete(Long postId, Integer questionType, Integer isDelete);
    List<QuestionBank> findByIsDelete(Integer isDelete);
    List<QuestionBank> findByKnowledgePointAndIsDelete(String knowledgePoint, Integer isDelete);

    Optional<QuestionBank> findFirstByPostIdAndQuestionTitleAndIsDelete(Long postId, String questionTitle, Integer isDelete);

    @Query("select distinct q.categoryId from QuestionBank q " +
            "where q.isDelete = 0 and q.postId = :postId and q.knowledgePoint = :knowledgePoint and q.categoryId is not null")
    List<Long> findDistinctCategoryIdsByPostAndKnowledgePoint(@Param("postId") Long postId,
                                                               @Param("knowledgePoint") String knowledgePoint);

    @Query("select distinct q.categoryId from QuestionBank q " +
            "where q.isDelete = 0 and q.postId = :postId and q.questionType = :questionType and q.categoryId is not null")
    List<Long> findDistinctCategoryIdsByPostAndQuestionType(@Param("postId") Long postId,
                                                             @Param("questionType") Integer questionType);
}