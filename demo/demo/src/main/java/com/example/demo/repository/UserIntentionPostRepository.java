package com.example.demo.repository;

import com.example.demo.entity.UserIntentionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserIntentionPostRepository extends JpaRepository<UserIntentionPost, Long> {
    List<UserIntentionPost> findByUserIdAndIsDelete(Long userId, Integer isDelete);
}