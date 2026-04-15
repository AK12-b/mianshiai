package com.example.demo.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将简历优化智能体输出按约定 Markdown 二级标题拆分为：
 * <ul>
 *   <li>「一、简历诊断结果」+「三、优化依据说明」→ 存入 diagnose_result</li>
 *   <li>「二、优化后完整简历」→ 存入 optimize_content，导出 PDF 仅使用此段</li>
 * </ul>
 */
public final class ResumeOptimizationOutputSplitter {

    private ResumeOptimizationOutputSplitter() {}

    /** 与智能体约定的二级标题（允许 ## 与标题间少量空格、顿号/英文点） */
    private static final Pattern H1 = Pattern.compile("(?m)^##\\s*一[、,. ]\\s*简历诊断结果\\s*$");
    private static final Pattern H2 = Pattern.compile("(?m)^##\\s*二[、,. ]\\s*优化后完整简历\\s*$");
    private static final Pattern H3 = Pattern.compile("(?m)^##\\s*三[、,. ]\\s*优化依据说明\\s*$");

    /**
     * @param diagnoseOneAndThree 「一」+「三」合并文本（用于 diagnose_result）
     * @param resumePartTwo 「二、优化后完整简历」整段（含该节标题行，用于 optimize_content 与 PDF）
     * @param structured 是否成功命中「二」节标题（命中则按规范拆分）
     */
    public record Parts(String diagnoseOneAndThree, String resumePartTwo, boolean structured) {}

    public static Parts split(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Parts("", "", false);
        }
        String text = raw.replace("\r\n", "\n");
        Matcher m2 = H2.matcher(text);
        if (!m2.find()) {
            return new Parts("", text.trim(), false);
        }
        int p2 = m2.start();
        String head = text.substring(0, p2);
        Matcher m1in = H1.matcher(head);
        int p1start = m1in.find() ? m1in.start() : -1;
        String sectionOne = (p1start >= 0 ? text.substring(p1start, p2) : head).trim();

        String tail = text.substring(p2);
        Matcher m3in = H3.matcher(tail);
        final String sectionTwo;
        final String sectionThree;
        if (m3in.find()) {
            int p3 = p2 + m3in.start();
            sectionTwo = text.substring(p2, p3).trim();
            sectionThree = text.substring(p3).trim();
        } else {
            sectionTwo = text.substring(p2).trim();
            sectionThree = "";
        }
        String diagnose = (sectionOne + "\n\n" + sectionThree).trim();
        return new Parts(diagnose, sectionTwo, true);
    }
}
