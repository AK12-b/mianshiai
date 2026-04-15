package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Resume;
import com.example.demo.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据目标岗位描述，AI 归纳岗位所需能力
     */
    @PostMapping("/analyze-job")
    public ApiResponse<Map<String, String>> analyzeJobAbilities(@RequestBody(required = false) Map<String, String> body) {
        String jd = body == null ? null : body.get("jobDescription");
        String abilities = resumeService.analyzeJobAbilities(jd);
        Map<String, String> data = new HashMap<>();
        data.put("abilities", abilities != null ? abilities : "");
        return ApiResponse.success(data);
    }

    /**
     * 下载优化后的高清 PDF（需先完成「优化」）
     */
    /**
     * 优化稿正文预览（满意后再调导出 PDF）
     */
    @GetMapping("/{resumeId}/optimized-preview")
    public ApiResponse<Map<String, Object>> optimizedPreview(@PathVariable Long resumeId) {
        try {
            Map<String, Object> preview = resumeService.getOptimizedPreview(resumeId);
            if (preview == null) {
                return ApiResponse.error("暂无优化稿，请先点击「优化」");
            }
            return ApiResponse.success(preview);
        } catch (Exception e) {
            return ApiResponse.error("读取优化稿失败：" + e.getMessage());
        }
    }

    @GetMapping("/{resumeId}/export/pdf")
    public ResponseEntity<Resource> exportOptimizedPdf(@PathVariable Long resumeId) {
        Path path = resumeService.getOptimizedPdfPath(resumeId);
        if (path == null || !Files.isRegularFile(path)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(path.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume_optimized.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}