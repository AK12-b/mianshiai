package com.example.demo.service;

import com.example.demo.entity.PostEvaluateStandard;
import com.example.demo.repository.PostEvaluateStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostEvaluateStandardService {

    private final PostEvaluateStandardRepository postEvaluateStandardRepository;

    @Transactional
    public PostEvaluateStandard createStandard(Long postId, Integer techWeight, Integer knowledgeDepthWeight,
                                             Integer logicWeight, Integer expressWeight, Integer matchWeight,
                                             String scoreRule) {
        PostEvaluateStandard standard = new PostEvaluateStandard();
        standard.setPostId(postId);
        standard.setTechWeight(techWeight);
        standard.setKnowledgeDepthWeight(knowledgeDepthWeight);
        standard.setLogicWeight(logicWeight);
        standard.setExpressWeight(expressWeight);
        standard.setMatchWeight(matchWeight);
        standard.setScoreRule(scoreRule);
        standard.setIsDelete(0);

        return postEvaluateStandardRepository.save(standard);
    }

    @Transactional
    public PostEvaluateStandard updateStandard(Long standardId, Integer techWeight, Integer knowledgeDepthWeight,
                                             Integer logicWeight, Integer expressWeight, Integer matchWeight,
                                             String scoreRule) {
        PostEvaluateStandard standard = postEvaluateStandardRepository.findById(standardId).orElse(null);
        if (standard != null) {
            standard.setTechWeight(techWeight);
            standard.setKnowledgeDepthWeight(knowledgeDepthWeight);
            standard.setLogicWeight(logicWeight);
            standard.setExpressWeight(expressWeight);
            standard.setMatchWeight(matchWeight);
            standard.setScoreRule(scoreRule);
            return postEvaluateStandardRepository.save(standard);
        }
        return null;
    }

    @Transactional
    public boolean deleteStandard(Long standardId) {
        PostEvaluateStandard standard = postEvaluateStandardRepository.findById(standardId).orElse(null);
        if (standard != null) {
            standard.setIsDelete(1);
            postEvaluateStandardRepository.save(standard);
            return true;
        }
        return false;
    }

    public List<PostEvaluateStandard> getAllStandards() {
        return postEvaluateStandardRepository.findAll();
    }

    public PostEvaluateStandard getStandardByPostId(Long postId) {
        return postEvaluateStandardRepository.findByPostIdAndIsDelete(postId, 0).orElse(null);
    }
}
