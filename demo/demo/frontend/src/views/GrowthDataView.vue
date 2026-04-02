<template>
  <section class="growth-wrap">
    <div class="card">
      <div class="growth-head">
        <h3>成长数据</h3>
        <div class="toolbar" style="margin: 0">
          <button
            class="btn"
            :class="activeCycle === 1 ? 'btn-primary' : 'btn-outline'"
            @click="activeCycle = 1"
          >
            日
          </button>
          <button
            class="btn"
            :class="activeCycle === 2 ? 'btn-primary' : 'btn-outline'"
            @click="activeCycle = 2"
          >
            周
          </button>
          <button
            class="btn"
            :class="activeCycle === 3 ? 'btn-primary' : 'btn-outline'"
            @click="activeCycle = 3"
          >
            月
          </button>
          <button class="btn btn-outline" @click="loadGrowth">刷新数据</button>
          <button class="btn btn-outline" @click="app.showToast('图表下载功能待接入')">下载图表</button>
        </div>
      </div>

      <div class="growth-kpi-grid">
        <article class="growth-kpi-card">
          <div class="muted">面试次数</div>
          <div class="growth-kpi-value">{{ summary.interviewCount }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="muted">平均得分</div>
          <div class="growth-kpi-value">{{ summary.avgScore }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="muted">练习题数</div>
          <div class="growth-kpi-value">{{ summary.practiceQuestionCount }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="muted">训练时长(分钟)</div>
          <div class="growth-kpi-value">{{ summary.totalTrainDuration }}</div>
        </article>
      </div>
    </div>

    <div class="card">
      <div class="dashboard-section-title">
        <h3>能力成长曲线（{{ cycleText(activeCycle) }}）</h3>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th>周期</th>
            <th>平均得分</th>
            <th>面试次数</th>
            <th>练习题数</th>
            <th>错题数</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in currentCycleRows" :key="row.growthId">
            <td>{{ row.cycleTime || "--" }}</td>
            <td>{{ score(row.avgScore) }}</td>
            <td>{{ safeNum(row.interviewCount) }}</td>
            <td>{{ safeNum(row.practiceQuestionCount) }}</td>
            <td>{{ safeNum(row.wrongQuestionCount) }}</td>
          </tr>
          <tr v-if="!currentCycleRows.length">
            <td colspan="5" class="empty-cell">当前周期暂无成长数据记录。</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="growth-two-col">
      <div class="card">
        <div class="dashboard-section-title">
          <h3>能力均衡度（雷达图数据）</h3>
        </div>
        <div class="radar-list">
          <div class="radar-row">
            <span>技术正确性</span>
            <div class="radar-bar"><i :style="{ width: metric.tech + '%' }"></i></div>
            <b>{{ metric.tech }}</b>
          </div>
          <div class="radar-row">
            <span>知识深度</span>
            <div class="radar-bar"><i :style="{ width: metric.depth + '%' }"></i></div>
            <b>{{ metric.depth }}</b>
          </div>
          <div class="radar-row">
            <span>逻辑严谨性</span>
            <div class="radar-bar"><i :style="{ width: metric.logic + '%' }"></i></div>
            <b>{{ metric.logic }}</b>
          </div>
          <div class="radar-row">
            <span>岗位匹配度</span>
            <div class="radar-bar"><i :style="{ width: metric.match + '%' }"></i></div>
            <b>{{ metric.match }}</b>
          </div>
          <div class="radar-row">
            <span>表达能力</span>
            <div class="radar-bar"><i :style="{ width: metric.expression + '%' }"></i></div>
            <b>{{ metric.expression }}</b>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="dashboard-section-title">
          <h3>短板变化与建议</h3>
        </div>
        <div class="growth-note-item">
          <span class="muted">当前短板</span>
          <strong>{{ latestRow?.weakPoint || "--" }}</strong>
        </div>
        <div class="growth-note-item">
          <span class="muted">短板改善率</span>
          <strong>{{ rate(latestRow?.weakPointImproveRate) }}</strong>
        </div>
        <div class="growth-note-item">
          <span class="muted">能力提升</span>
          <strong>{{ latestRow?.abilityImprove || "--" }}</strong>
        </div>
        <div class="growth-note-item">
          <span class="muted">成长建议</span>
          <strong>{{ latestRow?.growthSuggest || "--" }}</strong>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const rows = ref<any[]>([]);
const activeCycle = ref(2);
const errorText = ref("");

const currentCycleRows = computed(() => {
  return rows.value.filter((r) => Number(r.statisticsCycle) === activeCycle.value);
});

const latestRow = computed(() => currentCycleRows.value[0] || rows.value[0] || null);

const summary = computed(() => {
  const list = currentCycleRows.value;
  if (!list.length) {
    return {
      interviewCount: 0,
      avgScore: "0",
      practiceQuestionCount: 0,
      totalTrainDuration: 0
    };
  }
  const interviewCount = list.reduce((s, r) => s + safeNum(r.interviewCount), 0);
  const practiceQuestionCount = list.reduce((s, r) => s + safeNum(r.practiceQuestionCount), 0);
  const totalTrainDuration = list.reduce((s, r) => s + safeNum(r.totalTrainDuration), 0);
  const avgScore = (list.reduce((s, r) => s + Number(r.avgScore || 0), 0) / list.length).toFixed(1);
  return { interviewCount, avgScore, practiceQuestionCount, totalTrainDuration };
});

const metric = computed(() => {
  const scoreBase = Number(latestRow.value?.avgScore || 0);
  const improve = Number(latestRow.value?.weakPointImproveRate || 0) * 100;
  return {
    tech: clamp(scoreBase),
    depth: clamp(scoreBase - 8),
    logic: clamp(scoreBase - 5),
    match: clamp(scoreBase - 3 + improve * 0.1),
    expression: clamp(scoreBase - 6 + improve * 0.15)
  };
});

function clamp(value: number) {
  return Math.max(0, Math.min(100, Math.round(value)));
}

function safeNum(v: any) {
  return Number(v || 0);
}

function score(v: any) {
  return Number(v || 0).toFixed(1);
}

function rate(v: any) {
  return `${(Number(v || 0) * 100).toFixed(1)}%`;
}

function cycleText(v: number) {
  if (v === 1) return "日";
  if (v === 3) return "月";
  return "周";
}

async function loadGrowth() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}`);
    rows.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    rows.value = [];
    errorText.value = e?.message || "成长数据加载失败";
  }
}

onMounted(loadGrowth);
</script>

<style scoped>
.growth-wrap {
  display: grid;
  gap: 12px;
}

.growth-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.growth-head h3 {
  margin: 0;
}

.growth-kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(120px, 1fr));
  gap: 10px;
}

.growth-kpi-card {
  border: 1px solid #e2eef7;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.growth-kpi-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #14466f;
}

.growth-two-col {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 12px;
}

.radar-list {
  display: grid;
  gap: 10px;
}

.radar-row {
  display: grid;
  grid-template-columns: 110px 1fr 40px;
  align-items: center;
  gap: 8px;
}

.radar-row span {
  font-size: 13px;
}

.radar-row b {
  text-align: right;
  font-size: 13px;
  color: #2b577b;
}

.radar-bar {
  height: 10px;
  border-radius: 999px;
  background: #eaf4fb;
  overflow: hidden;
}

.radar-bar i {
  display: block;
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #7ccbed, #3aaeff);
}

.growth-note-item {
  padding: 10px 0;
  border-bottom: 1px dashed #dfeef8;
  display: grid;
  gap: 4px;
}

.growth-note-item:last-child {
  border-bottom: 0;
}

@media (max-width: 1000px) {
  .growth-head {
    flex-direction: column;
    align-items: flex-start;
  }
  .growth-kpi-grid {
    grid-template-columns: repeat(2, minmax(120px, 1fr));
  }
  .growth-two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .growth-kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>

