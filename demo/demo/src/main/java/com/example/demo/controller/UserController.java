package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        User user = userService.login(request);
        if (user != null) {
            return ApiResponse.success(user);
        }
        return ApiResponse.error("登录失败，邮箱或密码错误");
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        if (registeredUser != null) {
            return ApiResponse.success(registeredUser);
        }
        return ApiResponse.error("注册失败，用户名或邮箱已存在，或提交信息不完整");
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ApiResponse.success(user);
        }
        return ApiResponse.error("用户不存在");
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setUserId(userId);
        User updatedUser = userService.updateUser(user);
        return ApiResponse.success(updatedUser);
    }

    @PostMapping("/change-password")
    public ApiResponse<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        boolean ok = userService.changePassword(
                request.getUserId(),
                request.getOldPassword(),
                request.getNewPassword()
        );
        if (ok) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("修改失败：请检查原密码是否正确，新密码至少 6 位且不能与原密码相同");
    }
}