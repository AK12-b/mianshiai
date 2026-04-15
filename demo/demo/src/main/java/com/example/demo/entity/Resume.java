package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    private Long userId;

    private Long postId;

    private String originalUrl;

    private Long resumeSize;

    private String resumeFormat;

    /** AI 诊断全文可能较长，须用 LONGTEXT，避免默认 255 截断导致列表接口读不到完整内容 */
    @Column(name = "diagnose_result", columnDefinition = "LONGTEXT")
    private String diagnoseResult;

    /**
     * 「二、优化后完整简历」Markdown 成稿；导出 PDF / 预览仅使用本字段（拆分失败时可退化为全文）。
     */
    @Column(name = "optimize_content", columnDefinition = "LONGTEXT")
    private String optimizeContent;

    private String optimizeUrl;

    /** 优化结果 PDF（高清导出）本地路径 */
    @Column(name = "optimize_pdf_url", length = 1024)
    private String optimizePdfUrl;

    /** AI 从简历全文解析出的结构化 JSON（基础信息、教育、技能、项目、实习等） */
    @Column(name = "parsed_json", columnDefinition = "LONGTEXT")
    private String parsedJson;

    private Integer isOptimizeApply;

    private LocalDateTime optimizeTime;

    private LocalDateTime createTime;

    private Integer isDelete;
}