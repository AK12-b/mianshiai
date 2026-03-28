/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : mianshi

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 29/03/2026 00:03:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ability_growth
-- ----------------------------
DROP TABLE IF EXISTS `ability_growth`;
CREATE TABLE `ability_growth`  (
  `growth_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `statistics_cycle` tinyint(1) NOT NULL COMMENT '统计周期：1=日，2=周，3=月',
  `cycle_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '周期时间（如202603-202604）',
  `interview_count` int NULL DEFAULT 0 COMMENT '面试总次数',
  `avg_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '平均得分：0-100',
  `practice_question_count` int NOT NULL DEFAULT 0 COMMENT '累计练习题目数量',
  `total_train_duration` int NOT NULL DEFAULT 0 COMMENT '累计训练时长（分钟）',
  `wrong_question_count` int NOT NULL DEFAULT 0 COMMENT '累计错题总数',
  `weak_point` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '核心短板',
  `weak_point_improve_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '短板改善率：0-100',
  `ability_improve` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '能力提升情况描述',
  `growth_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '针对性训练建议',
  `create_time` datetime NOT NULL COMMENT '统计时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`growth_id`) USING BTREE,
  INDEX `fk_ag_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_ag_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_ag_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ag_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '能力成长统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ability_growth
-- ----------------------------

-- ----------------------------
-- Table structure for clock_in_record
-- ----------------------------
DROP TABLE IF EXISTS `clock_in_record`;
CREATE TABLE `clock_in_record`  (
  `clock_in_id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡记录唯一标识',
  `user_id` bigint NOT NULL COMMENT '打卡用户ID',
  `task_id` bigint NOT NULL COMMENT '所属练习任务ID（关联practice_master_task.master_id）',
  `clock_in_time` datetime NOT NULL COMMENT '实际打卡时间',
  `task_finish_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '打卡时任务完成状态：0=未完成任务打卡，1=完成任务打卡',
  `train_duration` int NULL DEFAULT NULL COMMENT '本次打卡训练时长（分钟）',
  `create_time` datetime NOT NULL COMMENT '记录创建时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`clock_in_id`) USING BTREE,
  INDEX `fk_cir_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_cir_task_id`(`task_id` ASC) USING BTREE,
  CONSTRAINT `fk_cir_task_id` FOREIGN KEY (`task_id`) REFERENCES `practice_master_task` (`master_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_cir_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打卡记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clock_in_record
-- ----------------------------

-- ----------------------------
-- Table structure for interview_dialog
-- ----------------------------
DROP TABLE IF EXISTS `interview_dialog`;
CREATE TABLE `interview_dialog`  (
  `dialog_id` bigint NOT NULL AUTO_INCREMENT COMMENT '对话唯一标识',
  `interview_id` bigint NOT NULL COMMENT '所属面试ID',
  `speaker` tinyint(1) NOT NULL COMMENT '发言方：1=AI面试官，2=用户',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文本发言纯文字内容',
  `question_id` bigint NULL DEFAULT NULL COMMENT '对话对应题目ID',
  `dialogue_round` int NOT NULL COMMENT '对话轮次，标记问答顺序',
  `voice_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '语音文件存储路径',
  `voice_duration` int NULL DEFAULT NULL COMMENT '语音发言时长（秒）',
  `single_question_timeout` tinyint(1) NOT NULL DEFAULT 0 COMMENT '单题是否超时：0=未超时，1=超时',
  `create_time` datetime NOT NULL COMMENT '发言时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`dialog_id`) USING BTREE,
  INDEX `fk_id_interview_id`(`interview_id` ASC) USING BTREE,
  INDEX `fk_id_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_id_interview_id` FOREIGN KEY (`interview_id`) REFERENCES `mock_interview` (`interview_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_id_question_id` FOREIGN KEY (`question_id`) REFERENCES `question_bank` (`question_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试对话详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interview_dialog
-- ----------------------------

-- ----------------------------
-- Table structure for interview_evaluate
-- ----------------------------
DROP TABLE IF EXISTS `interview_evaluate`;
CREATE TABLE `interview_evaluate`  (
  `evaluate_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评估唯一标识',
  `interview_id` bigint NOT NULL COMMENT '所属面试ID',
  `post_id` bigint NOT NULL COMMENT '所属岗位ID',
  `total_score` decimal(5, 2) NOT NULL COMMENT '面试总分：0-100',
  `tech_score` decimal(5, 2) NOT NULL COMMENT '技术正确性得分：0-100',
  `knowledge_depth_score` decimal(5, 2) NOT NULL COMMENT '知识深度得分：0-100',
  `logic_score` decimal(5, 2) NOT NULL COMMENT '逻辑严谨性得分：0-100',
  `express_score` decimal(5, 2) NOT NULL COMMENT '表达能力得分：0-100',
  `match_score` decimal(5, 2) NOT NULL COMMENT '岗位匹配度得分：0-100',
  `bright_point` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '面试亮点总结',
  `problem_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '问题分析',
  `gap_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '岗位能力差距',
  `suggest_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '建议指导',
  `tech_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技术正确性维度详细分析',
  `knowledge_depth_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '知识深度维度详细分析',
  `logic_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '逻辑严谨性维度详细分析',
  `express_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '表达能力维度详细分析',
  `match_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '岗位匹配度维度详细分析',
  `create_time` datetime NOT NULL COMMENT '评估生成时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`evaluate_id`) USING BTREE,
  UNIQUE INDEX `uk_interview_id`(`interview_id` ASC) USING BTREE,
  INDEX `fk_ie_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_ie_interview_id` FOREIGN KEY (`interview_id`) REFERENCES `mock_interview` (`interview_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ie_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试评估表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interview_evaluate
-- ----------------------------

-- ----------------------------
-- Table structure for interview_wrong_question
-- ----------------------------
DROP TABLE IF EXISTS `interview_wrong_question`;
CREATE TABLE `interview_wrong_question`  (
  `wrong_id` bigint NOT NULL AUTO_INCREMENT COMMENT '错题唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `interview_id` bigint NULL DEFAULT NULL COMMENT '所属面试ID',
  `practice_id` bigint NULL DEFAULT NULL COMMENT '所属练习ID（关联practice_single_task.single_id）',
  `wrong_reason` tinyint(1) NOT NULL COMMENT '失分原因：1=知识点遗漏，2=逻辑混乱，3=表达失误，4=思路错误',
  `knowledge_point` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错题核心考点',
  `is_collect` tinyint(1) NULL DEFAULT 0 COMMENT '是否收藏：0=未收藏，1=已收藏',
  `create_time` datetime NOT NULL COMMENT '错题收录时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`wrong_id`) USING BTREE,
  INDEX `fk_iwq_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_iwq_question_id`(`question_id` ASC) USING BTREE,
  INDEX `fk_iwq_interview_id`(`interview_id` ASC) USING BTREE,
  INDEX `fk_iwq_practice_id`(`practice_id` ASC) USING BTREE,
  CONSTRAINT `fk_iwq_interview_id` FOREIGN KEY (`interview_id`) REFERENCES `mock_interview` (`interview_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_iwq_practice_id` FOREIGN KEY (`practice_id`) REFERENCES `practice_single_task` (`single_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_iwq_question_id` FOREIGN KEY (`question_id`) REFERENCES `question_bank` (`question_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_iwq_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试错题本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interview_wrong_question
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base`  (
  `knowledge_id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识点唯一标识',
  `post_id` bigint NOT NULL COMMENT '所属岗位ID',
  `knowledge_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识点名称',
  `knowledge_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识点详细内容',
  `vector_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量数据库索引ID（RAG检索用）',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`knowledge_id`) USING BTREE,
  INDEX `fk_kb_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_kb_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_base
-- ----------------------------

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `notice_type` tinyint(1) NOT NULL COMMENT '通知类型：1=训练提醒，2=报告查看通知',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `send_time` datetime NOT NULL COMMENT '通知实际推送时间',
  `remind_cycle` tinyint(1) NOT NULL DEFAULT 0 COMMENT '提醒周期：0=单次，1=每日，2=每周',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '阅读状态：0=未读，1=已读',
  `is_close` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否手动关闭通知：0=未关闭，1=已关闭',
  `create_time` datetime NOT NULL COMMENT '通知发送时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `fk_mn_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_mn_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_notice
-- ----------------------------

-- ----------------------------
-- Table structure for mock_interview
-- ----------------------------
DROP TABLE IF EXISTS `mock_interview`;
CREATE TABLE `mock_interview`  (
  `interview_id` bigint NOT NULL AUTO_INCREMENT COMMENT '面试唯一标识',
  `user_id` bigint NOT NULL COMMENT '面试用户ID',
  `post_id` bigint NOT NULL COMMENT '面试岗位ID',
  `interview_mode` tinyint(1) NOT NULL COMMENT '面试模式：1=全流程面试，2=专项面试',
  `interview_module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专项面试模块：个人信息/项目经历/岗位知识/竞赛经历/实习经历',
  `ai_character` tinyint(1) NOT NULL COMMENT 'AI面试官性格：1=亲和型，2=压力型，3=严谨型',
  `ai_gender` tinyint(1) NOT NULL COMMENT 'AI面试官性别：1=男，2=女',
  `interview_duration` int NOT NULL COMMENT '面试时长（分钟）：5/10/15/20/30/60',
  `single_question_time_limit` int NOT NULL COMMENT '单题最长作答时间（秒）',
  `remind_before_timeout` int NOT NULL DEFAULT 10 COMMENT '超时前提醒时间（秒）',
  `input_type` tinyint(1) NOT NULL COMMENT '作答输入方式：1=语音，2=文本',
  `start_time` datetime NOT NULL COMMENT '面试开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '面试结束时间',
  `interview_status` tinyint(1) NULL DEFAULT 1 COMMENT '面试状态：1=进行中，2=已完成，3=已终止，4=已暂停',
  `is_timeout` tinyint(1) NULL DEFAULT 0 COMMENT '面试整体是否超时：0=未超时，1=超时',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`interview_id`) USING BTREE,
  INDEX `fk_mi_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_mi_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_mi_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_mi_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模拟面试主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mock_interview
-- ----------------------------

-- ----------------------------
-- Table structure for personal_suggestion
-- ----------------------------
DROP TABLE IF EXISTS `personal_suggestion`;
CREATE TABLE `personal_suggestion`  (
  `suggestion_id` bigint NOT NULL AUTO_INCREMENT COMMENT '建议唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `tech_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技术短板建议',
  `knowledge_depth_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '知识深度短板建议',
  `logic_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '逻辑思维短板建议',
  `express_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '表达短板建议',
  `post_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '岗位短板建议',
  `create_time` datetime NOT NULL COMMENT '建议生成时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`suggestion_id`) USING BTREE,
  INDEX `fk_ps_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_ps_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_ps_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ps_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个性化提升建议表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of personal_suggestion
-- ----------------------------

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位唯一标识',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称：Java后端/Web前端/大模型算法等',
  `post_stack` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位核心技术栈',
  `post_focus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '岗位面试侧重点说明',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  `post_intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '岗位详细介绍（职责/要求）',
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_post_name`(`post_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------

-- ----------------------------
-- Table structure for post_evaluate_standard
-- ----------------------------
DROP TABLE IF EXISTS `post_evaluate_standard`;
CREATE TABLE `post_evaluate_standard`  (
  `standard_id` bigint NOT NULL AUTO_INCREMENT COMMENT '标准唯一标识',
  `post_id` bigint NOT NULL COMMENT '所属岗位ID',
  `tech_weight` tinyint NOT NULL COMMENT '技术正确性权重：0-100',
  `knowledge_depth_weight` tinyint NOT NULL COMMENT '知识深度权重：0-100',
  `logic_weight` tinyint NOT NULL COMMENT '逻辑严谨性权重：0-100',
  `express_weight` tinyint NOT NULL COMMENT '表达能力权重：0-100',
  `match_weight` tinyint NOT NULL COMMENT '岗位匹配度权重：0-100',
  `score_rule` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '量化评分规则',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`standard_id`) USING BTREE,
  INDEX `fk_pes_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_pes_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位评估标准表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_evaluate_standard
-- ----------------------------

-- ----------------------------
-- Table structure for post_recommend
-- ----------------------------
DROP TABLE IF EXISTS `post_recommend`;
CREATE TABLE `post_recommend`  (
  `recommend_id` bigint NOT NULL AUTO_INCREMENT COMMENT '推荐唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `original_post_id` bigint NOT NULL COMMENT '原意向岗位ID',
  `recommend_post_id` bigint NOT NULL COMMENT '推荐岗位ID',
  `match_rate` decimal(5, 2) NOT NULL COMMENT '岗位总适配度：0-100',
  `skill_match_rate` decimal(5, 2) NOT NULL COMMENT '技能匹配度：0-100',
  `project_match_rate` decimal(5, 2) NOT NULL COMMENT '项目经历适配度：0-100',
  `interview_match_rate` decimal(5, 2) NOT NULL COMMENT '面试表现适配度：0-100',
  `match_highlight` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '匹配亮点',
  `ability_gap` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '能力缺口',
  `industry_prospect` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '推荐岗位行业前景简介',
  `create_time` datetime NOT NULL COMMENT '推荐生成时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`recommend_id`) USING BTREE,
  INDEX `fk_pr_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_pr_original_post_id`(`original_post_id` ASC) USING BTREE,
  INDEX `fk_pr_recommend_post_id`(`recommend_post_id` ASC) USING BTREE,
  CONSTRAINT `fk_pr_original_post_id` FOREIGN KEY (`original_post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pr_recommend_post_id` FOREIGN KEY (`recommend_post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pr_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '跨岗位推荐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_recommend
-- ----------------------------

-- ----------------------------
-- Table structure for practice_master_task
-- ----------------------------
DROP TABLE IF EXISTS `practice_master_task`;
CREATE TABLE `practice_master_task`  (
  `master_id` bigint NOT NULL AUTO_INCREMENT COMMENT '总任务唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `suggestion_id` bigint NULL DEFAULT NULL COMMENT '关联的提升建议ID',
  `knowledge_point` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '练习知识点',
  `practice_level` tinyint(1) NOT NULL COMMENT '练习难度等级：1=基础，2=进阶，3=高阶',
  `task_cycle` tinyint(1) NOT NULL COMMENT '任务周期：1=每日，2=每周',
  `task_quantity` int NOT NULL COMMENT '单次任务题目数量',
  `plan_start_time` datetime NOT NULL COMMENT '计划开始时间',
  `plan_end_time` datetime NOT NULL COMMENT '计划结束时间',
  `master_status` tinyint(1) NULL DEFAULT 0 COMMENT '总任务状态：0=进行中，1=已结束，2=已终止',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`master_id`) USING BTREE,
  INDEX `fk_pmt_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_pmt_post_id`(`post_id` ASC) USING BTREE,
  INDEX `fk_pmt_suggestion_id`(`suggestion_id` ASC) USING BTREE,
  CONSTRAINT `fk_pmt_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pmt_suggestion_id` FOREIGN KEY (`suggestion_id`) REFERENCES `personal_suggestion` (`suggestion_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pmt_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '练习总任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of practice_master_task
-- ----------------------------

-- ----------------------------
-- Table structure for practice_question_record
-- ----------------------------
DROP TABLE IF EXISTS `practice_question_record`;
CREATE TABLE `practice_question_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录唯一标识',
  `single_id` bigint NOT NULL COMMENT '所属单次任务ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户作答内容',
  `input_type` tinyint(1) NOT NULL COMMENT '作答方式：1=语音，2=文本',
  `answer_duration` int NULL DEFAULT NULL COMMENT '单题耗时（秒）',
  `tech_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '技术得分：0-100',
  `knowledge_depth_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '知识深度得分：0-100',
  `logic_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '逻辑得分：0-100',
  `express_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '表达得分：0-100',
  `match_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '匹配度得分：0-100',
  `error_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '作答错误类型：0=无错误，1=概念错误，2=逻辑错误，3=遗漏要点',
  `create_time` datetime NOT NULL COMMENT '答题时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `fk_pqr_single_id`(`single_id` ASC) USING BTREE,
  INDEX `fk_pqr_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_pqr_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_pqr_question_id` FOREIGN KEY (`question_id`) REFERENCES `question_bank` (`question_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pqr_single_id` FOREIGN KEY (`single_id`) REFERENCES `practice_single_task` (`single_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pqr_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '练习题目作答表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of practice_question_record
-- ----------------------------

-- ----------------------------
-- Table structure for practice_single_report
-- ----------------------------
DROP TABLE IF EXISTS `practice_single_report`;
CREATE TABLE `practice_single_report`  (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告唯一标识',
  `single_id` bigint NOT NULL COMMENT '所属单次任务ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `master_id` bigint NOT NULL COMMENT '所属总任务ID',
  `score_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '得分维度分析',
  `improve_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '优化提升建议',
  `create_time` datetime NOT NULL COMMENT '报告生成时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`report_id`) USING BTREE,
  UNIQUE INDEX `uk_single_id`(`single_id` ASC) USING BTREE,
  INDEX `fk_psr_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_psr_master_id`(`master_id` ASC) USING BTREE,
  CONSTRAINT `fk_psr_master_id` FOREIGN KEY (`master_id`) REFERENCES `practice_master_task` (`master_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_psr_single_id` FOREIGN KEY (`single_id`) REFERENCES `practice_single_task` (`single_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_psr_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '练习单次任务报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of practice_single_report
-- ----------------------------

-- ----------------------------
-- Table structure for practice_single_task
-- ----------------------------
DROP TABLE IF EXISTS `practice_single_task`;
CREATE TABLE `practice_single_task`  (
  `single_id` bigint NOT NULL AUTO_INCREMENT COMMENT '单次任务唯一标识',
  `master_id` bigint NOT NULL COMMENT '所属总任务ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `single_status` tinyint(1) NULL DEFAULT 0 COMMENT '单次任务状态：0=未开始，1=进行中，2=已完成，3=已逾期',
  `total_duration` int NULL DEFAULT NULL COMMENT '单次任务总耗时（秒）',
  `total_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '单次任务总得分：0-100',
  `skip_quantity` int NOT NULL DEFAULT 0 COMMENT '本次练习跳过题目数量',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始答题时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成答题时间',
  `create_time` datetime NOT NULL COMMENT '任务生成时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`single_id`) USING BTREE,
  INDEX `fk_pst_master_id`(`master_id` ASC) USING BTREE,
  INDEX `fk_pst_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_pst_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_pst_master_id` FOREIGN KEY (`master_id`) REFERENCES `practice_master_task` (`master_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pst_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pst_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '练习单次任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of practice_single_task
-- ----------------------------

-- ----------------------------
-- Table structure for question_bank
-- ----------------------------
DROP TABLE IF EXISTS `question_bank`;
CREATE TABLE `question_bank`  (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目唯一标识',
  `post_id` bigint NOT NULL COMMENT '所属岗位ID',
  `question_type` tinyint(1) NOT NULL COMMENT '题目类型：1=技术知识，2=项目深挖，3=场景题，4=行为题',
  `question_level` tinyint(1) NOT NULL COMMENT '题目难度：1=基础，2=进阶，3=高阶',
  `question_title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `question_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标准答案',
  `knowledge_point` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联知识点',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  `is_ai_generate` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为AI生成题目：0=非AI生成，1=AI生成',
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `fk_qb_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_qb_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_bank
-- ----------------------------

-- ----------------------------
-- Table structure for resume
-- ----------------------------
DROP TABLE IF EXISTS `resume`;
CREATE TABLE `resume`  (
  `resume_id` bigint NOT NULL AUTO_INCREMENT COMMENT '简历唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '定向岗位ID',
  `original_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始简历路径',
  `resume_size` bigint NOT NULL COMMENT '简历文件大小（字节）',
  `resume_format` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简历文件格式：Word/PDF',
  `diagnose_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简历诊断结果',
  `optimize_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '优化后简历路径',
  `is_optimize_apply` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否应用AI优化建议：0=未应用，1=已应用',
  `optimize_time` datetime NULL DEFAULT NULL COMMENT '简历优化时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`resume_id`) USING BTREE,
  INDEX `fk_resume_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_resume_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_resume_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_resume_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resume
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录邮箱',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别：0=未知，1=男，2=女',
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历：专科/本科/硕士/博士',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级：大一大二大三大四/研一研二研三博一等',
  `project_exp` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目经历描述',
  `skill_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人技能标签',
  `skill_proficiency` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技能熟练度（JSON格式：{\"Java\":\"精通\"}）',
  `resume_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简历文件存储路径',
  `theme_mode` tinyint(1) NULL DEFAULT 0 COMMENT '界面主题：0=日间模式，1=夜间模式',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '上次登录时间',
  `create_time` datetime NOT NULL COMMENT '账号创建时间',
  `update_time` datetime NOT NULL COMMENT '信息更新时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称/登录名',
  `competition_award` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '竞赛获奖经历描述',
  `intern_exp` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '实习经历描述',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户核心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------

-- ----------------------------
-- Table structure for user_intention_post
-- ----------------------------
DROP TABLE IF EXISTS `user_intention_post`;
CREATE TABLE `user_intention_post`  (
  `uip_id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '意向岗位ID',
  `create_time` datetime NOT NULL COMMENT '添加意向岗位时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`uip_id`) USING BTREE,
  INDEX `fk_uip_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_uip_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_uip_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_uip_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-意向岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_intention_post
-- ----------------------------

-- ----------------------------
-- Table structure for user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志唯一标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `logout_time` datetime NULL DEFAULT NULL COMMENT '登出时间',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '软删除标记：0=未删，1=已删',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `fk_ull_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_ull_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_login_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
