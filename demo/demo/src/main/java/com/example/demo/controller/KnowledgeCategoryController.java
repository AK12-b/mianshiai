package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.KnowledgeCategory;
import com.example.demo.repository.KnowledgeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-category")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KnowledgeCategoryController {

    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    @GetMapping("/list")
    public ApiResponse<List<KnowledgeCategory>> list() {
        return ApiResponse.success(knowledgeCategoryRepository.findAll());
    }
}

