<template>
  <section class="card">
    <h3 style="margin-top: 0">学习资源 / 题库管理</h3>
    <div class="toolbar">
      <button class="btn btn-primary" @click="openCreate">新增题目</button>
      <button class="btn btn-outline" @click="loadAll">刷新数据</button>
      <div class="rv-tabs">
        <button class="btn" :class="activeMode === 'knowledge' ? 'btn-primary' : 'btn-outline'" @click="activeMode = 'knowledge'">
          按知识点
        </button>
        <button class="btn" :class="activeMode === 'question' ? 'btn-primary' : 'btn-outline'" @click="activeMode = 'question'">
          按题目
        </button>
      </div>
    </div>

    <div v-if="activeMode === 'knowledge'" class="rv-sections-scroll">
      <div class="rv-sections">
        <section v-for="sec in sectionCards" :key="sec.key" class="rv-section">
          <div class="rv-section-head">
            <h3>{{ sec.title }}</h3>
          </div>
          <div class="rv-grid">
            <article
              v-for="kp in sec.items"
              :key="`${sec.key}-${kp.name}-${kp.knowledgeKey || ''}`"
              class="rv-card"
              @click="openKnowledgeKbModal(kp.knowledgeKey ?? kp.name)"
            >
              <div class="rv-card-head">
                <h4>{{ kp.name }}</h4>
                <span class="rv-count">{{ kp.count }}</span>
              </div>
              <p class="muted">点击查看该知识点内容</p>
            </article>
          </div>
        </section>
        <div v-if="!hasAnyCards" class="card muted">暂无知识点数据，请新增题目或先进行面试沉淀。</div>
      </div>
    </div>

    <div v-else class="rv-sections-scroll">
      <div class="rv-sections">
        <section v-for="sec in questionSectionCards" :key="sec.key" class="rv-section">
          <div class="rv-section-head">
            <h3>{{ sec.title }}</h3>
          </div>
          <div class="rv-grid">
            <article
              v-for="kp in sec.items"
              :key="`${sec.key}-${kp.name}-${kp.knowledgeKey || ''}`"
              class="rv-card"
              @click="openKnowledgeQuestionModal(kp.knowledgeKey ?? kp.name)"
            >
              <div class="rv-card-head">
                <h4>{{ kp.name }}</h4>
                <span class="rv-count">{{ kp.count }}</span>
              </div>
              <p class="muted">点击查看该分类下全部题目与答案</p>
            </article>
          </div>
        </section>
        <div v-if="!hasAnyQuestionCards" class="card muted">暂无题目数据，请新增题目或先生成题库。</div>
      </div>
    </div>

    <div v-if="knowledgeKbModalVisible" class="modal-mask">
      <div class="modal-card rv-detail-modal">
        <h3>{{ currentKnowledgePoint }}（{{ currentKnowledges.length }}条）</h3>
        <div class="rv-question-list">
          <article v-for="row in currentKnowledges" :key="row.knowledgeId" class="rv-question-item">
            <div class="rv-q-head">
              <strong>{{ row.knowledgeName || "--" }}</strong>
            </div>
            <div class="field">
              <label class="rv-label-plain">知识点内容</label>
              <div class="api-output" style="margin-top: 0; white-space: pre-wrap">{{ row.knowledgeContent || "--" }}</div>
            </div>
          </article>
          <div v-if="!currentKnowledges.length" class="muted">该知识点暂无内容。</div>
        </div>
        <div style="display: flex; justify-content: flex-end; margin-top: 10px">
          <button class="btn btn-outline" @click="knowledgeKbModalVisible = false">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="knowledgeQuestionModalVisible" class="modal-mask">
      <div class="modal-card rv-detail-modal">
        <h3>{{ currentKnowledgePoint }}（{{ currentQuestions.length }}题）</h3>
        <div class="rv-question-list">
          <article v-for="row in currentQuestions" :key="row.questionId" class="rv-question-item">
            <div class="rv-q-head">
              <strong>{{ typeText(row.questionType) }} · {{ levelText(row.questionLevel) }}</strong>
              <div style="display: flex; gap: 8px">
                <button class="btn btn-text" @click="openEdit(row)">编辑</button>
                <button class="btn btn-text" @click="removeQuestion(row.questionId)">删除</button>
              </div>
            </div>
            <div class="field">
              <label>题目</label>
              <div class="api-output" style="margin-top: 0">{{ row.questionTitle || "--" }}</div>
            </div>
            <div class="field">
              <label>答案</label>
              <div class="api-output" style="margin-top: 0">{{ row.questionAnswer || "--" }}</div>
            </div>
          </article>
          <div v-if="!currentQuestions.length" class="muted">该知识点暂无题目。</div>
        </div>
        <div style="display: flex; justify-content: flex-end; margin-top: 10px">
          <button class="btn btn-outline" @click="knowledgeQuestionModalVisible = false">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="modalVisible" class="modal-mask">
      <div class="modal-card">
        <h3>{{ modalTitle }}</h3>
        <div class="config-grid">
          <div class="field">
            <label>岗位编号</label>
            <input v-model.number="modalForm.postId" type="number" min="1" :disabled="modalMode === 'edit'" />
          </div>
          <div class="field">
            <label>题目类型</label>
            <select v-model.number="modalForm.questionType">
              <option :value="1">单选题</option>
              <option :value="2">多选题</option>
              <option :value="3">简答题</option>
            </select>
          </div>
          <div class="field">
            <label>难度等级</label>
            <select v-model.number="modalForm.questionLevel">
              <option :value="1">初级</option>
              <option :value="2">中级</option>
              <option :value="3">高级</option>
            </select>
          </div>
          <div class="field">
            <label>知识点</label>
            <input v-model="modalForm.knowledgePoint" placeholder="如：JVM、Redis、Spring" />
          </div>
          <div class="field">
            <label>AI生成</label>
            <select v-model.number="modalForm.isAiGenerate" :disabled="modalMode === 'edit'">
              <option :value="0">否</option>
              <option :value="1">是</option>
            </select>
          </div>
        </div>
        <div class="field">
          <label>题目内容</label>
          <textarea v-model="modalForm.questionTitle" rows="4" placeholder="请输入题目标题/题干" />
        </div>
        <div class="field">
          <label>参考答案</label>
          <textarea v-model="modalForm.questionAnswer" rows="5" placeholder="请输入参考答案" />
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 10px">
          <button class="btn btn-outline" @click="modalVisible = false">取消</button>
          <button class="btn btn-primary" :disabled="!canSubmit" @click="submit">保存</button>
        </div>
      </div>
    </div>
    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

/** 卡片展示名与题库 knowledge_point 可能不同（如「技术基础」改为「综合题库」） */
type KpCard = { name: string; count: number; knowledgeKey?: string };

const app = useAppStore();
const errorText = ref("");
const activeMode = ref<"knowledge" | "question">("knowledge");
const knowledgeKbModalVisible = ref(false);
const knowledgeQuestionModalVisible = ref(false);
const currentKnowledgePoint = ref("");
const currentCategoryId = ref<number | null>(null);

const kbRows = ref<any[]>([]);
const catRows = ref<any[]>([]);

const modalVisible = ref(false);
const modalTitle = ref("");
const modalMode = ref<"create" | "edit">("create");
const modalId = ref<number | null>(null);
const modalForm = ref({
  postId: 1,
  questionType: 1,
  questionLevel: 1,
  questionTitle: "",
  questionAnswer: "",
  knowledgePoint: "",
  isAiGenerate: 0
});

const canSubmit = computed(() => {
  return (
    Number(modalForm.value.postId) > 0 &&
    Number(modalForm.value.questionType) > 0 &&
    Number(modalForm.value.questionLevel) > 0 &&
    String(modalForm.value.questionTitle || "").trim() &&
    String(modalForm.value.questionAnswer || "").trim() &&
    String(modalForm.value.knowledgePoint || "").trim()
  );
});

/** 后端 KnowledgePointUiMapper 映射后的规范名，用于与界面卡片对齐 */
function uiKnowledgeLabel(q: any): string {
  return String(q?.uiKnowledgePoint || q?.knowledgePoint || "").trim() || "未分类知识点";
}

const filteredQuestions = computed(() => {
  const list = Array.isArray(app.lists.questions) ? app.lists.questions : [];
  return list;
});

type CatRow = { categoryId: number; categoryName: string; parentId: number };

const catsById = computed(() => {
  const map = new Map<number, CatRow>();
  (Array.isArray(catRows.value) ? catRows.value : []).forEach((r: any) => {
    const id = Number(r?.categoryId);
    if (!Number.isFinite(id) || id <= 0) return;
    map.set(id, { categoryId: id, categoryName: String(r?.categoryName || ""), parentId: Number(r?.parentId || 0) });
  });
  return map;
});

function normName(s: any): string {
  return String(s || "").trim().toLowerCase();
}

const catIdByName = computed(() => {
  const map = new Map<string, number>();
  (Array.isArray(catRows.value) ? catRows.value : []).forEach((r: any) => {
    const id = Number(r?.categoryId);
    const name = normName(r?.categoryName);
    if (!Number.isFinite(id) || id <= 0 || !name) return;
    map.set(name, id);
  });
  return map;
});

function rootCategoryId(id: number): number {
  const seen = new Set<number>();
  let cur = id;
  while (cur && !seen.has(cur)) {
    seen.add(cur);
    const row = catsById.value.get(cur);
    if (!row) return cur;
    if (!row.parentId || row.parentId === 0) return row.categoryId;
    cur = row.parentId;
  }
  return id;
}

const kbCountByCategoryId = computed(() => {
  const map = new Map<number, number>();
  (Array.isArray(kbRows.value) ? kbRows.value : []).forEach((k: any) => {
    const cid = Number(k?.categoryId);
    if (!Number.isFinite(cid) || cid <= 0) return;
    map.set(cid, (map.get(cid) || 0) + 1);
  });
  return map;
});

const kbByCategoryId = computed(() => {
  const map = new Map<number, any[]>();
  (Array.isArray(kbRows.value) ? kbRows.value : []).forEach((k: any) => {
    const cid = Number(k?.categoryId);
    if (!Number.isFinite(cid) || cid <= 0) return;
    if (!map.has(cid)) map.set(cid, []);
    map.get(cid)!.push(k);
  });
  return map;
});

const questionCountByCategoryId = computed(() => {
  const map = new Map<number, number>();
  filteredQuestions.value.forEach((q: any) => {
    const cid = Number(q?.categoryId);
    if (!Number.isFinite(cid) || cid <= 0) return;
    map.set(cid, (map.get(cid) || 0) + 1);
  });
  return map;
});

const rootCategories = computed(() => {
  const roots = Array.from(catsById.value.values())
    .filter((c) => Number(c.parentId || 0) === 0)
    .sort((a, b) => Number(a.categoryId || 0) - Number(b.categoryId || 0));
  // 兜底：分类表异常时，维持原有四大模块展示
  if (roots.length) return roots;
  return [
    { categoryId: 1, categoryName: "专业基础", parentId: 0 },
    { categoryId: 2, categoryName: "Web前端", parentId: 0 },
    { categoryId: 3, categoryName: "Java后端", parentId: 0 },
    { categoryId: 4, categoryName: "大模型算法", parentId: 0 }
  ] as CatRow[];
});

const sectionCards = computed(() => {
  // 动态按分类表分组，避免数据库新增分类后前端仍显示旧固定模块
  const roots = rootCategories.value;
  const allCats = Array.from(catsById.value.values());
  const itemsByRoot = new Map<number, KpCard[]>();
  roots.forEach((r) => itemsByRoot.set(r.categoryId, []));

  allCats.forEach((c) => {
    const rootId = rootCategoryId(c.categoryId);
    if (!itemsByRoot.has(rootId)) return;
    const knowledgeCount = kbCountByCategoryId.value.get(c.categoryId) || 0;
    const questionCount = questionCountByCategoryId.value.get(c.categoryId) || 0;
    if (knowledgeCount <= 0 && questionCount <= 0) return;
    itemsByRoot.get(rootId)!.push({
      name: c.categoryName,
      count: knowledgeCount,
      knowledgeKey: String(c.categoryId)
    });
  });

  return roots.map((r) => ({
    key: `root-${r.categoryId}`,
    title: r.categoryName,
    items: (itemsByRoot.get(r.categoryId) || []).sort((a, b) => a.name.localeCompare(b.name))
  }));
});

const hasAnyCards = computed(() => sectionCards.value.some((x) => x.items.length > 0));

const questionSectionCards = computed(() => {
  const roots = rootCategories.value;
  const allCats = Array.from(catsById.value.values());
  const itemsByRoot = new Map<number, KpCard[]>();
  roots.forEach((r) => itemsByRoot.set(r.categoryId, []));

  allCats.forEach((c) => {
    const rootId = rootCategoryId(c.categoryId);
    if (!itemsByRoot.has(rootId)) return;
    const questionCount = questionCountByCategoryId.value.get(c.categoryId) || 0;
    if (questionCount <= 0) return;
    itemsByRoot.get(rootId)!.push({
      name: c.categoryName,
      count: questionCount,
      knowledgeKey: String(c.categoryId)
    });
  });

  return roots.map((r) => ({
    key: `root-${r.categoryId}`,
    title: r.categoryName,
    items: (itemsByRoot.get(r.categoryId) || []).sort((a, b) => a.name.localeCompare(b.name))
  }));
});

const hasAnyQuestionCards = computed(() => questionSectionCards.value.some((x) => x.items.length > 0));

const currentKnowledges = computed(() => {
  const cid = currentCategoryId.value;
  if (cid == null) return [];
  const list = kbByCategoryId.value.get(cid) || [];
  return list.slice().sort((a: any, b: any) => Number(b?.knowledgeId || 0) - Number(a?.knowledgeId || 0));
});

const currentQuestions = computed(() => {
  if (!currentKnowledgePoint.value) return [];
  const cid = currentCategoryId.value;
  if (cid != null) {
    return filteredQuestions.value.filter((q: any) => Number(q?.categoryId) === Number(cid));
  }
  const cur = currentKnowledgePoint.value;
  return filteredQuestions.value.filter((q: any) => uiKnowledgeLabel(q) === cur || String(q?.knowledgePoint || "").trim() === cur);
});

async function loadQuestions() {
  try {
    app.lists.questions = (await app.apiRequest("/api/question-bank/list")) || [];
    app.pager.questionPage = 1;
  } catch {
    app.lists.questions = [];
  }
}

async function loadKnowledgeBase() {
  try {
    const data = await app.apiRequest("/api/knowledge-base/list");
    kbRows.value = Array.isArray(data) ? data : [];
  } catch {
    kbRows.value = [];
  }
}

async function loadCategories() {
  try {
    const data = await app.apiRequest("/api/knowledge-category/list");
    catRows.value = Array.isArray(data) ? data : [];
  } catch {
    catRows.value = [];
  }
}

async function loadAll() {
  errorText.value = "";
  try {
    await Promise.all([loadCategories(), loadKnowledgeBase(), loadQuestions()]);
  } catch (e: any) {
    errorText.value = e?.message || "加载失败";
  }
}

function openCreate() {
  modalTitle.value = "新增题目";
  modalMode.value = "create";
  modalId.value = null;
  modalForm.value = {
    postId: 1,
    questionType: 1,
    questionLevel: 1,
    questionTitle: "",
    questionAnswer: "",
    knowledgePoint: "",
    isAiGenerate: 0
  };
  modalVisible.value = true;
}

function openEdit(row: any) {
  modalTitle.value = "编辑题目";
  modalMode.value = "edit";
  modalId.value = row.questionId;
  modalForm.value = {
    postId: Number(row.postId || 1),
    questionType: row.questionType,
    questionLevel: row.questionLevel,
    questionTitle: row.questionTitle,
    questionAnswer: row.questionAnswer,
    knowledgePoint: row.knowledgePoint,
    isAiGenerate: Number(row.isAiGenerate || 0)
  };
  modalVisible.value = true;
}

function resolveCategoryFromKey(kp: string) {
  // kp 可能是 categoryId（字符串）或 categoryName
  const maybeId = Number(kp);
  if (Number.isFinite(maybeId) && maybeId > 0) {
    currentCategoryId.value = maybeId;
    currentKnowledgePoint.value = catsById.value.get(maybeId)?.categoryName || String(kp);
  } else {
    currentCategoryId.value = null;
    currentKnowledgePoint.value = kp;
  }
}

function openKnowledgeKbModal(kp: string) {
  resolveCategoryFromKey(kp);
  knowledgeKbModalVisible.value = true;
}

async function openKnowledgeQuestionModal(kp: string) {
  resolveCategoryFromKey(kp);
  knowledgeQuestionModalVisible.value = true;
  await ensureAnswersForKnowledge(String(currentCategoryId.value || currentKnowledgePoint.value));
}

async function ensureAnswersForKnowledge(kp: string) {
  try {
    const updated = await app.apiRequest("/api/question-bank/ensure-answers", "POST", { knowledgePoint: kp });
    if (!Array.isArray(updated) || !updated.length) return;
    const map = new Map<number, any>();
    (Array.isArray(app.lists.questions) ? app.lists.questions : []).forEach((q: any) => map.set(Number(q.questionId), q));
    updated.forEach((q: any) => map.set(Number(q.questionId), q));
    app.lists.questions = Array.from(map.values());
  } catch (e: any) {
    app.showToast(e?.message || "AI参考答案生成失败，请稍后重试");
  }
}

async function submit() {
  errorText.value = "";
  try {
    const payload: any = {
      postId: Number(modalForm.value.postId),
      questionType: Number(modalForm.value.questionType),
      questionLevel: Number(modalForm.value.questionLevel),
      questionTitle: String(modalForm.value.questionTitle || "").trim(),
      questionAnswer: String(modalForm.value.questionAnswer || "").trim(),
      knowledgePoint: String(modalForm.value.knowledgePoint || "").trim(),
      isAiGenerate: Number(modalForm.value.isAiGenerate || 0)
    };
    if (modalMode.value === "create") {
      await app.apiRequest("/api/question-bank/add", "POST", payload);
      app.showToast("新增成功");
    } else {
      await app.apiRequest(`/api/question-bank/${modalId.value}`, "PUT", payload);
      app.showToast("更新成功");
    }
    modalVisible.value = false;
    await loadQuestions();
  } catch (e: any) {
    errorText.value = e?.message || "保存失败";
  }
}

async function removeQuestion(id: number) {
  if (!window.confirm("确认删除这道题目吗？")) return;
  errorText.value = "";
  try {
    await app.apiRequest(`/api/question-bank/${id}`, "DELETE");
    app.showToast("删除成功");
    await loadQuestions();
  } catch (e: any) {
    errorText.value = e?.message || "删除失败";
  }
}

function typeText(type: any) {
  const v = Number(type);
  if (v === 2) return "多选题";
  if (v === 3) return "简答题";
  return "单选题";
}

function levelText(level: any) {
  const v = Number(level);
  if (v === 2) return "中级";
  if (v === 3) return "高级";
  return "初级";
}

onMounted(loadAll);
</script>

<style scoped>
.rv-tabs {
  display: flex;
  gap: 8px;
  margin-left: auto;
}
.rv-sections-scroll {
  max-height: min(58vh, 680px);
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
  -webkit-overflow-scrolling: touch;
}
.rv-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 12px;
}
.rv-sections {
  display: grid;
  gap: 16px;
}
.rv-section {
  border: 1px solid #e2eef8;
  border-radius: 14px;
  background: #fafdff;
  padding: 14px;
}
.rv-section-head {
  margin-bottom: 10px;
}
.rv-section-head h3 {
  margin: 0;
  color: #173a5e;
}
.rv-card {
  border: 1px solid #dcecf8;
  border-left: 4px solid #59bcf6;
  border-radius: 12px;
  background: #fff;
  padding: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
.rv-card:hover {
  box-shadow: 0 8px 20px rgba(47, 99, 153, 0.12);
  transform: translateY(-1px);
}
.rv-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}
.rv-card-head h4 {
  margin: 0;
  color: #1f3854;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rv-count {
  min-width: 28px;
  height: 28px;
  border-radius: 999px;
  background: #e9f6ff;
  color: #0e6eb7;
  display: inline-grid;
  place-items: center;
  font-weight: 700;
  font-size: 13px;
}
.rv-detail-modal {
  width: min(980px, 95vw);
  border-radius: 16px;
  background: #f4f7fb;
  border: 1px solid #dbe5f0;
  color: #274863;
}
.rv-detail-modal h3 {
  margin: 0 0 12px 0;
  color: #123a5c;
  font-size: 20px;
  font-weight: 700;
}
.rv-detail-modal .field > label {
  color: #123a5c;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 8px;
}
.rv-detail-modal .field > label.rv-label-plain {
  font-weight: 400;
}
.rv-detail-modal .api-output {
  border-radius: 10px;
  background: #edf3f9;
  border: 1px solid #dce7f2;
  padding: 10px 14px;
  color: #4b647d;
  line-height: 1.6;
  font-size: 14px;
}
.rv-question-list {
  max-height: 62vh;
  overflow: auto;
  display: grid;
  gap: 10px;
}
.rv-question-item {
  border: 1px solid #e4eff8;
  border-radius: 10px;
  background: #fbfeff;
  padding: 10px;
}
.rv-q-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
@media (max-width: 760px) {
  .rv-grid {
    grid-template-columns: 1fr;
  }
  .rv-q-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

