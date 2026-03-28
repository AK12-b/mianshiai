package com.example.demo.service;

import com.example.demo.entity.AbilityGrowth;
import com.example.demo.repository.AbilityGrowthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbilityGrowthService {

    private final AbilityGrowthRepository abilityGrowthRepository;

    @Transactional
    public AbilityGrowth createGrowthRecord(Long userId, Long postId, Integer statisticsCycle, 
                                           String cycleTime, Integer interviewCount, BigDecimal avgScore,
                                           Integer practiceQuestionCount, Integer totalTrainDuration,
                                           Integer wrongQuestionCount, String weakPoint,
                                           BigDecimal weakPointImproveRate, String abilityImprove, String growthSuggest) {
        AbilityGrowth growth = new AbilityGrowth();
        growth.setUserId(userId);
        growth.setPostId(postId);
        growth.setStatisticsCycle(statisticsCycle);
        growth.setCycleTime(cycleTime);
        growth.setInterviewCount(interviewCount);
        growth.setAvgScore(avgScore);
        growth.setPracticeQuestionCount(practiceQuestionCount);
        growth.setTotalTrainDuration(totalTrainDuration);
        growth.setWrongQuestionCount(wrongQuestionCount);
        growth.setWeakPoint(weakPoint);
        growth.setWeakPointImproveRate(weakPointImproveRate);
        growth.setAbilityImprove(abilityImprove);
        growth.setGrowthSuggest(growthSuggest);
        growth.setCreateTime(LocalDateTime.now());
        growth.setIsDelete(0);

        return abilityGrowthRepository.save(growth);
    }

    public List<AbilityGrowth> getUserGrowthRecords(Long userId) {
        return abilityGrowthRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }
}