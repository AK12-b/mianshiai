<template>
  <section class="card settings-wrap">
    <h3 style="margin-top: 0">系统设置</h3>
    <p class="muted">管理你的默认面试偏好、账号安全与系统信息。</p>

    <div class="settings-grid">
      <article class="settings-card">
        <h4>面试默认设置</h4>
        <div class="settings-inline-fields">
          <div class="field">
            <label>默认面试模式</label>
            <select v-model.number="defaults.mode">
              <option :value="1">全流程</option>
              <option :value="2">专项</option>
            </select>
          </div>
          <div class="field">
            <label>默认面试模块</label>
            <select v-model="defaults.module">
              <option value="技术基础">技术基础</option>
              <option value="项目经历">项目经历</option>
              <option value="场景题">场景题</option>
              <option value="行为面试">行为面试</option>
            </select>
          </div>
          <div class="field">
            <label>默认时长</label>
            <select v-model.number="defaults.duration">
              <option :value="5">5 分钟</option>
              <option :value="10">10 分钟</option>
              <option :value="15">15 分钟</option>
              <option :value="20">20 分钟</option>
            </select>
          </div>
        </div>
        <div class="settings-actions">
          <button class="btn btn-outline" @click="saveDefaults">保存默认设置</button>
        </div>
      </article>

      <article class="settings-card">
        <h4>账号安全</h4>
        <p class="muted">保护你的账号安全，敏感操作前请确认已备份数据。</p>
        <div class="settings-actions">
          <button class="btn btn-outline" @click="showPwdForm = !showPwdForm">
            {{ showPwdForm ? "收起修改密码" : "修改密码" }}
          </button>
          <button class="btn btn-danger" @click="confirmDelete">注销账号</button>
        </div>
        <div v-if="showPwdForm" class="settings-password-form">
          <div class="field">
            <label>原密码</label>
            <input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />
          </div>
          <div class="field">
            <label>新密码</label>
            <input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（至少 6 位）" />
          </div>
          <div class="field">
            <label>确认新密码</label>
            <input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
          </div>
          <div class="settings-actions">
            <button class="btn btn-primary" @click="submitChangePassword">确认修改</button>
          </div>
        </div>
      </article>

      <article class="settings-card">
        <h4>通知设置</h4>
        <div class="settings-notify-row">
          <span><span class="muted">通知开关：</span>{{ app.notifyEnabled ? "已开启" : "已关闭" }}</span>
          <label class="notify-switch" :class="{ on: app.notifyEnabled }">
            <input
              class="notify-switch-input"
              type="checkbox"
              :checked="app.notifyEnabled"
              @change="onNotifySwitchChange"
            />
            <span class="notify-switch-track">
              <span class="notify-switch-thumb" />
            </span>
          </label>
        </div>
      </article>

      <article class="settings-card">
        <h4>关于我们</h4>
        <div class="settings-info">
          <div>InterYou 面小优，专注 AI 模拟面试与能力提升</div>
          <div><span class="muted">系统版本：</span>v1.0.0</div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const defaults = reactive({
  mode: 1,
  module: "技术基础",
  duration: 15
});
const showPwdForm = ref(false);
const pwdForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

async function loadDefaults() {
  try {
    const u = await app.apiRequest(`/api/user/${app.uid}`);
    defaults.mode = Number(u?.defaultInterviewMode || 1);
    defaults.module = String(u?.defaultInterviewModule || "技术基础");
    defaults.duration = Number(u?.defaultInterviewDuration || 15);
  } catch {
    // keep fallback defaults
  }
}

async function saveDefaults() {
  try {
    const u = await app.apiRequest(`/api/user/${app.uid}`);
    await app.apiRequest(`/api/user/${app.uid}`, "PUT", {
      userId: Number(app.uid),
      userName: String(u?.userName || app.user?.name || ""),
      email: String(u?.email || ""),
      gender: Number(u?.gender || 0),
      skillProficiency: String(u?.skillProficiency || ""),
      education: String(u?.education || ""),
      grade: String(u?.grade || ""),
      projectExp: String(u?.projectExp || ""),
      competitionAward: String(u?.competitionAward || ""),
      skillTag: String(u?.skillTag || ""),
      defaultInterviewMode: Number(defaults.mode || 1),
      defaultInterviewModule: String(defaults.module || "技术基础"),
      defaultInterviewDuration: Number(defaults.duration || 15)
    });
    app.showToast("默认设置已保存到数据库");
  } catch (e: any) {
    app.showToast(e?.message || "默认设置保存失败");
  }
}

function confirmDelete() {
  if (!window.confirm("确认注销账号吗？该操作不可恢复。")) return;
  app.showToast("注销账号功能待接入");
}

function toggleNotify(enabled: boolean) {
  if (app.notifyEnabled === enabled) return;
  app.setNotifyEnabled(enabled);
}

function onNotifySwitchChange(e: Event) {
  const target = e.target as HTMLInputElement | null;
  toggleNotify(!!target?.checked);
}

async function submitChangePassword() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword || !pwdForm.confirmPassword) {
    app.showToast("请完整填写密码信息");
    return;
  }
  if (pwdForm.newPassword.length < 6) {
    app.showToast("新密码至少 6 位");
    return;
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    app.showToast("两次输入的新密码不一致");
    return;
  }
  try {
    await app.apiRequest("/api/user/change-password", "POST", {
      userId: Number(app.uid),
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    });
    app.showToast("密码修改成功，请使用新密码登录");
    pwdForm.oldPassword = "";
    pwdForm.newPassword = "";
    pwdForm.confirmPassword = "";
    showPwdForm.value = false;
  } catch (e: any) {
    app.showToast(e?.message || "修改密码失败");
  }
}

onMounted(loadDefaults);
</script>

<style scoped>
.settings-wrap {
  display: grid;
  gap: 10px;
}

.settings-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.settings-card {
  border: 1px solid #dcecf8;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.settings-card h4 {
  margin: 0 0 10px;
  color: #173a5e;
}

.settings-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.settings-inline-fields {
  display: grid;
  grid-template-columns: repeat(3, minmax(180px, 1fr));
  gap: 10px;
}

.settings-password-form {
  margin-top: 10px;
  border-top: 1px dashed #dcecf8;
  padding-top: 10px;
}

.settings-info {
  display: grid;
  gap: 8px;
  line-height: 1.6;
}

.settings-notify-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-top: 4px;
}

.btn-danger {
  background: rgba(239, 108, 99, 0.12);
  color: #b34843;
  border: 1px solid rgba(239, 108, 99, 0.42);
}

.notify-switch {
  width: 66px;
  height: 36px;
  position: relative;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}

.notify-switch-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  margin: 0;
  cursor: pointer;
  z-index: 2;
}

.notify-switch-track {
  width: 66px;
  height: 36px;
  border-radius: 999px;
  background: #e7e7ea;
  position: relative;
  transition: background-color 0.28s ease;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.08);
}

.notify-switch-thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 8px 18px rgba(12, 27, 44, 0.16);
  transition: left 0.28s ease, transform 0.28s ease;
}

.notify-switch.on {
  background: transparent;
}

.notify-switch.on .notify-switch-track {
  background: #2286e6;
}

.notify-switch.on .notify-switch-thumb {
  left: 33px;
}

@media (max-width: 980px) {
  .settings-inline-fields {
    grid-template-columns: 1fr;
  }
  .settings-notify-row {
    flex-direction: column;
    align-items: flex-start;
  }
}

</style>

