package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.QuestionBank;
import com.example.demo.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question-bank")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    /** 字面路径放在含路径变量的映射之前，避免个别环境下与 /{id} 类模式冲突 */
    @GetMapping("/list")
    public ApiResponse<List<QuestionBank>> getAllQuestions() {
        List<QuestionBank> questions = questionBankService.getAllQuestions();
        return ApiResponse.success(questions);
    }

    @PostMapping("/add")
    public ApiResponse<QuestionBank> addQuestion(@RequestBody Map<String, Object> request) {
        Long postId = Long.valueOf(request.get("postId").toString());
        Integer questionType = Integer.valueOf(request.get("questionType").toString());
        Integer questionLevel = Integer.valueOf(request.get("questionLevel").toString());
        String questionTitle = (String) request.get("questionTitle");
        String questionAnswer = (String) request.get("questionAnswer");
        String knowledgePoint = (String) request.get("knowledgePoint");
        Integer isAiGenerate = request.get("isAiGenerate") != null ? 
            Integer.valueOf(request.get("isAiGenerate").toString()) : 0;

        QuestionBank question = questionBankService.addQuestion(postId, questionType, questionLevel, 
            questionTitle, questionAnswer, knowledgePoint, isAiGenerate);
        return ApiResponse.success(question);
    }

    @PutMapping("/{questionId}")
    public ApiResponse<QuestionBank> updateQuestion(@PathVariable Long questionId, @RequestBody Map<String, Object> request) {
        Integer questionType = Integer.valueOf(request.get("questionType").toString());
        Integer questionLevel = Integer.valueOf(request.get("questionLevel").toString());
        String questionTitle = (String) request.get("questionTitle");
        String questionAnswer = (String) request.get("questionAnswer");
        String knowledgePoint = (String) request.get("knowledgePoint");

        QuestionBank question = questionBankService.updateQuestion(questionId, questionType, questionLevel, 
            questionTitle, questionAnswer, knowledgePoint);
        if (question != null) {
            return ApiResponse.success(question);
        }
        return ApiResponse.error("更新失败");
    }

    @DeleteMapping("/{questionId}")
    public ApiResponse<String> deleteQuestion(@PathVariable Long questionId) {
        boolean deleted = questionBankService.deleteQuestion(questionId);
        if (deleted) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<List<QuestionBank>> getQuestionsByPostId(@PathVariable Long postId) {
        List<QuestionBank> questions = questionBankService.getQuestionsByPostId(postId);
        return ApiResponse.success(questions);
    }

    @GetMapping("/post/{postId}/type/{questionType}")
    public ApiResponse<List<QuestionBank>> getQuestionsByPostIdAndType(@PathVariable Long postId, 
                                                                        @PathVariable Integer questionType) {
        List<QuestionBank> questions = questionBankService.getQuestionsByPostIdAndType(postId, questionType);
        return ApiResponse.success(questions);
    }

    /** 使用 /question/{id}，避免与字面路径 /list 在部分环境下的匹配歧义（list 被当成 id 会导致 500） */
    @GetMapping("/question/{questionId}")
    public ApiResponse<QuestionBank> getQuestionById(@PathVariable Long questionId) {
        QuestionBank question = questionBankService.getQuestionById(questionId);
        if (question != null) {
            return ApiResponse.success(question);
        }
        return ApiResponse.error("题目不存在");
    }

    @PostMapping("/ensure-answers")
    public ApiResponse<List<QuestionBank>> ensureAnswersByKnowledgePoint(@RequestBody Map<String, Object> request) {
        String knowledgePoint = request.get("knowledgePoint") == null ? "" : String.valueOf(request.get("knowledgePoint"));
        List<QuestionBank> questions = questionBankService.ensureAnswersByKnowledgePoint(knowledgePoint);
        return ApiResponse.success(questions);
    }
}
