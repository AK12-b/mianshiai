package com.example.demo.repository;

import com.example.demo.entity.PostRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRecommendRepository extends JpaRepository<PostRecommend, Long> {
    List<PostRecommend> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
}