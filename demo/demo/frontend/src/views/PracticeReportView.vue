<template>
  <section class="card">
    <div class="dashboard-section-title">
      <h3 style="margin: 0">专项练习分析报告</h3>
      <div style="display:flex; gap:8px">
        <button class="btn btn-outline" @click="backToPractice">返回专项练习</button>
      </div>
    </div>

    <div class="muted" style="margin-top: 8px">
      练习任务：{{ singleId }} ｜ 主任务：{{ masterId || "--" }} ｜ 完成时间：{{ fmtTime(report?.createTime) }}
    </div>

    <div class="grid-3" style="margin-top: 12px">
      <div class="card">
        <div class="muted">及格率</div>
        <div style="font-size:28px; font-weight:800">{{ correctRate }}%</div>
      </div>
      <div class="card">
        <div class="muted">错题数量</div>
        <div style="font-size:28px; font-weight:800">{{ wrongCount }}</div>
      </div>
      <div class="card">
        <div class="muted">平均分</div>
        <div style="font-size:28px; font-weight:800">{{ avgScore }}</div>
      </div>
    </div>

    <div class="card" style="margin-top: 12px">
      <h4 style="margin:0 0 8px">错题列表与解析</h4>
      <table class="data-table">
        <thead>
          <tr>
            <th>题目</th>
            <th>错误类型</th>
            <th>评分均值</th>
            <th>建议</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in wrongRecords" :key="r.recordId">
            <td>{{ r.questionId }}</td>
            <td>{{ errorTypeText(r.errorType) }}</td>
            <td>{{ avgOfRecord(r) }}</td>
            <td>建议加入错题本并复盘同知识点题目</td>
          </tr>
          <tr v-if="!wrongRecords.length">
            <td colspan="4" class="empty-cell">本轮暂无错题，继续保持！</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="card" style="margin-top: 12px">
      <h4 style="margin:0 0 8px">优化建议</h4>
      <pre class="api-output report-suggest-output" style="margin-top:0">{{ report?.improveSuggest || "暂无建议" }}</pre>
      <div style="display:flex; justify-content:flex-end; margin-top:8px">
        <button class="btn btn-primary" @click="goWrongBook">去错题本复盘</button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();
const report = ref<any>(null);
const records = ref<any[]>([]);
const singleId = computed(() => Number(route.params.singleId));
const masterId = computed(() => Number(route.query.masterId || 0));

const wrongRecords = computed(() => records.value.filter((r) => Number(r.errorType || 0) !== 0));
const wrongCount = computed(() => wrongRecords.value.length);
const correctRate = computed(() => (records.value.length ? Math.round(((records.value.length - wrongCount.value) / records.value.length) * 100) : 0));
const avgScore = computed(() => {
  if (!records.value.length) return 0;
  const sum = records.value.reduce((s, r) => s + avgOfRecord(r), 0);
  return Math.round((sum / records.value.length) * 100) / 100;
});

function avgOfRecord(r: any) {
  const vals = [r.techScore, r.knowledgeDepthScore, r.logicScore, r.expressScore, r.matchScore].map((n) => Number(n || 0));
  return Math.round((vals.reduce((a, b) => a + b, 0) / 5) * 100) / 100;
}

function errorTypeText(v: any) {
  const n = Number(v || 0);
  if (n === 1) return "概念错误";
  if (n === 2) return "逻辑问题";
  if (n === 3) return "遗漏要点";
  return "可通过";
}

function fmtTime(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 19);
}

function backToPractice() {
  router.push("/app/practice");
}

function goWrongBook() {
  router.push("/app/wrong-book");
}

async function loadData() {
  try {
    report.value = await app.apiRequest(`/api/practice/report/single/${singleId.value}`);
  } catch {
    report.value = null;
  }
  try {
    const arr = await app.apiRequest(`/api/practice/question-records/single/${singleId.value}`);
    records.value = Array.isArray(arr) ? arr : [];
  } catch {
    records.value = [];
  }
}

onMounted(loadData);
</script>

<style scoped>
.report-suggest-output {
  background: linear-gradient(180deg, #ffffff 0%, #f8fcff 100%);
  color: #24384d;
  border: 1px solid #cfe1ef;
  border-left: 4px solid #78b8e6;
  border-radius: 12px;
  padding: 14px 16px;
  min-height: 92px;
  line-height: 1.8;
  font-size: 13px;
  letter-spacing: 0.2px;
  box-shadow: 0 6px 16px rgba(18, 70, 112, 0.08);
}
</style>

