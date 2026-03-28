package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.example.demo.dto.QwenRequest;
import com.example.demo.dto.QwenResponse;
import com.example.demo.entity.Resume;
import com.example.demo.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String apiUrl;

    private final ResumeRepository resumeRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public Resume uploadResume(Long userId, Long postId, MultipartFile file) throws IOException {
        String uploadDir = "uploads/resume/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = userId + "_" + System.currentTimeMillis() + fileExtension;
        String filePath = uploadDir + newFilename;

        file.transferTo(new File(filePath));

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setPostId(postId);
        resume.setOriginalUrl(filePath);
        resume.setResumeSize(file.getSize());
        resume.setResumeFormat(fileExtension.replace(".", ""));
        resume.setIsOptimizeApply(0);
        resume.setCreateTime(LocalDateTime.now());
        resume.setIsDelete(0);

        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume diagnoseResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElse(null);
        if (resume == null) {
            return null;
        }

        String prompt = buildDiagnosePrompt(resume.getOriginalUrl());
        String diagnoseResult = callQwenApi(prompt);
        
        resume.setDiagnoseResult(diagnoseResult);
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume optimizeResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElse(null);
        if (resume == null) {
            return null;
        }

        String prompt = buildOptimizePrompt(resume.getOriginalUrl(), resume.getDiagnoseResult());
        String optimizeResult = callQwenApi(prompt);

        try {
            String uploadDir = "uploads/resume/optimized/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String optimizedFilename = "optimized_" + resumeId + ".txt";
            String optimizedPath = uploadDir + optimizedFilename;
            Files.write(Paths.get(optimizedPath), optimizeResult.getBytes());

            resume.setOptimizeUrl(optimizedPath);
            resume.setIsOptimizeApply(1);
            resume.setOptimizeTime(LocalDateTime.now());

            return resumeRepository.save(resume);
        } catch (IOException e) {
            log.error("简历优化失败", e);
            return null;
        }
    }

    private String buildDiagnosePrompt(String resumePath) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请对以下简历进行全维度诊断评估：\n\n");
        prompt.append("简历路径：").append(resumePath).append("\n\n");
        prompt.append("请从以下维度进行评估：\n");
        prompt.append("1. 内容完整性（是否包含基本信息、教育背景、项目经历、技能等）\n");
        prompt.append("2. 表述专业性（语言表达是否专业、准确）\n");
        prompt.append("3. 岗位匹配度（是否包含目标岗位所需的核心技能和关键词）\n");
        prompt.append("4. 亮点突出度（项目成果是否量化、是否有突出的亮点）\n\n");
        prompt.append("请以JSON格式返回诊断结果，包含：\n");
        prompt.append("{\n");
        prompt.append("  \"content_completeness\": \"评估结果\",\n");
        prompt.append("  \"expression_professionalism\": \"评估结果\",\n");
        prompt.append("  \"post_match\": \"评估结果\",\n");
        prompt.append("  \"highlight_prominence\": \"评估结果\",\n");
        prompt.append("  \"overall_score\": \"综合评分(0-100)\"\n");
        prompt.append("}");
        
        return prompt.toString();
    }

    private String buildOptimizePrompt(String resumePath, String diagnoseResult) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据简历诊断结果，对简历进行优化改写：\n\n");
        prompt.append("原简历路径：").append(resumePath).append("\n\n");
        prompt.append("诊断结果：").append(diagnoseResult).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 修复诊断中发现的问题\n");
        prompt.append("2. 优化语言表达，使其更专业\n");
        prompt.append("3. 量化项目成果\n");
        prompt.append("4. 突出个人亮点\n");
        prompt.append("5. 确保符合目标岗位要求\n");
        prompt.append("6. 保持简历结构清晰\n\n");
        prompt.append("请直接输出优化后的简历内容");
        
        return prompt.toString();
    }

    private String callQwenApi(String prompt) {
        try {
            QwenRequest request = new QwenRequest();
            request.setModel("qwen-turbo");
            request.setStream(false);

            List<QwenRequest.Message> messages = new ArrayList<>();
            QwenRequest.Message message = new QwenRequest.Message();
            message.setRole("user");
            message.setContent(prompt);
            messages.add(message);
            request.setMessages(messages.toArray(new QwenRequest.Message[0]));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<QwenRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                QwenResponse qwenResponse = JSON.parseObject(response.getBody(), QwenResponse.class);
                if (qwenResponse.getChoices() != null && !qwenResponse.getChoices().isEmpty()) {
                    return qwenResponse.getChoices().get(0).getMessage().getContent();
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("调用千问API失败", e);
            return null;
        }
    }

    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }
}