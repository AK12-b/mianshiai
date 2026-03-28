package com.example.demo.service;

import com.example.demo.entity.MessageNotice;
import com.example.demo.repository.MessageNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageNoticeService {

    private final MessageNoticeRepository messageNoticeRepository;

    @Transactional
    public MessageNotice createNotice(Long userId, Integer noticeType, String noticeContent, 
                                     LocalDateTime sendTime, Integer remindCycle) {
        MessageNotice notice = new MessageNotice();
        notice.setUserId(userId);
        notice.setNoticeType(noticeType);
        notice.setNoticeContent(noticeContent);
        notice.setSendTime(sendTime);
        notice.setRemindCycle(remindCycle);
        notice.setIsRead(0);
        notice.setIsClose(0);
        notice.setCreateTime(LocalDateTime.now());
        notice.setIsDelete(0);

        return messageNoticeRepository.save(notice);
    }

    @Transactional
    public MessageNotice markAsRead(Long noticeId) {
        MessageNotice notice = messageNoticeRepository.findById(noticeId).orElse(null);
        if (notice != null) {
            notice.setIsRead(1);
            return messageNoticeRepository.save(notice);
        }
        return null;
    }

    @Transactional
    public MessageNotice closeNotice(Long noticeId) {
        MessageNotice notice = messageNoticeRepository.findById(noticeId).orElse(null);
        if (notice != null) {
            notice.setIsClose(1);
            return messageNoticeRepository.save(notice);
        }
        return null;
    }

    public List<MessageNotice> getUserNotices(Long userId) {
        return messageNoticeRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }

    public List<MessageNotice> getUnreadNotices(Long userId) {
        return messageNoticeRepository.findByUserIdAndIsReadAndIsDelete(userId, 0, 0);
    }
}