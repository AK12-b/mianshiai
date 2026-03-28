package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.AbilityGrowth;
import com.example.demo.service.AbilityGrowthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ability-growth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AbilityGrowthController {

    private final AbilityGrowthService abilityGrowthService;

    @PostMapping("/create")
    public ApiResponse<AbilityGrowth> createGrowthRecord(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long postId = Long.valueOf(request.get("postId").toString());
        Integer statisticsCycle = Integer.valueOf(request.get("statisticsCycle").toString());
        String cycleTime = (String) request.get("cycleTime");
        Integer interviewCount = Integer.valueOf(request.get("interviewCount").toString());
        BigDecimal avgScore = new BigDecimal(request.get("avgScore").toString());
        Integer practiceQuestionCount = Integer.valueOf(request.get("practiceQuestionCount").toString());
        Integer totalTrainDuration = Integer.valueOf(request.get("totalTrainDuration").toString());
        Integer wrongQuestionCount = Integer.valueOf(request.get("wrongQuestionCount").toString());
        String weakPoint = (String) request.get("weakPoint");
        BigDecimal weakPointImproveRate = new BigDecimal(request.get("weakPointImproveRate").toString());
        String abilityImprove = (String) request.get("abilityImprove");
        String growthSuggest = (String) request.get("growthSuggest");

        AbilityGrowth growth = abilityGrowthService.createGrowthRecord(userId, postId, statisticsCycle, 
            cycleTime, interviewCount, avgScore, practiceQuestionCount, totalTrainDuration, 
            wrongQuestionCount, weakPoint, weakPointImproveRate, abilityImprove, growthSuggest);
        return ApiResponse.success(growth);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AbilityGrowth>> getUserGrowthRecords(@PathVariable Long userId) {
        List<AbilityGrowth> growths = abilityGrowthService.getUserGrowthRecords(userId);
        return ApiResponse.success(growths);
    }
}