package com.example.demo.repository;

import com.example.demo.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {
    Optional<KnowledgeCategory> findFirstByCategoryName(String categoryName);
}

