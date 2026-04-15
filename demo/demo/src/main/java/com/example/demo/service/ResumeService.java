package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.example.demo.entity.Resume;
import com.example.demo.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final QwenService qwenService;
    private final ResumeTextExtractor resumeTextExtractor;

    /**
     * 简历存储根目录，对应配置 app.upload.root。
     * 注意：嵌入式 Tomcat 下 {@code user.dir} 往往指向临时目录 {@code .../Tomcat/localhost/ROOT}，
     * 若用其作为上传根路径，易出现目录不存在或权限问题。因此默认改为 {@code user.home/.interview/uploads}。
     */
    @Value("${app.upload.root:}")
    private String configuredUploadRoot;

    private Path resumeUploadDir() {
        Path base = uploadBase();
        return base.resolve("resume").toAbsolutePath().normalize();
    }

    private Path resumeOptimizedDir() {
        return resumeUploadDir().resolve("optimized").toAbsolutePath().normalize();
    }

    /**
     * 上传根目录：优先配置 app.upload.root；未配置则用用户目录下 .interview/uploads（避免依赖 Tomcat 的 user.dir）。
     */
    private Path uploadBase() {
        if (configuredUploadRoot != null && !configuredUploadRoot.isBlank()) {
            return Paths.get(configuredUploadRoot.trim()).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.home", "."), ".interview", "uploads")
                .toAbsolutePath()
                .normalize();
    }

    @Transactional
    public Resume uploadResume(Long userId, Long postId, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "resume.bin";
        }
        int dot = originalFilename.lastIndexOf('.');
        String fileExtension = dot >= 0 ? originalFilename.substring(dot) : "";
        if (fileExtension.isEmpty()) {
            fileExtension = ".bin";
        }

        Path dir = resumeUploadDir();
        Files.createDirectories(dir);
        log.debug("简历保存目录: {}", dir);

        String newFilename = userId + "_" + System.currentTimeMillis() + fileExtension;
        Path target = dir.resolve(newFilename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String formatLower = fileExtension.replace(".", "").toLowerCase(Locale.ROOT);
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setPostId(postId);
        resume.setOriginalUrl(target.toAbsolutePath().toString());
        resume.setResumeSize(file.getSize());
        resume.setResumeFormat(formatLower);
        resume.setIsOptimizeApply(0);
        resume.setCreateTime(LocalDateTime.now());
        resume.setIsDelete(0);

        Resume saved = resumeRepository.save(resume);

        try {
            String plain = resumeTextExtractor.extractPlainText(target, formatLower);
            if (plain != null && !plain.isBlank()) {
                String rawJson = qwenService.parseResumeStructure(plain.trim());
                saved.setParsedJson(sanitizeJsonResponse(rawJson));
                validateParsedJson(saved.getParsedJson());
                saved = resumeRepository.save(saved);
            }
        } catch (Exception e) {
            log.warn("简历结构化解析未完成: {}", e.getMessage());
        }

        return saved;
    }

    private void validateParsedJson(String json) {
        if (json == null || json.isBlank()) {
            return;
        }
        try {
            JSON.parseObject(json);
        } catch (Exception e) {
            log.debug("解析结果非严格 JSON，仍保存原文");
        }
    }

    private static String sanitizeJsonResponse(String raw) {
        if (raw == null) {
            return "{}";
        }
        String s = raw.trim();
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl > 0) {
                s = s.substring(nl + 1);
            }
            if (s.endsWith("```")) {
                s = s.substring(0, s.length() - 3).trim();
            }
        }
        return s.isEmpty() ? "{}" : s;
    }

    @Transactional
    public Resume diagnoseResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElse(null);
        if (resume == null) {
            return null;
        }

        String prompt = buildDiagnosePrompt(resume.getOriginalUrl(), resume.getResumeFormat());
        String diagnoseResult = qwenService.diagnoseResume(prompt);

        resume.setDiagnoseResult(diagnoseResult);
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume optimizeResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElse(null);
        if (resume == null) {
            return null;
        }

        String prompt = buildOptimizePrompt(resume.getOriginalUrl(), resume.getResumeFormat(), resume.getDiagnoseResult());
        String optimizeResult = qwenService.optimizeResume(prompt);

        try {
            Path optDir = resumeOptimizedDir();
            Files.createDirectories(optDir);

            ResumeOptimizationOutputSplitter.Parts parts = ResumeOptimizationOutputSplitter.split(optimizeResult);
            String bodyForStorage;
            if (parts.structured()) {
                if (parts.diagnoseOneAndThree() != null && !parts.diagnoseOneAndThree().isBlank()) {
                    resume.setDiagnoseResult(parts.diagnoseOneAndThree());
                }
                bodyForStorage = parts.resumePartTwo() != null ? parts.resumePartTwo() : "";
            } else {
                log.warn(
                        "简历优化输出未命中「## 二、优化后完整简历」分节标题，已退回为全文写入 optimize_content；"
                                + "请确认 DashScope 简历优化应用已使用含三段的提示词。");
                bodyForStorage = optimizeResult == null ? "" : optimizeResult.trim();
            }
            resume.setOptimizeContent(bodyForStorage);

            String optimizedFilename = "optimized_" + resumeId + ".txt";
            Path optimizedFile = optDir.resolve(optimizedFilename);
            Files.write(optimizedFile, bodyForStorage.getBytes(StandardCharsets.UTF_8));

            resume.setOptimizeUrl(optimizedFile.toAbsolutePath().toString());
            resume.setIsOptimizeApply(1);
            resume.setOptimizeTime(LocalDateTime.now());

            Path pdfFile = optDir.resolve("optimized_" + resumeId + ".pdf");
            try {
                ResumePdfExporter.writeMarkdownResumePdf(bodyForStorage, pdfFile);
                resume.setOptimizePdfUrl(pdfFile.toAbsolutePath().toString());
            } catch (IOException pdfEx) {
                log.warn("生成优化 PDF 失败（已保留 TXT）: {}", pdfEx.getMessage());
                resume.setOptimizePdfUrl(null);
            }

            return resumeRepository.save(resume);
        } catch (IOException e) {
            log.error("简历优化失败", e);
            return null;
        }
    }

    /**
     * 供下载接口：若尚未生成 PDF 则返回 null
     */
    public Path getOptimizedPdfPath(Long resumeId) {
        Resume r = resumeRepository.findById(resumeId).orElse(null);
        if (r == null || r.getOptimizePdfUrl() == null || r.getOptimizePdfUrl().isBlank()) {
            return null;
        }
        Path p = Paths.get(r.getOptimizePdfUrl());
        return Files.isRegularFile(p) ? p : null;
    }

    /**
     * 读取已生成的优化稿正文（TXT），供前端预览后再导出 PDF
     */
    public java.util.Map<String, Object> getOptimizedPreview(Long resumeId) throws IOException {
        Resume r = resumeRepository.findById(resumeId).orElse(null);
        if (r == null) {
            return null;
        }
        String text = r.getOptimizeContent();
        if (text == null || text.isBlank()) {
            if (r.getOptimizeUrl() == null || r.getOptimizeUrl().isBlank()) {
                return null;
            }
            Path p = Paths.get(r.getOptimizeUrl());
            if (!Files.isRegularFile(p)) {
                return null;
            }
            text = Files.readString(p, StandardCharsets.UTF_8);
        }
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("content", text);
        m.put("hasPdf", r.getOptimizePdfUrl() != null && !r.getOptimizePdfUrl().isBlank());
        return m;
    }

    private String readResumeTextForPrompt(String resumePath, String formatLower) {
        if (resumePath == null || resumePath.isBlank()) {
            return "";
        }
        try {
            Path p = Paths.get(resumePath);
            if (!Files.isRegularFile(p)) {
                return "";
            }
            String t = resumeTextExtractor.extractPlainText(p, formatLower);
            if (t != null && t.length() > 14000) {
                return t.substring(0, 14000) + "\n\n...(正文过长已截断)";
            }
            return t != null ? t : "";
        } catch (Exception e) {
            log.warn("读取简历文件失败: {}", e.getMessage());
        }
        return "";
    }

    private String buildDiagnosePrompt(String resumePath, String formatLower) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请对以下简历进行全维度诊断评估：\n\n");

        String body = readResumeTextForPrompt(resumePath, formatLower);
        if (!body.isEmpty()) {
            prompt.append("简历正文（已从 PDF/Word/文本 提取）：\n").append(body).append("\n\n");
        } else {
            prompt.append("简历文件路径（本地）：").append(resumePath).append("\n");
            prompt.append("说明：未能从文件提取到文本，请从「常见简历结构」角度给出通用诊断建议。\n\n");
        }

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

    private String buildOptimizePrompt(String resumePath, String formatLower, String diagnoseResult) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据简历诊断结果，对简历进行优化改写：\n\n");

        String body = readResumeTextForPrompt(resumePath, formatLower);
        if (!body.isEmpty()) {
            prompt.append("原简历正文：\n").append(body).append("\n\n");
        } else {
            prompt.append("原简历文件路径：").append(resumePath).append("\n\n");
        }

        prompt.append("诊断结果：").append(diagnoseResult == null ? "" : diagnoseResult).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 修复诊断中发现的问题\n");
        prompt.append("2. 优化语言表达，使其更专业\n");
        prompt.append("3. 量化项目成果\n");
        prompt.append("4. 突出个人亮点\n");
        prompt.append("5. 确保符合目标岗位要求\n");
        prompt.append("6. 保持简历结构清晰\n\n");
        prompt.append("【输出结构 — 必须严格遵守，便于后端拆分：仅「二」进入 PDF】\n");
        prompt.append("全文使用 Markdown，且必须依次包含以下三个二级标题（「##」与「一、」之间可有一个空格，标题文字需一致）：\n");
        prompt.append("## 一、简历诊断结果\n");
        prompt.append("## 二、优化后完整简历\n");
        prompt.append("## 三、优化依据说明\n");
        prompt.append("说明：「一」「三」为诊断与依据；「二」中只写可投递的最终简历成品（含个人信息/教育/项目等），无任何诊断或建议口吻；\n");
        prompt.append("系统导出 PDF 时只截取「二、优化后完整简历」一节，不会导出「一」「三」。\n");

        return prompt.toString();
    }

    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }

    public String analyzeJobAbilities(String jobDescription) {
        if (jobDescription == null || jobDescription.isBlank()) {
            return "";
        }
        return qwenService.analyzeJobAbilities(jobDescription.trim());
    }
}
