package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.PersonalSuggestion;
import com.example.demo.service.PersonalSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal-suggestion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonalSuggestionController {

    private final PersonalSuggestionService personalSuggestionService;

    @PostMapping("/create")
    public ApiResponse<PersonalSuggestion> createSuggestion(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long postId = Long.valueOf(request.get("postId").toString());
        String techSuggest = (String) request.get("techSuggest");
        String knowledgeDepthSuggest = (String) request.get("knowledgeDepthSuggest");
        String logicSuggest = (String) request.get("logicSuggest");
        String expressSuggest = (String) request.get("expressSuggest");
        String postSuggest = (String) request.get("postSuggest");

        PersonalSuggestion suggestion = personalSuggestionService.createSuggestion(userId, postId, 
            techSuggest, knowledgeDepthSuggest, logicSuggest, expressSuggest, postSuggest);
        return ApiResponse.success(suggestion);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PersonalSuggestion>> getUserSuggestions(@PathVariable Long userId) {
        List<PersonalSuggestion> suggestions = personalSuggestionService.getUserSuggestions(userId);
        return ApiResponse.success(suggestions);
    }
}