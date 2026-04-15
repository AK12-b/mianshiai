package com.example.demo.repository;

import com.example.demo.entity.InterviewWrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewWrongQuestionRepository extends JpaRepository<InterviewWrongQuestion, Long> {
    List<InterviewWrongQuestion> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    List<InterviewWrongQuestion> findByUserIdAndIsCollectAndIsDelete(Long userId, Integer isCollect, Integer isDelete);
    boolean existsByUserIdAndInterviewIdAndQuestionIdAndIsDelete(Long userId, Long interviewId, Long questionId, Integer isDelete);
    boolean existsByUserIdAndQuestionIdAndIsDelete(Long userId, Long questionId, Integer isDelete);

    @Query("""
            select w from InterviewWrongQuestion w
            where w.userId = :userId
              and w.isDelete = 0
              and w.createTime >= :startTime
              and w.createTime < :endTime
            order by w.createTime desc
            """)
    List<InterviewWrongQuestion> findByUserIdAndCreateTimeRange(@Param("userId") Long userId,
                                                               @Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);
}