package com.example.demo.repository;

import com.example.demo.entity.MessageNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageNoticeRepository extends JpaRepository<MessageNotice, Long> {
    List<MessageNotice> findByUserIdAndIsDeleteOrderByCreateTimeDesc(Long userId, Integer isDelete);
    List<MessageNotice> findByUserIdAndIsReadAndIsDelete(Long userId, Integer isRead, Integer isDelete);
}