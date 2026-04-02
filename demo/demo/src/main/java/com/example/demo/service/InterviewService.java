package com.example.demo.service;

import com.example.demo.dto.AnswerRequest;
import com.example.demo.dto.InterviewConfigRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewDialogRepository interviewDialogRepository;
    private final InterviewEvaluateRepository interviewEvaluateRepository;
    private final PostRepository postRepository;
    private final QwenService qwenService;

    @Transactional
    public MockInterview createInterview(Long userId, InterviewConfigRequest config) {
        MockInterview interview = new MockInterview();
        interview.setUserId(userId);
        interview.setPostId(config.getPostId());
        interview.setInterviewMode(config.getInterviewMode());
        interview.setInterviewModule(config.getInterviewModule());
        interview.setAiCharacter(config.getAiCharacter());
        interview.setAiGender(config.getAiGender());
        interview.setInterviewDuration(config.getInterviewDuration());
        interview.setSingleQuestionTimeLimit(120);
        interview.setRemindBeforeTimeout(10);
        interview.setInputType(config.getInputType());
        interview.setStartTime(LocalDateTime.now());
        interview.setInterviewStatus(1);
        interview.setIsTimeout(0);
        interview.setIsDelete(0);

        return mockInterviewRepository.save(interview);
    }

    @Transactional
    public InterviewDialog generateFirstQuestion(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }

        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        String questionType = "技术基础";
        if (interview.getInterviewMode() == 2) {
            questionType = interview.getInterviewModule();
        }

        String questionContent = qwenService.generateQuestion(post.getPostName(), questionType, null);

        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(interviewId);
        dialog.setSpeaker(1);
        dialog.setContentText(questionContent);
        dialog.setDialogueRound(1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    @Transactional
    public InterviewDialog generateFollowUpQuestion(Long interviewId, String userAnswer) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }

        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        if (dialogs.isEmpty()) {
            return null;
        }

        InterviewDialog lastQuestion = dialogs.get(dialogs.size() - 1);
        String followUp = qwenService.generateFollowUp(lastQuestion.getContentText(), userAnswer, post.getPostName());

        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(interviewId);
        dialog.setSpeaker(1);
        dialog.setContentText(followUp);
        dialog.setDialogueRound(dialogs.size() + 1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    @Transactional
    public InterviewDialog submitAnswer(AnswerRequest request) {
        InterviewDialog dialog = new InterviewDialog();
        dialog.setInterviewId(request.getInterviewId());
        dialog.setSpeaker(2);
        dialog.setContentText(request.getAnswerText());
        dialog.setQuestionId(request.getQuestionId());
        dialog.setVoiceUrl(request.getVoiceUrl());
        dialog.setVoiceDuration(request.getVoiceDuration());

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(request.getInterviewId(), 0);
        dialog.setDialogueRound(dialogs.size() + 1);
        dialog.setSingleQuestionTimeout(0);
        dialog.setCreateTime(LocalDateTime.now());
        dialog.setIsDelete(0);

        return interviewDialogRepository.save(dialog);
    }

    @Transactional
    public InterviewEvaluate evaluateInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview == null) {
            return null;
        }

        List<InterviewDialog> dialogs = interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
        if (dialogs.isEmpty()) {
            return null;
        }

        Post post = postRepository.findById(interview.getPostId()).orElse(null);
        if (post == null) {
            return null;
        }

        StringBuilder allAnswers = new StringBuilder();
        for (InterviewDialog dialog : dialogs) {
            if (dialog.getSpeaker() == 2) {
                allAnswers.append(dialog.getContentText()).append("\n");
            }
        }

        String evaluation = qwenService.evaluateAnswer("面试综合评估", allAnswers.toString(), "标准答案");

        InterviewEvaluate evaluate = new InterviewEvaluate();
        evaluate.setInterviewId(interviewId);
        evaluate.setPostId(interview.getPostId());
        
        if (evaluation != null && !evaluation.isEmpty()) {
            evaluate.setTotalScore(new BigDecimal("75.00"));
            evaluate.setTechScore(new BigDecimal("75.00"));
            evaluate.setKnowledgeDepthScore(new BigDecimal("75.00"));
            evaluate.setLogicScore(new BigDecimal("75.00"));
            evaluate.setExpressScore(new BigDecimal("75.00"));
            evaluate.setMatchScore(new BigDecimal("75.00"));
            evaluate.setBrightPoint("表现良好");
            evaluate.setProblemAnalysis("需要加强基础知识");
            evaluate.setGapAnalysis("与岗位要求有一定差距");
            evaluate.setSuggestGuide("建议多练习基础题目");
            evaluate.setTechAnalysis("技术基础扎实");
            evaluate.setKnowledgeDepthAnalysis("知识深度有待提高");
            evaluate.setLogicAnalysis("逻辑清晰");
            evaluate.setExpressAnalysis("表达流畅");
            evaluate.setMatchAnalysis("岗位匹配度良好");
        } else {
            evaluate.setTotalScore(new BigDecimal("60.00"));
            evaluate.setTechScore(new BigDecimal("60.00"));
            evaluate.setKnowledgeDepthScore(new BigDecimal("60.00"));
            evaluate.setLogicScore(new BigDecimal("60.00"));
            evaluate.setExpressScore(new BigDecimal("60.00"));
            evaluate.setMatchScore(new BigDecimal("60.00"));
            evaluate.setBrightPoint("表现一般");
            evaluate.setProblemAnalysis("基础知识薄弱");
            evaluate.setGapAnalysis("与岗位要求差距较大");
            evaluate.setSuggestGuide("建议系统学习基础知识");
            evaluate.setTechAnalysis("技术基础有待加强");
            evaluate.setKnowledgeDepthAnalysis("知识深度不足");
            evaluate.setLogicAnalysis("逻辑需要更清晰");
            evaluate.setExpressAnalysis("表达需要更流畅");
            evaluate.setMatchAnalysis("岗位匹配度一般");
        }
        
        evaluate.setCreateTime(LocalDateTime.now());
        evaluate.setIsDelete(0);

        return interviewEvaluateRepository.save(evaluate);
    }

    public Optional<MockInterview> getInterviewById(Long interviewId) {
        return mockInterviewRepository.findById(interviewId);
    }

    @Transactional
    public MockInterview endInterview(Long interviewId) {
        return endInterview(interviewId, false);
    }

    @Transactional
    public MockInterview endInterview(Long interviewId, boolean timeout) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview != null) {
            interview.setEndTime(LocalDateTime.now());
            interview.setInterviewStatus(2);
            if (timeout) {
                interview.setIsTimeout(1);
            }
            return mockInterviewRepository.save(interview);
        }
        return null;
    }

    @Transactional
    public MockInterview pauseInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview != null) {
            interview.setInterviewStatus(4);
            return mockInterviewRepository.save(interview);
        }
        return null;
    }

    @Transactional
    public MockInterview terminateInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId).orElse(null);
        if (interview != null) {
            interview.setEndTime(LocalDateTime.now());
            interview.setInterviewStatus(3);
            return mockInterviewRepository.save(interview);
        }
        return null;
    }

    public List<MockInterview> getUserInterviews(Long userId) {
        return mockInterviewRepository.findByUserIdAndIsDeleteOrderByStartTimeDesc(userId, 0);
    }

    public List<MockInterview> getUserInterviewsByPostId(Long userId, Long postId) {
        return mockInterviewRepository.findByUserIdAndPostIdAndIsDeleteOrderByStartTimeDesc(userId, postId, 0);
    }

    public List<MockInterview> getUserInterviewsByMode(Long userId, Integer interviewMode) {
        return mockInterviewRepository.findByUserIdAndInterviewModeAndIsDeleteOrderByStartTimeDesc(userId, interviewMode, 0);
    }

    public List<MockInterview> getUserInterviewsByStatus(Long userId, Integer status) {
        return mockInterviewRepository.findByUserIdAndInterviewStatusAndIsDelete(userId, status, 0);
    }

    public List<InterviewDialog> getInterviewDialogs(Long interviewId) {
        return interviewDialogRepository.findByInterviewIdAndIsDeleteOrderByDialogueRoundAsc(interviewId, 0);
    }

    public Optional<InterviewEvaluate> getInterviewEvaluate(Long interviewId) {
        return interviewEvaluateRepository.findByInterviewIdAndIsDelete(interviewId, 0);
    }
}