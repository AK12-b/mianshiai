package com.example.demo.service;

import com.example.demo.entity.InterviewWrongQuestion;
import com.example.demo.repository.InterviewWrongQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongQuestionService {

    private final InterviewWrongQuestionRepository interviewWrongQuestionRepository;

    @Transactional
    public InterviewWrongQuestion addWrongQuestion(Long userId, Long questionId, Long interviewId, 
                                              Long practiceId, Integer wrongReason, String knowledgePoint) {
        InterviewWrongQuestion wrongQuestion = new InterviewWrongQuestion();
        wrongQuestion.setUserId(userId);
        wrongQuestion.setQuestionId(questionId);
        wrongQuestion.setInterviewId(interviewId);
        wrongQuestion.setPracticeId(practiceId);
        wrongQuestion.setWrongReason(wrongReason);
        wrongQuestion.setKnowledgePoint(knowledgePoint);
        wrongQuestion.setIsCollect(0);
        wrongQuestion.setCreateTime(LocalDateTime.now());
        wrongQuestion.setIsDelete(0);

        return interviewWrongQuestionRepository.save(wrongQuestion);
    }

    @Transactional
    public InterviewWrongQuestion toggleCollect(Long wrongId) {
        InterviewWrongQuestion wrongQuestion = interviewWrongQuestionRepository.findById(wrongId).orElse(null);
        if (wrongQuestion != null) {
            wrongQuestion.setIsCollect(wrongQuestion.getIsCollect() == 0 ? 1 : 0);
            return interviewWrongQuestionRepository.save(wrongQuestion);
        }
        return null;
    }

    @Transactional
    public boolean deleteWrongQuestion(Long wrongId) {
        InterviewWrongQuestion wrongQuestion = interviewWrongQuestionRepository.findById(wrongId).orElse(null);
        if (wrongQuestion != null) {
            wrongQuestion.setIsDelete(1);
            interviewWrongQuestionRepository.save(wrongQuestion);
            return true;
        }
        return false;
    }

    public List<InterviewWrongQuestion> getUserWrongQuestions(Long userId) {
        return interviewWrongQuestionRepository.findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0);
    }

    public List<InterviewWrongQuestion> getUserCollectedQuestions(Long userId) {
        return interviewWrongQuestionRepository.findByUserIdAndIsCollectAndIsDelete(userId, 1, 0);
    }
}