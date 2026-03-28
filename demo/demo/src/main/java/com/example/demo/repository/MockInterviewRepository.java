package com.example.demo.repository;

import com.example.demo.entity.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    List<MockInterview> findByUserIdAndIsDeleteOrderByStartTimeDesc(Long userId, Integer isDelete);
    List<MockInterview> findByUserIdAndInterviewStatusAndIsDelete(Long userId, Integer status, Integer isDelete);
    List<MockInterview> findByUserIdAndPostIdAndIsDeleteOrderByStartTimeDesc(Long userId, Long postId, Integer isDelete);
    List<MockInterview> findByUserIdAndInterviewModeAndIsDeleteOrderByStartTimeDesc(Long userId, Integer interviewMode, Integer isDelete);
}
