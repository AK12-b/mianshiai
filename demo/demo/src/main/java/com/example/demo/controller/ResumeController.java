package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Resume;
import com.example.demo.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ApiResponse<Resume> uploadResume(@RequestParam("userId") Long userId,
                                          @RequestParam("postId") Long postId,
                                          @RequestParam("file") MultipartFile file) {
        try {
            Resume resume = resumeService.uploadResume(userId, postId, file);
            return ApiResponse.success(resume);
        } catch (Exception e) {
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/{resumeId}/diagnose")
    public ApiResponse<Resume> diagnoseResume(@PathVariable Long resumeId) {
        Resume resume = resumeService.diagnoseResume(resumeId);
        if (resume != null) {
            return ApiResponse.success(resume);
        }
        return ApiResponse.error("诊断失败");
    }

    @PostMapping("/{resumeId}/optimize")
    public ApiResponse<Resume> optimizeResume(@PathVariable Long resumeId) {
        Resume resume = resumeService.optimizeResume(resumeId);
        if (resume != null) {
            return ApiResponse.success(resume);
        }
        return ApiResponse.error("优化失败");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Resume>> getUserResumes(@PathVariable Long userId) {
        List<Resume> resumes = resumeService.getUserResumes(userId);
        return ApiResponse.success(resumes);
    }
}