package com.example.demo.service;

import com.example.demo.entity.KnowledgeBase;
import com.example.demo.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    @Transactional
    public KnowledgeBase addKnowledge(Long postId, String knowledgeName, String knowledgeContent, String vectorId) {
        KnowledgeBase knowledge = new KnowledgeBase();
        knowledge.setPostId(postId);
        knowledge.setKnowledgeName(knowledgeName);
        knowledge.setKnowledgeContent(knowledgeContent);
        knowledge.setVectorId(vectorId);
        knowledge.setUpdateTime(LocalDateTime.now());
        knowledge.setIsDelete(0);

        return knowledgeBaseRepository.save(knowledge);
    }

    @Transactional
    public KnowledgeBase updateKnowledge(Long knowledgeId, String knowledgeName, String knowledgeContent) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(knowledgeId).orElse(null);
        if (knowledge != null) {
            knowledge.setKnowledgeName(knowledgeName);
            knowledge.setKnowledgeContent(knowledgeContent);
            knowledge.setUpdateTime(LocalDateTime.now());
            return knowledgeBaseRepository.save(knowledge);
        }
        return null;
    }

    @Transactional
    public boolean deleteKnowledge(Long knowledgeId) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(knowledgeId).orElse(null);
        if (knowledge != null) {
            knowledge.setIsDelete(1);
            knowledgeBaseRepository.save(knowledge);
            return true;
        }
        return false;
    }

    public List<KnowledgeBase> getKnowledgeByPostId(Long postId) {
        return knowledgeBaseRepository.findByPostIdAndIsDelete(postId, 0);
    }
}