package com.example.demo.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 将数据库中任意 {@code knowledge_point} 映射为学习资源页使用的「规范知识点名」（与前端卡片一致），
 * 避免仅靠库内标签与界面固定文案完全一致才能统计到数字。
 */
public final class KnowledgePointUiMapper {

    private KnowledgePointUiMapper() {}

    private static final List<String> BASE_POINTS = List.of(
            "编译原理", "操作系统", "基础编程练习", "计算机网络", "计算机组成原理",
            "数据结构与算法", "正则表达式", "Linux");

    private static final List<String> FRONTEND_POINTS = List.of(
            "前端工程化", "CSS", "HTML", "JavaScript", "Node.js", "PWA", "React",
            "TypeScript", "jQuery", "Vue", "Web 前端开发");

    private static final List<String> BACKEND_POINTS = List.of(
            "Hibernate", "MyBatis", "Netty", "Maven", "Java", "Spring", "Spring Cloud",
            "Spring MVC", "Tomcat", "Spring Boot");

    private static final List<String> LLM_POINTS = List.of(
            "LLM", "RAG", "Agent", "Prompt", "微调", "向量库");

    private static final String[] FRONTEND_KEYS = {
            "前端", "web", "html", "css", "javascript", "js", "typescript", "ts",
            "react", "vue", "node", "jquery", "pwa", "web前端", "web 前端"
    };

    private static final String[] BACKEND_KEYS = {
            "java", "spring", "spring boot", "spring cloud", "spring mvc",
            "mybatis", "hibernate", "maven", "tomcat", "netty", "后端",
            "jvm", "redis", "mysql", "数据库"
    };

    /** 常见库内写法 → 界面规范名（可按实际数据继续补充） */
    private static final Map<String, String> EXACT_ALIASES = new LinkedHashMap<>();

    static {
        EXACT_ALIASES.put("技术基础", "基础编程练习");
        EXACT_ALIASES.put("计算机基础", "基础编程练习");
        EXACT_ALIASES.put("通识", "基础编程练习");
        EXACT_ALIASES.put("OS", "操作系统");
        EXACT_ALIASES.put("Operating System", "操作系统");
        EXACT_ALIASES.put("operating system", "操作系统");
        EXACT_ALIASES.put("计网", "计算机网络");
        EXACT_ALIASES.put("Computer Network", "计算机网络");
        EXACT_ALIASES.put("computer network", "计算机网络");
        EXACT_ALIASES.put("计组", "计算机组成原理");
        EXACT_ALIASES.put("DSA", "数据结构与算法");
        EXACT_ALIASES.put("Compiler", "编译原理");
        EXACT_ALIASES.put("compiler", "编译原理");
        EXACT_ALIASES.put("Regex", "正则表达式");
        EXACT_ALIASES.put("regex", "正则表达式");
        EXACT_ALIASES.put("linux", "Linux");
        EXACT_ALIASES.put("LINUX", "Linux");
        // 与前端 DISPLAY 一致
        EXACT_ALIASES.put("Web前端开发", "Web 前端开发");
        EXACT_ALIASES.put("web前端开发", "Web 前端开发");
    }

    public static String toUiLabel(String knowledgePoint) {
        if (knowledgePoint == null || knowledgePoint.isBlank()) {
            return "未分类知识点";
        }
        String t = knowledgePoint.trim().replace("\uFEFF", "").replaceAll("\\s+", " ");
        String direct = EXACT_ALIASES.get(t);
        if (direct != null) {
            return direct;
        }
        for (Map.Entry<String, String> e : EXACT_ALIASES.entrySet()) {
            if (e.getKey().equalsIgnoreCase(t)) {
                return e.getValue();
            }
        }

        String display = canonicalDisplay(t);
        String cat = categoryOf(display, t);
        return switch (cat) {
            case "llm" -> pickMatch(display, t, LLM_POINTS, display);
            case "frontend" -> pickMatch(display, t, FRONTEND_POINTS, display);
            case "backend" -> pickMatch(display, t, BACKEND_POINTS, t);
            default -> {
                // 面试/历史数据常见「技术基础」等：归入专业基础下的「基础编程练习」卡片
                if (t.contains("技术基础") && !t.contains("前端") && !t.contains("Web") && !t.contains("web")) {
                    yield "基础编程练习";
                }
                yield pickMatch(display, t, BASE_POINTS, display);
            }
        };
    }

    /** 与前端点击知识点、补答案时一致：同一规范组内任意原始标签视为同一桶 */
    public static boolean sameUiGroup(String rawKnowledgePoint, String clickedKnowledgePoint) {
        if (clickedKnowledgePoint == null || clickedKnowledgePoint.isBlank()) {
            return false;
        }
        String a = toUiLabel(rawKnowledgePoint);
        String b = toUiLabel(clickedKnowledgePoint);
        return a.equals(b);
    }

    private static String canonicalDisplay(String raw) {
        if ("Web前端开发".equals(raw) || "web前端开发".equalsIgnoreCase(raw)) {
            return "Web 前端开发";
        }
        String compact = raw.replaceAll("\\s+", "");
        if ("Web前端开发".equals(compact)) {
            return "Web 前端开发";
        }
        return raw;
    }

    private static String categoryOf(String display, String raw) {
        String lower = display.toLowerCase(Locale.ROOT);
        String rawLower = raw.toLowerCase(Locale.ROOT);
        for (String x : LLM_POINTS) {
            String xl = x.toLowerCase(Locale.ROOT);
            if (lower.contains(xl) || rawLower.contains(xl)) {
                return "llm";
            }
        }
        if (matchAny(lower, FRONTEND_KEYS) || matchAny(rawLower, FRONTEND_KEYS)) {
            return "frontend";
        }
        if (matchAny(lower, BACKEND_KEYS) || matchAny(rawLower, BACKEND_KEYS)) {
            return "backend";
        }
        return "base";
    }

    private static boolean matchAny(String text, String[] keys) {
        for (String k : keys) {
            if (text.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private static String pickMatch(String display, String raw, List<String> points, String fallback) {
        String m = matchCatalogPoint(display, raw, points);
        return m != null ? m : fallback;
    }

    private static String matchCatalogPoint(String display, String raw, List<String> points) {
        String trimmed = raw.trim();
        if (display.equals(trimmed)) {
            for (String x : points) {
                if (x.equals(display) || x.equals(trimmed)) {
                    return x;
                }
            }
        }
        String d = display.toLowerCase(Locale.ROOT);
        String r = trimmed.toLowerCase(Locale.ROOT);
        for (String x : points) {
            if (x.equals(display) || x.equals(trimmed)) {
                return x;
            }
        }
        List<String> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparingInt(String::length).reversed());
        for (String x : sorted) {
            String xl = x.toLowerCase(Locale.ROOT);
            if (d.contains(xl) || r.contains(xl) || xl.contains(d) || xl.contains(r)) {
                return x;
            }
        }
        return null;
    }
}
