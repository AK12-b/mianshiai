package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserLoginLogService userLoginLogService;

    public User login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmailAndPassword(
            request.getEmail(), 
            request.getPassword()
        );
        
        if (user.isPresent()) {
            User u = user.get();
            if (u.getIsDelete() == 0) {
                u.setLastLoginTime(LocalDateTime.now());
                userRepository.save(u);
                
                userLoginLogService.createLoginLog(u.getUserId());
                
                return u;
            }
        }
        return null;
    }

    public User register(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getUserName() == null || user.getUserName().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return null;
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return null;
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            return null;
        }

        try {
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setIsDelete(0);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            return null;
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User updateUser(User user) {
        if (user.getUserId() == null) return null;
        User db = userRepository.findById(user.getUserId()).orElse(null);
        if (db == null) return null;

        // 仅更新允许修改的字段；创建时间/更新时间由系统维护
        db.setUserName(user.getUserName());
        db.setEmail(user.getEmail());
        db.setGender(user.getGender());
        db.setSkillProficiency(user.getSkillProficiency()); // 作为“专业”使用
        db.setEducation(user.getEducation());
        db.setGrade(user.getGrade());
        db.setProjectExp(user.getProjectExp());
        db.setCompetitionAward(user.getCompetitionAward());
        db.setSkillTag(user.getSkillTag());
        if (user.getDefaultInterviewMode() != null) db.setDefaultInterviewMode(user.getDefaultInterviewMode());
        if (user.getDefaultInterviewModule() != null) db.setDefaultInterviewModule(user.getDefaultInterviewModule());
        if (user.getDefaultInterviewDuration() != null) db.setDefaultInterviewDuration(user.getDefaultInterviewDuration());
        db.setUpdateTime(LocalDateTime.now());
        return userRepository.save(db);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }
        String oldPwd = oldPassword.trim();
        String newPwd = newPassword.trim();
        if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwd.length() < 6) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getIsDelete() == null || user.getIsDelete() != 0) {
            return false;
        }
        if (!oldPwd.equals(user.getPassword())) {
            return false;
        }
        if (oldPwd.equals(newPwd)) {
            return false;
        }

        user.setPassword(newPwd);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }
}