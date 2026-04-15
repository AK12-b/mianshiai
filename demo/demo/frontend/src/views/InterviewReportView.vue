<template>
  <section class="card">
    <h3 style="margin-top: 0">面试报告</h3>

    <div class="toolbar">
      <select v-model.number="filter.mode" style="min-width: 160px">
        <option :value="0">全部模式</option>
        <option :value="1">全流程</option>
        <option :value="2">专项</option>
      </select>

      <select v-model.number="filter.status" style="min-width: 160px">
        <option :value="-1">全部状态</option>
        <option :value="0">待开始</option>
        <option :value="1">进行中</option>
        <option :value="2">已完成</option>
        <option :value="3">已终止</option>
        <option :value="4">已暂停</option>
      </select>

      <select v-model.trim="filter.postId" style="min-width: 180px">
        <option value="">全部岗位</option>
        <option v-for="p in posts" :key="p.postId" :value="String(p.postId)">{{ p.postName }}</option>
      </select>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>岗位</th>
          <th>模式</th>
          <th>面试模块</th>
          <th>面试时长</th>
          <th>创建时间</th>
          <th>状态</th>
          <th>报告</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in currentPageRows" :key="row.interviewId">
          <td>{{ postNameById(row.postId) }}</td>
          <td>{{ modeText(row.interviewMode) }}</td>
          <td>{{ moduleText(row.interviewModule) }}</td>
          <td>{{ durationText(row.interviewDuration) }}</td>
          <td>{{ timeText(row.createTime ?? row.interviewCreateTime) }}</td>
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
          <td class="empty-cell" colspan="8">暂无面试报告数据，请调整筛选或先创建面试。</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button class="btn btn-outline" @click="page = Math.max(1, page - 1)">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button class="btn btn-outline" @click="page = Math.min(totalPages, page + 1)">下一页</button>
    </div>

    <Teleport to="body">
      <div
        v-if="reportModalVisible"
        class="modal-mask report-modal-mask"
        @wheel.prevent
        @touchmove.prevent
      >
        <div class="modal-card report-modal-card" @wheel.stop @touchmove.stop>
          <div class="report-modal-header">
            <h2 class="report-main-title">面试评估报告</h2>
          </div>

          <div class="report-modal-body">
            <div v-if="reportLoading" class="muted report-loading">正在加载报告...</div>

            <template v-else>
              <div class="report-score-section">
                <div class="report-total-score">
                  <span class="report-total-label">总分</span>
                  <strong class="report-total-value">{{ reportData?.totalScore ?? "--" }}</strong>
                </div>
                <div class="report-dims-label">维度评分</div>
                <div class="score-grid">
                  <div class="score-item">
                    <span class="muted">技术</span><strong>{{ reportData?.techScore ?? "--" }}</strong>
                  </div>
                  <div class="score-item">
                    <span class="muted">深度</span><strong>{{ reportData?.knowledgeDepthScore ?? "--" }}</strong>
                  </div>
                  <div class="score-item">
                    <span class="muted">逻辑</span><strong>{{ reportData?.logicScore ?? "--" }}</strong>
                  </div>
                  <div class="score-item">
                    <span class="muted">表达</span><strong>{{ reportData?.expressScore ?? "--" }}</strong>
                  </div>
                  <div class="score-item">
                    <span class="muted">匹配</span><strong>{{ reportData?.matchScore ?? "--" }}</strong>
                  </div>
                </div>
              </div>

              <div class="report-split">
                <div class="report-col report-col-records">
                  <div class="report-section-title">详细面试记录</div>
                  <p class="report-section-hint muted">题目、我的回答与参考回答</p>
                  <div v-if="qaLoading" class="muted">正在加载问答详情...</div>
                  <div v-else class="qa-card-list">
                    <div v-for="(qa, idx) in reportQaList" :key="qa.dialogId || idx" class="qa-card">
                      <div class="qa-title">问题 {{ idx + 1 }}</div>
                      <div class="qa-row">
                        <div class="qa-label">面试问题</div>
                        <div class="qa-content">{{ qa.question || "--" }}</div>
                      </div>
                      <div class="qa-row">
                        <div class="qa-label">我的回答</div>
                        <div class="qa-content">{{ qa.userAnswer || "（本题未作答）" }}</div>
                      </div>
                      <div class="qa-row">
                        <div class="qa-label">参考回答</div>
                        <div class="qa-content qa-ref">{{ qa.referenceAnswer || "--" }}</div>
                      </div>
                    </div>
                    <div v-if="!reportQaList.length" class="muted">暂无问答记录</div>
                  </div>
                </div>

                <div class="report-col report-col-analysis">
                  <div class="report-section-title">综合分析</div>
                  <p class="report-section-hint muted">亮点、问题、各维度解读与岗位差距</p>

                  <div class="analysis-block">
                    <div class="analysis-block-title">面试亮点</div>
                    <pre class="analysis-pre">{{ reportData?.brightPoint || "--" }}</pre>
                  </div>
                  <div class="analysis-block">
                    <div class="analysis-block-title">问题分析</div>
                    <pre class="analysis-pre">{{ reportData?.problemAnalysis || "--" }}</pre>
                  </div>
                  <div class="analysis-block analysis-block-dims">
                    <div class="analysis-block-title">各维度得分分析</div>
                    <div class="dim-analysis-row">
                      <span class="dim-analysis-name">技术</span>
                      <pre class="analysis-pre analysis-pre-inline">{{ reportData?.techAnalysis || "--" }}</pre>
                    </div>
                    <div class="dim-analysis-row">
                      <span class="dim-analysis-name">深度</span>
                      <pre class="analysis-pre analysis-pre-inline">{{ reportData?.knowledgeDepthAnalysis || "--" }}</pre>
                    </div>
                    <div class="dim-analysis-row">
                      <span class="dim-analysis-name">逻辑</span>
                      <pre class="analysis-pre analysis-pre-inline">{{ reportData?.logicAnalysis || "--" }}</pre>
                    </div>
                    <div class="dim-analysis-row">
                      <span class="dim-analysis-name">表达</span>
                      <pre class="analysis-pre analysis-pre-inline">{{ reportData?.expressAnalysis || "--" }}</pre>
                    </div>
                    <div class="dim-analysis-row">
                      <span class="dim-analysis-name">匹配</span>
                      <pre class="analysis-pre analysis-pre-inline">{{ reportData?.matchAnalysis || "--" }}</pre>
                    </div>
                  </div>
                  <div class="analysis-block">
                    <div class="analysis-block-title">岗位差距分析</div>
                    <pre class="analysis-pre">{{ reportData?.gapAnalysis || "--" }}</pre>
                  </div>
                  <div class="analysis-block">
                    <div class="analysis-block-title">学习建议</div>
                    <pre class="analysis-pre">{{ reportData?.suggestGuide || "--" }}</pre>
                  </div>
                </div>
              </div>
            </template>
          </div>

          <div class="report-modal-footer">
            <button type="button" class="btn btn-outline" @click="reportModalVisible = false">关闭</button>
          </div>
        </div>
      </div>
    </Teleport>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch, watchEffect } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const posts = ref<any[]>([]);

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
  const raw = row.interviewStatus ?? row.interviewStatusCode ?? row.status;
  const n = Number(raw);
  return Number.isFinite(n) ? n : 0;
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
  if (v === 2) return "已完成";
  if (v === 3) return "已终止";
  if (v === 4) return "已暂停";
  return "待开始";
}

function hasEvaluate(interviewId: number) {
  return evaluateCache.value[String(interviewId)] !== undefined && evaluateCache.value[String(interviewId)] !== null;
}

function postNameById(postId: any) {
  const hit = posts.value.find((p) => String(p.postId) === String(postId));
  return hit?.postName || "--";
}

function moduleText(v: any) {
  const text = String(v || "").trim();
  return text || "--";
}

function durationText(v: any) {
  const n = Number(v);
  if (!Number.isFinite(n) || n <= 0) return "--";
  return `${n} 分钟`;
}

function timeText(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 19);
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
const qaLoading = ref(false);
const reportQaList = ref<any[]>([]);

const prevBodyOverflow = ref("");
const prevHtmlOverflow = ref("");
const prevBodyPosition = ref("");
const prevBodyTop = ref("");
const prevBodyWidth = ref("");
const prevHtmlPosition = ref("");
const prevHtmlTop = ref("");
const prevHtmlWidth = ref("");
const lockScrollY = ref(0);

function buildQaFromDialogs(dialogs: any[]) {
  const list = Array.isArray(dialogs) ? dialogs : [];
  const result: any[] = [];
  for (let i = 0; i < list.length; i++) {
    const d = list[i];
    if (Number(d?.speaker) !== 1) continue;
    const question = String(d?.contentText || "").trim();
    if (!question) continue;
    let userAnswer = "";
    for (let j = i + 1; j < list.length; j++) {
      const nd = list[j];
      if (Number(nd?.speaker) === 1) break;
      if (Number(nd?.speaker) === 2) {
        userAnswer = String(nd?.contentText || "");
        break;
      }
    }
    result.push({
      dialogId: d?.dialogId,
      question,
      userAnswer,
      referenceAnswer: "参考回答生成中（可稍后刷新报告查看）"
    });
  }
  return result;
}

async function openReport(interviewId: number) {
  reportModalVisible.value = true;
  reportLoading.value = true;
  qaLoading.value = true;
  activeInterviewId.value = interviewId;
  reportData.value = null;
  reportQaList.value = [];

  try {
    // 并行拉取：评估 + 报告问答详情（问题/我的回答/参考回答）+ dialogs兜底
    const qaPromise = app
      .apiRequest(`/api/interview/${interviewId}/report-detail`)
      .then((d) => (Array.isArray(d) ? d : []))
      .catch(() => []);
    const dialogsPromise = app
      .apiRequest(`/api/interview/${interviewId}/dialogs`)
      .then((d) => (Array.isArray(d) ? d : []))
      .catch(() => []);

    // 强制重新拉取一次评估，避免命中旧缓存导致一直显示“--”
    const data = await app.apiRequest(`/api/interview/${interviewId}/evaluate`);
    evaluateCache.value[String(interviewId)] = data || null;
    reportData.value = data || null;
    const [qa, dialogs] = await Promise.all([qaPromise, dialogsPromise]);
    reportQaList.value = qa.length ? qa : buildQaFromDialogs(dialogs);
  } catch (e: any) {
    reportData.value = null;
  } finally {
    reportLoading.value = false;
    qaLoading.value = false;
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

async function loadPosts() {
  try {
    posts.value = (await app.apiRequest("/api/post/list")) || [];
  } catch {
    posts.value = [];
  }
}

onMounted(async () => {
  await loadPosts();
  await loadInterviews();
});

watch(
  () => reportModalVisible.value,
  (visible) => {
    if (visible) {
      lockScrollY.value = window.scrollY || window.pageYOffset || 0;
      prevBodyOverflow.value = document.body.style.overflow || "";
      prevHtmlOverflow.value = document.documentElement.style.overflow || "";
      prevBodyPosition.value = document.body.style.position || "";
      prevBodyTop.value = document.body.style.top || "";
      prevBodyWidth.value = document.body.style.width || "";
      prevHtmlPosition.value = document.documentElement.style.position || "";
      prevHtmlTop.value = document.documentElement.style.top || "";
      prevHtmlWidth.value = document.documentElement.style.width || "";
      document.body.style.overflow = "hidden";
      document.documentElement.style.overflow = "hidden";
      // 同时锁定 body 与 html，避免滚轮穿透到底层页面容器
      document.body.style.position = "fixed";
      document.body.style.top = `-${lockScrollY.value}px`;
      document.body.style.width = "100%";
      document.documentElement.style.position = "fixed";
      document.documentElement.style.top = `-${lockScrollY.value}px`;
      document.documentElement.style.width = "100%";
      return;
    }
    document.body.style.overflow = prevBodyOverflow.value;
    document.documentElement.style.overflow = prevHtmlOverflow.value;
    document.body.style.position = prevBodyPosition.value;
    document.body.style.top = prevBodyTop.value;
    document.body.style.width = prevBodyWidth.value;
    document.documentElement.style.position = prevHtmlPosition.value;
    document.documentElement.style.top = prevHtmlTop.value;
    document.documentElement.style.width = prevHtmlWidth.value;
    window.scrollTo(0, lockScrollY.value);
  }
);

onBeforeUnmount(() => {
  document.body.style.overflow = prevBodyOverflow.value;
  document.documentElement.style.overflow = prevHtmlOverflow.value;
  document.body.style.position = prevBodyPosition.value;
  document.body.style.top = prevBodyTop.value;
  document.body.style.width = prevBodyWidth.value;
  document.documentElement.style.position = prevHtmlPosition.value;
  document.documentElement.style.top = prevHtmlTop.value;
  document.documentElement.style.width = prevHtmlWidth.value;
});

// 翻页/筛选后，更新当前页评估缓存
watchEffect(() => {
  // reset page if out of range
  if (page.value > totalPages.value) page.value = totalPages.value;
  markCacheUndefinedForPage();
  loadEvaluatesForCurrentPage();
});
</script>

<style scoped>
.report-modal-mask {
  align-items: center;
  overflow: hidden;
  overscroll-behavior: contain;
}

.report-modal-card {
  width: min(1120px, 94vw) !important;
  height: min(86vh, 900px);
  max-height: min(86vh, 900px) !important;
  overflow: hidden !important;
  display: flex;
  flex-direction: column;
}

.report-modal-header {
  flex: 0 0 auto;
  text-align: center;
  padding: 14px 20px 12px;
  border-bottom: 1px solid #e9f1f7;
  background: linear-gradient(180deg, #fafdff 0%, #fff 100%);
}

.report-main-title {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #173a5e;
  letter-spacing: 0.02em;
}

.report-modal-body {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0 16px;
  overscroll-behavior: contain;
}

.report-loading {
  padding: 24px;
  text-align: center;
}

.report-score-section {
  flex: 0 0 auto;
  text-align: center;
  padding: 14px 0 16px;
  border-bottom: 1px dashed #dfeef8;
}

.report-total-score {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 10px;
  margin-bottom: 12px;
}

.report-total-label {
  font-size: 15px;
  color: #5a7a94;
}

.report-total-value {
  font-size: 28px;
  font-weight: 800;
  color: #1177c8;
  line-height: 1;
}

.report-dims-label {
  font-size: 12px;
  color: #6a879c;
  margin-bottom: 8px;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  max-width: 720px;
  margin: 0 auto;
}

.score-item {
  border: 1px solid #e2eef7;
  border-radius: 12px;
  padding: 8px 10px;
  background: linear-gradient(180deg, #ffffff, #f7fcff);
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.report-split {
  flex: 1 1 auto;
  min-height: 0;
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 16px;
  padding: 14px 0 12px;
}

.report-col {
  min-height: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
  padding-right: 4px;
}

.report-col-records {
  border-right: 1px solid #e8f2f9;
  padding-right: 16px;
}

.report-section-title {
  font-size: 14px;
  font-weight: 800;
  color: #155985;
  margin: 0 0 4px;
}

.report-section-hint {
  font-size: 12px;
  margin: 0 0 12px;
}

.analysis-block {
  margin-bottom: 14px;
}

.analysis-block-title {
  font-size: 12px;
  font-weight: 700;
  color: #173a5e;
  margin-bottom: 6px;
}

.analysis-pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.65;
  color: #2c4a63;
  background: #f3f7fb;
  border: 1px solid #e2eef7;
  border-radius: 10px;
  padding: 10px 12px;
  max-height: 220px;
  overflow-y: auto;
}

.analysis-block-dims .analysis-pre {
  max-height: 140px;
}

.dim-analysis-row {
  display: grid;
  grid-template-columns: 40px 1fr;
  gap: 8px;
  align-items: start;
  margin-bottom: 8px;
}

.dim-analysis-row:last-child {
  margin-bottom: 0;
}

.dim-analysis-name {
  font-size: 11px;
  font-weight: 700;
  color: #1177c8;
  padding-top: 10px;
}

.analysis-pre-inline {
  max-height: 120px;
}

.report-modal-footer {
  flex: 0 0 auto;
  display: flex;
  justify-content: flex-end;
  padding: 10px 16px 14px;
  border-top: 1px solid #e9f1f7;
  background: #fff;
}

.qa-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.qa-card {
  border: 1px solid #dcecf8;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcff 100%);
  padding: 12px;
}

.qa-title {
  font-weight: 700;
  color: #155985;
  margin-bottom: 8px;
}

.qa-row + .qa-row {
  margin-top: 10px;
}

.qa-label {
  font-size: 12px;
  color: #6a879c;
  margin-bottom: 4px;
}

.qa-content {
  white-space: pre-wrap;
  line-height: 1.7;
  background: #fff;
  border: 1px solid #e8f2f9;
  border-radius: 10px;
  padding: 8px 10px;
}

.qa-ref {
  background: #f3faff;
  border-color: #d7eefe;
}

@media (max-width: 900px) {
  .report-split {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .report-col-records {
    border-right: none;
    padding-right: 4px;
    border-bottom: 1px solid #e8f2f9;
    padding-bottom: 16px;
  }

  .score-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 520px) {
  .score-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
