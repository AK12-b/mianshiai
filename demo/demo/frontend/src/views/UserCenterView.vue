<template>
  <section class="user-center-wrap">
    <div class="card">
      <h3 style="margin-top: 0">个人信息</h3>
      <div class="user-profile-layout">
        <div class="user-avatar-panel">
          <div class="user-avatar">{{ avatarText }}</div>
          <strong>{{ profile?.userName || app.user.name }}</strong>
          <span class="muted">{{ profile?.email || "未填写邮箱" }}</span>
        </div>
        <div class="user-info-panel">
          <div class="user-info-grid">
            <div class="user-info-item">
              <span class="muted">用户ID</span>
              <strong>{{ profile?.userId ?? "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">用户名</span>
              <strong>{{ profile?.userName || "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">邮箱</span>
              <strong>{{ profile?.email || "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">性别</span>
              <strong>{{ profile?.gender || "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">学历</span>
              <strong>{{ profile?.education || "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">工作年限</span>
              <strong>{{ profile?.workYear || "--" }}</strong>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="dashboard-section-title">
        <h3>登录日志</h3>
        <button class="btn btn-outline" @click="loadLoginLogs">刷新日志</button>
      </div>
      <pre class="api-output">{{ logsOutput }}</pre>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const profile = ref<any>(null);
const logsOutput = ref("");

const avatarText = computed(() => {
  const text = String(profile.value?.userName || app.user.name || "用户");
  return text.slice(0, 1).toUpperCase();
});

async function loadMe() {
  const data = await app.apiRequest(`/api/user/${app.uid}`);
  profile.value = data;
}

async function loadLoginLogs() {
  const data = await app.apiRequest(`/api/user-login-log/user/${app.uid}`);
  logsOutput.value = app.pretty(data);
}

onMounted(() => {
  loadMe();
  loadLoginLogs();
});
</script>

