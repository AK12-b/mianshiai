package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.PostRecommend;
import com.example.demo.service.PostRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post-recommend")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostRecommendController {

    private final PostRecommendService postRecommendService;

    @PostMapping("/generate")
    public ApiResponse<PostRecommend> generateRecommend(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long originalPostId = Long.valueOf(request.get("originalPostId").toString());
        Long recommendPostId = Long.valueOf(request.get("recommendPostId").toString());
        BigDecimal matchRate = new BigDecimal(request.get("matchRate").toString());
        BigDecimal skillMatchRate = new BigDecimal(request.get("skillMatchRate").toString());
        BigDecimal projectMatchRate = new BigDecimal(request.get("projectMatchRate").toString());
        BigDecimal interviewMatchRate = new BigDecimal(request.get("interviewMatchRate").toString());
        String matchHighlight = (String) request.get("matchHighlight");
        String abilityGap = (String) request.get("abilityGap");
        String industryProspect = (String) request.get("industryProspect");

        PostRecommend recommend = postRecommendService.generateRecommend(userId, originalPostId, 
            recommendPostId, matchRate, skillMatchRate, projectMatchRate, 
            interviewMatchRate, matchHighlight, abilityGap, industryProspect);
        return ApiResponse.success(recommend);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PostRecommend>> getUserRecommendations(@PathVariable Long userId) {
        List<PostRecommend> recommends = postRecommendService.getUserRecommendations(userId);
        return ApiResponse.success(recommends);
    }
}