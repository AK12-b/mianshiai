package com.example.demo.repository;

import com.example.demo.entity.PersonalSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalSuggestionRepository extends JpaRepository<PersonalSuggestion, Long> {
    List<PersonalSuggestion> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    boolean existsByUserIdAndPostIdAndIsDelete(Long userId, Long postId, Integer isDelete);
}