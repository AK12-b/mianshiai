package com.example.demo.repository;

import com.example.demo.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    List<KnowledgeBase> findByPostIdAndIsDelete(Long postId, Integer isDelete);
}