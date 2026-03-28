package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.KnowledgeBase;
import com.example.demo.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge-base")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/add")
    public ApiResponse<KnowledgeBase> addKnowledge(@RequestBody Map<String, Object> request) {
        Long postId = Long.valueOf(request.get("postId").toString());
        String knowledgeName = (String) request.get("knowledgeName");
        String knowledgeContent = (String) request.get("knowledgeContent");
        String vectorId = (String) request.get("vectorId");

        KnowledgeBase knowledge = knowledgeBaseService.addKnowledge(postId, knowledgeName, 
            knowledgeContent, vectorId);
        return ApiResponse.success(knowledge);
    }

    @PutMapping("/{knowledgeId}")
    public ApiResponse<KnowledgeBase> updateKnowledge(@PathVariable Long knowledgeId, @RequestBody Map<String, Object> request) {
        String knowledgeName = (String) request.get("knowledgeName");
        String knowledgeContent = (String) request.get("knowledgeContent");

        KnowledgeBase knowledge = knowledgeBaseService.updateKnowledge(knowledgeId, knowledgeName, knowledgeContent);
        if (knowledge != null) {
            return ApiResponse.success(knowledge);
        }
        return ApiResponse.error("更新失败");
    }

    @DeleteMapping("/{knowledgeId}")
    public ApiResponse<String> deleteKnowledge(@PathVariable Long knowledgeId) {
        boolean deleted = knowledgeBaseService.deleteKnowledge(knowledgeId);
        if (deleted) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<List<KnowledgeBase>> getKnowledgeByPostId(@PathVariable Long postId) {
        List<KnowledgeBase> knowledges = knowledgeBaseService.getKnowledgeByPostId(postId);
        return ApiResponse.success(knowledges);
    }
}