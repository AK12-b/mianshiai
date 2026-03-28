package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.UserIntentionPost;
import com.example.demo.service.UserIntentionPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-intention-post")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserIntentionPostController {

    private final UserIntentionPostService userIntentionPostService;

    @PostMapping("/add")
    public ApiResponse<UserIntentionPost> addIntentionPost(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long postId = Long.valueOf(request.get("postId").toString());

        UserIntentionPost intentionPost = userIntentionPostService.addIntentionPost(userId, postId);
        return ApiResponse.success(intentionPost);
    }

    @DeleteMapping("/{uipId}")
    public ApiResponse<String> removeIntentionPost(@PathVariable Long uipId) {
        boolean removed = userIntentionPostService.removeIntentionPost(uipId);
        if (removed) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserIntentionPost>> getUserIntentionPosts(@PathVariable Long userId) {
        List<UserIntentionPost> intentionPosts = userIntentionPostService.getUserIntentionPosts(userId);
        return ApiResponse.success(intentionPosts);
    }
}