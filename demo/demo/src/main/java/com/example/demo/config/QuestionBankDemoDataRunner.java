package com.example.demo.config;

import com.example.demo.entity.Post;
import com.example.demo.entity.QuestionBank;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.QuestionBankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 学习资源页读取的是 {@code question_bank}，与 {@code knowledge_base}（面试/RAG 沉淀）不是同一张表。
 * 若库中无任何题目，启动时写入一批演示数据，避免页面全为 0 且无内容可点。
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class QuestionBankDemoDataRunner implements CommandLineRunner {

    private final QuestionBankRepository questionBankRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (!questionBankRepository.findByIsDelete(0).isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        long javaPostId = ensurePost("Java后端", "Java/Spring/MySQL/Redis", "后端开发与系统设计", now);
        long webPostId = ensurePost("Web前端", "Vue/React/TypeScript", "前端工程与性能优化", now);
        long llmPostId = ensurePost("大模型算法", "LLM/RAG/Agent", "大模型应用与检索增强", now);

        List<QuestionBank> rows = new ArrayList<>();
        // 专业基础（挂 Java 岗位即可）
        rows.add(q(javaPostId, now, "编译原理", "词法分析与语法分析的作用分别是什么？", "词法分析将字符流切分为记号；语法分析根据文法构建语法树。"));
        rows.add(q(javaPostId, now, "操作系统", "进程与线程的主要区别？", "进程是资源分配单位，线程是 CPU 调度单位，同一进程内多线程共享地址空间。"));
        rows.add(q(javaPostId, now, "基础编程练习", "实现判断链表是否有环的思路？", "快慢指针：快指针每次两步、慢指针一步，若有环必相遇。"));
        rows.add(q(javaPostId, now, "计算机网络", "TCP 三次握手的目的？", "同步双方序列号与窗口，确认双向可达并建立可靠传输上下文。"));
        rows.add(q(javaPostId, now, "计算机组成原理", "Cache 的作用？", "在 CPU 与主存之间缓存常用数据，降低平均访存延迟。"));
        rows.add(q(javaPostId, now, "数据结构与算法", "平衡二叉树相比普通 BST 的优势？", "保证树高为 O(log n)，查找/插入/删除最坏仍为对数时间。"));
        rows.add(q(javaPostId, now, "正则表达式", "匹配中国大陆手机号的简要思路？", "以 1 开头，第二位 3-9，共 11 位数字；可用 ^1[3-9]\\d{9}$ 等形式。"));
        rows.add(q(javaPostId, now, "Linux", "查看当前目录磁盘占用的常用命令？", "du -sh . 或 df -h 查看挂载点空间。"));
        // Web 前端
        rows.add(q(webPostId, now, "前端工程化", "Webpack 与 Vite 在开发体验上的典型差异？", "Vite 开发态基于原生 ESM 与按需编译，冷启动更快；Webpack 更偏打包生态成熟。"));
        rows.add(q(webPostId, now, "CSS", "BFC 是什么？能解决哪些布局问题？", "块级格式化上下文；可清除浮动、避免 margin 折叠、实现自适应两栏等。"));
        rows.add(q(webPostId, now, "HTML", "语义化标签的好处？", "利于 SEO、无障碍、可读性与维护，结构表达更清晰。"));
        rows.add(q(webPostId, now, "JavaScript", "let 与 var 的主要区别？", "let 块级作用域、无变量提升污染 TDZ；var 为函数作用域且存在提升。"));
        rows.add(q(webPostId, now, "Node.js", "Node 适合 IO 密集型的原因？", "单线程事件循环 + 非阻塞 IO，高并发连接下线程开销小。"));
        rows.add(q(webPostId, now, "PWA", "Service Worker 的典型用途？", "离线缓存、后台同步、推送通知与网络请求拦截。"));
        rows.add(q(webPostId, now, "React", "Hooks 中 useEffect 的依赖数组作用？", "控制副作用执行时机；空数组仅挂载/卸载时执行，不传则每次渲染后执行。"));
        rows.add(q(webPostId, now, "TypeScript", "interface 与 type 的常见选用场景？", "interface 可合并声明适合对象形状；type 可做联合/交叉与别名更灵活。"));
        rows.add(q(webPostId, now, "jQuery", "jQuery 事件委托的写法要点？", "在父元素上监听，用 event.target 判断子元素，减少监听器数量。"));
        rows.add(q(webPostId, now, "Vue", "Vue3 Composition API 相对 Options API 的优势？", "逻辑按功能聚合、类型推断更好、便于抽离复用 composable。"));
        rows.add(q(webPostId, now, "Web前端开发", "前端首屏优化的三个方向？", "减少请求体积、利用缓存与 CDN、优化关键渲染路径与懒加载。"));
        // Java 后端
        rows.add(q(javaPostId, now, "Hibernate", "Hibernate 一级缓存与二级缓存区别？", "一级缓存与会话绑定；二级缓存跨会话，需配置区域与并发策略。"));
        rows.add(q(javaPostId, now, "MyBatis", "#{} 与 ${} 在防 SQL 注入上的差异？", "#{} 预编译参数化安全；${} 为字符串替换，需自行校验。"));
        rows.add(q(javaPostId, now, "Netty", "Netty 的 Reactor 模型简述？", "Boss 线程接受连接，Worker 线程处理 IO，事件驱动提升并发效率。"));
        rows.add(q(javaPostId, now, "Maven", "Maven 中 scope=provided 的含义？", "编译与测试需要，运行时由容器等环境提供，不会打入默认包。"));
        rows.add(q(javaPostId, now, "Java", "什么是 Java 虚拟机（JVM）？", "JVM 是运行字节码的抽象机器，负责加载、验证、执行与垃圾回收等。"));
        rows.add(q(javaPostId, now, "Spring", "Spring IoC 容器的核心作用？", "管理 Bean 生命周期与依赖注入，解耦组件创建与使用。"));
        rows.add(q(javaPostId, now, "Spring Cloud", "服务注册与发现解决什么问题？", "动态地址、健康检查与负载均衡，避免硬编码服务实例。"));
        rows.add(q(javaPostId, now, "Spring MVC", "DispatcherServlet 在请求处理中的角色？", "前端控制器：统一接收请求并分发给合适的处理器与视图解析。"));
        rows.add(q(javaPostId, now, "Tomcat", "Tomcat 中 Connector 的职责？", "监听端口、解析协议并将请求交给 Engine/Host/Context 处理。"));
        rows.add(q(javaPostId, now, "Spring Boot", "Spring Boot 自动装配的大致原理？", "通过 @EnableAutoConfiguration 与 spring.factories/imports 加载条件化配置。"));
        // 大模型
        rows.add(q(llmPostId, now, "LLM", "大语言模型「幻觉」的常见缓解手段？", "RAG、引用检索、约束解码、人类反馈对齐与事实校验管线。"));
        rows.add(q(llmPostId, now, "RAG", "RAG 的基本流程？", "检索相关文档片段，与问题一并拼入提示词，再由模型生成答案。"));
        rows.add(q(llmPostId, now, "Agent", "Agent 与单次问答的主要区别？", "Agent 可规划多步工具调用、观察环境并迭代直到任务完成。"));
        rows.add(q(llmPostId, now, "Prompt", "Few-shot Prompting 的作用？", "通过示例示范输出格式与推理风格，提高零样本难以稳定的行为。"));
        rows.add(q(llmPostId, now, "微调", "全量微调与 LoRA 的取舍？", "全量表达力强但成本高；LoRA 低秩适配参数少、训练快、易部署。"));
        rows.add(q(llmPostId, now, "向量库", "向量检索为何用近似最近邻（ANN）？", "高维下精确暴力检索代价高，ANN 在可接受精度下显著降延迟。"));

        questionBankRepository.saveAll(rows);
        log.info("question_bank 为空，已初始化演示题目 {} 条（含岗位与知识点）", rows.size());
    }

    private long ensurePost(String name, String stack, String focus, LocalDateTime now) {
        Post existing = postRepository.findByPostName(name);
        if (existing != null) {
            return existing.getPostId();
        }
        Post p = new Post();
        p.setPostName(name);
        p.setPostStack(stack);
        p.setPostFocus(focus);
        p.setPostIntro("");
        p.setCreateTime(now);
        p.setIsDelete(0);
        return postRepository.save(p).getPostId();
    }

    private static QuestionBank q(long postId, LocalDateTime now, String knowledgePoint, String title, String answer) {
        QuestionBank qb = new QuestionBank();
        qb.setPostId(postId);
        // 演示数据：统一落到一个合法分类（基础编程练习=7），避免外键约束报错；实际使用会由新增/编辑时按知识点匹配分类
        qb.setCategoryId(7L);
        qb.setQuestionType(3);
        qb.setQuestionLevel(1);
        qb.setQuestionTitle(title);
        qb.setQuestionAnswer(answer);
        qb.setKnowledgePoint(knowledgePoint);
        qb.setCreateTime(now);
        qb.setIsDelete(0);
        qb.setIsAiGenerate(0);
        return qb;
    }
}
