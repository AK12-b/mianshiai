package com.example.demo.repository;

import com.example.demo.entity.PracticeSingleTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeSingleTaskRepository extends JpaRepository<PracticeSingleTask, Long> {
    List<PracticeSingleTask> findByMasterIdAndIsDeleteOrderByCreateTimeDesc(Long masterId, Integer isDelete);
    List<PracticeSingleTask> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    List<PracticeSingleTask> findByUserIdAndPostIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Long postId, Integer isDelete);
}