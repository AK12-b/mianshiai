package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostRepository postRepository;

    @GetMapping("/list")
    public ApiResponse<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findByIsDeleteOrderByCreateTimeDesc(0);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{postId}")
    public ApiResponse<Post> getPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            return ApiResponse.success(post);
        }
        return ApiResponse.error("岗位不存在");
    }

    @GetMapping("/name/{postName}")
    public ApiResponse<Post> getPostByName(@PathVariable String postName) {
        Post post = postRepository.findByPostName(postName);
        if (post != null) {
            return ApiResponse.success(post);
        }
        return ApiResponse.error("岗位不存在");
    }
}