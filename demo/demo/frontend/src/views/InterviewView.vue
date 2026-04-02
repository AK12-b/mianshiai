<template>
  <section class="card">
    <h3 style="margin-top: 0">模拟面试 - 启动配置</h3>
    <p v-if="posts.length === 0" class="muted" style="margin: 0 0 10px">
      当前没有岗位基础数据，所以列表和下拉会为空。请先在数据库中初始化 `post` 表数据，再创建面试。
    </p>

    <div class="toolbar">
      <button class="btn btn-primary" :disabled="!canCreate" @click="createInterview">创建面试</button>
      <button class="btn btn-outline" @click="loadInterviews">刷新列表</button>
      <select v-model="app.filters.interviewMode">
        <option value="">全部模式</option>
        <option value="1">全流程</option>
        <option value="2">专项</option>
      </select>
      <select v-model="app.filters.interviewStatus">
        <option value="">全部状态</option>
        <option value="0">待开始</option>
        <option value="1">进行中</option>
        <option value="2">已结束</option>
        <option value="3">已暂停</option>
        <option value="4">已终止</option>
      </select>
    </div>

    <div class="config-grid">
      <div class="field">
        <label>岗位</label>
        <select v-if="posts.length > 0" v-model.number="form.postId">
          <option v-for="p in posts" :key="p.postId" :value="p.postId">{{ p.postName }}</option>
        </select>
        <input v-else v-model.number="form.postId" type="number" min="1" placeholder="暂无岗位数据，请手动输入岗位ID（如 1）" />
        <small v-if="posts.length === 0" class="muted">提示：当前岗位列表为空，请先在后台新增岗位，或临时手动填写岗位ID。</small>
      </div>
      <div class="field">
        <label>面试模式</label>
        <select v-model.number="form.interviewMode">
          <option :value="1">全流程</option>
          <option :value="2">专项</option>
        </select>
      </div>
      <div class="field">
        <label>面试模块</label>
        <select v-model="form.interviewModule">
          <option value="技术基础">技术基础</option>
          <option value="项目经历">项目经历</option>
          <option value="场景题">场景题</option>
          <option value="行为面试">行为面试</option>
        </select>
      </div>
      <div class="field">
        <label>面试官风格</label>
        <select v-model.number="form.aiCharacter">
          <option :value="1">严谨型</option>
          <option :value="2">亲和型</option>
          <option :value="3">压力型</option>
        </select>
      </div>
      <div class="field">
        <label>面试官性别</label>
        <select v-model.number="form.aiGender">
          <option :value="1">男</option>
          <option :value="2">女</option>
        </select>
      </div>
      <div class="field">
        <label>时长(分钟)</label>
        <select v-model.number="form.interviewDuration">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="15">15</option>
          <option :value="20">20</option>
        </select>
      </div>
      <div class="field">
        <label>输入方式</label>
        <select v-model.number="form.inputType">
          <option :value="1">语音</option>
          <option :value="2">文本</option>
        </select>
      </div>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>岗位</th>
          <th>模式</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in app.pagedList(app.interviewFiltered, app.pager.interviewPage)" :key="row.interviewId">
          <td>{{ row.interviewId }}</td>
          <td>{{ row.postId }}</td>
          <td>{{ app.modeText(row.interviewMode) }}</td>
          <td><span class="status-badge">{{ app.statusText(row.status) }}</span></td>
          <td>
            <button class="btn btn-text" @click="goSession(row.interviewId)">开始</button>
            <button class="btn btn-text" @click="call(`/api/interview/${row.interviewId}/pause`)">暂停</button>
            <button class="btn btn-text" @click="call(`/api/interview/${row.interviewId}/end`)">结束</button>
          </td>
        </tr>
        <tr v-if="app.interviewFiltered.length === 0">
          <td colspan="5" class="empty-cell">暂无面试记录，请先点击“创建面试”。</td>
        </tr>
      </tbody>
    </table>

    <div class="pager">
      <button class="btn btn-outline" @click="app.pager.interviewPage = Math.max(1, app.pager.interviewPage - 1)">上一页</button>
      <span>{{ app.pager.interviewPage }} / {{ app.totalPages(app.interviewFiltered) }}</span>
      <button class="btn btn-outline" @click="app.pager.interviewPage = Math.min(app.totalPages(app.interviewFiltered), app.pager.interviewPage + 1)">
        下一页
      </button>
    </div>
    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useAppStore } from "../stores/app";
import { reactive, ref } from "vue";
import { computed } from "vue";
import { useRouter } from "vue-router";

const app = useAppStore();
const router = useRouter();
const errorText = ref("");
const posts = ref<any[]>([]);
const canCreate = computed(() => posts.value.length > 0 && Number(form.postId) > 0);
const form = reactive({
  postId: 1,
  interviewMode: 1,
  interviewModule: "技术基础",
  aiCharacter: 1,
  aiGender: 2,
  interviewDuration: 15,
  inputType: 2
});

async function loadInterviews() {
  errorText.value = "";
  try {
    app.lists.interviews = (await app.apiRequest(`/api/interview/user/${app.uid}`)) || [];
    app.pager.interviewPage = 1;
  } catch (e: any) {
    errorText.value = `加载失败：${e?.message || "未知错误"}`;
    app.showToast(e?.message || "加载失败");
  }
}

async function createInterview() {
  errorText.value = "";
  try {
    if (!form.postId) {
      app.showToast("请先填写岗位ID");
      return;
    }
    if (posts.value.length === 0) {
      app.showToast("当前没有岗位数据，无法创建面试，请先新增岗位");
      return;
    }
    const payload = {
      userId: app.uid,
      postId: Number(form.postId),
      interviewMode: Number(form.interviewMode),
      interviewModule: form.interviewModule,
      aiCharacter: Number(form.aiCharacter),
      aiGender: Number(form.aiGender),
      interviewDuration: Number(form.interviewDuration),
      inputType: Number(form.inputType)
    };
    const res = await app.apiRequest("/api/interview/create", "POST", payload);
    app.showToast("创建成功");
    await loadInterviews();
    // 创建后可直接进入面试环节
    if (res?.interviewId) router.push(`/app/interview-session/${res.interviewId}`);
  } catch (e: any) {
    errorText.value = `创建失败：${e?.message || "接口失败"}`;
    app.showToast(e?.message || "创建失败");
  }
}

async function call(url: string) {
  errorText.value = "";
  try {
    await app.apiRequest(url, "POST");
    app.showToast("操作成功");
    await loadInterviews();
  } catch (e: any) {
    errorText.value = `操作失败：${e?.message || "未知错误"}`;
    app.showToast(e?.message || "操作失败");
  }
}

function goSession(interviewId: number) {
  router.push(`/app/interview-session/${interviewId}`);
}

onMounted(loadInterviews);
onMounted(async () => {
  try {
    posts.value = (await app.apiRequest("/api/post/list")) || [];
    if (posts.value.length > 0 && !posts.value.find((p) => p.postId === form.postId)) {
      form.postId = posts.value[0].postId;
    }
  } catch {
    posts.value = [];
  }
});
</script>

