<template>
  <section class="dashboard-wrap">
    <div class="dashboard-head">
      <h2>个人工作台</h2>
      <p class="muted">欢迎回来，今天继续完成你的面试提升计划。</p>
    </div>

    <div class="section dashboard-stats">
      <article v-for="card in stats" :key="card.title" class="dashboard-stat-card">
        <div class="muted">{{ card.title }}</div>
        <div class="dashboard-stat-value">{{ card.value }}</div>
        <div class="muted">{{ card.sub }}</div>
      </article>
    </div>

    <div class="section dashboard-main">
      <div class="dashboard-left card">
        <div class="dashboard-section-title">
          <h3>核心功能入口</h3>
          <button class="btn btn-text" @click="go('/app/interview')">查看全部</button>
        </div>
        <div class="dashboard-entry-grid">
          <article v-for="entry in entries" :key="entry.title" class="dashboard-entry-card">
            <strong>{{ entry.title }}</strong>
            <p class="muted">{{ entry.desc }}</p>
            <button class="btn btn-outline" @click="go(entry.path)">{{ entry.action }}</button>
          </article>
        </div>
      </div>

      <div class="dashboard-right card">
        <div class="dashboard-section-title">
          <h3>我的待办</h3>
          <span class="tag">4 项</span>
        </div>
        <div v-for="item in todos" :key="item.text" class="todo-item">
          <span>{{ item.text }}</span>
          <button class="btn btn-text" @click="go(item.path)">立即处理</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const stats = [
  { title: "累计面试次数", value: 16, sub: "最高得分 89" },
  { title: "专项练习总题数", value: 124, sub: "平均正确率 72%" },
  { title: "已掌握知识点", value: 43, sub: "待补强 11" },
  { title: "打卡天数", value: 18, sub: "连续 7 天" }
];
const entries = [
  { title: "模拟面试", desc: "真实流程，沉浸式提问", action: "立即开始", path: "/app/interview" },
  { title: "专项练习", desc: "针对短板快速补强", action: "开始刷题", path: "/app/practice" },
  { title: "面试报告", desc: "查看历次报告与筛选", action: "查看全部", path: "/app/interview-reports" },
  { title: "错题本", desc: "高频错误集中复盘", action: "去复盘", path: "/app/wrong-book" },
  { title: "简历优化", desc: "AI诊断+岗位改写", action: "上传简历", path: "/app/resume" },
  { title: "跨岗位推荐", desc: "拓展更适配岗位", action: "查看推荐", path: "/app/resources" }
];
const todos = [
  { text: "完成 Java 后端专项练习 10 题", path: "/app/practice" },
  { text: "复盘 3 道高频错题", path: "/app/wrong-book" },
  { text: "查看最新面试报告", path: "/app/interview-reports" },
  { text: "更新项目经历中的优化指标", path: "/app/resume" }
];

function go(path: string) {
  router.push(path);
}
</script>

