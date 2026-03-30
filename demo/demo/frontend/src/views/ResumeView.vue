<template>
  <section class="card">
    <h3 style="margin-top: 0">AI 简历优化</h3>
    <div class="toolbar">
      <button class="btn btn-outline" @click="loadResumes">查询我的简历</button>
      <button class="btn btn-outline" @click="diagnose1">诊断 #1</button>
      <button class="btn btn-primary" @click="optimize1">优化 #1</button>
    </div>
    <pre class="api-output">{{ output }}</pre>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const output = ref("");

async function loadResumes() {
  const data = await app.apiRequest(`/api/resume/user/${app.uid}`);
  output.value = app.pretty(data);
}

async function diagnose1() {
  const data = await app.apiRequest("/api/resume/1/diagnose", "POST");
  output.value = app.pretty(data);
}

async function optimize1() {
  const data = await app.apiRequest("/api/resume/1/optimize", "POST");
  output.value = app.pretty(data);
}
</script>

