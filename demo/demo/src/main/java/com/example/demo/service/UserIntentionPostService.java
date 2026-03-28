package com.example.demo.service;

import com.example.demo.entity.UserIntentionPost;
import com.example.demo.repository.UserIntentionPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserIntentionPostService {

    private final UserIntentionPostRepository userIntentionPostRepository;

    @Transactional
    public UserIntentionPost addIntentionPost(Long userId, Long postId) {
        UserIntentionPost intentionPost = new UserIntentionPost();
        intentionPost.setUserId(userId);
        intentionPost.setPostId(postId);
        intentionPost.setCreateTime(LocalDateTime.now());
        intentionPost.setIsDelete(0);

        return userIntentionPostRepository.save(intentionPost);
    }

    @Transactional
    public boolean removeIntentionPost(Long uipId) {
        UserIntentionPost intentionPost = userIntentionPostRepository.findById(uipId).orElse(null);
        if (intentionPost != null) {
            intentionPost.setIsDelete(1);
            userIntentionPostRepository.save(intentionPost);
            return true;
        }
        return false;
    }

    public List<UserIntentionPost> getUserIntentionPosts(Long userId) {
        return userIntentionPostRepository.findByUserIdAndIsDelete(userId, 0);
    }
}