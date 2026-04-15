package com.example.demo.config;

import com.example.demo.entity.MockInterview;
import com.example.demo.entity.PersonalSuggestion;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostRecommend;
import com.example.demo.entity.PostEvaluateStandard;
import com.example.demo.entity.User;
import com.example.demo.repository.MockInterviewRepository;
import com.example.demo.repository.PersonalSuggestionRepository;
import com.example.demo.repository.PostEvaluateStandardRepository;
import com.example.demo.repository.PostRecommendRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@Order(120)
@RequiredArgsConstructor
public class BusinessDataBootstrapRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final PersonalSuggestionRepository personalSuggestionRepository;
    private final PostRecommendRepository postRecommendRepository;
    private final PostEvaluateStandardRepository postEvaluateStandardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedPostEvaluateStandards();
        seedPersonalSuggestions();
        seedPostRecommendations();
    }

    private void seedPostEvaluateStandards() {
        List<Post> posts = postRepository.findByIsDelete(0);
        if (posts.isEmpty()) return;
        int inserted = 0;
        for (Post p : posts) {
            if (p == null || p.getPostId() == null) continue;
            if (postEvaluateStandardRepository.findByPostIdAndIsDelete(p.getPostId(), 0).isPresent()) continue;
            PostEvaluateStandard s = new PostEvaluateStandard();
            s.setPostId(p.getPostId());
            s.setTechWeight(35);
            s.setKnowledgeDepthWeight(20);
            s.setLogicWeight(15);
            s.setExpressWeight(15);
            s.setMatchWeight(15);
            s.setScoreRule("总分=技术*35%+知识深度*20%+逻辑*15%+表达*15%+匹配度*15%");
            s.setIsDelete(0);
            postEvaluateStandardRepository.save(s);
            inserted++;
        }
        if (inserted > 0) {
            log.info("post_evaluate_standard 初始化 {} 条", inserted);
        }
    }

    private void seedPersonalSuggestions() {
        List<Post> posts = postRepository.findByIsDelete(0);
        if (posts.isEmpty()) return;
        Long fallbackPostId = posts.get(0).getPostId();
        int inserted = 0;
        for (User u : userRepository.findAll()) {
            if (u == null || u.getUserId() == null) continue;
            if (u.getIsDelete() != null && u.getIsDelete() != 0) continue;
            Long postId = resolveLatestPostId(u.getUserId(), fallbackPostId);
            if (postId == null || postId <= 0) continue;
            if (personalSuggestionRepository.existsByUserIdAndPostIdAndIsDelete(u.getUserId(), postId, 0)) continue;
            PersonalSuggestion ps = new PersonalSuggestion();
            ps.setUserId(u.getUserId());
            ps.setPostId(postId);
            ps.setTechSuggest("建议优先巩固岗位核心技术点，围绕高频面试题做专题复盘。");
            ps.setKnowledgeDepthSuggest("建议结合项目场景回答“为什么这样设计”，增强原理深度。");
            ps.setLogicSuggest("建议使用“结论-依据-案例”三段式回答，提高结构化表达。");
            ps.setExpressSuggest("建议控制回答节奏，先总后分，重点内容适当量化。");
            ps.setPostSuggest("建议结合目标岗位 JD 持续补齐短板，并进行模拟面试验证效果。");
            ps.setCreateTime(LocalDateTime.now());
            ps.setIsDelete(0);
            personalSuggestionRepository.save(ps);
            inserted++;
        }
        if (inserted > 0) {
            log.info("personal_suggestion 初始化 {} 条", inserted);
        }
    }

    private void seedPostRecommendations() {
        List<Post> posts = postRepository.findByIsDelete(0);
        if (posts.size() < 2) return;
        int inserted = 0;
        for (User u : userRepository.findAll()) {
            if (u == null || u.getUserId() == null) continue;
            if (u.getIsDelete() != null && u.getIsDelete() != 0) continue;
            Long originalPostId = resolveLatestPostId(u.getUserId(), posts.get(0).getPostId());
            for (Post p : posts) {
                if (p == null || p.getPostId() == null) continue;
                if (p.getPostId().equals(originalPostId)) continue;
                if (postRecommendRepository.existsByUserIdAndOriginalPostIdAndRecommendPostIdAndIsDelete(
                        u.getUserId(), originalPostId, p.getPostId(), 0)) {
                    continue;
                }
                PostRecommend r = new PostRecommend();
                r.setUserId(u.getUserId());
                r.setOriginalPostId(originalPostId);
                r.setRecommendPostId(p.getPostId());
                r.setMatchRate(new BigDecimal("72"));
                r.setSkillMatchRate(new BigDecimal("70"));
                r.setProjectMatchRate(new BigDecimal("73"));
                r.setInterviewMatchRate(new BigDecimal("74"));
                r.setMatchHighlight("已有能力与目标岗位存在可迁移点，可通过专项训练快速补强。");
                r.setAbilityGap("建议补齐目标岗位核心技术栈与项目实战经验。");
                r.setIndustryProspect("该方向需求稳定，建议结合近期招聘要求持续迭代。");
                r.setCreateTime(LocalDateTime.now());
                r.setIsDelete(0);
                postRecommendRepository.save(r);
                inserted++;
            }
        }
        if (inserted > 0) {
            log.info("post_recommend 初始化 {} 条", inserted);
        }
    }

    private Long resolveLatestPostId(Long userId, Long fallbackPostId) {
        List<MockInterview> list = mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
        MockInterview latest = list.stream()
                .filter(it -> it != null && it.getPostId() != null)
                .max(Comparator.comparing(it -> it.getStartTime() == null ? LocalDateTime.MIN : it.getStartTime()))
                .orElse(null);
        if (latest != null && latest.getPostId() != null) return latest.getPostId();
        return fallbackPostId;
    }
}

