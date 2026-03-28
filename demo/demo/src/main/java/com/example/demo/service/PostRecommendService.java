package com.example.demo.service;

import com.example.demo.entity.PostRecommend;
import com.example.demo.repository.PostRecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostRecommendService {

    private final PostRecommendRepository postRecommendRepository;

    @Transactional
    public PostRecommend generateRecommend(Long userId, Long originalPostId, Long recommendPostId,
                                        BigDecimal matchRate, BigDecimal skillMatchRate,
                                        BigDecimal projectMatchRate, BigDecimal interviewMatchRate,
                                        String matchHighlight, String abilityGap, String industryProspect) {
        PostRecommend recommend = new PostRecommend();
        recommend.setUserId(userId);
        recommend.setOriginalPostId(originalPostId);
        recommend.setRecommendPostId(recommendPostId);
        recommend.setMatchRate(matchRate);
        recommend.setSkillMatchRate(skillMatchRate);
        recommend.setProjectMatchRate(projectMatchRate);
        recommend.setInterviewMatchRate(interviewMatchRate);
        recommend.setMatchHighlight(matchHighlight);
        recommend.setAbilityGap(abilityGap);
        recommend.setIndustryProspect(industryProspect);
        recommend.setCreateTime(LocalDateTime.now());
        recommend.setIsDelete(0);

        return postRecommendRepository.save(recommend);
    }

    public List<PostRecommend> getUserRecommendations(Long userId) {
        return postRecommendRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }
}