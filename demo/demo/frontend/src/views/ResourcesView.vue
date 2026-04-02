<template>
  <section class="card">
    <h3 style="margin-top: 0">学习资源 / 题库管理</h3>
    <div class="toolbar">
      <button class="btn btn-primary" @click="openCreate">新增题目</button>
      <button class="btn btn-outline" @click="loadQuestions">刷新题库</button>
      <input v-model.trim="app.filters.questionKeyword" placeholder="题干/知识点筛选" />
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>题目</th>
          <th>类型</th>
          <th>难度</th>
          <th>知识点</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in app.pagedList(app.questionFiltered, app.pager.questionPage)" :key="row.questionId">
          <td>{{ row.questionId }}</td>
          <td>{{ row.questionTitle || "--" }}</td>
          <td>{{ typeText(row.questionType) }}</td>
          <td>{{ levelText(row.questionLevel) }}</td>
          <td>{{ row.knowledgePoint || "--" }}</td>
          <td>
            <button class="btn btn-text" @click="openEdit(row)">编辑</button>
            <button class="btn btn-text" @click="removeQuestion(row.questionId)">删除</button>
          </td>
        </tr>
        <tr v-if="!app.questionFiltered.length">
          <td class="empty-cell" colspan="6">暂无题库数据，先新增一道题目。</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button class="btn btn-outline" @click="app.pager.questionPage = Math.max(1, app.pager.questionPage - 1)">上一页</button>
      <span>{{ app.pager.questionPage }} / {{ app.totalPages(app.questionFiltered) }}</span>
      <button class="btn btn-outline" @click="app.pager.questionPage = Math.min(app.totalPages(app.questionFiltered), app.pager.questionPage + 1)">下一页</button>
    </div>

    <div v-if="modalVisible" class="modal-mask">
      <div class="modal-card">
        <h3>{{ modalTitle }}</h3>
        <div class="config-grid">
          <div class="field">
            <label>岗位ID</label>
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

const app = useAppStore();
const errorText = ref("");

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

async function loadQuestions() {
  errorText.value = "";
  try {
    app.lists.questions = (await app.apiRequest("/api/question-bank/list")) || [];
    app.pager.questionPage = 1;
  } catch (e: any) {
    app.lists.questions = [];
    errorText.value = e?.message || "题库加载失败";
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

onMounted(loadQuestions);
</script>

