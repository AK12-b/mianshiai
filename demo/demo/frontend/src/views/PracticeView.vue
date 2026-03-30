<template>
  <section class="card">
    <h3 style="margin-top: 0">专项练习</h3>
    <div class="field">
      <label>创建母任务配置</label>
      <textarea v-model="app.forms.practiceMaster" rows="7" />
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" @click="createMaster">创建母任务</button>
      <button class="btn btn-outline" @click="loadMasters">查询我的母任务</button>
    </div>
    <pre class="api-output">{{ output }}</pre>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const output = ref("");

async function createMaster() {
  const payload = app.parseJson(app.forms.practiceMaster);
  payload.userId = app.uid;
  const data = await app.apiRequest("/api/practice/master-task/create", "POST", payload);
  output.value = app.pretty(data);
  app.showToast("创建成功");
}

async function loadMasters() {
  const data = await app.apiRequest(`/api/practice/master-task/user/${app.uid}`);
  output.value = app.pretty(data);
}
</script>

