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
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }
}