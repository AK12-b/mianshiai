<template>
  <section class="card">
    <h3 style="margin-top: 0">错题本</h3>

    <div class="config-grid">
      <div class="field">
        <label>题目ID</label>
        <input v-model.number="form.questionId" type="number" min="1" placeholder="请输入题目ID" />
      </div>
      <div class="field">
        <label>知识点</label>
        <input v-model="form.knowledgePoint" placeholder="例如：Redis缓存、JVM调优" />
      </div>
      <div class="field">
        <label>错误原因</label>
        <select v-model.number="form.wrongReason">
          <option :value="1">知识点不熟</option>
          <option :value="2">审题失误</option>
          <option :value="3">表达不清</option>
          <option :value="4">其他原因</option>
        </select>
      </div>
      <div class="field">
        <label>面试ID（可选）</label>
        <input v-model.number="form.interviewId" type="number" min="1" placeholder="不填则留空" />
      </div>
      <div class="field">
        <label>练习ID（可选）</label>
        <input v-model.number="form.practiceId" type="number" min="1" placeholder="不填则留空" />
      </div>
    </div>

    <div class="toolbar">
      <button class="btn btn-primary" @click="addWrong">新增错题</button>
      <button class="btn btn-outline" @click="loadWrongs">刷新</button>
      <input v-model.trim="app.filters.wrongKeyword" placeholder="按知识点筛选" />
      <label><input type="checkbox" v-model="app.filters.wrongCollectedOnly" /> 仅收藏</label>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>知识点</th>
          <th>原因</th>
          <th>收藏</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in app.pagedList(app.wrongFiltered, app.pager.wrongPage)" :key="row.wrongId">
          <td>{{ row.wrongId }}</td>
          <td>{{ row.knowledgePoint || "--" }}</td>
          <td>{{ reasonText(row.wrongReason) }}</td>
          <td>
            <span class="status-badge">{{ Number(row.isCollect) === 1 ? "已收藏" : "未收藏" }}</span>
          </td>
          <td>
            <button class="btn btn-text" @click="toggleCollect(row.wrongId)">切换收藏</button>
            <button class="btn btn-text" @click="removeWrong(row.wrongId)">删除</button>
          </td>
        </tr>
        <tr v-if="!app.wrongFiltered.length">
          <td class="empty-cell" colspan="5">暂无错题数据，先新增一条错题记录。</td>
        </tr>
      </tbody>
    </table>
    <div class="pager">
      <button class="btn btn-outline" @click="app.pager.wrongPage = Math.max(1, app.pager.wrongPage - 1)">上一页</button>
      <span>{{ app.pager.wrongPage }} / {{ app.totalPages(app.wrongFiltered) }}</span>
      <button class="btn btn-outline" @click="app.pager.wrongPage = Math.min(app.totalPages(app.wrongFiltered), app.pager.wrongPage + 1)">下一页</button>
    </div>
    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const errorText = ref("");
const form = ref({
  questionId: 1,
  interviewId: null as null | number,
  practiceId: null as null | number,
  wrongReason: 1,
  knowledgePoint: "Redis缓存"
});

async function loadWrongs() {
  errorText.value = "";
  try {
    app.lists.wrongs = (await app.apiRequest(`/api/wrong-question/user/${app.uid}`)) || [];
    app.pager.wrongPage = 1;
  } catch (e: any) {
    app.lists.wrongs = [];
    errorText.value = e?.message || "查询失败";
  }
}

async function addWrong() {
  errorText.value = "";
  if (!form.value.questionId || !String(form.value.knowledgePoint || "").trim()) {
    app.showToast("请填写题目ID和知识点");
    return;
  }
  try {
    const payload: any = {
      userId: app.uid,
      questionId: Number(form.value.questionId),
      wrongReason: Number(form.value.wrongReason),
      knowledgePoint: String(form.value.knowledgePoint || "").trim()
    };
    if (form.value.interviewId) payload.interviewId = Number(form.value.interviewId);
    if (form.value.practiceId) payload.practiceId = Number(form.value.practiceId);
    await app.apiRequest("/api/wrong-question/add", "POST", payload);
    app.showToast("新增成功");
    await loadWrongs();
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

onMounted(loadWrongs);
</script>

