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
          <td>{{ row.questionTitle }}</td>
          <td>{{ row.questionType }}</td>
          <td>{{ row.questionLevel }}</td>
          <td>{{ row.knowledgePoint }}</td>
          <td>
            <button class="btn btn-text" @click="openEdit(row)">编辑</button>
            <button class="btn btn-text" @click="removeQuestion(row.questionId)">删除</button>
          </td>
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
        <textarea v-model="modalPayload" rows="12" />
        <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 10px">
          <button class="btn btn-outline" @click="modalVisible = false">取消</button>
          <button class="btn btn-primary" @click="submit">保存</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();

const modalVisible = ref(false);
const modalTitle = ref("");
const modalPayload = ref("");
const modalMode = ref<"create" | "edit">("create");
const modalId = ref<number | null>(null);

async function loadQuestions() {
  app.lists.questions = (await app.apiRequest("/api/question-bank/list")) || [];
  app.pager.questionPage = 1;
}

function openCreate() {
  modalTitle.value = "新增题目";
  modalMode.value = "create";
  modalId.value = null;
  modalPayload.value = app.forms.questionAdd;
  modalVisible.value = true;
}

function openEdit(row: any) {
  modalTitle.value = "编辑题目";
  modalMode.value = "edit";
  modalId.value = row.questionId;
  modalPayload.value = app.pretty({
    questionType: row.questionType,
    questionLevel: row.questionLevel,
    questionTitle: row.questionTitle,
    questionAnswer: row.questionAnswer,
    knowledgePoint: row.knowledgePoint
  });
  modalVisible.value = true;
}

async function submit() {
  const payload = app.parseJson(modalPayload.value);
  if (modalMode.value === "create") {
    await app.apiRequest("/api/question-bank/add", "POST", payload);
  } else {
    await app.apiRequest(`/api/question-bank/${modalId.value}`, "PUT", payload);
  }
  modalVisible.value = false;
  await loadQuestions();
}

async function removeQuestion(id: number) {
  if (!window.confirm("确认删除这道题目吗？")) return;
  await app.apiRequest(`/api/question-bank/${id}`, "DELETE");
  await loadQuestions();
}

onMounted(loadQuestions);
</script>

