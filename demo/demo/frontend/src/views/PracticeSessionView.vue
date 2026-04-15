<template>
  <section class="practice-wrap">
    <main class="card practice-main">
      <div class="dashboard-section-title">
        <h3 style="margin: 0">刷题作答区</h3>
        <div style="display:flex; gap:8px">
          <button class="btn btn-outline" @click="goBack">返回任务列表</button>
          <button class="btn btn-outline" :disabled="!sessionStarted" @click="pauseSession">暂停练习</button>
        </div>
      </div>

      <div class="toolbar">
        <select v-model.number="sessionConfig.taskQuantity" :disabled="sessionStarted">
          <option :value="5">5题</option>
          <option :value="10">10题</option>
          <option :value="15">15题</option>
        </select>
        <select v-model.number="sessionConfig.practiceLevel" :disabled="sessionStarted">
          <option :value="1">基础</option>
          <option :value="2">进阶</option>
          <option :value="3">高阶</option>
        </select>
        <button class="btn btn-primary" :disabled="sessionStarted" @click="startSession">开始刷题</button>
      </div>

      <div v-if="sessionStarted" class="card" style="margin-top:10px">
        <div style="display:flex; justify-content:space-between; gap:8px; align-items:center">
          <div class="muted">第 {{ currentIndex + 1 }} / {{ questions.length }} 题 · 跳过剩余 {{ maxSkips - skipCount }} 次</div>
        </div>
        <div class="progress-wrap"><i :style="{ width: progressPercent + '%' }"></i></div>
        <div style="margin-top:8px"><strong>题目：</strong>{{ currentQuestion?.questionTitle || "--" }}</div>
        <div class="muted" style="margin-top:4px">知识点：{{ currentQuestion?.knowledgePoint || selectedWeakPoint || "--" }}</div>

        <div class="field" style="margin-top:10px">
          <label>我的作答</label>
          <textarea v-model.trim="userAnswer" rows="7" placeholder="请输入你的作答..." />
        </div>

        <div style="display:flex; justify-content:space-between; gap:8px; margin-top:8px">
          <div style="display:flex; gap:8px">
            <button class="btn btn-outline" :disabled="currentIndex <= 0" @click="prevQuestion">上一题</button>
            <button class="btn btn-outline" :disabled="currentIndex >= questions.length - 1" @click="nextQuestion">下一题</button>
          </div>
          <div style="display:flex; gap:8px">
            <button class="btn btn-text" :disabled="maxSkips - skipCount <= 0" @click="skipCurrent">跳过题目</button>
            <button class="btn btn-primary" :disabled="!userAnswer || isSubmitting" @click="submitAnswer">
              {{ isSubmitting ? "提交中..." : "提交答案" }}
            </button>
          </div>
        </div>

        <div v-if="dynamicTip" class="muted" style="margin-top:8px">{{ dynamicTip }}</div>
      </div>

      <div class="card" style="margin-top:10px">
        <h4 style="margin:0 0 8px">即时批改结果</h4>
        <table class="data-table">
          <thead>
            <tr>
              <th>题目</th>
              <th>得分(均值)</th>
              <th>结果</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in localResults" :key="r.questionId + '-' + r.idx">
              <td>{{ r.questionId }}</td>
              <td>{{ r.avgScore }}</td>
              <td>{{ r.isCorrect ? "通过" : "待加强" }}</td>
            </tr>
            <tr v-if="!localResults.length"><td colspan="3" class="empty-cell">暂无批改结果</td></tr>
          </tbody>
        </table>
      </div>

      <div v-if="sessionFinished" class="card" style="margin-top:10px">
        <h4 style="margin:0 0 8px">本轮练习结果</h4>
        <div class="muted">正确率：{{ correctRate }}% ｜ 总耗时：{{ totalDuration }} 秒 ｜ 平均单题：{{ avgDurationPerQuestion }} 秒</div>
        <div style="display:flex; justify-content:flex-end; margin-top:8px">
          <button class="btn btn-primary" @click="finishAndGoReport">立即打卡并查看分析报告</button>
        </div>
      </div>

      <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
    </main>

    <aside class="card practice-side">
      <div class="dashboard-section-title">
        <h3 style="margin: 0">我的短板知识点</h3>
        <button class="btn btn-outline" @click="loadWeakPoints">刷新归集</button>
      </div>
      <div class="practice-weak-list">
        <article
          v-for="item in weakPoints"
          :key="item.knowledgePoint"
          class="practice-weak-item"
          :class="{ active: selectedWeakPoint === item.knowledgePoint }"
          @click="selectedWeakPoint = item.knowledgePoint"
        >
          <div><strong>{{ item.knowledgePoint }}</strong></div>
          <div class="muted">失分{{ item.wrongCount }}次 · {{ item.mastery }} · {{ levelText(item.level) }}</div>
        </article>
        <div v-if="!weakPoints.length" class="muted">暂无短板数据，将使用当前任务知识点。</div>
      </div>
      <button class="btn btn-text" @click="selectWeakPointAndStart">选择短板刷题</button>
    </aside>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();
const errorText = ref("");
const singleId = computed(() => Number(route.params.singleId));
const masterId = computed(() => Number(route.query.masterId || 0));
const postId = computed(() => Number(route.query.postId || 0));
const queryKnowledgePoint = computed(() => String(route.query.knowledgePoint || "").trim());
const queryLevel = computed(() => Number(route.query.practiceLevel || 1));
const queryQty = computed(() => Number(route.query.taskQuantity || 10));

const weakPoints = ref<any[]>([]);
const selectedWeakPoint = ref("");
const questions = ref<any[]>([]);
const currentIndex = ref(0);
const userAnswer = ref("");
const sessionStarted = ref(false);
const sessionFinished = ref(false);
const sessionStartAt = ref<number>(0);
const skipCount = ref(0);
const maxSkips = 2;
const dynamicTip = ref("");
const localResults = ref<any[]>([]);
const isSubmitting = ref(false);
const progressLoaded = ref(false);

const sessionConfig = ref({
  taskQuantity: queryQty.value > 0 ? queryQty.value : 10,
  practiceLevel: queryLevel.value >= 1 && queryLevel.value <= 3 ? queryLevel.value : 1
});

const currentQuestion = computed(() => questions.value[currentIndex.value] || null);
const progressPercent = computed(() => (questions.value.length ? ((currentIndex.value + 1) / questions.value.length) * 100 : 0));
const correctCount = computed(() => localResults.value.filter((r) => r.isCorrect).length);
const correctRate = computed(() => (localResults.value.length ? Math.round((correctCount.value / localResults.value.length) * 100) : 0));
const totalDuration = computed(() => (sessionStartAt.value ? Math.max(1, Math.round((Date.now() - sessionStartAt.value) / 1000)) : 0));
const avgDurationPerQuestion = computed(() => (localResults.value.length ? Math.round(totalDuration.value / localResults.value.length) : 0));
const progressStorageKey = computed(() => `practice_session_progress_${singleId.value}`);

function goBack() {
  router.push("/app/practice");
}

function levelText(level: any) {
  const v = Number(level ?? 1);
  if (v === 2) return "中级";
  if (v === 3) return "高级";
  return "初级";
}

async function loadWeakPoints() {
  try {
    const list = await app.apiRequest(`/api/wrong-question/user/${app.uid}`);
    const arr = Array.isArray(list) ? list : [];
    const map = new Map<string, any>();
    for (const r of arr) {
      const kp = String(r.knowledgePoint || "").trim();
      if (!kp) continue;
      const old = map.get(kp) || { knowledgePoint: kp, wrongCount: 0, level: 1, mastery: "未掌握" };
      old.wrongCount += 1;
      map.set(kp, old);
    }
    weakPoints.value = Array.from(map.values()).sort((a, b) => b.wrongCount - a.wrongCount).slice(0, 8);
    if (!weakPoints.value.length) {
      const kp = queryKnowledgePoint.value || "通用基础";
      weakPoints.value = [{ knowledgePoint: kp, wrongCount: 0, level: sessionConfig.value.practiceLevel, mastery: "待训练" }];
    }
    if (!selectedWeakPoint.value) selectedWeakPoint.value = weakPoints.value[0]?.knowledgePoint || queryKnowledgePoint.value || "";
  } catch {
    const kp = queryKnowledgePoint.value || "通用基础";
    weakPoints.value = [{ knowledgePoint: kp, wrongCount: 0, level: sessionConfig.value.practiceLevel, mastery: "待训练" }];
    selectedWeakPoint.value = kp;
  }
}

async function loadQuestions() {
  const list = await app.apiRequest(`/api/question-bank/post/${postId.value}`);
  let arr = Array.isArray(list) ? list : [];
  if (selectedWeakPoint.value) arr = arr.filter((q: any) => String(q.knowledgePoint || "").includes(selectedWeakPoint.value));
  arr = arr.filter((q: any) => Number(q.questionLevel || 1) === Number(sessionConfig.value.practiceLevel));
  if (!arr.length) {
    // 放宽难度匹配兜底
    const all = Array.isArray(list) ? list : [];
    arr = selectedWeakPoint.value
      ? all.filter((q: any) => String(q.knowledgePoint || "").includes(selectedWeakPoint.value))
      : all;
  }
  if (!arr.length) {
    // 再兜底：从全量题库里取同岗位/同知识点近似题
    const allQuestions = await app.apiRequest("/api/question-bank/list");
    const all = Array.isArray(allQuestions) ? allQuestions : [];
    let byPost = all.filter((q: any) => Number(q.postId) === postId.value);
    if (selectedWeakPoint.value) {
      const byKp = byPost.filter((q: any) => String(q.knowledgePoint || "").includes(selectedWeakPoint.value));
      if (byKp.length) byPost = byKp;
    }
    arr = byPost.length ? byPost : all;
  }
  questions.value = arr.slice(0, Number(sessionConfig.value.taskQuantity) || 10);
  currentIndex.value = 0;
}

async function startSession() {
  errorText.value = "";
  try {
    await loadQuestions();
    if (!questions.value.length) {
      errorText.value = "当前题库暂无匹配题，已尝试自动放宽筛选，仍未找到可用题目。请先补充题库后再试。";
      return;
    }
    sessionStarted.value = true;
    sessionFinished.value = false;
    sessionStartAt.value = Date.now();
    skipCount.value = 0;
    localResults.value = [];
    userAnswer.value = "";
    dynamicTip.value = "";
    app.showToast("已开始刷题");
    saveProgress();
  } catch (e: any) {
    errorText.value = e?.message || "开始刷题失败";
  }
}

function prevQuestion() {
  if (currentIndex.value > 0) {
    currentIndex.value -= 1;
    userAnswer.value = "";
    saveProgress();
  }
}

function nextQuestion() {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value += 1;
    userAnswer.value = "";
    saveProgress();
  }
}

function skipCurrent() {
  if (maxSkips - skipCount.value <= 0) return;
  skipCount.value += 1;
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value += 1;
    userAnswer.value = "";
  } else {
    sessionFinished.value = true;
  }
  saveProgress();
}

async function submitAnswer() {
  if (isSubmitting.value) return;
  errorText.value = "";
  const q = currentQuestion.value;
  if (!q) return;
  isSubmitting.value = true;
  app.showToast("答案已提交，正在批改...");
  try {
    const rec = await app.apiRequest("/api/practice/submit-answer", "POST", {
      singleId: singleId.value,
      userId: app.uid,
      questionId: Number(q.questionId),
      userAnswer: userAnswer.value,
      inputType: 2,
      answerDuration: 60
    });
    const avg = Math.round((Number(rec.techScore || 0) + Number(rec.knowledgeDepthScore || 0) + Number(rec.logicScore || 0) + Number(rec.expressScore || 0) + Number(rec.matchScore || 0)) / 5);
    localResults.value.push({
      idx: localResults.value.length + 1,
      questionId: q.questionId,
      avgScore: avg,
      isCorrect: Number(rec.errorType || 0) === 0
    });
    userAnswer.value = "";

    if (correctRate.value >= 80) dynamicTip.value = "正确率较高，下一题将建议提升难度。";
    else if (correctRate.value < 50 && localResults.value.length >= 2) dynamicTip.value = "正确率偏低，建议先巩固基础题。";
    else dynamicTip.value = "";

    if (currentIndex.value < questions.value.length - 1) currentIndex.value += 1;
    else sessionFinished.value = true;
    app.showToast("批改完成");
    saveProgress();
  } catch (e: any) {
    errorText.value = e?.message || "提交失败";
    app.showToast(errorText.value);
  } finally {
    isSubmitting.value = false;
  }
}

function pauseSession() {
  saveProgress();
  app.showToast("已暂停并保存进度，下次可继续作答");
}

async function finishAndGoReport() {
  errorText.value = "";
  try {
    const avgScore = localResults.value.length
      ? Number((localResults.value.reduce((s, r) => s + Number(r.avgScore || 0), 0) / localResults.value.length).toFixed(2))
      : 0;
    await app.apiRequest("/api/practice/single-task/finish", "POST", {
      singleId: singleId.value,
      totalDuration: totalDuration.value,
      totalScore: avgScore,
      skipQuantity: skipCount.value,
      finishTime: new Date().toISOString().slice(0, 19)
    });
    await app.apiRequest("/api/practice/report/generate", "POST", {
      singleId: singleId.value,
      userId: app.uid,
      masterId: masterId.value,
      scoreAnalysis: `本轮正确率${correctRate.value}%，平均分${avgScore}，共${questions.value.length}题。`,
      improveSuggest: dynamicTip.value || "建议持续按短板知识点刷题，逐步提升正确率。"
    });
    await app.apiRequest("/api/clock-in/record", "POST", {
      userId: app.uid,
      taskId: masterId.value || singleId.value,
      taskFinishStatus: 1,
      trainDuration: totalDuration.value
    });
    clearProgress();
    router.push(`/app/practice-report/${singleId.value}?masterId=${masterId.value}`);
  } catch (e: any) {
    errorText.value = e?.message || "结算失败，请稍后重试";
  }
}

function selectWeakPointAndStart() {
  if (!selectedWeakPoint.value && weakPoints.value.length) selectedWeakPoint.value = weakPoints.value[0].knowledgePoint;
  startSession();
}

function saveProgress() {
  if (!singleId.value || !sessionStarted.value) return;
  const payload = {
    sessionStarted: sessionStarted.value,
    sessionFinished: sessionFinished.value,
    sessionStartAt: sessionStartAt.value,
    skipCount: skipCount.value,
    selectedWeakPoint: selectedWeakPoint.value,
    sessionConfig: sessionConfig.value,
    questions: questions.value,
    currentIndex: currentIndex.value,
    localResults: localResults.value,
    dynamicTip: dynamicTip.value
  };
  localStorage.setItem(progressStorageKey.value, JSON.stringify(payload));
}

function restoreProgress() {
  if (!singleId.value) return false;
  const raw = localStorage.getItem(progressStorageKey.value);
  if (!raw) return false;
  try {
    const p = JSON.parse(raw);
    if (!Array.isArray(p.questions) || !p.questions.length) return false;
    sessionStarted.value = !!p.sessionStarted;
    sessionFinished.value = !!p.sessionFinished;
    sessionStartAt.value = Number(p.sessionStartAt || 0);
    skipCount.value = Number(p.skipCount || 0);
    selectedWeakPoint.value = String(p.selectedWeakPoint || selectedWeakPoint.value || "");
    sessionConfig.value = {
      taskQuantity: Number(p.sessionConfig?.taskQuantity || sessionConfig.value.taskQuantity || 10),
      practiceLevel: Number(p.sessionConfig?.practiceLevel || sessionConfig.value.practiceLevel || 1)
    };
    questions.value = p.questions;
    currentIndex.value = Math.min(Math.max(Number(p.currentIndex || 0), 0), Math.max(p.questions.length - 1, 0));
    localResults.value = Array.isArray(p.localResults) ? p.localResults : [];
    dynamicTip.value = String(p.dynamicTip || "");
    return true;
  } catch {
    return false;
  }
}

function clearProgress() {
  if (!singleId.value) return;
  localStorage.removeItem(progressStorageKey.value);
}

onMounted(() => {
  loadWeakPoints().finally(() => {
    const restored = restoreProgress();
    progressLoaded.value = true;
    if (restored) app.showToast("已恢复上次练习进度");
  });
});
</script>

<style scoped>
.practice-wrap {
  display: grid;
  grid-template-columns: minmax(0, 2.6fr) minmax(320px, 1fr);
  gap: 12px;
  align-items: start;
}
.practice-main {
  min-width: 0;
}
.practice-side {
  position: sticky;
  top: 90px;
  max-height: calc(100vh - 140px);
  overflow: auto;
}
.practice-weak-list { display: flex; flex-direction: column; gap: 8px; margin: 10px 0; }
.practice-weak-item { border: 1px solid #e2eef7; border-radius: 10px; padding: 8px; cursor: pointer; }
.practice-weak-item.active { border-color: #89c8f0; background: rgba(89,188,246,0.08); }
.progress-wrap { margin-top: 8px; height: 8px; background: #e8f3fb; border-radius: 999px; overflow: hidden; }
.progress-wrap i { display:block; height:100%; background: linear-gradient(90deg,#59bcf6,#3aaeff); }

@media (max-width: 1100px) {
  .practice-wrap {
    grid-template-columns: 1fr;
  }
  .practice-side {
    position: static;
    max-height: none;
  }
}
</style>

