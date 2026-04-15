<template>
  <section class="card pv-wrap">
    <h3 style="margin-top: 0; margin-bottom: 8px">专项练习 - 启动配置</h3>

    <div class="pv-steps">
      <template v-for="(s, idx) in steps" :key="s.key">
        <div class="pv-step" :class="{ active: idx === currentStep }" @click="currentStep = idx">
          <div class="pv-step-index">{{ idx + 1 }}</div>
          <div class="pv-step-label">{{ s.label }}</div>
          <div class="pv-step-value">{{ stepValueText(s.key) }}</div>
        </div>
        <div v-if="idx < steps.length - 1" class="pv-step-line" />
      </template>
    </div>

    <div class="pv-main">
      <section class="pv-left card">
        <div class="pv-left-head">
          <strong>当前步骤配置</strong>
          <span class="muted">{{ currentStep + 1 }} / {{ steps.length }}</span>
        </div>

        <div class="field" v-if="stepKey === 'postId'">
          <label>选择岗位</label>
          <select v-model.number="form.postId">
            <option v-for="p in posts" :key="p.postId" :value="Number(p.postId)">{{ p.postName }}</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'knowledgePoint'">
          <label>知识点</label>
          <select v-model="form.knowledgePoint">
            <option v-for="kp in knowledgePointOptions" :key="kp.code" :value="kp.label">{{ kp.label }}</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'practiceLevel'">
          <label>难度等级</label>
          <select v-model.number="form.practiceLevel">
            <option :value="1">初级</option>
            <option :value="2">中级</option>
            <option :value="3">高级</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'taskCycle'">
          <label>任务周期</label>
          <select v-model.number="form.taskCycle">
            <option :value="1">每日</option>
            <option :value="2">每周</option>
            <option :value="3">每月</option>
          </select>
        </div>
        <div class="field" v-else-if="stepKey === 'taskQuantity'">
          <label>题目数量</label>
          <input v-model.number="form.taskQuantity" type="number" min="1" />
        </div>
        <div class="field" v-else-if="stepKey === 'planStartTime'">
          <label>开始时间</label>
          <input v-model="form.planStartTime" type="datetime-local" />
        </div>
        <div class="field" v-else>
          <label>结束时间</label>
          <input v-model="form.planEndTime" type="datetime-local" />
        </div>

        <div class="pv-actions">
          <button class="btn btn-outline" :disabled="currentStep === 0" @click="currentStep = Math.max(0, currentStep - 1)">上一步</button>
          <button class="btn btn-outline" :disabled="currentStep === steps.length - 1" @click="currentStep = Math.min(steps.length - 1, currentStep + 1)">
            下一步
          </button>
          <button class="btn btn-primary" :disabled="!canCreate" @click="createMaster">创建练习任务</button>
          <button class="btn btn-outline" @click="loadMasters">刷新列表</button>
        </div>

        <div class="pv-summary">
          <div class="pv-summary-title">当前选择</div>
          <div class="pv-summary-grid">
            <div v-for="s in steps" :key="s.key" class="pv-summary-item">
              <span class="muted">{{ s.label }}</span>
              <strong>{{ stepValueText(s.key) }}</strong>
            </div>
          </div>
        </div>
      </section>

      <section class="pv-right card">
        <div class="pv-right-head">
          <strong>专项练习记录</strong>
          <div class="pv-filters">
            <select v-model="statusFilter">
              <option value="">全部状态</option>
              <option value="0">待开始</option>
              <option value="1">进行中</option>
              <option value="2">已完成</option>
            </select>
          </div>
        </div>

        <table class="data-table">
          <thead>
            <tr>
              <th>岗位</th>
              <th>知识点</th>
              <th>难度</th>
              <th>周期</th>
              <th>题量</th>
              <th>计划时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in app.pagedList(filteredTasks, app.pager.questionPage)" :key="item.masterId">
              <td>{{ postNameById(item.postId) }}</td>
              <td>{{ item.knowledgePoint || "--" }}</td>
              <td>{{ levelText(item.practiceLevel) }}</td>
              <td>{{ cycleText(item.taskCycle) }}</td>
              <td>{{ item.taskQuantity || 0 }}</td>
              <td>{{ timeRange(item.planStartTime, item.planEndTime) }}</td>
              <td><span class="pv-op-badge status-badge">{{ statusText(item.masterStatus) }}</span></td>
              <td>
                <div class="pv-op-group">
                  <button class="pv-op-badge status-badge" @click="enterPractice(item)">进入练习</button>
                </div>
              </td>
            </tr>
            <tr v-if="!filteredTasks.length">
              <td class="empty-cell" colspan="8">暂无练习任务数据，先创建一条任务试试。</td>
            </tr>
          </tbody>
        </table>

        <div class="pager">
          <button class="btn btn-outline" @click="app.pager.questionPage = Math.max(1, app.pager.questionPage - 1)">上一页</button>
          <span>{{ app.pager.questionPage }} / {{ app.totalPages(filteredTasks) }}</span>
          <button class="btn btn-outline" @click="app.pager.questionPage = Math.min(app.totalPages(filteredTasks), app.pager.questionPage + 1)">下一页</button>
        </div>
      </section>
    </div>
    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const router = useRouter();
const errorText = ref("");
const statusFilter = ref("");
const currentStep = ref(0);
const tasks = ref<any[]>([]);
const posts = ref<any[]>([]);
const steps = [
  { key: "postId", label: "选择岗位" },
  { key: "knowledgePoint", label: "选择模块" },
  { key: "practiceLevel", label: "难度等级" },
  { key: "taskCycle", label: "任务周期" },
  { key: "taskQuantity", label: "题目数量" },
  { key: "planStartTime", label: "开始时间" },
  { key: "planEndTime", label: "结束时间" }
] as const;
const knowledgePointOptions = [
  { code: 1, label: "技术知识" },
  { code: 2, label: "项目深挖" },
  { code: 3, label: "场景题" },
  { code: 4, label: "行为题" }
] as const;
type StepKey = (typeof steps)[number]["key"];
const stepKey = computed<StepKey>(() => steps[currentStep.value].key);
const form = reactive({
  postId: 1,
  knowledgePoint: "技术知识",
  practiceLevel: 1,
  taskCycle: 1,
  taskQuantity: 10,
  planStartTime: "2026-03-30T08:00",
  planEndTime: "2026-04-06T08:00"
});

const canCreate = computed(() => {
  return (
    Number(form.postId) > 0 &&
    String(form.knowledgePoint || "").trim() &&
    Number(form.taskQuantity) > 0 &&
    !!form.planStartTime &&
    !!form.planEndTime
  );
});

const filteredTasks = computed(() => {
  if (!statusFilter.value) return tasks.value;
  return tasks.value.filter((item) => String(item.masterStatus ?? 0) === statusFilter.value);
});

async function createMaster() {
  errorText.value = "";
  try {
    const payload = {
      userId: app.uid,
      postId: Number(form.postId),
      knowledgePoint: String(form.knowledgePoint || "").trim(),
      practiceLevel: Number(form.practiceLevel),
      taskCycle: Number(form.taskCycle),
      taskQuantity: Number(form.taskQuantity),
      planStartTime: `${form.planStartTime}:00`,
      planEndTime: `${form.planEndTime}:00`
    };
    await app.apiRequest("/api/practice/master-task/create", "POST", payload);
    app.showToast("创建成功");
    await loadMasters();
  } catch (e: any) {
    errorText.value = e?.message || "创建失败，请检查输入格式";
  }
}

async function loadMasters() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/practice/master-task/user/${app.uid}`);
    tasks.value = Array.isArray(data) ? data : [];
    app.pager.questionPage = 1;
  } catch (e: any) {
    tasks.value = [];
    errorText.value = e?.message || "查询失败";
  }
}

async function loadPosts() {
  try {
    const data = (await app.apiRequest("/api/post/list")) || [];
    posts.value = (Array.isArray(data) ? data : []).filter((p: any) => String(p?.postName || "").trim() !== "通用岗位");
    if (posts.value.length > 0 && !posts.value.find((p) => Number(p.postId) === Number(form.postId))) {
      form.postId = Number(posts.value[0].postId);
    }
  } catch {
    posts.value = [];
  }
}

async function enterPractice(item: any) {
  if (Number(item?.masterStatus) !== 1) {
    app.showToast("仅“进行中”任务可进入练习");
    return;
  }
  try {
    // 优先复用未完成的 single task，避免重复创建
    const list = await app.apiRequest(`/api/practice/single-task/master/${item.masterId}`);
    const arr = Array.isArray(list) ? list : [];
    let single = arr.find((s: any) => Number(s?.singleStatus) !== 2);
    if (!single) {
      single = await app.apiRequest("/api/practice/single-task/create", "POST", {
        masterId: Number(item.masterId),
        userId: app.uid,
        postId: Number(item.postId)
      });
    }
    await app.apiRequest(`/api/practice/single-task/${single.singleId}/start`, "POST");
    app.showToast(`已进入练习（任务#${single.singleId}）`);
    router.push({
      path: `/app/practice-session/${single.singleId}`,
      query: {
        masterId: String(item.masterId),
        postId: String(item.postId || ""),
        knowledgePoint: String(item.knowledgePoint || ""),
        practiceLevel: String(item.practiceLevel || ""),
        taskQuantity: String(item.taskQuantity || "")
      }
    });
  } catch (e: any) {
    errorText.value = e?.message || "进入练习失败，请稍后重试";
  }
}

function statusText(status: any) {
  const v = Number(status ?? 0);
  if (v === 1) return "进行中";
  if (v === 2) return "已完成";
  return "待开始";
}

function levelText(level: any) {
  const v = Number(level ?? 1);
  if (v === 2) return "中级";
  if (v === 3) return "高级";
  return "初级";
}

function cycleText(cycle: any) {
  const v = Number(cycle ?? 1);
  if (v === 2) return "每周";
  if (v === 3) return "每月";
  return "每日";
}

function timeRange(start: any, end: any) {
  const s = String(start || "--").replace("T", " ").slice(0, 16);
  const e = String(end || "--").replace("T", " ").slice(0, 16);
  return `${s} ~ ${e}`;
}

function postNameById(postId: any) {
  const p = posts.value.find((x) => Number(x.postId) === Number(postId));
  return p?.postName || "--";
}

function stepValueText(key: StepKey): string {
  if (key === "postId") return postNameById(form.postId);
  if (key === "knowledgePoint") return form.knowledgePoint || "--";
  if (key === "practiceLevel") return levelText(form.practiceLevel);
  if (key === "taskCycle") return cycleText(form.taskCycle);
  if (key === "taskQuantity") return `${form.taskQuantity} 题`;
  if (key === "planStartTime") return String(form.planStartTime || "--").replace("T", " ");
  return String(form.planEndTime || "--").replace("T", " ");
}

onMounted(() => {
  loadPosts();
  loadMasters();
});
</script>

<style scoped>
.pv-wrap {
  display: grid;
  gap: 12px;
}
.pv-steps {
  display: flex;
  align-items: stretch;
  gap: 4px;
  overflow-x: auto;
  margin-top: -80px;
  padding-bottom: 40px;
}
.pv-step {
  min-width: 150px;
  border: 1px solid #e3eef8;
  border-radius: 12px;
  background: #fff;
  padding: 8px 10px;
  cursor: pointer;
    cursor: pointer;
   text-align: center;
   display: flex;
  flex-direction: column;
  justify-content: center; /* 让内容在垂直方向居中 */
}
.pv-step.active {
  border-color: #7ccbed;
  background: linear-gradient(180deg, rgba(122, 203, 237, 0.16), rgba(255, 255, 255, 0.98));
}
.pv-step-index {
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
.pv-step-label {
  font-size: 13px;
  font-weight: 700;
  color: #173a5e;
}
.pv-step-value {
  margin-top: 2px;
  font-size: 12px;
  color: #5b6f86;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.pv-step-line {
  width: 18px;
  align-self: center;
  border-top: 2px dashed #cfe2f0;
  flex: 0 0 auto;
}
.pv-main {
  display: grid;
  grid-template-columns: 1.1fr 1.9fr;
  gap: 12px;
}
.pv-left,
.pv-right {
  padding: 14px;
}
.pv-left-head,
.pv-right-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.pv-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
.pv-summary {
  margin-top: 12px;
  border-top: 1px dashed #dcebf7;
  padding-top: 10px;
}
.pv-summary-title {
  font-weight: 700;
  margin-bottom: 8px;
}
.pv-summary-grid {
  display: grid;
  gap: 6px;
}
.pv-summary-item {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 13px;
}
.pv-filters {
  display: flex;
  gap: 8px;
}
.pv-filters select {
  border: 1px solid #cfe5f3;
  border-radius: 10px;
  padding: 6px 10px;
}
.pv-op-group {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.pv-op-badge {
  border: none;
  cursor: pointer;
  line-height: 1;
  min-width: 64px;
  text-align: center;
}
@media (max-width: 980px) {
  .pv-main {
    grid-template-columns: 1fr;
  }
}
</style>

