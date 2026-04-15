package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.demo.dto.QwenRequest;
import com.example.demo.dto.QwenResponse;
import com.example.demo.entity.MockInterview;
import com.example.demo.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QwenService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String apiUrl;

    @Value("${dashscope.app.ai-interviewer.url:}")
    private String aiInterviewerAppUrl;

    @Value("${dashscope.app.interview-evaluator.url:}")
    private String interviewEvaluatorAppUrl;

    @Value("${dashscope.app.resume-optimizer.url:}")
    private String resumeOptimizerAppUrl;

    @Value("${dashscope.app.practice-tutor.url:}")
    private String practiceTutorAppUrl;

    @Value("${dashscope.app.wrongbook-analyst.url:}")
    private String wrongbookAnalystAppUrl;

    @Value("${dashscope.app.growth-analyst.url:}")
    private String growthAnalystAppUrl;

    @Value("${dashscope.app.post-match.url:}")
    private String postMatchAppUrl;

    /** 学习资源/题库：补参考答案专用百炼应用（completion URL）；空则走面试官链路与兼容模式 */
    @Value("${dashscope.app.question-bank.url:}")
    private String questionBankAppUrl;

    /** 知识库：题目/正文归类到 category_id（百炼 completion；空则仅用兼容模式 + 服务端 Key） */
    @Value("${dashscope.app.knowledge-classifier.url:}")
    private String knowledgeClassifierAppUrl;

    private final RestTemplate restTemplate = buildRestTemplate();

    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 防止外部模型接口长时间阻塞，导致前端“请求失败”
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(25000);
        return new RestTemplate(factory);
    }

    /**
     * 模拟面试出题/追问：将「ai面试官」完整系统指令 + 本轮任务一次性交给智能体（或兼容模式 qwen-turbo）。
     */
    public String generateInterviewQuestion(
            MockInterview interview,
            Post post,
            String questionTypeOrModule,
            String previousRoundAnswerSummary,
            boolean followUp,
            String lastQuestion,
            String lastUserAnswer) {
        String instructions = InterviewInterviewerPromptBuilder.buildInstructions(interview, post);
        int inputType = interview.getInputType() == null ? 2 : interview.getInputType();
        String qt = questionTypeOrModule == null ? "技术基础" : questionTypeOrModule;
        StringBuilder task = new StringBuilder();
        if (followUp) {
            task.append("上轮面试官题目：\n")
                    .append(safe(lastQuestion))
                    .append("\n\n候选人回答：\n")
                    .append(safe(lastUserAnswer))
                    .append("\n\n请生成一条追问，只输出追问正文。");
        } else {
            task.append("当前为面试的第一道题。请生成一道「").append(qt).append("」方向的面试题，只输出题目正文。");
            if (previousRoundAnswerSummary != null && !previousRoundAnswerSummary.isBlank()) {
                task.append("\n可参考上下文：").append(previousRoundAnswerSummary);
            }
        }
        if (inputType == 1) {
            task.append("\n\n【输出格式】语音面试模式：纯口语短句，无 Markdown、无序号、无特殊符号堆砌，适合实时语音播报。");
        } else {
            task.append("\n\n【输出格式】文本面试模式：表述清晰、重点明确，可使用适当标点，不要大段套话。");
        }
        String full = instructions + "\n\n---\n【本轮指令】\n" + task;
        return callInterviewer(full);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /** 兼容旧调用：练习/其它场景仍可用简化提示词 */
    public String generateQuestion(String postName, String questionType, String previousAnswer) {
        String prompt = buildQuestionPrompt(postName, questionType, previousAnswer);
        return callInterviewer(prompt);
    }

    public String evaluateAnswer(String question, String answer, String standardAnswer) {
        String prompt = buildEvaluatePrompt(question, answer, standardAnswer);
        return callEvaluator(prompt);
    }

    public String generateFollowUp(String question, String answer, String postName) {
        String prompt = buildFollowUpPrompt(question, answer, postName);
        return callInterviewer(prompt);
    }

    /**
     * 生成单题参考回答（报告展示用）
     */
    public String generateReferenceAnswer(String question, String postName) {
        return callInterviewer(buildReferenceAnswerPrompt(question, postName));
    }

    /**
     * 学习资源/题库：补参考答案；优先 questionBankAppUrl 百炼应用（服务端带 API Key），避免无 Key 直连报错。
     */
    public String generateReferenceAnswerForQuestionBank(String question, String postName) {
        String prompt = buildReferenceAnswerPrompt(question, postName);
        String text = callAppWithFallback(questionBankAppUrl, prompt);
        text = sanitizeAnswerText(text);
        // 若百炼应用返回了“分类 JSON”等非回答内容，则回退到兼容模式生成真正回答
        if (text == null || text.isBlank() || looksLikeCategoryJson(text)) {
            String fallback = callQwenChatCompletions(prompt);
            fallback = sanitizeAnswerText(fallback);
            return fallback;
        }
        return text;
    }

    private static String sanitizeAnswerText(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isBlank()) return t;
        // 去掉 ```json fences
        if (t.startsWith("```")) {
            int nl = t.indexOf('\n');
            if (nl > 0) t = t.substring(nl + 1);
            int end = t.lastIndexOf("```");
            if (end > 0) t = t.substring(0, end).trim();
        }
        return t;
    }

    /** 防止把分类器输出当成“答案”写入题库 */
    private static boolean looksLikeCategoryJson(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isBlank()) return false;
        if (!(t.startsWith("{") && t.endsWith("}"))) return false;
        String lower = t.toLowerCase();
        return lower.contains("category_id") || lower.contains("categoryid") || lower.contains("category_name");
    }

    /**
     * 知识库分类：服务端请求百炼应用（Bearer 使用 qwen.api.key），解析返回 JSON 中的 category_id。
     * 约定：1=专业基础 2=Web前端 3=Java后端 4=大模型算法。解析失败返回 null，由业务层按岗位兜底。
     */
    public Long classifyKnowledgeCategory(String knowledgeName, String knowledgeContent, String postNameHint) {
        String prompt = buildKnowledgeClassificationPrompt(knowledgeName, knowledgeContent, postNameHint);
        String raw = callAppWithFallback(knowledgeClassifierAppUrl, prompt);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return parseKnowledgeCategoryId(raw);
    }

    private static String buildKnowledgeClassificationPrompt(String knowledgeName, String knowledgeContent, String postNameHint) {
        StringBuilder p = new StringBuilder();
        p.append("你是技术知识库分类器。请根据「名称」和「正文」判断所属大类，只输出一个 JSON 对象，不要 Markdown、不要解释文字。\n");
        p.append("JSON 格式严格为：{\"category_id\":整数}\n");
        p.append("整数只能取 1～4：1=专业基础（操作系统/网络/组成原理/数据结构/编译/Linux 等计算机基础）");
        p.append(" 2=Web前端（HTML/CSS/JS/Vue/React/Node/工程化等）");
        p.append(" 3=Java后端（Java/Spring/MyBatis/中间件/JVM 等后端）");
        p.append(" 4=大模型算法（LLM/RAG/Agent/Prompt/微调/向量库等）\n");
        p.append("若同时涉及多类，按「最主要考查方向」选一个。\n");
        if (postNameHint != null && !postNameHint.isBlank()) {
            p.append("候选人/条目关联岗位名称（仅供参考）：").append(postNameHint).append("\n");
        }
        p.append("名称：").append(knowledgeName == null ? "" : knowledgeName).append("\n");
        p.append("正文：").append(knowledgeContent == null ? "" : knowledgeContent);
        return p.toString();
    }

    static Long parseKnowledgeCategoryId(String raw) {
        if (raw == null) return null;
        String t = stripJsonFences(raw.trim());
        Long fromObj = tryParseCategoryFromJson(t);
        if (fromObj != null) return fromObj;
        int i = t.indexOf('{');
        int j = t.lastIndexOf('}');
        if (i >= 0 && j > i) {
            return tryParseCategoryFromJson(t.substring(i, j + 1));
        }
        return null;
    }

    private static String stripJsonFences(String s) {
        String x = s;
        if (x.startsWith("```")) {
            int nl = x.indexOf('\n');
            if (nl > 0) x = x.substring(nl + 1);
            int end = x.lastIndexOf("```");
            if (end > 0) x = x.substring(0, end).trim();
        }
        return x;
    }

    private static Long tryParseCategoryFromJson(String json) {
        try {
            JSONObject obj = JSON.parseObject(json);
            if (obj == null) return null;
            long id = obj.getLongValue("category_id");
            if (obj.containsKey("category_id") && id >= 1 && id <= 4) {
                return id;
            }
            if (obj.containsKey("categoryId")) {
                long id2 = obj.getLongValue("categoryId");
                if (id2 >= 1 && id2 <= 4) return id2;
            }
        } catch (Exception ignored) {
            // fall through
        }
        return null;
    }

    private static String buildReferenceAnswerPrompt(String question, String postName) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是资深").append(postName == null ? "技术" : postName).append("岗位面试官。\n");
        prompt.append("请针对下面这道面试题，给出“可参考的高质量回答”。\n\n");
        prompt.append("题目：").append(question == null ? "" : question).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 回答结构清晰，先结论后细节；\n");
        prompt.append("2. 尽量体现工程实践与取舍；\n");
        prompt.append("3. 控制在 200-400 字；\n");
        prompt.append("4. 仅输出参考回答正文，不要再复述题目。\n");
        return prompt.toString();
    }

    /**
     * 简历诊断：交给「简历优化助手」智能体输出诊断 JSON/文本
     */
    public String diagnoseResume(String prompt) {
        return callAppWithFallback(resumeOptimizerAppUrl, prompt);
    }

    /**
     * 简历优化：交给「简历优化助手」智能体输出优化后的简历内容
     */
    public String optimizeResume(String prompt) {
        return callAppWithFallback(resumeOptimizerAppUrl, prompt);
    }

    /**
     * 根据用户填写的目标岗位描述，归纳该岗位应具备的核心能力
     */
    public String analyzeJobAbilities(String jobDescription) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是资深招聘与岗位分析顾问。\n");
        prompt.append("请根据用户提供的「目标岗位描述」，归纳该岗位候选人应具备的核心能力、知识与技能要点。\n\n");
        prompt.append("目标岗位描述：\n").append(jobDescription).append("\n\n");
        prompt.append("输出要求：\n");
        prompt.append("1. 用简洁中文分条列出，5-12 条，每条一行，以「-」或「1.」开头均可；\n");
        prompt.append("2. 侧重可面试、可评估的能力项；\n");
        prompt.append("3. 不要开场白与结束语，直接输出列表。\n");
        return callAppWithFallback(resumeOptimizerAppUrl, prompt.toString());
    }

    /**
     * 从简历全文中抽取结构化 JSON：基础信息、教育、技能、项目、实习等
     */
    public String parseResumeStructure(String resumeFullText) {
        if (resumeFullText == null || resumeFullText.isBlank()) {
            return "{}";
        }
        String t = resumeFullText.length() > 24000 ? resumeFullText.substring(0, 24000) : resumeFullText;
        StringBuilder p = new StringBuilder();
        p.append("你是简历信息抽取助手。请阅读下列简历全文，抽取并仅输出一个 JSON 对象（不要 Markdown 代码围栏，不要任何解释文字）。\n");
        p.append("JSON 结构必须为：\n");
        p.append("{\"basicInfo\":{\"name\":\"\",\"phone\":\"\",\"email\":\"\",\"location\":\"\"},");
        p.append("\"education\":[{\"school\":\"\",\"degree\":\"\",\"major\":\"\",\"time\":\"\"}],");
        p.append("\"skills\":[\"\"],");
        p.append("\"projects\":[{\"name\":\"\",\"role\":\"\",\"desc\":\"\",\"time\":\"\"}],");
        p.append("\"internships\":[{\"company\":\"\",\"role\":\"\",\"desc\":\"\",\"time\":\"\"}],");
        p.append("\"otherNotes\":\"\"}\n");
        p.append("缺失字段用空字符串或空数组。简历全文：\n\n");
        p.append(t);
        return callAppWithFallback(resumeOptimizerAppUrl, p.toString());
    }

    /**
     * 专项练习评测：交给「专项练习导师智能体」输出评测 JSON/文本
     */
    public String evaluatePracticeAnswer(String prompt) {
        return callAppWithFallback(practiceTutorAppUrl, prompt);
    }

    /**
     * 错题分析与复盘：交给「错题分析与复盘智能体」
     */
    public String analyzeWrongQuestion(String prompt) {
        return callAppWithFallback(wrongbookAnalystAppUrl, prompt);
    }

    /**
     * 个性化成长分析：交给「个性化成长分析智能体」
     */
    public String analyzeGrowth(String prompt) {
        return callAppWithFallback(growthAnalystAppUrl, prompt);
    }

    /**
     * 跨岗位匹配推荐：交给「跨岗位匹配推荐智能体」
     */
    public String recommendPostMatch(String prompt) {
        return callAppWithFallback(postMatchAppUrl, prompt);
    }

    private String buildQuestionPrompt(String postName, String questionType, String previousAnswer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的面试官，正在为").append(postName).append("岗位进行面试。\n");
        prompt.append("请生成一道").append(questionType).append("类型的面试题目。\n");
        
        if (previousAnswer != null && !previousAnswer.isEmpty()) {
            prompt.append("基于用户之前的回答：\n").append(previousAnswer).append("\n");
            prompt.append("请生成一道追问题目。\n");
        } else {
            prompt.append("这是面试的第一道题目。\n");
        }
        
        prompt.append("要求：\n");
        prompt.append("1. 题目要符合岗位要求\n");
        prompt.append("2. 难度适中\n");
        prompt.append("3. 直接输出题目内容，不要有多余说明\n");
        
        return prompt.toString();
    }

    private String buildEvaluatePrompt(String question, String answer, String standardAnswer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请评估以下面试回答的质量：\n\n");
        prompt.append("题目：").append(question).append("\n\n");
        prompt.append("用户回答：").append(answer).append("\n\n");
        prompt.append("标准答案参考：").append(standardAnswer).append("\n\n");
        prompt.append("请从以下维度进行评估（每项0-100分）：\n");
        prompt.append("1. 技术正确性\n");
        prompt.append("2. 知识深度\n");
        prompt.append("3. 逻辑严谨性\n");
        prompt.append("4. 表达能力\n");
        prompt.append("5. 岗位匹配度\n\n");
        prompt.append("请以JSON格式返回评估结果，包含：\n");
        prompt.append("{\n");
        prompt.append("  \"tech_score\": 分数,\n");
        prompt.append("  \"knowledge_depth_score\": 分数,\n");
        prompt.append("  \"logic_score\": 分数,\n");
        prompt.append("  \"express_score\": 分数,\n");
        prompt.append("  \"match_score\": 分数,\n");
        prompt.append("  \"total_score\": 总分,\n");
        prompt.append("  \"bright_point\": \"亮点总结\",\n");
        prompt.append("  \"problem_analysis\": \"问题分析\",\n");
        prompt.append("  \"suggest_guide\": \"改进建议\"\n");
        prompt.append("}");
        
        return prompt.toString();
    }

    private String buildFollowUpPrompt(String question, String answer, String postName) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为").append(postName).append("岗位的面试官，\n");
        prompt.append("针对用户的回答生成一个追问。\n\n");
        prompt.append("原题目：").append(question).append("\n\n");
        prompt.append("用户回答：").append(answer).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 追问要针对回答中的不足或可以深入探讨的点\n");
        prompt.append("2. 追问要简洁明确\n");
        prompt.append("3. 直接输出追问内容\n");
        
        return prompt.toString();
    }

    private String callInterviewer(String prompt) {
        // 优先走 DashScope 应用（ai 面试官），失败则回退到兼容模式 chat/completions
        String text = callDashScopeAppCompletion(aiInterviewerAppUrl, prompt);
        if (text != null && !text.isBlank()) return text;
        return callQwenChatCompletions(prompt);
    }

    private String callEvaluator(String prompt) {
        // 优先走 DashScope 应用（面试评估与能力分析官），失败则回退到兼容模式 chat/completions
        String text = callDashScopeAppCompletion(interviewEvaluatorAppUrl, prompt);
        if (text != null && !text.isBlank()) return text;
        return callQwenChatCompletions(prompt);
    }

    private String callAppWithFallback(String appCompletionUrl, String prompt) {
        String text = callDashScopeAppCompletion(appCompletionUrl, prompt);
        if (text != null && !text.isBlank()) return text;
        return callQwenChatCompletions(prompt);
    }

    /**
     * DashScope 应用（智能体）completion 接口：
     * POST https://dashscope.aliyuncs.com/api/v1/apps/{app_id}/completion
     * Body: { "input": { "prompt": "..." }, "parameters": {}, "debug": {} }
     * Resp: { "output": { "text": "...", "session_id": "..." }, "request_id": "..." ... }
     */
    private String callDashScopeAppCompletion(String appCompletionUrl, String prompt) {
        if (appCompletionUrl == null || appCompletionUrl.isBlank()) return null;
        try {
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);
            Map<String, Object> body = new HashMap<>();
            body.put("input", input);
            body.put("parameters", new HashMap<>());
            body.put("debug", new HashMap<>());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(appCompletionUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isBlank()) {
                JSONObject json = JSON.parseObject(response.getBody());
                JSONObject output = json.getJSONObject("output");
                if (output != null) {
                    String text = output.getString("text");
                    if (text != null) return text;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("调用 DashScope 应用 completion 失败", e);
            return null;
        }
    }

    private String callQwenChatCompletions(String prompt) {
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
            log.error("调用千问兼容模式 API 失败", e);
            return null;
        }
    }
}