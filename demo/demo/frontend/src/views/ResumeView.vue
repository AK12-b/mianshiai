<template>
  <section class="card">
    <h3 style="margin-top: 0">AI 简历优化</h3>

    <input
      ref="filePickerRef"
      type="file"
      accept=".pdf,.doc,.docx,.txt"
      style="display: none"
      @change="onFileChange"
    />

    <div class="toolbar">
      <button class="btn btn-primary" :disabled="uploading" @click="openFilePicker">
        {{ uploading ? "上传中..." : "上传简历" }}
      </button>
      <button class="btn btn-outline" @click="loadResumes">查询我的简历</button>
      <span class="muted" v-if="selectedFileName">已选择：{{ selectedFileName }}</span>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>岗位ID</th>
          <th>格式</th>
          <th>大小(KB)</th>
          <th>上传时间</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in resumes" :key="row.resumeId">
          <td>{{ row.resumeId }}</td>
          <td>{{ row.postId || "--" }}</td>
          <td>{{ row.resumeFormat || "--" }}</td>
          <td>{{ sizeKb(row.resumeSize) }}</td>
          <td>{{ fmt(row.createTime) }}</td>
          <td>
            <span class="status-badge">{{ row.isOptimizeApply === 1 ? "已优化" : "待优化" }}</span>
          </td>
          <td>
            <button class="btn btn-text" @click="diagnose(row.resumeId)">诊断</button>
            <button class="btn btn-text" @click="optimize(row.resumeId)">优化</button>
          </td>
        </tr>
        <tr v-if="!resumes.length">
          <td class="empty-cell" colspan="7">暂无简历数据，请先上传一份简历。</td>
        </tr>
      </tbody>
    </table>

    <div class="card" style="margin-top: 12px">
      <div class="dashboard-section-title">
        <h3>诊断与优化结果</h3>
      </div>
      <div class="field">
        <label>诊断结果</label>
        <div class="api-output" style="margin-top: 0; max-height: 180px">{{ currentDiagnose }}</div>
      </div>
      <div class="field">
        <label>优化后地址</label>
        <div>
          <a v-if="currentOptimizeUrl" :href="currentOptimizeUrl" target="_blank" rel="noopener noreferrer">
            {{ currentOptimizeUrl }}
          </a>
          <span v-else class="muted">暂无优化文件链接</span>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const resumes = ref<any[]>([]);
const errorText = ref("");
const currentDiagnose = ref("暂无诊断结果");
const currentOptimizeUrl = ref("");
const uploadPostId = ref(1);
const selectedFileName = ref("");
const filePickerRef = ref<HTMLInputElement | null>(null);
const uploading = ref(false);

async function loadResumes() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/user/${app.uid}`);
    resumes.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    resumes.value = [];
    errorText.value = e?.message || "查询简历失败";
  }
}

function openFilePicker() {
  errorText.value = "";
  filePickerRef.value?.click();
}

function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0] || null;
  selectedFileName.value = file?.name || "";
  if (!file) return;
  uploadResume(file).finally(() => {
    input.value = "";
  });
}

async function uploadResume(file: File) {
  errorText.value = "";
  uploading.value = true;
  try {
    const formData = new FormData();
    formData.append("userId", String(app.uid));
    formData.append("postId", String(uploadPostId.value));
    formData.append("file", file);

    const res = await fetch("/api/resume/upload", { method: "POST", body: formData });
    const data = await res.json();
    if (data.code !== 200) throw new Error(data.message || "上传失败");
    app.showToast("上传成功");
    await loadResumes();
  } catch (e: any) {
    errorText.value = e?.message || "上传失败";
  } finally {
    uploading.value = false;
  }
}

async function diagnose(resumeId: number) {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/${resumeId}/diagnose`, "POST");
    currentDiagnose.value = data?.diagnoseResult || "暂无诊断内容";
    currentOptimizeUrl.value = data?.optimizeUrl || "";
    app.showToast("诊断完成");
    await loadResumes();
  } catch (e: any) {
    errorText.value = e?.message || "诊断失败";
  }
}

async function optimize(resumeId: number) {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/${resumeId}/optimize`, "POST");
    currentDiagnose.value = data?.diagnoseResult || currentDiagnose.value;
    currentOptimizeUrl.value = data?.optimizeUrl || "";
    app.showToast("优化完成");
    await loadResumes();
  } catch (e: any) {
    errorText.value = e?.message || "优化失败";
  }
}

function sizeKb(v: any) {
  const kb = Number(v || 0) / 1024;
  return kb > 0 ? kb.toFixed(1) : "0.0";
}

function fmt(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 16);
}

onMounted(loadResumes);
</script>

