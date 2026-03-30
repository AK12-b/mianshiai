<template>
  <section class="card">
    <h3 style="margin-top: 0">错题本</h3>
    <div class="toolbar">
      <button class="btn btn-primary" @click="addWrong">新增错题</button>
      <button class="btn btn-outline" @click="loadWrongs">刷新</button>
      <input v-model.trim="app.filters.wrongKeyword" placeholder="按知识点筛选" />
      <label><input type="checkbox" v-model="app.filters.wrongCollectedOnly" /> 仅收藏</label>
    </div>
    <div class="field">
      <label>新增错题配置</label>
      <textarea v-model="app.forms.wrongAdd" rows="4" />
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
          <td>{{ row.knowledgePoint }}</td>
          <td>{{ row.wrongReason }}</td>
          <td>
            <span class="status-badge">{{ Number(row.isCollect) === 1 ? "已收藏" : "未收藏" }}</span>
          </td>
          <td>
            <button class="btn btn-text" @click="toggleCollect(row.wrongId)">切换收藏</button>
            <button class="btn btn-text" @click="removeWrong(row.wrongId)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="pager">
      <button class="btn btn-outline" @click="app.pager.wrongPage = Math.max(1, app.pager.wrongPage - 1)">上一页</button>
      <span>{{ app.pager.wrongPage }} / {{ app.totalPages(app.wrongFiltered) }}</span>
      <button class="btn btn-outline" @click="app.pager.wrongPage = Math.min(app.totalPages(app.wrongFiltered), app.pager.wrongPage + 1)">下一页</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();

async function loadWrongs() {
  app.lists.wrongs = (await app.apiRequest(`/api/wrong-question/user/${app.uid}`)) || [];
  app.pager.wrongPage = 1;
}

async function addWrong() {
  const payload = app.parseJson(app.forms.wrongAdd);
  payload.userId = app.uid;
  await app.apiRequest("/api/wrong-question/add", "POST", payload);
  app.showToast("新增成功");
  await loadWrongs();
}

async function toggleCollect(id: number) {
  await app.apiRequest(`/api/wrong-question/${id}/toggle-collect`, "POST");
  await loadWrongs();
}

async function removeWrong(id: number) {
  if (!window.confirm("确认删除这条错题吗？")) return;
  await app.apiRequest(`/api/wrong-question/${id}`, "DELETE");
  await loadWrongs();
}

onMounted(loadWrongs);
</script>

