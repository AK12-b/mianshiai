package com.example.demo.service;

import com.example.demo.entity.PersonalSuggestion;
import com.example.demo.repository.PersonalSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalSuggestionService {

    private final PersonalSuggestionRepository personalSuggestionRepository;

    @Transactional
    public PersonalSuggestion createSuggestion(Long userId, Long postId, String techSuggest,
                                            String knowledgeDepthSuggest, String logicSuggest,
                                            String expressSuggest, String postSuggest) {
        PersonalSuggestion suggestion = new PersonalSuggestion();
        suggestion.setUserId(userId);
        suggestion.setPostId(postId);
        suggestion.setTechSuggest(techSuggest);
        suggestion.setKnowledgeDepthSuggest(knowledgeDepthSuggest);
        suggestion.setLogicSuggest(logicSuggest);
        suggestion.setExpressSuggest(expressSuggest);
        suggestion.setPostSuggest(postSuggest);
        suggestion.setCreateTime(LocalDateTime.now());
        suggestion.setIsDelete(0);

        return personalSuggestionRepository.save(suggestion);
    }

    public List<PersonalSuggestion> getUserSuggestions(Long userId) {
        return personalSuggestionRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }
}