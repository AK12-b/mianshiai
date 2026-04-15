package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 将优化后的简历正文导出为 A4 PDF（嵌入中文字体，便于高清打印）。
 */
@Slf4j
public final class ResumePdfExporter {

    private ResumePdfExporter() {}

    public static void writeTextAsPdf(String utf8Text, Path outputPath) throws IOException {
        String text = utf8Text == null ? "" : utf8Text.replace("\r\n", "\n");
        Files.createDirectories(outputPath.getParent());

        try (PDDocument document = new PDDocument()) {
            PDType0Font font = loadChineseFont(document);
            float fontSize = 11f;
            float margin = 48f;
            float pageWidth = PDRectangle.A4.getWidth();
            float pageHeight = PDRectangle.A4.getHeight();
            float maxWidth = pageWidth - 2 * margin;
            float lineHeight = fontSize * 1.38f;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(document, page);
            float y = pageHeight - margin;

            String[] paragraphs = text.split("\n", -1);
            for (String para : paragraphs) {
                List<String> lines = wrapLines(para, font, fontSize, maxWidth);
                for (String line : lines) {
                    if (y < margin + lineHeight) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        cs = new PDPageContentStream(document, page);
                        y = pageHeight - margin;
                    }
                    cs.beginText();
                    cs.setFont(font, fontSize);
                    cs.newLineAtOffset(margin, y);
                    cs.showText(line.isEmpty() ? " " : line);
                    cs.endText();
                    y -= lineHeight;
                }
                y -= lineHeight * 0.25f;
            }
            cs.close();
            document.save(outputPath.toFile());
        }
    }

    /**
     * 将「二、优化后完整简历」Markdown 渲染为 A4 PDF：二级标题略大、三级标题略强调、表格行略小字号，白底黑字。
     */
    public static void writeMarkdownResumePdf(String md, Path outputPath) throws IOException {
        String text = md == null ? "" : md.replace("\r\n", "\n");
        Files.createDirectories(outputPath.getParent());

        try (PDDocument document = new PDDocument()) {
            PDType0Font font = loadChineseFont(document);
            float margin = 48f;
            float pageWidth = PDRectangle.A4.getWidth();
            float pageHeight = PDRectangle.A4.getHeight();
            float maxWidth = pageWidth - 2 * margin;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(document, page);
            float y = pageHeight - margin;

            String[] lines = text.split("\n", -1);
            for (String line : lines) {
                String trim = line.trim();
                if (trim.isEmpty()) {
                    y -= 6f;
                    continue;
                }
                if (trim.matches("^[-_*]{3,}$")) {
                    y -= 10f;
                    continue;
                }

                float fontSize = 11f;
                String draw = trim;
                int hashes = countMarkdownHeadingLevel(trim);
                if (hashes > 0) {
                    draw = trim.substring(hashes).replaceFirst("^\\s+", "");
                    if (hashes == 2) {
                        fontSize = 13f;
                    } else if (hashes == 3) {
                        fontSize = 12f;
                    } else {
                        fontSize = 11.5f;
                    }
                } else if (trim.startsWith("|") && !trim.contains("---")) {
                    fontSize = 10f;
                }
                draw = draw.replace("**", "");

                float lineHeight = fontSize * 1.38f;
                List<String> wrapped = wrapLines(draw, font, fontSize, maxWidth);
                for (String wl : wrapped) {
                    if (y < margin + lineHeight) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        cs = new PDPageContentStream(document, page);
                        y = pageHeight - margin;
                    }
                    cs.beginText();
                    cs.setFont(font, fontSize);
                    cs.newLineAtOffset(margin, y);
                    cs.showText(wl.isEmpty() ? " " : wl);
                    cs.endText();
                    y -= lineHeight;
                }
                y -= fontSize * 0.22f;
            }
            cs.close();
            document.save(outputPath.toFile());
        }
    }

    /** 行首 # 数量（1–6），且其后为空格，否则返回 0 */
    private static int countMarkdownHeadingLevel(String s) {
        int n = 0;
        while (n < s.length() && n < 6 && s.charAt(n) == '#') {
            n++;
        }
        if (n == 0) {
            return 0;
        }
        if (n < s.length() && s.charAt(n) == ' ') {
            return n;
        }
        return 0;
    }

    private static List<String> wrapLines(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String trial = cur.toString() + c;
            float w = font.getStringWidth(trial) / 1000f * fontSize;
            if (w > maxWidth && !cur.isEmpty()) {
                lines.add(cur.toString());
                cur = new StringBuilder(String.valueOf(c));
            } else {
                cur.append(c);
            }
        }
        if (!cur.isEmpty()) {
            lines.add(cur.toString());
        }
        return lines;
    }

    /**
     * 优先使用系统常见中文字体；均不可用时抛出异常，提示用户配置字体。
     */
    private static PDType0Font loadChineseFont(PDDocument doc) throws IOException {
        String windir = System.getenv("WINDIR");
        String[] candidates = new String[] {
            windir != null ? windir + "\\Fonts\\simhei.ttf" : null,
            windir != null ? windir + "\\Fonts\\msyh.ttc" : null,
            "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",
            "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc"
        };
        for (String p : candidates) {
            if (p == null) continue;
            Path path = Paths.get(p);
            if (Files.isRegularFile(path)) {
                try {
                    return PDType0Font.load(doc, path.toFile());
                } catch (IOException ex) {
                    log.debug("加载字体失败 {}: {}", p, ex.getMessage());
                }
            }
        }
        throw new IOException(
            "未找到可用的中文字体（已尝试 Windows 黑体/宋体/雅黑及常见 Linux 路径）。"
                + "请在系统中安装 SimHei / 微软雅黑，或设置环境变量 WINDIR 指向 Windows 目录。"
        );
    }
}
