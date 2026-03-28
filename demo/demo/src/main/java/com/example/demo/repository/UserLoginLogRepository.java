package com.example.demo.repository;

import com.example.demo.entity.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
    List<UserLoginLog> findByUserIdAndIsDeleteOrderByLoginTimeDesc(Long userId, Integer isDelete);
}