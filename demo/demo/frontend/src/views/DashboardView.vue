<template>
  <section class="dashboard-wrap">
    <div class="dashboard-head">
      <h2>{{ app.user.name }}的工作台</h2>
      <p class="muted">欢迎回来，今天继续完成你的面试提升计划。</p>
    </div>

    <div class="section dashboard-main">
      <div class="dashboard-left-stack">
        <div class="card dashboard-kpi-card">
          <div class="dashboard-section-title dashboard-kpi-title">
            <h3>关键数据概览</h3>
          </div>
          <div class="dashboard-stats">
            <article v-for="card in stats" :key="card.title" class="dashboard-stat-card">
              <div class="dashboard-stat-main">
                <div>
                  <div class="muted">{{ card.title }}</div>
                  <div class="dashboard-stat-value">{{ card.value }}</div>
                  <div class="muted">{{ card.sub }}</div>
                </div>
                <span class="dashboard-stat-visual" v-html="card.icon"></span>
              </div>
            </article>
          </div>
        </div>

        <div class="dashboard-left card">
          <div class="dashboard-section-title">
            <h3>核心功能入口</h3>
          </div>
          <div class="dashboard-entry-grid">
            <article v-for="(entry, idx) in entries" :key="entry.title" class="dashboard-entry-card">
              <div class="dashboard-entry-head">
                <span class="dashboard-entry-icon" v-html="entry.icon"></span>
                <strong>{{ entry.title }}</strong>
              </div>
              <p class="muted">{{ entry.desc }}</p>
              <button class="btn btn-outline" :class="`entry-btn-${idx + 1}`" @click="go(entry.path)">{{ entry.action }}</button>
            </article>
          </div>
        </div>
      </div>

      <div class="dashboard-right">
        <div class="dashboard-calendar card">
          <div class="dashboard-section-title">
            <h3>我的日历</h3>
            <span class="tag">{{ calendar.monthLabel }}</span>
          </div>
          <div class="dashboard-calendar-week">
            <span v-for="d in weekLabels" :key="d">{{ d }}</span>
          </div>
          <div class="dashboard-calendar-grid">
            <span
              v-for="(d, idx) in calendar.days"
              :key="idx"
              class="dashboard-calendar-day"
              :class="{ today: d === calendar.todayDate }"
            >
              {{ d || "" }}
            </span>
          </div>
        </div>

        <div class="dashboard-notice card">
          <div class="dashboard-section-title">
            <h3>我的通知</h3>
            <span class="tag">{{ notices.length }} 条</span>
          </div>
          <div v-for="n in notices" :key="n.text" class="todo-item">
            <span>{{ n.text }}</span>
            <button class="btn btn-text" @click="go(n.path)">查看</button>
          </div>
        </div>

        <div class="dashboard-todo card">
          <div class="dashboard-section-title">
            <h3>我的待办</h3>
            <span class="tag">{{ todos.length }} 项</span>
          </div>
          <div v-for="item in todos" :key="item.text" class="todo-item">
            <span>{{ item.text }}</span>
            <button class="btn btn-text" @click="go(item.path)">立即处理</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const router = useRouter();
const app = useAppStore();

const dashboard = ref({
  interviewCount: 0,
  highestScore: 0,
  practiceQuestionCount: 0,
  practiceAccuracy: 0,
  clockInDays: 0,
  streakDays: 0
});
const stats = computed(() => [
  {
    title: "累计面试次数",
    value: dashboard.value.interviewCount,
    sub: `最高得分 ${dashboard.value.highestScore}`,
    icon: '<svg viewBox="0 0 120 56"><polyline points="8,40 24,36 40,38 56,24 72,28 88,14 104,18" /><circle cx="104" cy="18" r="3.2"/></svg>'
  },
  {
    title: "专项练习总题数",
    value: dashboard.value.practiceQuestionCount,
    sub: `平均正确率 ${dashboard.value.practiceAccuracy}%`,
    icon: '<svg viewBox="0 0 120 56"><rect x="12" y="30" width="10" height="16" rx="2"/><rect x="30" y="24" width="10" height="22" rx="2"/><rect x="48" y="18" width="10" height="28" rx="2"/><rect x="66" y="12" width="10" height="34" rx="2"/><rect x="84" y="8" width="10" height="38" rx="2"/></svg>'
  },
  {
    title: "打卡天数",
    value: dashboard.value.clockInDays,
    sub: `连续 ${dashboard.value.streakDays} 天`,
    icon: '<svg viewBox="0 0 120 56"><rect x="30" y="8" width="12" height="12" rx="3" fill="#d6ecff"/><rect x="45" y="8" width="12" height="12" rx="3" fill="#bfe0ff"/><rect x="60" y="8" width="12" height="12" rx="3" fill="#9fd0ff"/><rect x="75" y="8" width="12" height="12" rx="3" fill="#7ebeff"/><rect x="30" y="23" width="12" height="12" rx="3" fill="#c7e5ff"/><rect x="45" y="23" width="12" height="12" rx="3" fill="#9fd0ff"/><rect x="60" y="23" width="12" height="12" rx="3" fill="#6fb4ff"/><rect x="75" y="23" width="12" height="12" rx="3" fill="#4298f5"/><rect x="30" y="38" width="12" height="12" rx="3" fill="#b9ddff"/><rect x="45" y="38" width="12" height="12" rx="3" fill="#86c3ff"/><rect x="60" y="38" width="12" height="12" rx="3" fill="#4aa0fb"/><rect x="75" y="38" width="12" height="12" rx="3" fill="#2e83df"/></svg>'
  }
]);
const entries = [
  { title: "模拟面试", desc: "真实流程，沉浸式提问", action: "立即开始", path: "/app/interview", icon: '<svg viewBox="0 0 24 24"><path d="M8 5a4 4 0 0 0 0 8h2l6 6v-6h1a4 4 0 0 0 0-8H8z"/></svg>' },
  { title: "专项练习", desc: "针对短板快速补强", action: "开始刷题", path: "/app/practice", icon: '<svg viewBox="0 0 24 24"><rect x="5" y="4" width="14" height="16" rx="2"/><path d="M9 8h6M9 12h6M9 16h4"/></svg>' },
  { title: "面试报告", desc: "查看历次报告与筛选", action: "查看全部", path: "/app/interview-reports", icon: '<svg viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 10h18M7 14h4"/></svg>' },
  { title: "错题本", desc: "高频错误集中复盘", action: "去复盘", path: "/app/wrong-book", icon: '<svg viewBox="0 0 24 24"><path d="M9 3h10a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H9"/><path d="m3 10 2 2 4-4"/></svg>' },
  { title: "简历优化", desc: "AI诊断+岗位改写", action: "上传简历", path: "/app/resume", icon: '<svg viewBox="0 0 24 24"><path d="M7 3h8l4 4v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/><path d="M15 3v4h4"/></svg>' },
  { title: "跨岗位推荐", desc: "拓展更适配岗位", action: "查看推荐", path: "/app/post-match", icon: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m15 9-5 2-2 5 5-2 2-5z"/></svg>' }
];
const todos = ref<Array<{ text: string; path: string }>>([]);
const notices = ref<Array<{ text: string; path: string }>>([]);

const weekLabels = ["一", "二", "三", "四", "五", "六", "日"];
const calendar = computed(() => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  const first = new Date(year, month, 1);
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const mondayBasedFirst = (first.getDay() + 6) % 7;
  const days: (number | null)[] = [];
  for (let i = 0; i < mondayBasedFirst; i += 1) days.push(null);
  for (let d = 1; d <= daysInMonth; d += 1) days.push(d);
  while (days.length % 7 !== 0) days.push(null);
  return {
    monthLabel: `${year}年${month + 1}月`,
    days,
    todayDate: now.getDate()
  };
});

function go(path: string) {
  router.push(path);
}

async function loadDashboard() {
  try {
    const data = await app.apiRequest(`/api/dashboard/user/${app.uid}`);
    const s = data?.stats || {};
    dashboard.value = {
      interviewCount: Number(s.interviewCount || 0),
      highestScore: Number(s.highestScore || 0),
      practiceQuestionCount: Number(s.practiceQuestionCount || 0),
      practiceAccuracy: Number(s.practiceAccuracy || 0),
      clockInDays: Number(s.clockInDays || 0),
      streakDays: Number(s.streakDays || 0)
    };
    notices.value = Array.isArray(data?.notices) ? data.notices : [];
    todos.value = Array.isArray(data?.todos) ? data.todos : [];
  } catch {
    notices.value = [];
    todos.value = [];
  }
}

onMounted(loadDashboard);
</script>

