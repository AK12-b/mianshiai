package com.example.demo.service;

import com.example.demo.entity.QuestionBank;
import com.example.demo.repository.QuestionBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;

    @Transactional
    public QuestionBank addQuestion(Long postId, Integer questionType, Integer questionLevel,
                                   String questionTitle, String questionAnswer, String knowledgePoint,
                                   Integer isAiGenerate) {
        QuestionBank question = new QuestionBank();
        question.setPostId(postId);
        question.setQuestionType(questionType);
        question.setQuestionLevel(questionLevel);
        question.setQuestionTitle(questionTitle);
        question.setQuestionAnswer(questionAnswer);
        question.setKnowledgePoint(knowledgePoint);
        question.setIsAiGenerate(isAiGenerate != null ? isAiGenerate : 0);
        question.setCreateTime(LocalDateTime.now());
        question.setIsDelete(0);

        return questionBankRepository.save(question);
    }

    @Transactional
    public QuestionBank updateQuestion(Long questionId, Integer questionType, Integer questionLevel,
                                     String questionTitle, String questionAnswer, String knowledgePoint) {
        QuestionBank question = questionBankRepository.findById(questionId).orElse(null);
        if (question != null) {
            question.setQuestionType(questionType);
            question.setQuestionLevel(questionLevel);
            question.setQuestionTitle(questionTitle);
            question.setQuestionAnswer(questionAnswer);
            question.setKnowledgePoint(knowledgePoint);
            return questionBankRepository.save(question);
        }
        return null;
    }

    @Transactional
    public boolean deleteQuestion(Long questionId) {
        QuestionBank question = questionBankRepository.findById(questionId).orElse(null);
        if (question != null) {
            question.setIsDelete(1);
            questionBankRepository.save(question);
            return true;
        }
        return false;
    }

    public List<QuestionBank> getQuestionsByPostId(Long postId) {
        return questionBankRepository.findByPostIdAndIsDelete(postId, 0);
    }

    public List<QuestionBank> getQuestionsByPostIdAndType(Long postId, Integer questionType) {
        return questionBankRepository.findByPostIdAndQuestionTypeAndIsDelete(postId, questionType, 0);
    }

    public QuestionBank getQuestionById(Long questionId) {
        return questionBankRepository.findById(questionId).orElse(null);
    }

    public List<QuestionBank> getAllQuestions() {
        return questionBankRepository.findByIsDelete(0);
    }
}
