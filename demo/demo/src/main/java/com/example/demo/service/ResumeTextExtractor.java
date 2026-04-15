package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * 从 PDF / Word / 纯文本 简历中提取纯文本，供 AI 解析与诊断。
 */
@Slf4j
@Component
public class ResumeTextExtractor {

    public String extractPlainText(Path filePath, String formatLower) {
        if (filePath == null || !Files.isRegularFile(filePath)) {
            return "";
        }
        String ext = formatLower != null ? formatLower.toLowerCase(Locale.ROOT).trim() : "";
        String name = filePath.getFileName().toString().toLowerCase(Locale.ROOT);
        if (ext.isEmpty()) {
            if (name.endsWith(".pdf")) ext = "pdf";
            else if (name.endsWith(".docx")) ext = "docx";
            else if (name.endsWith(".doc")) ext = "doc";
            else if (name.endsWith(".txt")) ext = "txt";
        }
        try {
            return switch (ext) {
                case "pdf" -> extractPdf(filePath);
                case "docx" -> extractDocx(filePath);
                case "doc" -> extractDoc(filePath);
                case "txt" -> Files.readString(filePath, StandardCharsets.UTF_8);
                default -> {
                    log.warn("未知简历格式: {}", ext);
                    yield "";
                }
            };
        } catch (Exception e) {
            log.warn("提取简历文本失败 [{}]: {}", filePath, e.getMessage());
            return "";
        }
    }

    private String extractPdf(Path path) throws Exception {
        try (PDDocument doc = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc);
        }
    }

    private String extractDocx(Path path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             XWPFDocument doc = new XWPFDocument(fis);
             XWPFWordExtractor ex = new XWPFWordExtractor(doc)) {
            return ex.getText();
        }
    }

    private String extractDoc(Path path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             HWPFDocument doc = new HWPFDocument(fis);
             WordExtractor ex = new WordExtractor(doc)) {
            return ex.getText();
        }
    }
}
