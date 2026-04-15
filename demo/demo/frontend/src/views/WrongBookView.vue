<template>
  <section class="card wb-wrap">
    <div class="wb-main">
      <section class="wb-top card">
        <div class="wb-sec-title">数据总览</div>
        <div class="wb-top-left">
          <div class="wb-top-h4">错误原因占比</div>
          <div class="wb-reason-stack">
            <div class="wb-donut" :style="reasonDonutStyle">
              <div class="wb-donut-inner">
                <strong>{{ wrongs.length }}</strong>
                <span class="muted">错题总数</span>
              </div>
            </div>
            <div class="wb-legend">
              <span v-for="x in reasonLegend" :key="x.key" class="wb-legend-item">
                <i class="wb-dot" :style="{ background: x.color }" />
                {{ x.label }} {{ x.percent }}%
              </span>
            </div>
          </div>
        </div>
        <div class="wb-top-right wb-kp-chart">
          <div class="wb-top-h4">知识点错题数量</div>
          <div v-if="knowledgeBars.length" class="wb-kp-bars-vertical">
            <div v-for="x in knowledgeBars" :key="x.name" class="wb-kp-col">
              <div class="wb-kp-vtrack">
                <span class="wb-kp-vfill" :style="{ height: `${x.percent}%` }" />
              </div>
              <span class="wb-kp-vcount">{{ x.count }}</span>
              <span class="wb-kp-vname" :title="x.name">{{ x.name }}</span>
            </div>
          </div>
          <div v-else class="muted">暂无知识点数据</div>
        </div>
      </section>

      <section class="wb-bottom card">
        <div class="wb-bottom-head">
          <div class="wb-sec-title">错题卡片</div>
          <button class="btn btn-primary" @click="openAddModal">新增错题</button>
        </div>
        <div class="wb-filters wb-filters-bottom">
          <input v-model.trim="keyword" placeholder="按知识点筛选" />
          <select v-model.number="reasonFilter">
            <option :value="0">全部错误类型</option>
            <option :value="1">知识点不熟</option>
            <option :value="2">审题失误</option>
            <option :value="3">表达不清</option>
            <option :value="4">其他原因</option>
          </select>
        </div>
        <div class="wb-card-grid-scroll">
          <div class="wb-card-grid">
          <article v-for="row in filteredRows" :key="row.wrongId" class="wb-card" @click="openSolveModal(row)">
            <div class="wb-card-head">
              <strong>题目 #{{ row.questionId }}</strong>
              <span class="wb-tag" :class="{ 'wb-tag-pending': !hasReason(row) }">
                {{ hasReason(row) ? reasonText(row.wrongReason) : "待选择类型" }}
              </span>
            </div>
            <p class="wb-title">{{ questionTitleById(row.questionId) }}</p>
            <div class="wb-meta muted"><span class="wb-kp-label">知识点</span>：{{ row.knowledgePoint || "--" }}</div>
            <div v-if="!hasReason(row)" class="wb-reason-picker">
              <select :value="pendingReasonMap[row.wrongId] || 1" @click.stop @change="onPendingReasonChange(row.wrongId, $event)">
                <option :value="1">知识点不熟</option>
                <option :value="2">审题失误</option>
                <option :value="3">表达不清</option>
                <option :value="4">其他原因</option>
              </select>
              <button class="btn btn-text" @click.stop="saveReason(row.wrongId)">保存类型</button>
            </div>
            <div class="wb-card-actions">
              <button class="btn btn-text" @click.stop="toggleCollect(row.wrongId)">
                {{ Number(row.isCollect) === 1 ? "取消收藏" : "收藏" }}
              </button>
              <button class="btn btn-text" @click.stop="openSolveModal(row)">去做题</button>
              <button class="btn btn-text" @click.stop="removeWrong(row.wrongId)">删除</button>
            </div>
          </article>
          <div v-if="!filteredRows.length" class="card muted">暂无匹配错题，请调整筛选或新增错题。</div>
          </div>
        </div>
      </section>
    </div>

    <div v-if="showAddModal" class="modal-mask">
      <div class="modal-card wb-modal">
        <h3>新增错题</h3>
        <div class="wb-add-form">
          <div class="field">
            <label>题目</label>
            <textarea v-model="addForm.questionTitle" rows="4" placeholder="请输入题目内容" />
          </div>
          <div class="field">
            <label>答案</label>
            <textarea v-model="addForm.questionAnswer" rows="4" placeholder="请输入参考答案" />
          </div>
          <div class="field">
            <label>知识点</label>
            <input v-model="addForm.knowledgePoint" placeholder="例如：Redis缓存、JVM调优" />
          </div>
          <div class="field">
            <label>错误原因</label>
            <select v-model.number="addForm.wrongReason">
              <option :value="1">知识点不熟</option>
              <option :value="2">审题失误</option>
              <option :value="3">表达不清</option>
              <option :value="4">其他原因</option>
            </select>
          </div>
        </div>
        <div class="wb-modal-actions">
          <button class="btn btn-outline" @click="showAddModal = false">取消</button>
          <button class="btn btn-primary" @click="submitAddWrong">保存</button>
        </div>
      </div>
    </div>

    <div v-if="showSolveModal && solvingRow" class="modal-mask">
      <div class="modal-card wb-modal">
        <h3>错题练习</h3>
        <div class="field">
          <label>题目</label>
          <div class="api-output" style="margin-top: 0">{{ questionTitleById(solvingRow.questionId) }}</div>
        </div>
        <div class="field">
          <label>我的答案</label>
          <textarea v-model="userAnswer" rows="4" placeholder="请输入你的回答" />
        </div>
        <div class="wb-modal-actions">
          <button class="btn btn-outline" @click="showSolveModal = false">关闭</button>
          <button class="btn btn-primary" @click="showResult = true">提交作答</button>
        </div>
        <div v-if="showResult" class="wb-result">
          <div><strong>正确答案：</strong>{{ questionAnswerById(solvingRow.questionId) }}</div>
          <div><strong>解析：</strong>{{ questionAnswerById(solvingRow.questionId) }}</div>
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
const errorText = ref("");
const keyword = ref("");
const reasonFilter = ref(0);
const showAddModal = ref(false);
const showSolveModal = ref(false);
const showResult = ref(false);
const solvingRow = ref<any>(null);
const userAnswer = ref("");
const pendingReasonMap = ref<Record<number, number>>({});
const questions = ref<any[]>([]);
const posts = ref<any[]>([]);
const addForm = ref({
  questionTitle: "",
  questionAnswer: "",
  knowledgePoint: "Redis缓存",
  wrongReason: 1,
  questionLevel: 1,
  questionType: 3
});
const wrongs = computed(() => (Array.isArray(app.lists.wrongs) ? app.lists.wrongs : []));
const filteredRows = computed(() => {
  return wrongs.value.filter((row) => {
    const kw = keyword.value.trim();
    const kwOk = !kw || String(row.knowledgePoint || "").includes(kw);
    const reasonOk = reasonFilter.value === 0 || Number(row.wrongReason || 0) === Number(reasonFilter.value);
    return kwOk && reasonOk;
  });
});
const reasonStats = computed(() => {
  const labeled = wrongs.value.filter((x) => hasReason(x));
  const total = labeled.length || 1;
  const count = { 1: 0, 2: 0, 3: 0, 4: 0 } as Record<number, number>;
  labeled.forEach((x) => {
    const k = Number(x.wrongReason) as 1 | 2 | 3 | 4;
    if (k >= 1 && k <= 4) count[k] += 1;
  });
  return {
    one: Math.round((count[1] / total) * 100),
    two: Math.round((count[2] / total) * 100),
    three: Math.round((count[3] / total) * 100),
    four: Math.max(0, 100 - Math.round((count[1] / total) * 100) - Math.round((count[2] / total) * 100) - Math.round((count[3] / total) * 100))
  };
});
const reasonLegend = computed(() => [
  { key: 1, label: "知识点不熟", percent: reasonStats.value.one, color: "#59bcf6" },
  { key: 2, label: "审题失误", percent: reasonStats.value.two, color: "#8b7cf6" },
  { key: 3, label: "表达不清", percent: reasonStats.value.three, color: "#f59e66" },
  { key: 4, label: "其他原因", percent: reasonStats.value.four, color: "#9ad287" }
]);
const knowledgeBars = computed(() => {
  const map = new Map<string, number>();
  wrongs.value.forEach((x) => {
    const name = String(x?.knowledgePoint || "").trim() || "未分类";
    map.set(name, (map.get(name) || 0) + 1);
  });
  const arr = Array.from(map.entries())
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 6);
  const max = arr.length ? arr[0].count : 1;
  return arr.map((x) => ({
    ...x,
    percent: Math.max(8, Math.round((x.count / max) * 100))
  }));
});
const reasonDonutStyle = computed(() => {
  const a = reasonStats.value.one;
  const b = reasonStats.value.two;
  const c = reasonStats.value.three;
  const d = reasonStats.value.four;
  const p1 = a;
  const p2 = a + b;
  const p3 = a + b + c;
  return {
    background: `conic-gradient(#59bcf6 0 ${p1}%, #8b7cf6 ${p1}% ${p2}%, #f59e66 ${p2}% ${p3}%, #9ad287 ${p3}% ${p3 + d}%)`
  };
});

async function loadWrongs() {
  errorText.value = "";
  try {
    app.lists.wrongs = (await app.apiRequest(`/api/wrong-question/user/${app.uid}`)) || [];
    // 若错题本为空：从已完成面试回填一批，确保可见数据
    if (!app.lists.wrongs?.length) {
      await app.apiRequest(`/api/wrong-question/user/${app.uid}/backfill`, { method: "POST" });
      app.lists.wrongs = (await app.apiRequest(`/api/wrong-question/user/${app.uid}`)) || [];
    }
    app.pager.wrongPage = 1;
  } catch (e: any) {
    app.lists.wrongs = [];
    errorText.value = e?.message || "查询失败";
  }
}

async function loadQuestions() {
  try {
    const data = await app.apiRequest("/api/question-bank/list");
    questions.value = Array.isArray(data) ? data : [];
  } catch {
    questions.value = [];
  }
}

async function loadPosts() {
  try {
    const data = await app.apiRequest("/api/post/list");
    posts.value = Array.isArray(data) ? data : [];
  } catch {
    posts.value = [];
  }
}

function openAddModal() {
  showAddModal.value = true;
}

async function submitAddWrong() {
  errorText.value = "";
  if (!String(addForm.value.questionTitle || "").trim() || !String(addForm.value.questionAnswer || "").trim()) {
    app.showToast("请填写题目和答案");
    return;
  }
  try {
    const postId = Number(posts.value[0]?.postId || 1);
    const created = await app.apiRequest("/api/question-bank/add", "POST", {
      postId,
      questionType: Number(addForm.value.questionType),
      questionLevel: Number(addForm.value.questionLevel),
      questionTitle: String(addForm.value.questionTitle || "").trim(),
      questionAnswer: String(addForm.value.questionAnswer || "").trim(),
      knowledgePoint: String(addForm.value.knowledgePoint || "").trim(),
      isAiGenerate: 0
    });

    const payload = {
      userId: app.uid,
      questionId: Number(created?.questionId),
      wrongReason: Number(addForm.value.wrongReason),
      knowledgePoint: String(addForm.value.knowledgePoint || "").trim()
    };
    await app.apiRequest("/api/wrong-question/add", "POST", payload);
    app.showToast("新增成功");
    showAddModal.value = false;
    addForm.value.questionTitle = "";
    addForm.value.questionAnswer = "";
    addForm.value.knowledgePoint = "Redis缓存";
    addForm.value.wrongReason = 1;
    await loadWrongs();
    await loadQuestions();
  } catch (e: any) {
    errorText.value = e?.message || "新增失败";
  }
}

async function toggleCollect(id: number) {
  errorText.value = "";
  try {
    await app.apiRequest(`/api/wrong-question/${id}/toggle-collect`, "POST");
    await loadWrongs();
  } catch (e: any) {
    errorText.value = e?.message || "操作失败";
  }
}

async function removeWrong(id: number) {
  if (!window.confirm("确认删除这条错题吗？")) return;
  errorText.value = "";
  try {
    await app.apiRequest(`/api/wrong-question/${id}`, "DELETE");
    await loadWrongs();
  } catch (e: any) {
    errorText.value = e?.message || "删除失败";
  }
}

function reasonText(reason: any) {
  const value = Number(reason);
  if (value === 2) return "审题失误";
  if (value === 3) return "表达不清";
  if (value === 4) return "其他原因";
  return "知识点不熟";
}

function hasReason(row: any) {
  const v = Number(row?.wrongReason ?? 0);
  return v >= 1 && v <= 4;
}

function onPendingReasonChange(wrongId: number, event: Event) {
  const value = Number((event.target as HTMLSelectElement).value || 1);
  pendingReasonMap.value[wrongId] = value;
}

async function saveReason(wrongId: number) {
  const wrongReason = Number(pendingReasonMap.value[wrongId] || 1);
  try {
    await app.apiRequest(`/api/wrong-question/${wrongId}/reason`, "PUT", { wrongReason });
    app.showToast("错误类型已更新");
    await loadWrongs();
  } catch (e: any) {
    errorText.value = e?.message || "更新类型失败";
  }
}

function questionTitleById(questionId: any) {
  const q = questions.value.find((x) => Number(x.questionId) === Number(questionId));
  return q?.questionTitle || "题目内容暂未加载";
}

function questionAnswerById(questionId: any) {
  const q = questions.value.find((x) => Number(x.questionId) === Number(questionId));
  return q?.questionAnswer || "暂无答案";
}

function openSolveModal(row: any) {
  solvingRow.value = row;
  userAnswer.value = "";
  showResult.value = false;
  showSolveModal.value = true;
}

onMounted(async () => {
  await loadWrongs();
  await loadQuestions();
  await loadPosts();
});
</script>

<style scoped>
.wb-wrap {
  display: block;
}
.wb-main {
  display: grid;
  gap: 14px;
  align-content: start;
}
.wb-top {
  display: grid;
  grid-template-columns: minmax(320px, 420px) 1fr;
  gap: 14px;
  align-items: start;
  padding: 14px;
}
.wb-top .wb-sec-title {
  grid-column: 1 / -1;
  margin: 0;
}
.wb-sec-title {
  font-weight: 700;
  color: #173a5e;
  margin-bottom: 8px;
}
.wb-top-left {
  display: grid;
  gap: 10px;
  align-content: start;
  min-height: 118px;
  padding-top: 2px;
}
.wb-top-h4 {
  margin: 0;
  color: #173a5e;
  font-weight: 700;
  line-height: 1.2;
}
.wb-reason-stack {
  display: flex;
  align-items: center;
  gap: 14px;
}
.wb-donut {
  width: 116px;
  height: 116px;
  flex: 0 0 116px;
  border-radius: 50%;
  display: grid;
  place-items: center;
}
.wb-donut-inner {
  width: 82px;
  height: 82px;
  border-radius: 50%;
  background: #fff;
  display: grid;
  place-items: center;
  text-align: center;
  line-height: 1.2;
}
.wb-donut-inner strong {
  font-size: 22px;
  color: #1f3854;
}
.wb-donut-inner .muted {
  font-size: 12px;
  white-space: nowrap;
}
.wb-top-right {
  display: grid;
  gap: 10px;
  align-content: start;
  padding-top: 2px;
}
.wb-kp-chart {
  align-self: start;
  margin-top: 12px;
}
.wb-filters {
  display: grid;
  grid-template-columns: 1fr 200px;
  gap: 10px;
}
.wb-filters-bottom {
  margin-bottom: 10px;
}
.wb-filters input,
.wb-filters select {
  border: 1px solid #d9e6f1;
  border-radius: 10px;
  padding: 9px 10px;
}
.wb-bottom {
  display: grid;
  gap: 10px;
  align-content: start;
  min-height: 320px;
  padding: 14px;
}
.wb-bottom-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.wb-card-grid-scroll {
  max-height: min(58vh, 680px);
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
  -webkit-overflow-scrolling: touch;
}
.wb-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 12px;
}
.wb-kp-chart .wb-top-h4 {
  margin-bottom: 6px;
}
.wb-kp-bars-vertical {
  display: flex;
  align-items: flex-end;
  gap: 14px;
  min-height: 130px;
  padding-top: 0;
}
.wb-kp-col {
  width: 52px;
  display: grid;
  justify-items: center;
  gap: 6px;
}
.wb-kp-vtrack {
  width: 24px;
  height: 92px;
  border-radius: 8px;
  background: #eaf3fb;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}
.wb-kp-vfill {
  width: 100%;
  border-radius: 8px 8px 0 0;
  background: linear-gradient(180deg, #7ac8fa, #2b8fd9);
}
.wb-kp-vcount {
  font-size: 12px;
  color: #5f7690;
  line-height: 1;
}
.wb-kp-vname {
  width: 88px;
  font-size: 12px;
  color: #3f5972;
  text-align: center;
  line-height: 1.2;
  white-space: normal;
  word-break: break-word;
}
.wb-kp-label {
  display: inline-block;
  color: #7b8cff;
  background: #f2eeff;
  border: 1px solid #e7e2ff;
  border-radius: 999px;
  padding: 1px 7px;
  font-size: 12px;
  margin-right: 2px;
}
.wb-card {
  border: 1px solid #dcecf8;
  border-radius: 12px;
  background: #fff;
  padding: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
.wb-card:hover {
  box-shadow: 0 8px 20px rgba(47, 99, 153, 0.12);
  transform: translateY(-1px);
}
.wb-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.wb-title {
  margin: 8px 0;
  line-height: 1.5;
  color: #173a5e;
}
.wb-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 12px;
  color: #0d6db6;
  background: rgba(89, 188, 246, 0.16);
}
.wb-tag-pending {
  color: #9b6b00;
  background: rgba(250, 193, 66, 0.2);
}
.wb-reason-picker {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.wb-reason-picker select {
  border: 1px solid #d9e6f1;
  border-radius: 8px;
  padding: 6px 8px;
  min-width: 140px;
}
.wb-card-actions {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.wb-legend {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.wb-legend-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #4a6279;
}
.wb-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}
.wb-modal {
  width: min(980px, 95vw);
  border-radius: 16px;
  background: #f4f7fb;
  border: 1px solid #dbe5f0;
  color: #274863;
}
.wb-modal h3 {
  margin: 0 0 12px 0;
  color: #123a5c;
  font-size: 20px;
  font-weight: 700;
}
.wb-modal :deep(.field > label) {
  color: #123a5c;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 8px;
}
.wb-modal :deep(.api-output) {
  border-radius: 10px;
  background: #edf3f9;
  border: 1px solid #dce7f2;
  padding: 10px 14px;
  color: #4b647d;
  line-height: 1.6;
  font-size: 14px;
}
.wb-modal :deep(textarea),
.wb-modal :deep(input),
.wb-modal :deep(select) {
  background: #fff;
  border: 1px solid #d6e4f1;
  border-radius: 10px;
}
.wb-add-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  max-height: 62vh;
  overflow: auto;
  padding-right: 2px;
}
.wb-modal-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.wb-result {
  margin-top: 12px;
  border-top: 1px dashed #dce7f2;
  padding-top: 10px;
  display: grid;
  gap: 8px;
  color: #4b647d;
}
@media (max-width: 1080px) {
  .wb-top {
    grid-template-columns: 1fr;
  }
  .wb-kp-chart {
    margin-top: 0;
  }
  .wb-card-grid {
    grid-template-columns: 1fr 1fr;
  }
}
@media (max-width: 760px) {
  .wb-add-form,
  .wb-filters,
  .wb-card-grid {
    grid-template-columns: 1fr;
  }
  .wb-bottom-head {
    flex-direction: column;
    align-items: stretch;
  }
  .wb-bottom-head .btn {
    width: 100%;
  }
  .wb-kp-bars-vertical {
    gap: 10px;
  }
  .wb-kp-col {
    width: 44px;
  }
  .wb-kp-vname {
    width: 72px;
  }
}
</style>
