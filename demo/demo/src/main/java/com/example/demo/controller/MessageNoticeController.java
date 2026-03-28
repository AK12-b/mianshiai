package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.MessageNotice;
import com.example.demo.service.MessageNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message-notice")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageNoticeController {

    private final MessageNoticeService messageNoticeService;

    @PostMapping("/create")
    public ApiResponse<MessageNotice> createNotice(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Integer noticeType = Integer.valueOf(request.get("noticeType").toString());
        String noticeContent = (String) request.get("noticeContent");
        LocalDateTime sendTime = LocalDateTime.parse(request.get("sendTime").toString());
        Integer remindCycle = Integer.valueOf(request.get("remindCycle").toString());

        MessageNotice notice = messageNoticeService.createNotice(userId, noticeType, noticeContent, 
            sendTime, remindCycle);
        return ApiResponse.success(notice);
    }

    @PostMapping("/{noticeId}/mark-read")
    public ApiResponse<MessageNotice> markAsRead(@PathVariable Long noticeId) {
        MessageNotice notice = messageNoticeService.markAsRead(noticeId);
        if (notice != null) {
            return ApiResponse.success(notice);
        }
        return ApiResponse.error("操作失败");
    }

    @PostMapping("/{noticeId}/close")
    public ApiResponse<MessageNotice> closeNotice(@PathVariable Long noticeId) {
        MessageNotice notice = messageNoticeService.closeNotice(noticeId);
        if (notice != null) {
            return ApiResponse.success(notice);
        }
        return ApiResponse.error("操作失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<MessageNotice>> getUserNotices(@PathVariable Long userId) {
        List<MessageNotice> notices = messageNoticeService.getUserNotices(userId);
        return ApiResponse.success(notices);
    }

    @GetMapping("/unread/user/{userId}")
    public ApiResponse<List<MessageNotice>> getUnreadNotices(@PathVariable Long userId) {
        List<MessageNotice> notices = messageNoticeService.getUnreadNotices(userId);
        return ApiResponse.success(notices);
    }
}