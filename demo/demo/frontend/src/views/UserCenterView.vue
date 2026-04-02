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
            <div class="user-info-item">
              <span class="muted">手机号</span>
              <strong>{{ profile?.phone || "--" }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">创建时间</span>
              <strong>{{ fmtTime(profile?.createTime) }}</strong>
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
      <table class="data-table">
        <thead>
          <tr>
            <th>日志ID</th>
            <th>登录时间</th>
            <th>登出时间</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in loginLogs" :key="row.logId">
            <td>{{ row.logId ?? "--" }}</td>
            <td>{{ fmtTime(row.loginTime) }}</td>
            <td>{{ fmtTime(row.logoutTime) }}</td>
            <td>
              <span class="status-badge">{{ row.logoutTime ? "已退出" : "在线中" }}</span>
            </td>
          </tr>
          <tr v-if="!loginLogs.length">
            <td class="empty-cell" colspan="4">暂无登录日志。</td>
          </tr>
        </tbody>
      </table>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const profile = ref<any>(null);
const loginLogs = ref<any[]>([]);
const errorText = ref("");

const avatarText = computed(() => {
  const text = String(profile.value?.userName || app.user.name || "用户");
  return text.slice(0, 1).toUpperCase();
});

async function loadMe() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/user/${app.uid}`);
    profile.value = data;
  } catch (e: any) {
    errorText.value = e?.message || "个人信息加载失败";
  }
}

async function loadLoginLogs() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/user-login-log/user/${app.uid}`);
    loginLogs.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    loginLogs.value = [];
    errorText.value = e?.message || "登录日志加载失败";
  }
}

function fmtTime(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 19);
}

onMounted(() => {
  loadMe();
  loadLoginLogs();
});
</script>

