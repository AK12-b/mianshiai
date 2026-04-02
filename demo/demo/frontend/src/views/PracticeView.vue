<template>
  <section class="card">
    <h3 style="margin-top: 0">专项练习</h3>

    <div class="config-grid">
      <div class="field">
        <label>岗位ID</label>
        <input v-model.number="form.postId" type="number" min="1" placeholder="请输入岗位ID" />
      </div>
      <div class="field">
        <label>知识点</label>
        <input v-model="form.knowledgePoint" placeholder="如：Java并发、Redis缓存" />
      </div>
      <div class="field">
        <label>难度等级</label>
        <select v-model.number="form.practiceLevel">
          <option :value="1">初级</option>
          <option :value="2">中级</option>
          <option :value="3">高级</option>
        </select>
      </div>
      <div class="field">
        <label>任务周期</label>
        <select v-model.number="form.taskCycle">
          <option :value="1">每日</option>
          <option :value="2">每周</option>
          <option :value="3">每月</option>
        </select>
      </div>
      <div class="field">
        <label>题目数量</label>
        <input v-model.number="form.taskQuantity" type="number" min="1" />
      </div>
      <div class="field">
        <label>开始时间</label>
        <input v-model="form.planStartTime" type="datetime-local" />
      </div>
      <div class="field">
        <label>结束时间</label>
        <input v-model="form.planEndTime" type="datetime-local" />
      </div>
    </div>

    <div class="toolbar">
      <button class="btn btn-primary" :disabled="!canCreate" @click="createMaster">创建练习任务</button>
      <button class="btn btn-outline" @click="loadMasters">刷新任务列表</button>
      <select v-model="statusFilter">
        <option value="">全部状态</option>
        <option value="0">待开始</option>
        <option value="1">进行中</option>
        <option value="2">已完成</option>
      </select>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>任务ID</th>
          <th>知识点</th>
          <th>难度</th>
          <th>周期</th>
          <th>题量</th>
          <th>计划时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in filteredTasks" :key="item.masterId">
          <td>{{ item.masterId }}</td>
          <td>{{ item.knowledgePoint || "--" }}</td>
          <td>{{ levelText(item.practiceLevel) }}</td>
          <td>{{ cycleText(item.taskCycle) }}</td>
          <td>{{ item.taskQuantity || 0 }}</td>
          <td>{{ timeRange(item.planStartTime, item.planEndTime) }}</td>
          <td><span class="status-badge">{{ statusText(item.masterStatus) }}</span></td>
        </tr>
        <tr v-if="!filteredTasks.length">
          <td class="empty-cell" colspan="7">暂无练习任务数据，先创建一条任务试试。</td>
        </tr>
      </tbody>
    </table>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const errorText = ref("");
const statusFilter = ref("");
const tasks = ref<any[]>([]);
const form = ref({
  postId: 1,
  knowledgePoint: "Java并发",
  practiceLevel: 1,
  taskCycle: 1,
  taskQuantity: 10,
  planStartTime: "2026-03-30T08:00",
  planEndTime: "2026-04-06T08:00"
});

const canCreate = computed(() => {
  return (
    Number(form.value.postId) > 0 &&
    String(form.value.knowledgePoint || "").trim() &&
    Number(form.value.taskQuantity) > 0 &&
    !!form.value.planStartTime &&
    !!form.value.planEndTime
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
      postId: Number(form.value.postId),
      knowledgePoint: String(form.value.knowledgePoint || "").trim(),
      practiceLevel: Number(form.value.practiceLevel),
      taskCycle: Number(form.value.taskCycle),
      taskQuantity: Number(form.value.taskQuantity),
      planStartTime: `${form.value.planStartTime}:00`,
      planEndTime: `${form.value.planEndTime}:00`
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
  } catch (e: any) {
    tasks.value = [];
    errorText.value = e?.message || "查询失败";
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

onMounted(() => {
  loadMasters();
});
</script>

