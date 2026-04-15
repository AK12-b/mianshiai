<template>
  <section class="card pm-page">
    <h3 style="margin-top: 0">跨岗位推荐</h3>

    <div class="pm-grid">
      <div class="card pm-left">
        <div class="pm-left-head">
          <h3 class="pm-left-title">我的能力画像</h3>
          <button class="btn btn-primary" :disabled="loading || !canGenerate" @click="generate">
            {{ loading ? "生成中..." : "生成推荐" }}
          </button>
        </div>

        <div class="pm-portrait-panel">
          <div class="pm-brain-wrap" role="img" aria-label="能力画像">
            <span class="pm-brain-emoji" role="presentation">&#129504;</span>

            <div
              v-for="(t, i) in displayPortraitTags"
              :key="'pt' + i"
              class="pm-portrait-tag"
              :style="tagStyle(i)"
              :title="t"
            >
              {{ t }}
            </div>
          </div>
        </div>

        <div class="pm-score-card" v-if="hasScores">
          <div class="pm-score-item">
            <div class="muted">技能匹配度</div>
            <strong>{{ profile.scores.skill_match }}</strong>
          </div>
          <div class="pm-score-item">
            <div class="muted">项目经历适配度</div>
            <strong>{{ profile.scores.project_fit }}</strong>
          </div>
          <div class="pm-score-item">
            <div class="muted">面试表现适配度</div>
            <strong>{{ profile.scores.interview_fit }}</strong>
          </div>
        </div>

        <div class="pm-summary" v-if="profile.summary">
          <div class="muted" style="margin-bottom: 6px">画像总结</div>
          <div class="pm-summary-text">{{ profile.summary }}</div>
          <button class="btn btn-text" style="margin-top: 8px" @click="goGrowth">查看详细能力分析</button>
        </div>

        <div v-if="errorText" class="api-output" style="margin-top: 12px">{{ errorText }}</div>
      </div>

      <div class="card pm-right">
        <div class="dashboard-section-title">
          <h3 style="margin: 0">岗位推荐列表</h3>
          <div class="pm-filters">
            <button class="btn btn-outline" :disabled="loading || !canGenerate" @click="generate">重新推荐</button>
          </div>
        </div>

        <div v-if="loading && !recommends.length" class="muted" style="margin-top: 10px">正在生成推荐...</div>

        <div class="pm-list" v-else>
          <div v-if="!sortedRecommends.length" class="muted" style="margin-top: 10px">暂无推荐结果，请点击左侧「生成推荐」。</div>
          <article v-for="(r, idx) in sortedRecommends" :key="idx" class="pm-card">
            <div class="pm-card-head">
              <div class="pm-post">
                <strong class="pm-post-name">{{ r.post_name || "--" }}</strong>
                <span class="pm-score">{{ r.match_score ?? "--" }} 分</span>
              </div>
              <button class="btn btn-text" @click="toPractice">去补强</button>
            </div>

            <div class="pm-card-sec">
              <div class="muted" style="margin-bottom: 6px">核心技能要求</div>
              <ul class="pm-skill-ul">
                <li v-for="(s, i) in r.core_skills || []" :key="i">{{ s }}</li>
              </ul>
            </div>

            <div class="pm-card-sec">
              <div class="muted" style="margin-bottom: 6px">行业前景简介</div>
              <div class="pm-sec-text">{{ r.industry_prospect || "--" }}</div>
            </div>

            <div class="pm-card-sec pm-green">
              <div class="pm-sec-title">适配分析 · 匹配亮点</div>
              <div class="pm-sec-text">{{ r.match_highlight || "--" }}</div>
            </div>

            <div class="pm-card-sec pm-red">
              <div class="pm-sec-title">适配分析 · 能力缺口</div>
              <div class="pm-sec-text">{{ r.ability_gap || "--" }}</div>
            </div>

            <div class="pm-card-sec" v-if="Array.isArray(r.improve_actions) && r.improve_actions.length">
              <div class="muted" style="margin-bottom: 6px">补强路线（可执行清单）</div>
              <ul class="pm-skill-ul">
                <li v-for="(a, i) in r.improve_actions" :key="i">{{ a }}</li>
              </ul>
            </div>
          </article>
        </div>

        <div v-if="rawText" class="pm-raw">
          <div class="dashboard-section-title">
            <h3 style="margin: 0">原始输出（兜底）</h3>
            <button class="btn btn-outline" @click="copyRaw">复制</button>
          </div>
          <pre class="pm-raw-pre">{{ rawText }}</pre>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";
import { useRouter } from "vue-router";

const app = useAppStore();
const router = useRouter();

const posts = ref<any[]>([]);
const loading = ref(false);
const errorText = ref("");
const rawText = ref("");

const profile = ref<any>({});
const recommends = ref<any[]>([]);
const fallbackTags = ref<string[]>([]);
const resolvedPostId = ref(0);

const ui = ref({
  industryFilter: "",
  scoreFilter: ""
});

/** 画像展示用：全部围绕「面试综合能力」（表达、项目讲述、技术问答、行为面试等） */
const INTERVIEW_PORTRAIT_TAGS = [
  "技术点表达清楚",
  "项目经历能讲透",
  "场景题思路清晰",
  "Java基础问答稳",
  "算法与数据结构",
  "沟通逻辑顺畅",
  "STAR行为面试",
  "分布式整体认知",
  "数据库与SQL",
  "面试节奏与礼仪",
  "抗压与复盘能力",
  "持续学习与自驱"
];

const EDU_OR_BG_PATTERN =
  /(本科|大专|硕士|博士|研究生|博[士士后]|大四|大三|大二|大一|应届|在读|学历|院校|学校|专业|在读生)/;

const PURE_ROLE_PATTERN = /^[\u4e00-\u9fa5A-Za-z0-9·\s]{1,16}(工程师|开发|程序员|研发|经理|专员|顾问|架构师)$/;

function shouldReplaceWithInterviewTag(s: string): boolean {
  const t = s.trim();
  if (!t) return true;
  if (EDU_OR_BG_PATTERN.test(t)) return true;
  if (PURE_ROLE_PATTERN.test(t)) return true;
  return false;
}

function toInterviewPortraitTags(raw: string[]): string[] {
  const out: string[] = [];
  let poolIdx = 0;
  const nextFromPool = () => INTERVIEW_PORTRAIT_TAGS[poolIdx++ % INTERVIEW_PORTRAIT_TAGS.length];

  for (const item of raw) {
    const s = String(item).trim();
    if (!s) continue;
    if (shouldReplaceWithInterviewTag(s)) {
      const rep = nextFromPool();
      if (!out.includes(rep)) out.push(rep);
      continue;
    }
    if (!out.includes(s)) out.push(s);
  }

  while (out.length < 6) {
    const rep = nextFromPool();
    if (!out.includes(rep)) out.push(rep);
  }
  return out.slice(0, 12);
}

const hasScores = computed(() => {
  const s = profile.value?.scores;
  return s && [s.skill_match, s.project_fit, s.interview_fit].some((v: any) => Number(v) >= 0);
});

/** 画像标签：接口生成优先，否则用档案技能标签；统一规范为面试综合能力向展示 */
const portraitTags = computed(() => {
  const tags = profile.value?.base_tags;
  const raw = Array.isArray(tags) && tags.length
    ? tags.map((t: string) => String(t).trim()).filter(Boolean)
    : fallbackTags.value.filter(Boolean);
  return toInterviewPortraitTags(raw.length ? raw : [...INTERVIEW_PORTRAIT_TAGS]);
});

const displayPortraitTags = computed(() => {
  const src = portraitTags.value.length ? portraitTags.value : [...INTERVIEW_PORTRAIT_TAGS];
  const out: string[] = [];
  const n = Math.min(10, Math.max(6, src.length));
  for (let i = 0; i < n; i++) {
    const t = src[i] || INTERVIEW_PORTRAIT_TAGS[i % INTERVIEW_PORTRAIT_TAGS.length];
    const s = String(t).trim();
    if (!s) continue;
    out.push(s.length > 14 ? s.slice(0, 14) + "…" : s);
  }
  return out.slice(0, 10);
});

function tagStyle(i: number) {
  const count = Math.max(6, displayPortraitTags.value.length || 0);
  const angle = (-Math.PI / 2) + (2 * Math.PI * i) / count;
  const r = 32; // 略小，避免贴边被裁切
  const x = 50 + r * Math.cos(angle);
  const y = 50 + r * Math.sin(angle);
  return {
    left: `${x}%`,
    top: `${y}%`,
    transform: "translate(-50%, -50%)",
    textAlign: "center" as const
  };
}

const filteredRecommends = computed(() => {
  let list = Array.isArray(recommends.value) ? [...recommends.value] : [];
  const minScore = ui.value.scoreFilter ? Number(ui.value.scoreFilter) : null;
  if (minScore !== null && Number.isFinite(minScore)) {
    list = list.filter((r) => Number(r?.match_score) >= minScore);
  }
  const ind = ui.value.industryFilter.trim();
  if (ind) {
    list = list.filter((r) => String(r?.industry_prospect || "").includes(ind) || String(r?.post_name || "").includes(ind));
  }
  return list;
});

const sortedRecommends = computed(() => {
  return [...filteredRecommends.value].sort((a, b) => Number(b?.match_score ?? 0) - Number(a?.match_score ?? 0));
});

const canGenerate = computed(() => resolvedPostId.value > 0 || posts.value.length > 0);

async function loadPosts() {
  try {
    posts.value = (await app.apiRequest("/api/post/list")) || [];
  } catch {
    posts.value = [];
  }
}

async function resolveDefaultPostId(): Promise<number> {
  if (resolvedPostId.value > 0) return resolvedPostId.value;
  try {
    const intentions = await app.apiRequest(`/api/user-intention-post/user/${app.uid}`);
    if (Array.isArray(intentions) && intentions.length) {
      const pid = Number(intentions[0]?.postId || 0);
      if (pid > 0) {
        resolvedPostId.value = pid;
        return pid;
      }
    }
  } catch {
    // ignore
  }
  if (posts.value.length) {
    resolvedPostId.value = Number(posts.value[0].postId);
    return resolvedPostId.value;
  }
  return 0;
}

async function buildProfileText(): Promise<string> {
  const chunks: string[] = [];
  try {
    const u = await app.apiRequest(`/api/user/${app.uid}`);
    if (u?.skillTag) chunks.push(`【技能标签】${u.skillTag}`);
    if (u?.projectExp) chunks.push(`【项目经历】${u.projectExp}`);
    if (u?.skillProficiency) chunks.push(`【专业/熟练度】${u.skillProficiency}`);
  } catch {
    // ignore
  }
  try {
    const growth = await app.apiRequest(`/api/ability-growth/user/${app.uid}/dimension-trend`);
    const arr = Array.isArray(growth) ? growth : [];
    if (arr.length) {
      const r = arr[0];
      if (r?.avgScore != null) chunks.push(`【成长数据·近期均分】约 ${r.avgScore}`);
    }
  } catch {
    // ignore
  }
  if (chunks.length) return chunks.join("\n");
  return "【说明】本人希望根据当前技能与经历，获得跨岗位匹配推荐与能力提升方向。";
}

async function loadFallbackTags() {
  try {
    const u = await app.apiRequest(`/api/user/${app.uid}`);
    const raw = String(u?.skillTag || "").trim();
    const parts = raw
      .split(/[,，、\s]+/)
      .map((s) => s.trim())
      .filter(Boolean)
      .slice(0, 8);
    fallbackTags.value = parts.length ? parts : [];
  } catch {
    fallbackTags.value = [];
  }
}

function storageKey() {
  return `post-match-cache:${app.uid}`;
}

function loadPersisted() {
  try {
    const raw = localStorage.getItem(storageKey());
    if (!raw) return;
    const parsed = JSON.parse(raw);
    if (parsed.profile && typeof parsed.profile === "object") profile.value = parsed.profile;
    if (Array.isArray(parsed.recommends)) recommends.value = parsed.recommends;
    if (typeof parsed.rawText === "string") rawText.value = parsed.rawText;
  } catch {
    // ignore
  }
}

async function loadFromDatabaseHistory() {
  try {
    const rows = await app.apiRequest(`/api/post-recommend/user/${app.uid}`);
    const list = Array.isArray(rows) ? rows : [];
    if (!list.length) return;
    // 只取最新一批（按创建时间降序），并映射为页面展示结构
    const top = list.slice(0, 6).map((r: any) => ({
      post_name: posts.value.find((p) => Number(p.postId) === Number(r.recommendPostId))?.postName || `岗位#${r.recommendPostId}`,
      match_score: Number(r.matchRate || 0),
      core_skills: [],
      match_highlight: String(r.matchHighlight || ""),
      ability_gap: String(r.abilityGap || ""),
      industry_prospect: String(r.industryProspect || ""),
      improve_actions: []
    }));
    recommends.value = top;
  } catch {
    // ignore history loading errors
  }
}

function persistSnapshot() {
  try {
    localStorage.setItem(
      storageKey(),
      JSON.stringify({
        profile: profile.value,
        recommends: recommends.value,
        rawText: rawText.value
      })
    );
  } catch {
    // ignore
  }
}

async function generate() {
  errorText.value = "";
  loading.value = true;
  try {
    await loadPosts();
    const postId = await resolveDefaultPostId();
    if (!postId) {
      errorText.value = "未找到可用岗位：请先在个人中心设置意向岗位，或确保系统内存在岗位数据。";
      return;
    }
    const profileText = await buildProfileText();
    const data = await app.apiRequest("/api/post-match/generate", "POST", {
      userId: app.uid,
      originalPostId: postId,
      profileText,
      filters: ""
    });
    const prof = data?.profile ?? data?.profileText;
    const nextProfile = prof && typeof prof === "object" ? prof : {};
    const rec = data?.recommends;
    const nextRecommends = Array.isArray(rec) ? rec : [];
    const nextRawText = String(data?.rawText || data?.raw || data?.text || "");

    // 只有成功拿到结果才覆盖旧结果：避免“生成中/失败”导致列表清空
    profile.value = nextProfile;
    recommends.value = nextRecommends;
    rawText.value = nextRawText;

    if (!nextRecommends.length && !nextRawText.trim()) {
      errorText.value = "未生成有效结果，请稍后重试";
    } else {
      persistSnapshot();
    }
  } catch (e: any) {
    errorText.value = e?.message || "生成失败，请重试";
  } finally {
    loading.value = false;
  }
}

function goGrowth() {
  router.push("/app/growth-data");
}

function toPractice() {
  router.push("/app/practice");
}

async function copyRaw() {
  try {
    await navigator.clipboard.writeText(rawText.value || "");
    app.showToast("已复制到剪贴板");
  } catch {
    app.showToast("复制失败");
  }
}

onMounted(async () => {
  loadPersisted();
  await loadPosts();
  await loadFromDatabaseHistory();
  await loadFallbackTags();
  await resolveDefaultPostId();
});
</script>

<style scoped>
.pm-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: 2fr 3fr;
}

.pm-left-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}
.pm-left-title {
  margin: 0;
  font-size: 16px;
  color: #173a5e;
}

.pm-portrait-panel {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 10px 12px 8px;
  box-sizing: border-box;
}
.pm-brain-wrap {
  position: relative;
  width: 100%;
  max-width: 360px;
  aspect-ratio: 1 / 1;
  border-radius: 16px;
  background: radial-gradient(circle at 50% 45%, rgba(255, 255, 255, 0.92), rgba(245, 252, 255, 0.82));
  border: 1px solid #e2eef7;
  overflow: visible;
  box-sizing: border-box;
}
.pm-brain-emoji {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: clamp(3.5rem, 16vw, 5.5rem);
  line-height: 1;
  user-select: none;
  filter: drop-shadow(0 6px 14px rgba(126, 184, 216, 0.25));
  font-family: "Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Twemoji Mozilla", sans-serif;
}
.pm-portrait-tag {
  position: absolute;
  z-index: 2;
  max-width: min(38%, 132px);
  padding: 6px 8px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(207, 229, 243, 0.95);
  color: #1a4a6e;
  font-weight: 700;
  font-size: 11px;
  line-height: 1.35;
  box-shadow: 0 8px 18px rgba(17, 119, 200, 0.08);
  word-break: break-word;
  hyphens: auto;
  text-align: center;
  backdrop-filter: blur(6px);
  user-select: none;
}

.pm-left,
.pm-right {
  min-height: 480px;
}

.pm-filters {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.pm-mini-select {
  border: 1px solid #cfe5f3;
  border-radius: 12px;
  padding: 8px 10px;
  background: #fff;
}

.pm-score-card {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.pm-score-item {
  border: 1px solid #e2eef7;
  border-radius: 12px;
  padding: 10px;
  background: linear-gradient(180deg, #ffffff, #f7fcff);
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.pm-summary {
  margin-top: 12px;
  border: 1px solid #e7f1f8;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.pm-summary-text {
  white-space: pre-wrap;
  line-height: 1.7;
  color: #173a5e;
}

.pm-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 680px;
  overflow: auto;
  padding-right: 2px;
}

.pm-card {
  border: 1px solid #dcecf8;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcff 100%);
  padding: 12px;
}

.pm-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.pm-post {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
.pm-post-name {
  color: #155985;
}
.pm-score {
  color: #1177c8;
  font-weight: 800;
}

.pm-card-sec {
  margin-top: 10px;
}
.pm-skill-ul {
  margin: 0;
  padding-left: 18px;
  line-height: 1.7;
}

.pm-green,
.pm-red {
  border-radius: 12px;
  padding: 10px;
}
.pm-green {
  background: rgba(66, 185, 131, 0.1);
  border: 1px solid rgba(66, 185, 131, 0.22);
}
.pm-red {
  background: rgba(244, 67, 54, 0.08);
  border: 1px solid rgba(244, 67, 54, 0.18);
}
.pm-sec-title {
  font-weight: 800;
  margin-bottom: 6px;
}
.pm-sec-text {
  white-space: pre-wrap;
  line-height: 1.7;
  color: #173a5e;
}

.pm-raw {
  margin-top: 12px;
  border-top: 1px dashed #dfeef8;
  padding-top: 12px;
}
.pm-raw-pre {
  margin: 10px 0 0;
  background: rgba(16, 44, 72, 0.92);
  color: #d9efff;
  border-radius: 12px;
  padding: 12px;
  max-height: 260px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.65;
  font-size: 12px;
}

@media (max-width: 860px) {
  .pm-grid {
    grid-template-columns: 1fr;
  }
  .pm-list {
    max-height: none;
  }
}
</style>
