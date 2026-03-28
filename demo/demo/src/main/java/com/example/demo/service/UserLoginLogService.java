package com.example.demo.service;

import com.example.demo.entity.UserLoginLog;
import com.example.demo.repository.UserLoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLoginLogService {

    private final UserLoginLogRepository userLoginLogRepository;

    @Transactional
    public UserLoginLog createLoginLog(Long userId) {
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setUserId(userId);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setIsDelete(0);

        return userLoginLogRepository.save(loginLog);
    }

    @Transactional
    public UserLoginLog updateLogoutTime(Long logId) {
        UserLoginLog loginLog = userLoginLogRepository.findById(logId).orElse(null);
        if (loginLog != null) {
            loginLog.setLogoutTime(LocalDateTime.now());
            return userLoginLogRepository.save(loginLog);
        }
        return null;
    }

    public List<UserLoginLog> getUserLoginLogs(Long userId) {
        return userLoginLogRepository.findByUserIdAndIsDeleteOrderByLoginTimeDesc(userId, 0);
    }

    public UserLoginLog getLatestLoginLog(Long userId) {
        List<UserLoginLog> logs = userLoginLogRepository.findByUserIdAndIsDeleteOrderByLoginTimeDesc(userId, 0);
        return logs.isEmpty() ? null : logs.get(0);
    }
}
