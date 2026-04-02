<template>
  <section class="card">
    <div class="dashboard-section-title">
      <div class="session-title-row">
        <h3 style="margin-top: 0">AI 面试</h3>
        <div
          v-if="interviewMeta && showCountdownBar(interviewMeta)"
          class="countdown"
          :class="{
            'countdown--warn': remainingSec !== null && remainingSec <= 60,
            'countdown--critical': remainingSec !== null && remainingSec <= 10
          }"
        >
          <span class="countdown-label">剩余时间</span>
          <span class="countdown-digits">{{ remainingSec !== null ? countdownLabel : "…" }}</span>
          <span class="countdown-meta muted">/ {{ totalDurationMin }} 分钟</span>
        </div>
      </div>
      <div class="toolbar" style="margin: 0">
        <button class="btn btn-outline" @click="reload" :disabled="sessionLocked">刷新对话</button>
        <button class="btn btn-outline" @click="pause" :disabled="sessionLocked">暂停</button>
        <button class="btn btn-primary" @click="end" :disabled="sessionLocked">结束并生成报告</button>
      </div>
    </div>

    <div class="chat-wrap">
      <div v-for="d in dialogs" :key="d.dialogId" class="chat-row" :class="d.speaker === 2 ? 'me' : 'ai'">
        <div class="chat-bubble">
          <div class="chat-meta muted">{{ d.speaker === 2 ? "我" : "AI面试官" }} · 第 {{ d.dialogueRound }} 轮</div>
          <div class="chat-text">{{ d.contentText }}</div>
        </div>
      </div>
      <div v-if="!dialogs.length" class="empty-cell" style="border: 1px dashed #dfeef8; border-radius: 12px">
        暂无对话内容，点击“刷新对话”或等待自动出题。
      </div>
    </div>

    <div class="field" style="margin-top: 12px">
      <label>我的作答</label>
      <textarea v-model="answerText" rows="4" placeholder="请输入你的回答..." />
    </div>
    <div class="toolbar">
      <button
        v-if="speechSupported"
        class="btn"
        :class="listening ? 'btn-primary' : 'btn-outline'"
        :disabled="sessionLocked || sending"
        @click="toggleListening"
      >
        {{ listening ? "停止语音输入" : "语音输入" }}
      </button>
      <button class="btn btn-primary" :disabled="sessionLocked || sending || !answerText.trim()" @click="sendAnswer">
        {{ sending ? "提交中..." : "提交并获取追问" }}
      </button>
      <button class="btn btn-outline" :disabled="sessionLocked || sending" @click="answerText = ''">清空</button>
    </div>
    <div v-if="speechSupported" class="muted" style="margin-top: 6px">
      {{ listening ? "正在听写中…请开始说话" : "提示：点击“语音输入”可将语音转成文字" }}
      <span v-if="speechLangNote">（{{ speechLangNote }}）</span>
    </div>
    <div v-else class="muted" style="margin-top: 6px">
      当前浏览器不支持语音输入，请使用 Chrome / Edge，并确保使用 https 或 localhost 访问。
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();

const interviewId = computed(() => Number(route.params.interviewId));
const dialogs = ref<any[]>([]);
const answerText = ref("");
const sending = ref(false);
const errorText = ref("");
const listening = ref(false);
const speechSupported = ref(false);
const speechLangNote = ref("zh-CN");
const interviewMeta = ref<any | null>(null);
const remainingSec = ref<number | null>(null);
const sessionLocked = ref(false);
const autoFinishing = ref(false);

let recognition: any | null = null;
let listenStartAt = 0;
let lastVoiceDurationSec: number | null = null;
let tickTimer: ReturnType<typeof setInterval> | null = null;

function formatMmSs(totalSec: number) {
  const s = Math.max(0, Math.floor(totalSec));
  const m = Math.floor(s / 60);
  const r = s % 60;
  return `${String(m).padStart(2, "0")}:${String(r).padStart(2, "0")}`;
}

const countdownLabel = computed(() => {
  if (remainingSec.value === null) return "--:--";
  return formatMmSs(remainingSec.value);
});

const totalDurationMin = computed(() => {
  const m = interviewMeta.value?.interviewDuration;
  return typeof m === "number" && m > 0 ? m : 0;
});

/** 与后端一致：1=进行中 */
function isInterviewRunning(meta: any) {
  return Number(meta?.interviewStatus) === 1;
}

/** 已结束/终止则不显示计时条 */
function isInterviewClosed(meta: any) {
  const s = Number(meta?.interviewStatus);
  return s === 2 || s === 3;
}

function showCountdownBar(meta: any) {
  if (!meta || isInterviewClosed(meta)) return false;
  const m = Number(meta.interviewDuration);
  return !Number.isNaN(m) && m > 0;
}

/** 兼容 ISO 字符串、时间戳、Jackson 数组 [y,M,d,H,m,s] */
function parseServerTime(val: unknown): number | null {
  if (val == null) return null;
  if (typeof val === "number" && !Number.isNaN(val)) {
    if (val > 1e12) return val;
    if (val > 1e9) return val * 1000;
    return null;
  }
  if (typeof val === "string") {
    let s = val.trim();
    if (!s) return null;
    if (!s.includes("T") && /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}/.test(s)) {
      s = s.replace(" ", "T");
    }
    const t = Date.parse(s);
    return Number.isNaN(t) ? null : t;
  }
  if (Array.isArray(val) && val.length >= 3) {
    const y = Number(val[0]);
    const mo = Number(val[1]);
    const d = Number(val[2]);
    const h = Number(val[3] ?? 0);
    const mi = Number(val[4] ?? 0);
    const sec = Number(val[5] ?? 0);
    const nano = Number(val[6] ?? 0);
    if ([y, mo, d].some((n) => Number.isNaN(n))) return null;
    const fracMs = Number.isFinite(nano) ? Math.floor(nano / 1e6) : 0;
    const ms = new Date(y, mo - 1, d, h, mi, sec, fracMs).getTime();
    return Number.isNaN(ms) ? null : ms;
  }
  return null;
}

function recomputeRemaining() {
  const meta = interviewMeta.value;
  if (!meta || isInterviewClosed(meta)) {
    remainingSec.value = null;
    return;
  }
  const startMs = parseServerTime(meta.startTime);
  const minutes = Number(meta.interviewDuration) || 0;
  if (startMs === null || minutes <= 0) {
    remainingSec.value = null;
    return;
  }
  const endMs = startMs + minutes * 60 * 1000;
  remainingSec.value = Math.max(0, Math.floor((endMs - Date.now()) / 1000));
}

function stopTickTimer() {
  if (tickTimer !== null) {
    clearInterval(tickTimer);
    tickTimer = null;
  }
}

function startTickTimer() {
  stopTickTimer();
  recomputeRemaining();
  tickTimer = setInterval(() => {
    recomputeRemaining();
    if (remainingSec.value !== null && remainingSec.value <= 0) {
      stopTickTimer();
      void onTimeUp();
    }
  }, 1000);
}

function applyInterviewMeta(raw: any) {
  if (!raw) {
    interviewMeta.value = null;
    return;
  }
  const merged = { ...raw };
  if (merged.interviewStatus == null && merged.status != null) {
    merged.interviewStatus = merged.status;
  }
  if (merged.interviewStatus != null) {
    merged.interviewStatus = Number(merged.interviewStatus);
  }
  interviewMeta.value = merged;
}

async function loadInterviewMeta() {
  const raw = await app.apiRequest(`/api/interview/${interviewId.value}`);
  applyInterviewMeta(raw);
  const data = interviewMeta.value;
  const status = Number(data?.interviewStatus);
  if (status === 2 || status === 3) {
    app.showToast("该面试已结束");
    router.push("/app/interview-reports");
    return false;
  }
  recomputeRemaining();
  if (remainingSec.value !== null && remainingSec.value <= 0 && interviewMeta.value && isInterviewRunning(interviewMeta.value)) {
    await onTimeUp();
    return false;
  }
  startTickTimer();
  return true;
}

async function onTimeUp() {
  if (autoFinishing.value || sessionLocked.value) return;
  autoFinishing.value = true;
  sessionLocked.value = true;
  stopTickTimer();
  try {
    listening.value = false;
    try {
      if (recognition) recognition.stop();
    } catch {
      // ignore
    }
    await app.apiRequest(`/api/interview/${interviewId.value}/end`, "POST", { timeout: true });
    app.showToast("面试时长已到，已自动结束并生成报告");
    router.push("/app/interview-reports");
  } catch (e: any) {
    app.showToast(e?.message || "自动结束失败");
    sessionLocked.value = false;
    autoFinishing.value = false;
    if (interviewMeta.value && isInterviewRunning(interviewMeta.value)) startTickTimer();
  }
}

async function reload() {
  errorText.value = "";
  try {
    if (!sessionLocked.value) {
      try {
        const metaRaw = await app.apiRequest(`/api/interview/${interviewId.value}`);
        applyInterviewMeta(metaRaw);
        recomputeRemaining();
      } catch {
        // 忽略元数据刷新失败，仍尝试加载对话
      }
    }
    const data = await app.apiRequest(`/api/interview/${interviewId.value}/dialogs`);
    dialogs.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    dialogs.value = [];
    errorText.value = e?.message || "加载对话失败";
  }
}

async function ensureFirstQuestion() {
  try {
    const list = await app.apiRequest(`/api/interview/${interviewId.value}/dialogs`);
    const arr = Array.isArray(list) ? list : [];
    if (arr.length > 0) {
      dialogs.value = arr;
      return;
    }
    await app.apiRequest(`/api/interview/${interviewId.value}/start`, "POST");
    await reload();
  } catch (e: any) {
    errorText.value = e?.message || "生成首题失败";
  }
}

async function sendAnswer() {
  if (!answerText.value.trim()) return;
  sending.value = true;
  errorText.value = "";
  const text = answerText.value.trim();
  try {
    await app.apiRequest("/api/interview/submit-answer", "POST", {
      interviewId: interviewId.value,
      questionId: null,
      answerText: text,
      voiceUrl: null,
      voiceDuration: lastVoiceDurationSec
    });
    await app.apiRequest(`/api/interview/${interviewId.value}/follow-up`, "POST", { userAnswer: text });
    answerText.value = "";
    lastVoiceDurationSec = null;
    await reload();
  } catch (e: any) {
    errorText.value = e?.message || "提交失败";
  } finally {
    sending.value = false;
  }
}

async function pause() {
  if (sessionLocked.value) return;
  try {
    await app.apiRequest(`/api/interview/${interviewId.value}/pause`, "POST");
    app.showToast("已暂停");
  } catch (e: any) {
    app.showToast(e?.message || "暂停失败");
  }
}

async function end() {
  if (sessionLocked.value) return;
  sessionLocked.value = true;
  stopTickTimer();
  try {
    await app.apiRequest(`/api/interview/${interviewId.value}/end`, "POST");
    app.showToast("已结束，报告生成中");
    router.push("/app/interview-reports");
  } catch (e: any) {
    sessionLocked.value = false;
    if (interviewMeta.value && isInterviewRunning(interviewMeta.value)) startTickTimer();
    app.showToast(e?.message || "结束失败");
  }
}

onMounted(async () => {
  if (!interviewId.value) {
    router.push("/app/interview");
    return;
  }
  initSpeech();
  try {
    const ok = await loadInterviewMeta();
    if (!ok) return;
  } catch (e: any) {
    errorText.value = e?.message || "加载面试信息失败";
    app.showToast(errorText.value);
    return;
  }
  await ensureFirstQuestion();
});

onBeforeUnmount(() => {
  stopTickTimer();
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  recognition = null;
  listening.value = false;
});

function initSpeech() {
  const w: any = window as any;
  const SR = w.SpeechRecognition || w.webkitSpeechRecognition;
  if (!SR) {
    speechSupported.value = false;
    return;
  }
  speechSupported.value = true;
  recognition = new SR();
  recognition.lang = "zh-CN";
  recognition.interimResults = true;
  recognition.continuous = true;

  recognition.onstart = () => {
    listening.value = true;
    listenStartAt = Date.now();
  };

  recognition.onend = () => {
    if (listening.value) {
      listening.value = false;
      if (listenStartAt) {
        lastVoiceDurationSec = Math.max(1, Math.round((Date.now() - listenStartAt) / 1000));
      }
    }
  };

  recognition.onerror = (event: any) => {
    listening.value = false;
    const msg = event?.error ? `语音输入失败：${event.error}` : "语音输入失败";
    app.showToast(msg);
  };

  recognition.onresult = (event: any) => {
    let finalText = "";
    let interimText = "";
    for (let i = event.resultIndex; i < event.results.length; i++) {
      const res = event.results[i];
      const txt = res?.[0]?.transcript || "";
      if (res.isFinal) finalText += txt;
      else interimText += txt;
    }
    const append = (finalText || interimText).trim();
    if (!append) return;
    // 追加到输入框末尾，避免覆盖用户已编辑内容
    const sep = answerText.value && !answerText.value.endsWith("\n") ? "\n" : "";
    answerText.value = `${answerText.value}${sep}${append}`.trimStart();
  };
}

function toggleListening() {
  if (!recognition) return;
  try {
    if (listening.value) {
      recognition.stop();
      return;
    }
    lastVoiceDurationSec = null;
    recognition.start();
  } catch (e: any) {
    app.showToast(e?.message || "语音输入启动失败");
  }
}
</script>

<style scoped>
.chat-wrap {
  border: 1px solid #e2eef7;
  border-radius: 14px;
  padding: 12px;
  background: #fff;
  max-height: 420px;
  overflow: auto;
}

.chat-row {
  display: flex;
  margin: 8px 0;
}

.chat-row.ai {
  justify-content: flex-start;
}

.chat-row.me {
  justify-content: flex-end;
}

.chat-bubble {
  width: min(740px, 92%);
  border: 1px solid #e7f1f8;
  border-radius: 14px;
  padding: 10px 12px;
  background: linear-gradient(180deg, #ffffff, #f7fcff);
}

.chat-row.me .chat-bubble {
  background: linear-gradient(180deg, rgba(231, 255, 201, 0.35), rgba(89, 188, 246, 0.08));
  border-color: #d3eefc;
}

.chat-meta {
  font-size: 12px;
  margin-bottom: 6px;
}

.chat-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.session-title-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 20px;
}

.countdown {
  display: inline-flex;
  align-items: baseline;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  background: linear-gradient(180deg, #f0f9ff, #e8f4fc);
  border: 1px solid #c5e3f5;
  font-size: 14px;
}

.countdown-label {
  font-size: 12px;
  color: #5c7a8f;
}

.countdown-digits {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
  font-size: 18px;
  letter-spacing: 0.04em;
  color: #0b4f7c;
}

.countdown-meta {
  font-size: 12px;
}

.countdown--warn {
  border-color: #f0c14d;
  background: linear-gradient(180deg, #fffbf0, #fff5e0);
}

.countdown--warn .countdown-digits {
  color: #a35f00;
}

.countdown--critical {
  border-color: #f59b9b;
  background: linear-gradient(180deg, #fff5f5, #ffe8e8);
  animation: pulse-soft 1s ease-in-out infinite;
}

.countdown--critical .countdown-digits {
  color: #b42318;
}

@keyframes pulse-soft {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.88;
  }
}
</style>

