package com.example.demo.repository;

import com.example.demo.entity.PostEvaluateStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostEvaluateStandardRepository extends JpaRepository<PostEvaluateStandard, Long> {
    Optional<PostEvaluateStandard> findByPostIdAndIsDelete(Long postId, Integer isDelete);
}