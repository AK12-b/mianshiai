<template>
  <section class="card">
    <h3 style="margin-top: 0">面试报告</h3>

    <div class="toolbar">
      <button class="btn btn-outline" @click="loadInterviews">刷新列表</button>

      <select v-model.number="filter.mode" style="min-width: 160px">
        <option :value="0">全部模式</option>
        <option :value="1">全流程</option>
        <option :value="2">专项</option>
      </select>

      <select v-model.number="filter.status" style="min-width: 160px">
        <option :value="-1">全部状态</option>
        <option :value="0">待开始</option>
        <option :value="1">进行中</option>
        <option :value="2">已结束</option>
        <option :value="3">已暂停</option>
        <option :value="4">已终止</option>
      </select>

      <input v-model.trim="filter.postId" type="number" min="1" placeholder="岗位ID(可选)" />

      <label style="display: flex; align-items: center; gap: 8px">
        <input type="checkbox" v-model="onlyHasReport" />
        仅看已生成报告
      </label>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>面试ID</th>
          <th>岗位ID</th>
          <th>模式</th>
          <th>状态</th>
          <th>报告</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in currentPageRows" :key="row.interviewId">
          <td>{{ row.interviewId }}</td>
          <td>{{ row.postId ?? "--" }}</td>
          <td>{{ modeText(row.interviewMode) }}</td>
          <td><span class="status-badge">{{ statusText(getStatus(row)) }}</span></td>
          <td>
            <span class="status-badge">
              {{ hasEvaluate(row.interviewId) ? "已生成" : "未生成" }}
            </span>
          </td>
          <td>
            <button class="btn btn-text" @click="openReport(row.interviewId)">查看</button>
          </td>
        </tr>
        <tr v-if="!currentPageRows.length">
          <td class="empty-cell" colspan="6">暂无面试报告数据，请调整筛选或先创建面试。</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button class="btn btn-outline" @click="page = Math.max(1, page - 1)">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button class="btn btn-outline" @click="page = Math.min(totalPages, page + 1)">下一页</button>
    </div>

    <div v-if="reportModalVisible" class="modal-mask">
      <div class="modal-card">
        <h3>报告详情</h3>

        <div v-if="reportLoading" class="muted">正在加载报告...</div>

        <div v-else>
          <div style="padding: 0 0 10px; border-bottom: 1px solid #dfeef8">
            <span class="muted">总分</span>
            <strong>{{ reportData?.totalScore ?? "--" }}</strong>
          </div>

          <div style="padding: 10px 0; border-bottom: 1px dashed #dfeef8">
            <span class="muted">亮点</span>
            <strong>{{ reportData?.brightPoint || "--" }}</strong>
          </div>

          <div class="field">
            <label>问题分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.problemAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>差距分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.gapAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>建议指导</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.suggestGuide || "--" }}</pre>
          </div>

          <div class="field">
            <label>技术分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.techAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>知识深度分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.knowledgeDepthAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>逻辑严谨性分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.logicAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>表达能力分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.expressAnalysis || "--" }}</pre>
          </div>

          <div class="field">
            <label>岗位匹配度分析</label>
            <pre class="api-output" style="margin-top: 0; max-height: 180px">{{ reportData?.matchAnalysis || "--" }}</pre>
          </div>

          <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 10px">
            <button class="btn btn-outline" @click="reportModalVisible = false">关闭</button>
          </div>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watchEffect } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();

const interviews = ref<any[]>([]);
const errorText = ref("");

const pageSize = 6;
const page = ref(1);

const filter = ref({
  mode: 0,
  status: -1,
  postId: "" as any
});

const onlyHasReport = ref(false);

const evaluateCache = ref<Record<string, any | null>>({});

function getStatus(row: any) {
  return row.status ?? row.interviewStatus ?? row.interviewStatusCode ?? row.interviewStatus;
}

function modeText(mode: any) {
  const v = Number(mode);
  if (v === 1) return "全流程";
  if (v === 2) return "专项";
  return "--";
}

function statusText(status: any) {
  const v = Number(status);
  if (v === 1) return "进行中";
  if (v === 2) return "已结束";
  if (v === 3) return "已暂停";
  if (v === 4) return "已终止";
  return "待开始";
}

function hasEvaluate(interviewId: number) {
  return evaluateCache.value[String(interviewId)] !== undefined && evaluateCache.value[String(interviewId)] !== null;
}

const filtered = computed(() => {
  let list = Array.isArray(interviews.value) ? interviews.value : [];
  if (filter.value.mode) list = list.filter((r) => Number(r.interviewMode) === filter.value.mode);
  if (filter.value.status !== -1) list = list.filter((r) => Number(getStatus(r)) === filter.value.status);
  if (filter.value.postId) list = list.filter((r) => String(r.postId) === String(filter.value.postId));
  if (onlyHasReport.value) {
    // 避免“缓存尚未加载时列表为空”的死锁：unknown 状态先放行，等评估请求完成后再收敛筛选结果
    list = list.filter((r) => {
      const key = String(r.interviewId);
      const v = evaluateCache.value[key];
      if (v === null) return false; // 已确认不存在报告
      return true; // unknown 或已存在报告都先保留
    });
  }
  return list;
});

const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)));

const currentPageRows = computed(() => {
  const start = (page.value - 1) * pageSize;
  return filtered.value.slice(start, start + pageSize);
});

function markCacheUndefinedForPage() {
  for (const row of currentPageRows.value) {
    const key = String(row.interviewId);
    if (evaluateCache.value[key] === undefined) evaluateCache.value[key] = undefined;
  }
}

async function loadEvaluatesForCurrentPage() {
  const needRows = currentPageRows.value;
  for (const row of needRows) {
    const key = String(row.interviewId);
    if (evaluateCache.value[key] !== undefined) continue;
    try {
      const data = await app.apiRequest(`/api/interview/${row.interviewId}/evaluate`);
      evaluateCache.value[key] = data || null;
    } catch (e: any) {
      evaluateCache.value[key] = null;
    }
  }
}

const reportModalVisible = ref(false);
const reportLoading = ref(false);
const activeInterviewId = ref<number | null>(null);
const reportData = ref<any | null>(null);

async function openReport(interviewId: number) {
  reportModalVisible.value = true;
  reportLoading.value = true;
  activeInterviewId.value = interviewId;
  reportData.value = null;

  try {
    if (evaluateCache.value[String(interviewId)] !== undefined && evaluateCache.value[String(interviewId)] !== null) {
      reportData.value = evaluateCache.value[String(interviewId)];
      return;
    }
    const data = await app.apiRequest(`/api/interview/${interviewId}/evaluate`);
    evaluateCache.value[String(interviewId)] = data || null;
    reportData.value = data || null;
  } catch (e: any) {
    reportData.value = null;
  } finally {
    reportLoading.value = false;
  }
}

async function loadInterviews() {
  errorText.value = "";
  try {
    interviews.value = (await app.apiRequest(`/api/interview/user/${app.uid}`)) || [];
    page.value = 1;
    evaluateCache.value = {};
    await loadEvaluatesForCurrentPage();
  } catch (e: any) {
    interviews.value = [];
    errorText.value = e?.message || "加载面试记录失败";
  }
}

onMounted(async () => {
  await loadInterviews();
});

// 翻页/筛选后，更新当前页评估缓存
watchEffect(() => {
  // reset page if out of range
  if (page.value > totalPages.value) page.value = totalPages.value;
  markCacheUndefinedForPage();
  loadEvaluatesForCurrentPage();
});
</script>

