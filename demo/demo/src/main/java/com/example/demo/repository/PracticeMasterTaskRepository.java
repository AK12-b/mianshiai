package com.example.demo.repository;

import com.example.demo.entity.PracticeMasterTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeMasterTaskRepository extends JpaRepository<PracticeMasterTask, Long> {
    List<PracticeMasterTask> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    List<PracticeMasterTask> findByUserIdAndMasterStatusAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer masterStatus, Integer isDelete);
    List<PracticeMasterTask> findByUserIdAndPostIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Long postId, Integer isDelete);
}