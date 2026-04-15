<template>
  <section class="user-center-wrap">
    <div class="card">
      <h3 style="margin-top: 0">个人信息</h3>
      <div class="user-profile-layout">
        <div class="user-top-panel">
          <div class="user-avatar-panel">
          <img v-if="avatarUrl" class="user-avatar-image" :src="avatarUrl" alt="头像" />
          <div v-else class="user-avatar">{{ avatarText }}</div>
          <strong>{{ form.userName || app.user.name }}</strong>
          <div class="field user-avatar-upload">
            <input ref="avatarInputRef" class="avatar-input-hidden" type="file" accept="image/*" @change="onAvatarChange" />
            <button type="button" class="btn btn-outline avatar-upload-btn" @click="openAvatarPicker">选择文件</button>
          </div>
        </div>
        </div>
        <div class="user-info-panel">
          <div class="user-info-grid">
            <div class="user-info-item">
              <span class="muted">用户名</span>
              <input v-model="form.userName" />
            </div>
            <div class="user-info-item">
              <span class="muted">邮箱</span>
              <input v-model="form.email" />
            </div>
            <div class="user-info-item">
              <span class="muted">性别</span>
              <select v-model.number="form.gender">
                <option :value="0">未设置</option>
                <option :value="1">男</option>
                <option :value="2">女</option>
              </select>
            </div>
            <div class="user-info-item">
              <span class="muted">专业</span>
              <input v-model="form.major" />
            </div>
            <div class="user-info-item">
              <span class="muted">学历</span>
              <input v-model="form.education" />
            </div>
            <div class="user-info-item">
              <span class="muted">年级</span>
              <input v-model="form.grade" />
            </div>
            <div class="user-info-item">
              <span class="muted">项目经历</span>
              <textarea v-model="form.projectExp" rows="3" />
            </div>
            <div class="user-info-item">
              <span class="muted">竞赛获奖</span>
              <textarea v-model="form.competitionAward" rows="3" />
            </div>
            <div class="user-info-item">
              <span class="muted">技能标签</span>
              <input v-model="form.skillTag" placeholder="例如：Java, Spring Boot, MySQL" />
            </div>
            <div class="user-info-item">
              <span class="muted">意向岗位</span>
              <select v-model.number="form.intentionPostId">
                <option :value="0">未设置</option>
                <option v-for="p in posts" :key="p.postId" :value="Number(p.postId)">{{ p.postName }}</option>
              </select>
            </div>
            <div class="user-info-item">
              <span class="muted">创建时间（系统维护）</span>
              <strong>{{ fmtTime(profile?.createTime) }}</strong>
            </div>
            <div class="user-info-item">
              <span class="muted">更新时间（系统维护）</span>
              <strong>{{ fmtTime(profile?.updateTime) }}</strong>
            </div>
          </div>
          <div style="display:flex; justify-content:flex-end; margin-top:10px">
            <button class="btn btn-primary" @click="saveProfile">保存资料</button>
          </div>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const profile = ref<any>(null);
const posts = ref<any[]>([]);
const intentionRows = ref<any[]>([]);
const errorText = ref("");
const avatarUrl = ref("");
const avatarInputRef = ref<HTMLInputElement | null>(null);
const form = reactive({
  userName: "",
  email: "",
  gender: 0,
  major: "",
  education: "",
  grade: "",
  projectExp: "",
  competitionAward: "",
  skillTag: "",
  intentionPostId: 0
});

const avatarText = computed(() => {
  const text = String(profile.value?.userName || app.user.name || "用户");
  return text.slice(0, 1).toUpperCase();
});

async function loadMe() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/user/${app.uid}`);
    profile.value = data;
    form.userName = String(data?.userName || "");
    form.email = String(data?.email || "");
    form.gender = Number(data?.gender || 0);
    form.major = String(data?.skillProficiency || "");
    form.education = String(data?.education || "");
    form.grade = String(data?.grade || "");
    form.projectExp = String(data?.projectExp || "");
    form.competitionAward = String(data?.competitionAward || "");
    form.skillTag = String(data?.skillTag || "");
    try {
      avatarUrl.value = localStorage.getItem(`faceai_avatar_${app.uid}`) || "";
    } catch {
      avatarUrl.value = "";
    }
  } catch (e: any) {
    errorText.value = e?.message || "个人信息加载失败";
  }
}

async function loadPosts() {
  try {
    posts.value = (await app.apiRequest("/api/post/list")) || [];
  } catch {
    posts.value = [];
  }
}

async function loadIntentionPost() {
  try {
    const data = await app.apiRequest(`/api/user-intention-post/user/${app.uid}`);
    intentionRows.value = Array.isArray(data) ? data : [];
    const first = intentionRows.value[0];
    form.intentionPostId = first ? Number(first.postId || 0) : 0;
  } catch {
    intentionRows.value = [];
    form.intentionPostId = 0;
  }
}

async function saveProfile() {
  errorText.value = "";
  try {
    const payload = {
      userId: Number(app.uid),
      userName: String(form.userName || "").trim(),
      email: String(form.email || "").trim(),
      gender: Number(form.gender || 0),
      skillProficiency: String(form.major || "").trim(),
      education: String(form.education || "").trim(),
      grade: String(form.grade || "").trim(),
      projectExp: String(form.projectExp || "").trim(),
      competitionAward: String(form.competitionAward || "").trim(),
      skillTag: String(form.skillTag || "").trim()
    };
    await app.apiRequest(`/api/user/${app.uid}`, "PUT", payload);

    // 同步意向岗位：先清旧再新增（若有选择）
    for (const row of intentionRows.value) {
      await app.apiRequest(`/api/user-intention-post/${row.uipId}`, "DELETE");
    }
    if (Number(form.intentionPostId) > 0) {
      await app.apiRequest("/api/user-intention-post/add", "POST", {
        userId: Number(app.uid),
        postId: Number(form.intentionPostId)
      });
    }
    app.showToast("个人资料已保存");
    await loadMe();
    await loadIntentionPost();
  } catch (e: any) {
    errorText.value = e?.message || "保存失败";
    app.showToast(e?.message || "保存失败");
  }
}

function onAvatarChange(e: Event) {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    const dataUrl = String(reader.result || "");
    avatarUrl.value = dataUrl;
    try {
      localStorage.setItem(`faceai_avatar_${app.uid}`, dataUrl);
    } catch {
      // ignore storage errors
    }
  };
  reader.readAsDataURL(file);
}

function openAvatarPicker() {
  avatarInputRef.value?.click();
}

function fmtTime(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 19);
}

onMounted(() => {
  loadMe();
  loadPosts();
  loadIntentionPost();
});
</script>

<style scoped>
.user-avatar-image {
  width: 92px;
  height: 92px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 10px 22px rgba(58, 174, 255, 0.28);
}

.user-profile-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.user-top-panel {
  border: 1px solid #e2eef7;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.user-avatar-panel {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 16px;
}

.user-avatar-panel,
.user-avatar-panel .field label {
  text-align: center;
}

.user-avatar-panel .field input {
  width: fit-content;
  max-width: 100%;
  margin: 0 auto;
  display: block;
}

.user-avatar-upload {
  margin-top: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  width: 240px !important;
  max-width: 100%;
  min-height: 42px;
  margin-left: auto;
  margin-right: auto;
  border: 0;
  border-radius: 0;
  background: transparent;
}

.user-avatar-upload label {
  display: none;
}

.avatar-input-hidden {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0, 0, 0, 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
}

.avatar-upload-btn {
  margin: 0 auto;
}

.user-info-item input,
.user-info-item select,
.user-info-item textarea {
  width: 100%;
  border: 1px solid #d9e6f1;
  border-radius: 10px;
  padding: 8px 10px;
  outline: none;
  font-size: 14px;
}

/* 让表头在容器内滚动时保持可见 */
</style>

