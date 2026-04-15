package com.example.demo.service;

import com.example.demo.entity.KnowledgeBase;
import com.example.demo.entity.Post;
import com.example.demo.repository.KnowledgeBaseRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseService {

    /**
     * 知识库大类 ID（与百炼分类智能体约定一致）：
     * 1=专业基础 2=Web前端 3=Java后端 4=大模型算法
     */
    public static final long DEFAULT_KNOWLEDGE_CATEGORY_ID = 1L;

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final PostRepository postRepository;
    private final QwenService qwenService;

    @Transactional
    public KnowledgeBase addKnowledge(Long postId, String knowledgeName, String knowledgeContent, String vectorId) {
        KnowledgeBase knowledge = new KnowledgeBase();
        knowledge.setPostId(postId);
        knowledge.setCategoryId(resolveCategoryId(postId, knowledgeName, knowledgeContent));
        knowledge.setKnowledgeName(knowledgeName);
        knowledge.setKnowledgeContent(knowledgeContent);
        knowledge.setVectorId(vectorId);
        LocalDateTime now = LocalDateTime.now();
        knowledge.setCreateTime(now);
        knowledge.setUpdateTime(now);
        knowledge.setIsDelete(0);

        return knowledgeBaseRepository.save(knowledge);
    }

    /**
     * 面试 AI 出题后沉淀知识库：独立事务，失败不影响面试主事务（避免 insert KB 失败导致整单 rollback-only）。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsertFromInterviewQuestion(Long postId, String questionTypeName, String questionText) {
        try {
            if (postId == null) return;
            String norm = normalizeText(questionText);
            if (norm.isBlank()) return;

            KnowledgeBase existing =
                    knowledgeBaseRepository
                            .findFirstByPostIdAndKnowledgeContentAndIsDelete(postId, questionText, 0)
                            .orElse(null);
            if (existing == null) {
                for (KnowledgeBase k : knowledgeBaseRepository.findByPostIdAndIsDelete(postId, 0)) {
                    if (k == null || k.getKnowledgeContent() == null) continue;
                    if (normalizeText(k.getKnowledgeContent()).equals(norm)) {
                        existing = k;
                        break;
                    }
                }
            }
            if (existing != null) return;

            String name =
                    (questionTypeName == null || questionTypeName.isBlank())
                            ? "面试题沉淀"
                            : (questionTypeName + "面试题沉淀");
            String nameDb = truncateDbVarchar(name, 255);

            KnowledgeBase kb = new KnowledgeBase();
            kb.setPostId(postId);
            kb.setCategoryId(resolveCategoryId(postId, nameDb, questionText));
            kb.setKnowledgeName(nameDb);
            kb.setKnowledgeContent(questionText);
            kb.setVectorId(null);
            LocalDateTime now = LocalDateTime.now();
            kb.setCreateTime(now);
            kb.setUpdateTime(now);
            kb.setIsDelete(0);
            knowledgeBaseRepository.save(kb);
        } catch (Exception e) {
            log.warn("知识库沉淀失败（已忽略，不影响面试） postId={} : {}", postId, e.getMessage());
        }
    }

    private static String normalizeText(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", " ")
                .replace("：", ":")
                .replace("，", ",")
                .replace("。", ".")
                .trim();
    }

    private static String truncateDbVarchar(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    @Transactional
    public KnowledgeBase updateKnowledge(Long knowledgeId, String knowledgeName, String knowledgeContent) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(knowledgeId).orElse(null);
        if (knowledge != null) {
            knowledge.setKnowledgeName(knowledgeName);
            knowledge.setKnowledgeContent(knowledgeContent);
            knowledge.setCategoryId(resolveCategoryId(knowledge.getPostId(), knowledgeName, knowledgeContent));
            knowledge.setUpdateTime(LocalDateTime.now());
            return knowledgeBaseRepository.save(knowledge);
        }
        return null;
    }

    /** 对已存在条目重新调用分类智能体并写回 category_id */
    @Transactional
    public KnowledgeBase reclassifyKnowledge(Long knowledgeId) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(knowledgeId).orElse(null);
        if (knowledge == null) {
            return null;
        }
        knowledge.setCategoryId(
                resolveCategoryId(
                        knowledge.getPostId(),
                        knowledge.getKnowledgeName(),
                        knowledge.getKnowledgeContent()));
        knowledge.setUpdateTime(LocalDateTime.now());
        return knowledgeBaseRepository.save(knowledge);
    }

    /**
     * 优先百炼分类（服务端带 Key）；失败或未配置 URL 时按岗位名称兜底。
     */
    private long resolveCategoryId(Long postId, String knowledgeName, String knowledgeContent) {
        if (postId == null) {
            return DEFAULT_KNOWLEDGE_CATEGORY_ID;
        }
        String postName = postRepository.findById(postId).map(Post::getPostName).orElse("");
        try {
            Long ai = qwenService.classifyKnowledgeCategory(knowledgeName, knowledgeContent, postName);
            if (ai != null && ai >= 1 && ai <= 4) {
                return ai;
            }
        } catch (Exception e) {
            log.warn("知识库智能分类失败，使用岗位兜底 postId={} : {}", postId, e.getMessage());
        }
        return inferCategoryFromPostName(postName);
    }

    private static long inferCategoryFromPostName(String postName) {
        if (postName == null || postName.isBlank()) {
            return DEFAULT_KNOWLEDGE_CATEGORY_ID;
        }
        String n = postName.trim();
        if (n.contains("前端")) {
            return 2L;
        }
        if (n.contains("大模型") || n.contains("算法")) {
            return 4L;
        }
        if (n.contains("Java") || n.contains("后端")) {
            return 3L;
        }
        return DEFAULT_KNOWLEDGE_CATEGORY_ID;
    }

    @Transactional
    public boolean deleteKnowledge(Long knowledgeId) {
        KnowledgeBase knowledge = knowledgeBaseRepository.findById(knowledgeId).orElse(null);
        if (knowledge != null) {
            knowledge.setIsDelete(1);
            knowledgeBaseRepository.save(knowledge);
            return true;
        }
        return false;
    }

    public List<KnowledgeBase> getKnowledgeByPostId(Long postId) {
        return knowledgeBaseRepository.findByPostIdAndIsDelete(postId, 0);
    }

    /** 学习资源页：获取全部知识点（来自 knowledge_base） */
    public List<KnowledgeBase> getAllKnowledge() {
        return knowledgeBaseRepository.findByIsDelete(0);
    }
}