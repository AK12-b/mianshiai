package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Post;
import com.example.demo.service.PostRecommendService;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.QwenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/post-match")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostMatchController {

    private final QwenService qwenService;
    private final PostRepository postRepository;
    private final PostRecommendService postRecommendService;

    /**
     * 新版：按《页面详细规划.md》“页面10：跨岗位智能推荐”返回结构化结果
     * - 左侧：能力画像（标签 + 三项分数 + 画像总结）
     * - 右侧：岗位推荐列表（支持筛选/重新推荐）
     */
    @PostMapping("/generate")
    public ApiResponse<Map<String, Object>> generate(@RequestBody Map<String, Object> request) {
        Long userId = request.get("userId") == null ? null : Long.valueOf(String.valueOf(request.get("userId")));
        Long originalPostId = request.get("originalPostId") == null ? null : Long.valueOf(String.valueOf(request.get("originalPostId")));
        String profileText = request.get("profileText") == null ? "" : String.valueOf(request.get("profileText"));
        String filters = request.get("filters") == null ? "" : String.valueOf(request.get("filters"));

        if (userId == null || userId <= 0) return ApiResponse.error("userId 不能为空");
        if (originalPostId == null || originalPostId <= 0) return ApiResponse.error("originalPostId 不能为空");
        if (profileText.isBlank()) return ApiResponse.error("请填写个人背景/技能信息");

        Post originalPost = postRepository.findById(originalPostId).orElse(null);
        String originalPostName = originalPost == null ? String.valueOf(originalPostId) : originalPost.getPostName();

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名资深职业发展顾问与技术面试官，擅长跨岗位评估与转岗路线规划。\n");
        prompt.append("请基于候选人信息，生成“能力画像 + 跨岗位推荐列表”。候选人当前方向为【").append(originalPostName).append("】。\n");
        if (!filters.isBlank()) {
            prompt.append("筛选偏好：").append(filters).append("\n");
        }
        prompt.append("\n候选人信息：\n").append(profileText).append("\n\n");
        prompt.append("请严格以 JSON 输出（不要 Markdown 代码块），字段如下：\n");
        prompt.append("{\n");
        prompt.append("  \"profile\": {\n");
        prompt.append("    \"base_tags\": [\"学历/年级\", \"意向岗位\", \"核心技能(含熟练度)\"],\n");
        prompt.append("    \"scores\": {\n");
        prompt.append("      \"skill_match\": 0,\n");
        prompt.append("      \"project_fit\": 0,\n");
        prompt.append("      \"interview_fit\": 0\n");
        prompt.append("    },\n");
        prompt.append("    \"summary\": \"画像总结\"\n");
        prompt.append("  },\n");
        prompt.append("  \"recommends\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"post_name\": \"岗位名称\",\n");
        prompt.append("      \"match_score\": 0,\n");
        prompt.append("      \"core_skills\": [\"技能1\", \"技能2\"],\n");
        prompt.append("      \"match_highlight\": \"匹配亮点\",\n");
        prompt.append("      \"ability_gap\": \"能力缺口\",\n");
        prompt.append("      \"industry_prospect\": \"行业前景简介\",\n");
        prompt.append("      \"improve_actions\": [\"2-4周行动1\", \"2-4周行动2\", \"1-3个月行动1\"]\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("规则：\n");
        prompt.append("1) profile.base_tags 需体现候选人「技能标签、项目经历、面试表现、练习/补强方向」等综合特征，每条 4-12 字，如「Java基础扎实」「分布式技术入门」「项目经验丰富」；\n");
        prompt.append("2) scores 三项分数 0-100，且总体符合 skill:project:interview 约 60:30:10；\n");
        prompt.append("3) recommends 必须按 match_score 从高到低排序，至少给 6 个岗位；\n");
        prompt.append("4) match_highlight 写匹配亮点（如「Java技能覆盖岗位核心要求」），ability_gap 写能力缺口（如「大数据方向需补充Hadoop基础」），industry_prospect 用简短中文；\n");
        prompt.append("5) improve_actions 给出可执行清单。\n");

        String raw = qwenService.recommendPostMatch(prompt.toString());
        if (raw == null || raw.isBlank()) return ApiResponse.error("跨岗位推荐生成失败，请稍后重试");

        JSONObject obj;
        try {
            obj = JSON.parseObject(raw);
        } catch (Exception e) {
            // 兜底：模型未严格输出 JSON，则返回原文，前端仍可展示
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("rawText", raw);
            fallback.put("profile", new HashMap<>());
            fallback.put("recommends", new ArrayList<>());
            return ApiResponse.success(fallback);
        }

        JSONObject profile = obj.getJSONObject("profile");
        JSONArray recommends = sortRecommendsByMatchScoreDesc(obj.getJSONArray("recommends"));

        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile == null ? new HashMap<>() : profile);
        data.put("recommends", recommends == null ? new ArrayList<>() : recommends);
        data.put("rawText", obj.getString("rawText")); // 允许模型补充原文，但不强制
        persistRecommendations(userId, originalPostId, recommends);
        return ApiResponse.success(data);
    }

    private void persistRecommendations(Long userId, Long originalPostId, JSONArray recommends) {
        if (userId == null || originalPostId == null || recommends == null || recommends.isEmpty()) return;
        for (int i = 0; i < recommends.size(); i++) {
            JSONObject r = recommends.getJSONObject(i);
            if (r == null) continue;
            String postName = r.getString("post_name");
            if (postName == null || postName.isBlank()) continue;
            Post post = resolveRecommendPost(postName);
            if (post == null || post.getPostId() == null) continue;

            BigDecimal matchRate = safeScore(r.get("match_score"));
            JSONObject scores = r.getJSONObject("scores");
            BigDecimal skill = scores == null ? matchRate : safeScore(scores.get("skill_match"));
            BigDecimal project = scores == null ? matchRate : safeScore(scores.get("project_fit"));
            BigDecimal interview = scores == null ? matchRate : safeScore(scores.get("interview_fit"));
            String highlight = r.getString("match_highlight");
            String gap = r.getString("ability_gap");
            String prospect = r.getString("industry_prospect");
            try {
                postRecommendService.generateRecommend(
                        userId,
                        originalPostId,
                        post.getPostId(),
                        matchRate,
                        skill,
                        project,
                        interview,
                        highlight,
                        gap,
                        prospect
                );
            } catch (Exception ignore) {
                // 单条持久化失败不影响整体推荐返回
            }
        }
    }

    private BigDecimal safeScore(Object v) {
        if (v == null) return BigDecimal.ZERO;
        try {
            String s = String.valueOf(v).replaceAll("[^0-9.\\-]", "").trim();
            if (s.isBlank()) return BigDecimal.ZERO;
            BigDecimal n = new BigDecimal(s);
            if (n.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
            BigDecimal max = new BigDecimal("100");
            if (n.compareTo(max) > 0) return max;
            return n;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Post resolveRecommendPost(String rawName) {
        if (rawName == null || rawName.isBlank()) return null;
        String name = rawName.trim();
        Post exact = postRepository.findByPostName(name);
        if (exact != null) return exact;

        String target = normalizePostName(name);
        List<Post> posts = postRepository.findByIsDelete(0);
        Post best = null;
        int bestScore = -1;
        for (Post p : posts) {
            if (p == null || p.getPostName() == null || p.getPostName().isBlank()) continue;
            String cur = normalizePostName(p.getPostName());
            int score = 0;
            if (cur.equals(target)) score += 100;
            if (cur.contains(target) || target.contains(cur)) score += 60;
            if (target.contains("java") && cur.contains("java")) score += 30;
            if (target.contains("前端") && cur.contains("前端")) score += 30;
            if (target.contains("大模型") && cur.contains("大模型")) score += 30;
            if (target.contains("算法") && cur.contains("算法")) score += 20;
            if (score > bestScore) {
                bestScore = score;
                best = p;
            }
        }
        return bestScore >= 30 ? best : null;
    }

    private String normalizePostName(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT)
                .replace("工程师", "")
                .replace("开发", "")
                .replace("岗位", "")
                .replace("方向", "")
                .replace(" ", "")
                .trim();
    }

    /** 适配度降序，保证列表与前端展示一致 */
    private static JSONArray sortRecommendsByMatchScoreDesc(JSONArray recommends) {
        if (recommends == null || recommends.isEmpty()) return recommends;
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < recommends.size(); i++) {
            list.add(recommends.getJSONObject(i));
        }
        list.sort(Comparator.comparingDouble((JSONObject o) -> o.getDoubleValue("match_score")).reversed());
        JSONArray out = new JSONArray();
        out.addAll(list);
        return out;
    }

    @PostMapping("/recommend")
    public ApiResponse<String> recommend(@RequestBody Map<String, Object> request) {
        Long userId = request.get("userId") == null ? null : Long.valueOf(String.valueOf(request.get("userId")));
        Long originalPostId = request.get("originalPostId") == null ? null : Long.valueOf(String.valueOf(request.get("originalPostId")));
        Long targetPostId = request.get("targetPostId") == null ? null : Long.valueOf(String.valueOf(request.get("targetPostId")));
        String profileText = request.get("profileText") == null ? "" : String.valueOf(request.get("profileText"));

        if (userId == null || userId <= 0) return ApiResponse.error("userId 不能为空");
        if (originalPostId == null || originalPostId <= 0) return ApiResponse.error("originalPostId 不能为空");
        if (targetPostId == null || targetPostId <= 0) return ApiResponse.error("targetPostId 不能为空");
        if (profileText.isBlank()) return ApiResponse.error("请填写个人背景/技能信息");

        Post originalPost = postRepository.findById(originalPostId).orElse(null);
        Post targetPost = postRepository.findById(targetPostId).orElse(null);
        String originalPostName = originalPost == null ? String.valueOf(originalPostId) : originalPost.getPostName();
        String targetPostName = targetPost == null ? String.valueOf(targetPostId) : targetPost.getPostName();

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名资深职业发展顾问与技术面试官，擅长跨岗位评估与转岗路线规划。\n");
        prompt.append("请根据候选人的信息，评估从【").append(originalPostName).append("】跨岗到【").append(targetPostName).append("】的匹配度，并给出转岗建议。\n\n");
        prompt.append("候选人信息：\n").append(profileText).append("\n\n");
        prompt.append("输出要求：\n");
        prompt.append("1) 先给出结论：是否推荐跨岗（推荐/谨慎/不推荐）+ 0-100 总匹配分；\n");
        prompt.append("2) 分维度打分（技能匹配/项目经历/学习成本/面试通过概率/行业前景），每项 0-100；\n");
        prompt.append("3) 给出 3-6 条优势亮点与 3-6 条主要差距；\n");
        prompt.append("4) 给出 2-4 周、1-3 个月两个阶段的学习与项目补强路线（可执行清单）；\n");
        prompt.append("5) 推荐 3 个更适合的目标岗位方向（含理由与关键关键词）；\n");
        prompt.append("6) 使用中文输出，结构清晰，尽量用小标题与列表。\n");

        String text = qwenService.recommendPostMatch(prompt.toString());
        if (text == null || text.isBlank()) return ApiResponse.error("跨岗位推荐生成失败，请稍后重试");
        return ApiResponse.success(text);
    }
}

