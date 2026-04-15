<template>
  <section class="card interview-session">
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

    <div class="session-body">
      <div class="chat-scroll">
        <div class="chat-wrap">
          <div v-for="d in dialogs" :key="d.dialogId" class="chat-row" :class="Number(d.speaker) === 2 ? 'me' : 'ai'">
            <template v-if="Number(d.speaker) !== 2">
              <div class="avatar-wrap">
                <img v-if="interviewerImgOk" :src="interviewerImgSrc" class="avatar-img" alt="面试官" @error="onInterviewerImgError" />
                <div v-else class="avatar-img avatar-fallback ai-fallback" aria-hidden="true">AI</div>
              </div>
              <div class="chat-col">
                <div class="chat-bubble">
                  <div class="chat-head">
                    <div class="chat-meta muted">AI面试官 · 第 {{ d.dialogueRound }} 轮</div>
                    <div class="chat-actions">
                      <button
                        v-if="textInterviewMode && ttsOk && canReadAloud(d.contentText)"
                        type="button"
                        class="btn-text-read"
                        :disabled="sessionLocked"
                        @click="speakText(String(d.contentText || ''))"
                      >
                        点读
                      </button>
                    </div>
                  </div>
                  <div class="chat-text">{{ d.contentText }}</div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="chat-col chat-col-me">
                <div class="chat-bubble bubble-me">
                  <div class="chat-head">
                    <div class="chat-meta muted">我 · 第 {{ d.dialogueRound }} 轮</div>
                    <div class="chat-actions">
                      <button
                        v-if="voiceInterviewMode && ttsOk && canReadAloud(d.contentText)"
                        type="button"
                        class="btn-text-read"
                        :disabled="sessionLocked"
                        @click="speakText(String(d.contentText || ''))"
                      >
                        回放
                      </button>
                    </div>
                  </div>
                  <div class="chat-text">{{ d.contentText }}</div>
                </div>
              </div>
              <div class="avatar-wrap">
                <img v-if="userAvatarSrc" :src="userAvatarSrc" class="avatar-img" alt="我" />
                <div v-else class="avatar-img avatar-fallback">{{ userAvatarLetter }}</div>
              </div>
            </template>
          </div>
          <div v-if="!dialogs.length" class="empty-cell">暂无对话内容，点击「刷新对话」或等待自动出题。</div>
        </div>
      </div>

      <!-- 文本面试：底部输入条，右侧麦克风 + 发送图标 -->
      <div v-if="textInterviewMode" class="composer composer-text">
        <div class="composer-text-row">
          <textarea
            v-model="answerText"
            class="composer-input"
            rows="3"
            placeholder="请输入你的回答…"
            :disabled="sessionLocked || sending"
            @keydown.enter.exact.prevent="trySendAnswer"
          />
          <div class="composer-icon-stack">
            <button
              v-if="speechSupported"
              type="button"
              class="icon-tool"
              :class="{ 'icon-tool--on': listening }"
              :disabled="sessionLocked || sending"
              title="语音输入"
              @click="toggleListening"
            >
              <svg class="svg-ic" viewBox="0 0 24 24" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M12 14c1.66 0 3-1.34 3-3V5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z"
                />
              </svg>
            </button>
            <button
              type="button"
              class="icon-tool icon-tool--send"
              :disabled="sessionLocked || sending || aiPending || !answerText.trim()"
              :title="sending ? '提交中' : aiPending ? '生成中' : '发送'"
              @click="sendAnswer"
            >
              <svg class="svg-ic" viewBox="0 0 24 24" aria-hidden="true">
                <path fill="currentColor" d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
              </svg>
            </button>
          </div>
        </div>
        <p v-if="speechSupported" class="composer-hint muted">
          {{ listening ? "正在听写…" : "右侧图标：麦克风转文字、纸飞机发送（Enter 也可发送）" }}
          <span v-if="speechLangNote">（{{ speechLangNote }}）</span>
        </p>
        <p v-else class="composer-hint muted">当前浏览器不支持语音输入，请使用 Chrome / Edge。</p>
      </div>

      <!-- 语音面试：实时听写 + 底部麦克风（旁附发送） -->
      <div v-else-if="voiceInterviewMode" class="composer composer-voice">
        <div v-if="speechSupported && (listening || interimTranscript)" class="live-transcript">
          <span class="live-transcript-label">实时听写</span>
          <div class="live-transcript-body">
            <span>{{ answerText }}</span><span v-if="interimTranscript" class="live-transcript-interim">{{ interimTranscript }}</span>
          </div>
        </div>
        <textarea
          v-model="answerText"
          class="composer-input composer-input--voice"
          rows="2"
          placeholder="可在此修订听写内容后发送…"
          :disabled="sessionLocked || sending"
        />
        <div class="voice-mic-bar">
          <button
            v-if="speechSupported"
            type="button"
            class="voice-mic-fab"
            :class="{ 'voice-mic-fab--on': listening }"
            :disabled="sessionLocked || sending"
            title="语音作答"
            @click="toggleListening"
          >
            <svg class="svg-ic svg-ic--lg" viewBox="0 0 24 24" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 14c1.66 0 3-1.34 3-3V5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3zm5.3-3c0 3-2.54 5.1-5.3 5.1S6.7 14 6.7 11H5c0 3.41 2.72 6.23 6 6.72V21h2v-3.28c3.28-.48 6-3.3 6-6.72h-1.7z"
              />
            </svg>
          </button>
          <button
            type="button"
            class="icon-tool icon-tool--send voice-send-side"
            :disabled="sessionLocked || sending || aiPending || !answerText.trim()"
            title="发送"
            @click="sendAnswer"
          >
            <svg class="svg-ic" viewBox="0 0 24 24" aria-hidden="true">
              <path fill="currentColor" d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
            </svg>
          </button>
        </div>
        <p v-if="speechSupported" class="composer-hint muted">
          {{
            listening ? "正在聆听并实时听写，可修改文字后点发送。" : "点击麦克风作答；题目会自动播报，也可等待播报结束后自动聆听。"
          }}
          <span v-if="speechLangNote">（{{ speechLangNote }}）</span>
        </p>
        <p v-else class="composer-hint muted">当前浏览器不支持语音识别。</p>
      </div>

      <div v-else class="composer composer-text">
        <div class="composer-text-row">
          <textarea v-model="answerText" class="composer-input" rows="3" placeholder="请输入你的回答…" :disabled="sessionLocked || sending" />
          <div class="composer-icon-stack">
            <button
              type="button"
              class="icon-tool icon-tool--send"
              :disabled="sessionLocked || sending || aiPending || !answerText.trim()"
              title="发送"
              @click="sendAnswer"
            >
              <svg class="svg-ic" viewBox="0 0 24 24" aria-hidden="true">
                <path fill="currentColor" d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { onBeforeRouteLeave, useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();

/** 非法 id（含 NaN）统一为 0，避免 NaN 被当作 falsy 误伤合法路由 */
const interviewId = computed(() => {
  const n = Number(route.params.interviewId);
  return Number.isFinite(n) && n > 0 ? n : 0;
});

watch(interviewId, (id, prev) => {
  if (!id || id === prev) return;
  lastSpokenDialogId = null;
  stopOmniTts();
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  listening.value = false;
  interimTranscript.value = "";
});

const dialogs = ref<any[]>([]);
const answerText = ref("");
const sending = ref(false);
const aiPending = ref(false);
const errorText = ref("");
const listening = ref(false);
const speechSupported = ref(false);
const speechLangNote = ref("zh-CN");
const interviewMeta = ref<any | null>(null);
const remainingSec = ref<number | null>(null);
const sessionLocked = ref(false);
const autoFinishing = ref(false);
/** 语音模式下 Web Speech 中间结果，用于「实时听写」展示 */
const interimTranscript = ref("");
/** 语音面试：Omni Realtime 返回的 WAV 播放实例 */
const activeTts = ref<{ audio: HTMLAudioElement; url: string } | null>(null);

/** inputType：1=语音面试 2=文本面试；未传时按文本处理，避免旧数据无按钮 */
const textInterviewMode = computed(() => {
  const t = interviewMeta.value?.inputType;
  if (t == null || t === "") return true;
  return Number(t) === 2;
});
const voiceInterviewMode = computed(() => {
  const t = interviewMeta.value?.inputType;
  if (t == null || t === "") return false;
  return Number(t) === 1;
});
const ttsOk = computed(() => typeof window !== "undefined" && !!(window as any).speechSynthesis);

/** public/mianshiguan.jpg（已复制自项目根目录供打包访问） */
const interviewerImgSrc = computed(() => {
  const b = import.meta.env.BASE_URL || "/";
  return (b.endsWith("/") ? b : b + "/") + "mianshiguan.jpg";
});
const interviewerImgOk = ref(true);
function onInterviewerImgError() {
  interviewerImgOk.value = false;
}

const userAvatarSrc = computed(() => {
  try {
    return localStorage.getItem(`faceai_avatar_${app.uid}`) || "";
  } catch {
    return "";
  }
});
const userAvatarLetter = computed(() => {
  const n = String(app.user.name || "我").trim();
  return n ? n.slice(0, 1).toUpperCase() : "我";
});

function trySendAnswer() {
  if (sessionLocked.value || sending.value || aiPending.value || !answerText.value.trim()) return;
  void sendAnswer();
}

let lastSpokenDialogId: string | number | null = null;
/** 语音合成 HTTP 进行中时中止，避免退出页面后仍解码播放 */
let ttsFetchAbort: AbortController | null = null;

function canReadAloud(text: unknown): boolean {
  const s = String(text ?? "").trim();
  if (!s) return false;
  if (s.includes("AI 正在生成")) return false;
  if (s.includes("追问生成失败")) return false;
  return true;
}

function stopOmniTts() {
  try {
    ttsFetchAbort?.abort();
  } catch {
    // ignore
  }
  ttsFetchAbort = null;
  const cur = activeTts.value;
  if (cur) {
    try {
      // 先卸掉监听再改 src，否则 src="" 会触发 error 事件，误走 fallback 与本机 TTS 叠播
      cur.audio.onended = null;
      cur.audio.onerror = null;
      cur.audio.pause();
    } catch {
      // ignore
    }
    cur.audio.src = "";
    try {
      URL.revokeObjectURL(cur.url);
    } catch {
      // ignore
    }
    activeTts.value = null;
  }
  try {
    window.speechSynthesis?.cancel();
  } catch {
    // ignore
  }
}

function speakText(raw: string) {
  const w = window as any;
  if (!w?.speechSynthesis) return;
  const plain = String(raw || "").trim();
  if (!canReadAloud(plain)) return;
  w.speechSynthesis.cancel();
  const u = new SpeechSynthesisUtterance(plain);
  u.lang = "zh-CN";
  w.speechSynthesis.speak(u);
}

function applyRecognitionForInputType() {
  if (!recognition) return;
  recognition.interimResults = Number(interviewMeta.value?.inputType) === 1;
  if (Number(interviewMeta.value?.inputType) !== 1) {
    interimTranscript.value = "";
  }
}

function tryStartListeningAfterAi() {
  if (!voiceInterviewMode.value || !speechSupported.value || sessionLocked.value || sending.value || aiPending.value) return;
  if (!recognition || listening.value) return;
  void nextTick(() => {
    try {
      lastVoiceDurationSec = null;
      recognition.start();
    } catch {
      app.showToast("请手动点击「语音作答」开始说话");
    }
  });
}

watch(
  () => interviewMeta.value?.inputType,
  () => applyRecognitionForInputType()
);

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

/** 与列表页一致：优先 interviewStatus，避免与其它 status 字段混淆 */
function readInterviewStatus(meta: any): number | null {
  if (!meta) return null;
  const raw = meta.interviewStatus ?? meta.interviewStatusCode ?? meta.status;
  const n = Number(raw);
  return Number.isFinite(n) ? n : null;
}

/** 与后端一致：1=进行中 */
function isInterviewRunning(meta: any) {
  return readInterviewStatus(meta) === 1;
}

/** 已完成(2) / 已终止(3) 不显示计时；已暂停(4) 进入会话时会先恢复为进行中 */
function isInterviewClosed(meta: any) {
  const s = readInterviewStatus(meta);
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
  const minutes = Number(meta.interviewDuration) || 0;
  if (minutes <= 0) {
    remainingSec.value = null;
    return;
  }
  const startMs = parseServerTime(meta.startTime);
  /** 后端约定：未生成首题前 startTime 为空，倒计时按满时长显示 */
  if (startMs === null) {
    if (readInterviewStatus(meta) === 1) {
      remainingSec.value = minutes * 60;
    } else {
      remainingSec.value = null;
    }
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
  const stRaw = merged.interviewStatus ?? merged.interviewStatusCode ?? merged.status;
  const stNum = Number(stRaw);
  if (Number.isFinite(stNum)) {
    merged.interviewStatus = stNum;
  }
  interviewMeta.value = merged;
}

async function loadInterviewMeta() {
  let raw = await app.apiRequest(`/api/interview/${interviewId.value}`);
  applyInterviewMeta(raw);
  let data = interviewMeta.value;
  let status = readInterviewStatus(data);

  if (status === 4) {
    try {
      await app.apiRequest(`/api/interview/${interviewId.value}/resume`, "POST");
    } catch (e: any) {
      app.showToast(e?.message || "恢复面试失败");
      router.push("/app/interview");
      return false;
    }
    raw = await app.apiRequest(`/api/interview/${interviewId.value}`);
    applyInterviewMeta(raw);
    data = interviewMeta.value;
    status = readInterviewStatus(data);
  }

  if (status === 2) {
    app.showToast("该面试已完成");
    router.push("/app/interview-reports");
    return false;
  }
  if (status === 3) {
    app.showToast("该面试已终止");
    router.push("/app/interview");
    return false;
  }
  if (status !== 1) {
    app.showToast(status == null ? "无法读取面试状态，请刷新列表后重试" : "当前面试无法继续");
    router.push("/app/interview");
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
  // 先本地立即回显，提升体感（AI 追问异步生成）
  sending.value = true;
  errorText.value = "";
  const text = answerText.value.trim();
  const now = Date.now();
  const optimisticUserDialog = {
    dialogId: `local-user-${now}`,
    interviewId: interviewId.value,
    speaker: 2,
    contentText: text,
    dialogueRound: dialogs.value.length ? dialogs.value[dialogs.value.length - 1]?.dialogueRound + 1 : dialogs.value.length + 1
  };
  const optimisticAiDialog = {
    dialogId: `local-ai-${now}`,
    interviewId: interviewId.value,
    speaker: 1,
    contentText: "AI 正在生成追问…",
    dialogueRound: optimisticUserDialog.dialogueRound + 1
  };
  dialogs.value = [...dialogs.value, optimisticUserDialog, optimisticAiDialog];
  answerText.value = "";
  try {
    await app.apiRequest("/api/interview/submit-answer", "POST", {
      interviewId: interviewId.value,
      questionId: null,
      answerText: text,
      voiceUrl: null,
      voiceDuration: lastVoiceDurationSec
    });
    lastVoiceDurationSec = null;
    // 不等待 AI 生成追问，先恢复按钮；追问在后台生成完成后再刷新对话
    sending.value = false;
    aiPending.value = true;
    void (async () => {
      try {
        await app.apiRequest(`/api/interview/${interviewId.value}/follow-up`, "POST", { userAnswer: text });
        await reload();
      } catch (e: any) {
        errorText.value = e?.message || "生成追问失败";
        // 失败时把占位的“AI 正在生成追问…”替换为失败提示
        dialogs.value = dialogs.value.map((d) =>
          d?.dialogId === optimisticAiDialog.dialogId ? { ...d, contentText: "追问生成失败，请点击“刷新对话”重试。" } : d
        );
      } finally {
        aiPending.value = false;
      }
    })();
  } catch (e: any) {
    errorText.value = e?.message || "提交失败";
    // 提交失败：移除乐观插入的两条
    dialogs.value = dialogs.value.filter((d) => d?.dialogId !== optimisticUserDialog.dialogId && d?.dialogId !== optimisticAiDialog.dialogId);
    // 把文本放回输入框，方便用户重试
    answerText.value = text;
  } finally {
    // 若已进入 aiPending，则 sending 已提前置 false
    if (!aiPending.value) sending.value = false;
  }
}

async function pause() {
  if (sessionLocked.value) return;
  stopTickTimer();
  stopOmniTts();
  lastSpokenDialogId = null;
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  listening.value = false;
  interimTranscript.value = "";
  try {
    await app.apiRequest(`/api/interview/${interviewId.value}/pause`, "POST");
    app.showToast("已暂停，可在模拟面试列表中继续");
    router.replace("/app/interview");
  } catch (e: any) {
    app.showToast(e?.message || "暂停失败");
  }
}

async function end() {
  if (sessionLocked.value) return;
  sessionLocked.value = true;
  stopTickTimer();
  stopOmniTts();
  lastSpokenDialogId = null;
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  listening.value = false;
  try {
    await app.apiRequest(`/api/interview/${interviewId.value}/end`, "POST");
    app.showToast("面试已完成，报告生成中");
    router.push("/app/interview-reports");
  } catch (e: any) {
    sessionLocked.value = false;
    if (interviewMeta.value && isInterviewRunning(interviewMeta.value)) startTickTimer();
    app.showToast(e?.message || "结束失败");
  }
}

onMounted(async () => {
  if (!interviewId.value) {
    app.showToast("链接无效，请从列表点击「开始」进入");
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
  lastSpokenDialogId = null;
  stopTickTimer();
  stopOmniTts();
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  recognition = null;
  listening.value = false;
  interimTranscript.value = "";
});

onBeforeRouteLeave(() => {
  lastSpokenDialogId = null;
  stopOmniTts();
  try {
    if (recognition) recognition.stop();
  } catch {
    // ignore
  }
  listening.value = false;
  interimTranscript.value = "";
});

/** 语音面试：优先 CosyVoice v3-flash（后端 WAV），失败则本机 TTS */
async function speakAiLine(text: string, onEnd?: () => void) {
  if (!voiceInterviewMode.value) return;
  const plain = String(text || "").trim();
  if (!canReadAloud(plain)) return;
  stopOmniTts();
  const ac = new AbortController();
  ttsFetchAbort = ac;
  try {
    const res = await fetch(`/api/interview/${interviewId.value}/realtime-tts`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId: app.uid, text: plain }),
      signal: ac.signal
    });
    if (ac.signal.aborted) return;
    if (!res.ok) {
      const errTxt = await res.text().catch(() => "");
      throw new Error(errTxt || `HTTP ${res.status}`);
    }
    const blob = await res.blob();
    if (ac.signal.aborted) return;
    const url = URL.createObjectURL(blob);
    const audio = new Audio(url);
    activeTts.value = { audio, url };
    const done = () => {
      try {
        audio.onended = null;
        audio.onerror = null;
      } catch {
        // ignore
      }
      stopOmniTts();
      onEnd?.();
    };
    audio.onended = done;
    audio.onerror = () => {
      try {
        audio.onended = null;
        audio.onerror = null;
      } catch {
        // ignore
      }
      stopOmniTts();
      fallbackSpeakLine(plain, onEnd);
    };
    try {
      await audio.play();
    } catch {
      if (ac.signal.aborted) return;
      stopOmniTts();
      fallbackSpeakLine(plain, onEnd);
    }
  } catch (e: any) {
    if (ac.signal.aborted || e?.name === "AbortError") return;
    fallbackSpeakLine(plain, onEnd);
  } finally {
    if (ttsFetchAbort === ac) ttsFetchAbort = null;
  }
}

function fallbackSpeakLine(plain: string, onEnd?: () => void) {
  const w = window as any;
  if (!w?.speechSynthesis) {
    onEnd?.();
    return;
  }
  w.speechSynthesis.cancel();
  const u = new SpeechSynthesisUtterance(plain);
  u.lang = "zh-CN";
  const done = () => onEnd?.();
  u.onend = done;
  u.onerror = done;
  w.speechSynthesis.speak(u);
}

watch(
  () => dialogs.value,
  (list) => {
    if (!voiceInterviewMode.value) return;
    const aiOnes = list.filter((d: any) => Number(d?.speaker) === 1);
    const last = aiOnes[aiOnes.length - 1];
    if (!last) return;
    const id = last.dialogId;
    if (id != null && id === lastSpokenDialogId) return;
    const txt = String(last.contentText || "");
    if (!canReadAloud(txt)) return;
    lastSpokenDialogId = id;
    void speakAiLine(txt, tryStartListeningAfterAi);
  },
  { deep: true }
);

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
  recognition.interimResults = false;
  recognition.continuous = true;

  recognition.onstart = () => {
    listening.value = true;
    listenStartAt = Date.now();
  };

  recognition.onend = () => {
    if (listening.value) {
      listening.value = false;
      if (Number(interviewMeta.value?.inputType) === 1) {
        interimTranscript.value = "";
      }
      if (listenStartAt) {
        lastVoiceDurationSec = Math.max(1, Math.round((Date.now() - listenStartAt) / 1000));
      }
    }
  };

  recognition.onerror = (event: any) => {
    listening.value = false;
    interimTranscript.value = "";
    const msg = event?.error ? `语音输入失败：${event.error}` : "语音输入失败";
    app.showToast(msg);
  };

  recognition.onresult = (event: any) => {
    const voice = Number(interviewMeta.value?.inputType) === 1;
    if (voice) {
      let patch = "";
      for (let i = 0; i < event.results.length; i++) {
        const res = event.results[i];
        if (!res.isFinal) patch += res[0]?.transcript || "";
      }
      interimTranscript.value = patch.trim();
    } else {
      interimTranscript.value = "";
    }

    let finalText = "";
    for (let i = event.resultIndex; i < event.results.length; i++) {
      const res = event.results[i];
      const txt = res?.[0]?.transcript || "";
      if (res.isFinal) finalText += txt;
    }
    const append = finalText.trim();
    if (!append) return;
    const sep = answerText.value && !answerText.value.endsWith("\n") ? "\n" : "";
    answerText.value = `${answerText.value}${sep}${append}`.trimStart();
  };

  applyRecognitionForInputType();
}

function toggleListening() {
  if (!recognition) return;
  try {
    if (listening.value) {
      recognition.stop();
      interimTranscript.value = "";
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
.interview-session {
  display: flex;
  flex-direction: column;
  min-height: min(78vh, 720px);
}

.session-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  margin-top: 8px;
}

.chat-scroll {
  flex: 1;
  min-height: 200px;
  overflow: auto;
  margin-bottom: 12px;
}

.chat-wrap {
  border: 1px solid #e2eef7;
  border-radius: 14px;
  padding: 12px 10px;
  background: #fff;
}

.chat-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin: 12px 0;
}

.chat-row.ai {
  justify-content: flex-start;
}

.chat-row.me {
  justify-content: flex-end;
}

.avatar-wrap {
  flex-shrink: 0;
}

.avatar-img {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #d4ebf8;
  background: #f0f9ff;
}

.avatar-fallback {
  display: grid;
  place-items: center;
  font-size: 15px;
  font-weight: 700;
  color: #1a6fa8;
  background: linear-gradient(145deg, #e8f4fc, #d0e8f5);
}

.ai-fallback {
  font-size: 11px;
  letter-spacing: 0.02em;
}

.chat-col {
  flex: 1;
  min-width: 0;
  max-width: min(720px, calc(100% - 56px));
}

.chat-col-me {
  display: flex;
  justify-content: flex-end;
}

.chat-bubble {
  width: 100%;
  border: 1px solid #e7f1f8;
  border-radius: 14px;
  padding: 10px 12px;
  background: linear-gradient(180deg, #ffffff, #f7fcff);
}

.bubble-me {
  background: linear-gradient(180deg, rgba(231, 255, 201, 0.35), rgba(89, 188, 246, 0.08));
  border-color: #d3eefc;
}

.chat-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 6px 10px;
  margin-bottom: 6px;
}

.chat-meta {
  font-size: 12px;
  margin-bottom: 0;
}

.chat-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.btn-text-read {
  border: none;
  background: transparent;
  color: #1a7fbd;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 6px;
}

.btn-text-read:hover:not(:disabled) {
  background: rgba(26, 127, 189, 0.1);
}

.btn-text-read:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.live-transcript {
  margin-bottom: 8px;
  padding: 8px 10px;
  border-radius: 10px;
  background: linear-gradient(180deg, #f0f9ff, #e8f4fc);
  border: 1px solid #c5e3f5;
  font-size: 13px;
  line-height: 1.5;
}

.live-transcript-label {
  display: block;
  font-size: 11px;
  color: #5c7a8f;
  margin-bottom: 4px;
  font-weight: 600;
}

.live-transcript-body {
  color: #173a5e;
  word-break: break-word;
  white-space: pre-wrap;
}

.live-transcript-interim {
  color: #6b8fa3;
  font-style: italic;
}

.chat-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.empty-cell {
  border: 1px dashed #dfeef8;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  color: #6b8fa3;
  font-size: 14px;
}

.composer {
  flex-shrink: 0;
  padding-top: 4px;
  border-top: 1px dashed #dcebf7;
}

.composer-text-row {
  display: flex;
  align-items: stretch;
  gap: 10px;
}

.composer-input {
  flex: 1;
  min-width: 0;
  border: 1px solid #cfe5f3;
  border-radius: 14px;
  padding: 12px 14px;
  font-size: 15px;
  line-height: 1.5;
  resize: vertical;
  font-family: inherit;
}

.composer-input:focus {
  outline: none;
  border-color: #7ccbed;
  box-shadow: 0 0 0 3px rgba(124, 203, 237, 0.25);
}

.composer-input--voice {
  margin-bottom: 12px;
}

.composer-icon-stack {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.icon-tool {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px solid #b8daf0;
  background: #fff;
  color: #1a7fbd;
  cursor: pointer;
  display: grid;
  place-items: center;
  transition:
    background 0.15s,
    color 0.15s,
    border-color 0.15s,
    transform 0.12s;
}

.icon-tool:hover:not(:disabled) {
  background: #eef8ff;
  border-color: #7ccbed;
}

.icon-tool:active:not(:disabled) {
  transform: scale(0.96);
}

.icon-tool:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.icon-tool--on {
  background: linear-gradient(180deg, #3d9fd9, #1a7fbd);
  color: #fff;
  border-color: #1a7fbd;
}

.icon-tool--send {
  background: linear-gradient(180deg, #42b4e8, #1a7fbd);
  color: #fff;
  border-color: #1a7fbd;
}

.icon-tool--send:disabled {
  background: #e2eef5;
  color: #9bb3c4;
  border-color: #d0dee8;
}

.svg-ic {
  width: 22px;
  height: 22px;
  display: block;
}

.svg-ic--lg {
  width: 32px;
  height: 32px;
}

.composer-hint {
  margin: 8px 0 0;
  font-size: 12px;
  line-height: 1.45;
}

.composer-voice .live-transcript {
  margin-bottom: 10px;
}

.voice-mic-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 8px 0 4px;
}

.voice-mic-fab {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(180deg, #5eb8e8, #1a7fbd);
  color: #fff;
  cursor: pointer;
  display: grid;
  place-items: center;
  box-shadow: 0 6px 20px rgba(26, 127, 189, 0.35);
  transition:
    transform 0.15s,
    box-shadow 0.15s;
}

.voice-mic-fab:hover:not(:disabled) {
  transform: scale(1.04);
  box-shadow: 0 8px 24px rgba(26, 127, 189, 0.45);
}

.voice-mic-fab:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  transform: none;
}

.voice-mic-fab--on {
  background: linear-gradient(180deg, #e85a5a, #c62828);
  box-shadow: 0 6px 20px rgba(198, 40, 40, 0.35);
  animation: mic-pulse 1.2s ease-in-out infinite;
}

@keyframes mic-pulse {
  0%,
  100% {
    box-shadow: 0 6px 20px rgba(198, 40, 40, 0.35);
  }
  50% {
    box-shadow: 0 6px 28px rgba(198, 40, 40, 0.55);
  }
}

.voice-send-side {
  width: 52px;
  height: 52px;
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

