package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.PostEvaluateStandard;
import com.example.demo.service.PostEvaluateStandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post-evaluate-standard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostEvaluateStandardController {

    private final PostEvaluateStandardService postEvaluateStandardService;

    @PostMapping("/create")
    public ApiResponse<PostEvaluateStandard> createStandard(@RequestBody Map<String, Object> request) {
        Long postId = Long.valueOf(request.get("postId").toString());
        Integer techWeight = Integer.valueOf(request.get("techWeight").toString());
        Integer knowledgeDepthWeight = Integer.valueOf(request.get("knowledgeDepthWeight").toString());
        Integer logicWeight = Integer.valueOf(request.get("logicWeight").toString());
        Integer expressWeight = Integer.valueOf(request.get("expressWeight").toString());
        Integer matchWeight = Integer.valueOf(request.get("matchWeight").toString());
        String scoreRule = (String) request.get("scoreRule");

        PostEvaluateStandard standard = postEvaluateStandardService.createStandard(postId, techWeight, 
            knowledgeDepthWeight, logicWeight, expressWeight, matchWeight, scoreRule);
        return ApiResponse.success(standard);
    }

    @PutMapping("/{standardId}")
    public ApiResponse<PostEvaluateStandard> updateStandard(@PathVariable Long standardId, @RequestBody Map<String, Object> request) {
        Integer techWeight = Integer.valueOf(request.get("techWeight").toString());
        Integer knowledgeDepthWeight = Integer.valueOf(request.get("knowledgeDepthWeight").toString());
        Integer logicWeight = Integer.valueOf(request.get("logicWeight").toString());
        Integer expressWeight = Integer.valueOf(request.get("expressWeight").toString());
        Integer matchWeight = Integer.valueOf(request.get("matchWeight").toString());
        String scoreRule = (String) request.get("scoreRule");

        PostEvaluateStandard standard = postEvaluateStandardService.updateStandard(standardId, techWeight, 
            knowledgeDepthWeight, logicWeight, expressWeight, matchWeight, scoreRule);
        if (standard != null) {
            return ApiResponse.success(standard);
        }
        return ApiResponse.error("更新失败");
    }

    @DeleteMapping("/{standardId}")
    public ApiResponse<String> deleteStandard(@PathVariable Long standardId) {
        boolean deleted = postEvaluateStandardService.deleteStandard(standardId);
        if (deleted) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<PostEvaluateStandard>> getAllStandards() {
        List<PostEvaluateStandard> standards = postEvaluateStandardService.getAllStandards();
        return ApiResponse.success(standards);
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<PostEvaluateStandard> getStandardByPostId(@PathVariable Long postId) {
        PostEvaluateStandard standard = postEvaluateStandardService.getStandardByPostId(postId);
        if (standard != null) {
            return ApiResponse.success(standard);
        }
        return ApiResponse.error("未找到该岗位的评估标准");
    }
}
