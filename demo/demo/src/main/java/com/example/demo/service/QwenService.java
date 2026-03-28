package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.example.demo.dto.QwenRequest;
import com.example.demo.dto.QwenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QwenService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateQuestion(String postName, String questionType, String previousAnswer) {
        String prompt = buildQuestionPrompt(postName, questionType, previousAnswer);
        return callQwenApi(prompt);
    }

    public String evaluateAnswer(String question, String answer, String standardAnswer) {
        String prompt = buildEvaluatePrompt(question, answer, standardAnswer);
        return callQwenApi(prompt);
    }

    public String generateFollowUp(String question, String answer, String postName) {
        String prompt = buildFollowUpPrompt(question, answer, postName);
        return callQwenApi(prompt);
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
}