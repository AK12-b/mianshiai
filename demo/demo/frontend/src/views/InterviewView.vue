<template>
  <section class="card iv-wrap">
    <h3 style="margin-top: 0">模拟面试 - 启动配置</h3>
    <p v-if="posts.length === 0" class="muted" style="margin: 0 0 10px">
      当前没有岗位基础数据，所以列表和下拉会为空。请先在数据库中初始化 `post` 表数据，再创建面试。
    </p>

    <div class="iv-steps">
      <template v-for="(s, idx) in steps" :key="s.key">
        <div class="iv-step" :class="{ active: idx === currentStep }" @click="currentStep = idx">
          <div class="iv-step-index">{{ idx + 1 }}</div>
          <div class="iv-step-label">{{ s.label }}</div>
          <div class="iv-step-value">{{ stepValueText(s.key) }}</div>
        </div>
        <div v-if="idx < steps.length - 1" class="iv-step-line" />
      </template>
    </div>

    <div class="iv-main">
      <section class="iv-left card">
        <div class="iv-left-head">
          <strong>当前步骤配置</strong>
          <span class="muted">{{ currentStep + 1 }} / {{ steps.length }}</span>
        </div>

        <div class="field" v-if="stepKey === 'postId'">
          <label>选择岗位</label>
          <select v-if="posts.length > 0" v-model.number="form.postId">
            <option v-for="p in posts" :key="p.postId" :value="p.postId">{{ p.postName }}</option>
          </select>
          <input v-else v-model.number="form.postId" type="number" min="1" placeholder="暂无岗位数据，请手动输入岗位编号（如 1）" />
        </div>
        <div class="field" v-else-if="stepKey === 'interviewMode'">
          <label>选择面试模式</label>
          <select v-model.number="form.interviewMode">
            <option :value="1">全流程</option>
            <option :value="2">专项</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'interviewModule'">
          <label>选择面试模块</label>
          <select v-model="form.interviewModule">
            <option value="技术基础">技术基础</option>
            <option value="项目经历">项目经历</option>
            <option value="场景题">场景题</option>
            <option value="行为面试">行为面试</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'aiCharacter'">
          <label>选择面试官风格</label>
          <select v-model.number="form.aiCharacter">
            <option :value="1">温柔亲和型</option>
            <option :value="2">压力型</option>
            <option :value="3">正常严谨型</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'aiGender'">
          <label>选择面试官性别</label>
          <select v-model.number="form.aiGender">
            <option :value="1">男</option>
            <option :value="2">女</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'interviewDuration'">
          <label>选择时长(分钟)</label>
          <select v-model.number="form.interviewDuration">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="15">15</option>
            <option :value="20">20</option>
          </select>
        </div>
        <div class="field" v-else>
          <label>选择输入方式</label>
          <select v-model.number="form.inputType">
            <option :value="1">语音</option>
            <option :value="2">文本</option>
          </select>
        </div>

        <div class="iv-actions">
          <button class="btn btn-outline" :disabled="currentStep === 0" @click="currentStep = Math.max(0, currentStep - 1)">上一步</button>
          <button class="btn btn-outline" :disabled="currentStep === steps.length - 1" @click="currentStep = Math.min(steps.length - 1, currentStep + 1)">下一步</button>
          <button class="btn btn-primary" :disabled="!canCreate" @click="createInterview">创建面试</button>
          <button class="btn btn-outline" @click="loadInterviews">刷新列表</button>
        </div>

        <div class="iv-summary">
          <div class="iv-summary-title">当前选择</div>
          <div class="iv-summary-grid">
            <div v-for="s in steps" :key="s.key" class="iv-summary-item">
              <span class="muted">{{ s.label }}</span>
              <strong>{{ stepValueText(s.key) }}</strong>
            </div>
          </div>
        </div>
      </section>

      <section class="iv-right card">
        <div class="iv-right-head">
          <strong>模拟面试记录</strong>
          <div class="iv-filters">
            <select v-model="app.filters.interviewMode">
              <option value="">全部模式</option>
              <option value="1">全流程</option>
              <option value="2">专项</option>
            </select>
            <select v-model="app.filters.interviewStatus">
              <option value="">全部状态</option>
              <option value="0">待开始</option>
              <option value="1">进行中</option>
              <option value="2">已完成</option>
              <option value="3">已终止</option>
              <option value="4">已暂停</option>
            </select>
          </div>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>岗位</th>
              <th>模式</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in app.pagedList(app.interviewFiltered, app.pager.interviewPage)" :key="row.interviewId">
              <td>{{ postNameById(row.postId) }}</td>
              <td>{{ app.modeText(row.interviewMode) }}</td>
              <td><span class="status-badge">{{ app.statusText(getStatus(row)) }}</span></td>
              <td>
                <div v-if="interviewOpsVisible(row)" class="iv-op-group">
                  <button
                    class="iv-op-badge status-badge"
                    :disabled="sessionStartingId === Number(row.interviewId)"
                    @click="goSession(row.interviewId)"
                  >
                    {{ sessionStartingId === Number(row.interviewId) ? "进入中…" : "开始" }}
                  </button>
                  <button
                    v-if="canPauseInterview(row)"
                    class="iv-op-badge status-badge"
                    @click="call(`/api/interview/${row.interviewId}/pause`)"
                  >
                    暂停
                  </button>
                  <button class="iv-op-badge status-badge" @click="callTerminate(row.interviewId)">结束</button>
                </div>
                <span v-else class="muted">—</span>
              </td>
            </tr>
            <tr v-if="app.interviewFiltered.length === 0">
              <td colspan="4" class="empty-cell">暂无面试记录，请先点击“创建面试”。</td>
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
      </section>
    </div>
    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useAppStore } from "../stores/app";
import { useRouter } from "vue-router";

const app = useAppStore();
const router = useRouter();
const errorText = ref("");
const sessionStartingId = ref<number | null>(null);
const posts = ref<any[]>([]);
const currentStep = ref(0);
const canCreate = computed(() => posts.value.length > 0 && Number(form.postId) > 0);
const steps = [
  { key: "postId", label: "选择岗位" },
  { key: "interviewMode", label: "选择面试模式" },
  { key: "interviewModule", label: "选择面试模块" },
  { key: "aiCharacter", label: "面试官风格" },
  { key: "aiGender", label: "面试官性别" },
  { key: "interviewDuration", label: "时长" },
  { key: "inputType", label: "输入方式" }
] as const;
type StepKey = (typeof steps)[number]["key"];
const stepKey = computed<StepKey>(() => steps[currentStep.value].key);
const form = reactive({
  postId: 1,
  interviewMode: 1,
  interviewModule: "技术基础",
  aiCharacter: 1,
  aiGender: 2,
  interviewDuration: 15,
  inputType: 2
});

function stepValueText(key: StepKey): string {
  if (key === "postId") {
    const post = posts.value.find((p) => Number(p.postId) === Number(form.postId));
    return post ? post.postName : `岗位:${form.postId}`;
  }
  if (key === "interviewMode") return form.interviewMode === 1 ? "全流程" : "专项";
  if (key === "interviewModule") return form.interviewModule;
  if (key === "aiCharacter") return form.aiCharacter === 1 ? "温柔亲和型" : form.aiCharacter === 2 ? "压力型" : "正常严谨型";
  if (key === "aiGender") return form.aiGender === 1 ? "男" : "女";
  if (key === "interviewDuration") return `${form.interviewDuration} 分钟`;
  return form.inputType === 1 ? "语音" : "文本";
}

function postNameById(postId: any): string {
  const p = posts.value.find((x) => Number(x.postId) === Number(postId));
  return p?.postName || String(postId ?? "--");
}

/** 与后端 mock_interview.interview_status 一致；优先 interviewStatus，避免与其它字段名 status 冲突 */
function getStatus(row: any): number {
  const raw = row?.interviewStatus ?? row?.interviewStatusCode ?? row?.status;
  const n = Number(raw);
  return Number.isFinite(n) ? n : 0;
}

/** 仅 进行中(1)、已暂停(4) 可操作；已完成(2)、已终止(3) 不显示任何按钮 */
function interviewOpsVisible(row: any) {
  const st = getStatus(row);
  return st === 1 || st === 4;
}

function canPauseInterview(row: any) {
  return getStatus(row) === 1;
}

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
      app.showToast("请先选择岗位");
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

async function callTerminate(interviewId: number) {
  const row = app.lists.interviews.find((r: any) => Number(r.interviewId) === Number(interviewId));
  const st = row ? getStatus(row) : null;
  if (st === 2 || st === 3) return;
  await call(`/api/interview/${interviewId}/terminate`);
}

async function goSession(id: number | string) {
  const interviewId = Number(id);
  if (!Number.isFinite(interviewId) || interviewId <= 0) {
    app.showToast("无效的面试编号");
    return;
  }
  const row = app.lists.interviews.find((r: any) => Number(r.interviewId) === interviewId);
  const st = row ? getStatus(row) : null;
  if (st === 2) {
    app.showToast("该面试已完成，请在「面试记录 / 报告」中查看");
    router.push("/app/interview-reports");
    return;
  }
  if (st === 3) {
    app.showToast("该面试已终止，无法继续");
    return;
  }
  if (st === 4) {
    sessionStartingId.value = interviewId;
    try {
      await app.apiRequest(`/api/interview/${interviewId}/resume`, "POST");
      await loadInterviews();
    } catch (e: any) {
      app.showToast(e?.message || "恢复面试失败");
      return;
    } finally {
      sessionStartingId.value = null;
    }
  }
  router.push({ path: `/app/interview-session/${interviewId}` });
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

<style scoped>
.iv-wrap {
  display: grid;
  gap: 2px;
}

.iv-steps {
  display: flex;
  align-items: stretch;
  gap: 4px;
  overflow-x: auto;
  margin-top: -50px;
  padding-bottom: 40px;
}
.iv-step {
  min-width: 150px;
  border: 1px solid #e3eef8;
  border-radius: 12px;
  background: #fff;
  padding: 8px 10px;
  cursor: pointer;
   text-align: center;
   display: flex;
  flex-direction: column;
  justify-content: center; /* 让内容在垂直方向居中 */

}
.iv-step.active {
  border-color: #7ccbed;
  background: linear-gradient(180deg, rgba(122, 203, 237, 0.16), rgba(255, 255, 255, 0.98));
}
.iv-step-index {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #eef6fc;
  color: #2b5b84;
  font-size: 12px;
  display: grid;
  place-items: center;
  margin-bottom: 4px;
    margin-left: auto;
  margin-right: auto;
}
.iv-step-label {
  font-size: 13px;
  font-weight: 700;
  color: #173a5e;
}
.iv-step-value {
  margin-top: 2px;
  font-size: 12px;
  color: #5b6f86;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.iv-step-line {
  width: 18px;
  align-self: center;
  border-top: 2px dashed #cfe2f0;
  flex: 0 0 auto;
}
.iv-main {
  display: grid;
  grid-template-columns: 1.1fr 1.9fr;
  gap: 12px;
}
.iv-left,
.iv-right {
  padding: 14px;
}
.iv-left-head,
.iv-right-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.iv-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
.iv-summary {
  margin-top: 12px;
  border-top: 1px dashed #dcebf7;
  padding-top: 10px;
}
.iv-summary-title {
  font-weight: 700;
  margin-bottom: 8px;
}
.iv-summary-grid {
  display: grid;
  gap: 6px;
}
.iv-summary-item {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 13px;
}
.iv-filters {
  display: flex;
  gap: 8px;
}
.iv-filters select {
  border: 1px solid #cfe5f3;
  border-radius: 10px;
  padding: 6px 10px;
}
.iv-op-group {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.iv-op-badge {
  border: none;
  cursor: pointer;
  line-height: 1;
  min-width: 44px;
  text-align: center;
}
@media (max-width: 980px) {
  .iv-main {
    grid-template-columns: 1fr;
  }
}
</style>

