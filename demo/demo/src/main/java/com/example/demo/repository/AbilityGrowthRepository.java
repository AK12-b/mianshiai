package com.example.demo.repository;

import com.example.demo.entity.AbilityGrowth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbilityGrowthRepository extends JpaRepository<AbilityGrowth, Long> {
    List<AbilityGrowth> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
}