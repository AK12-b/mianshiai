package com.example.demo.repository;

import com.example.demo.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    List<KnowledgeBase> findByPostIdAndIsDelete(Long postId, Integer isDelete);
    List<KnowledgeBase> findByIsDelete(Integer isDelete);

    Optional<KnowledgeBase> findFirstByPostIdAndKnowledgeContentAndIsDelete(Long postId, String knowledgeContent, Integer isDelete);
}