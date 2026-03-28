package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.UserLoginLog;
import com.example.demo.service.UserLoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-login-log")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserLoginLogController {

    private final UserLoginLogService userLoginLogService;

    @PostMapping("/create")
    public ApiResponse<UserLoginLog> createLoginLog(@RequestParam Long userId) {
        UserLoginLog loginLog = userLoginLogService.createLoginLog(userId);
        return ApiResponse.success(loginLog);
    }

    @PostMapping("/{logId}/logout")
    public ApiResponse<UserLoginLog> updateLogoutTime(@PathVariable Long logId) {
        UserLoginLog loginLog = userLoginLogService.updateLogoutTime(logId);
        if (loginLog != null) {
            return ApiResponse.success(loginLog);
        }
        return ApiResponse.error("更新失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserLoginLog>> getUserLoginLogs(@PathVariable Long userId) {
        List<UserLoginLog> loginLogs = userLoginLogService.getUserLoginLogs(userId);
        return ApiResponse.success(loginLogs);
    }

    @GetMapping("/user/{userId}/latest")
    public ApiResponse<UserLoginLog> getLatestLoginLog(@PathVariable Long userId) {
        UserLoginLog loginLog = userLoginLogService.getLatestLoginLog(userId);
        if (loginLog != null) {
            return ApiResponse.success(loginLog);
        }
        return ApiResponse.error("未找到登录记录");
    }
}
