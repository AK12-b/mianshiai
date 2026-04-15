<template>
  <section class="card resume-page">
    <h3 style="margin-top: 0">AI 简历优化</h3>

    <input
      ref="filePickerRef"
      type="file"
      accept=".pdf,.doc,.docx,.txt"
      style="display: none"
      @change="onFileChange"
    />

    <div class="resume-inner-scroll">
      <div class="toolbar">
        <button class="btn btn-primary" :disabled="uploading" @click="openFilePicker">
          {{ uploading ? "上传中..." : "上传简历" }}
        </button>
        <span class="muted" v-if="selectedFileName">已选择：{{ selectedFileName }}</span>
      </div>

      <div class="resume-split">
        <div class="resume-col resume-col-left">
          <div class="resume-panel">
            <div class="resume-panel-title">目标岗位描述</div>
            <p class="resume-panel-hint">请粘贴招聘 JD 或自行描述目标岗位的工作内容与要求。</p>
            <textarea
              v-model="targetJobDescription"
              class="resume-textarea"
              rows="7"
              placeholder="例如：负责 Java 后端开发，参与微服务设计与中间件运维，要求熟悉 Spring Cloud、MySQL、Redis…"
            />
            <button
              class="btn btn-outline resume-analyze-btn"
              type="button"
              :disabled="analyzingJob || !targetJobDescription.trim()"
              @click="analyzeJobAbilities"
            >
              {{ analyzingJob ? "分析中..." : "分析岗位能力" }}
            </button>
          </div>

          <div class="resume-panel resume-panel-grow">
            <div class="resume-panel-title">岗位所需能力（AI 分析）</div>
            <div class="resume-panel-body muted" v-if="!requiredAbilities.trim()">
              填写上方岗位描述后，点击「分析岗位能力」查看 AI 归纳的必备能力。
            </div>
            <div class="resume-panel-body resume-panel-pre" v-else>{{ requiredAbilities }}</div>
          </div>
        </div>

        <div class="resume-col resume-col-right">
          <div class="resume-panel resume-panel-full">
            <div class="resume-panel-title">简历亮点与优化建议</div>
            <div class="resume-panel-body muted" v-if="!highlightSuggestions.trim()">
              在下方列表中点击「优化」或表格行可选中简历后，将在此展示诊断意见或优化稿节选（已优化且无诊断记录时会自动加载优化正文摘要）。
            </div>
            <div class="resume-panel-body resume-panel-pre" v-else>{{ highlightSuggestions }}</div>
            <div v-if="isOptimized(selectedResume)" class="resume-optimize-link">
              <button type="button" class="btn btn-outline btn-sm" @click="openOptimizePreview(selectedResume.resumeId)">
                查看优化稿
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="hasParsedDisplay && displayParsedStruct" class="resume-parsed card">
        <div class="resume-panel-title">简历结构化解析</div>

        <div class="parsed-grid" v-if="displayParsedStruct.basicInfo">
          <div class="parsed-sec">
            <div class="parsed-sec-title">基础信息</div>
            <ul class="parsed-ul">
              <li v-if="displayParsedStruct.basicInfo.name"><strong>姓名</strong> {{ displayParsedStruct.basicInfo.name }}</li>
              <li v-if="displayParsedStruct.basicInfo.phone"><strong>电话</strong> {{ displayParsedStruct.basicInfo.phone }}</li>
              <li v-if="displayParsedStruct.basicInfo.email"><strong>邮箱</strong> {{ displayParsedStruct.basicInfo.email }}</li>
              <li v-if="displayParsedStruct.basicInfo.location"><strong>地点</strong> {{ displayParsedStruct.basicInfo.location }}</li>
            </ul>
          </div>
        </div>

      <div class="parsed-sec" v-if="displayParsedStruct.education?.length">
        <div class="parsed-sec-title">教育经历</div>
        <div v-for="(ed, i) in displayParsedStruct.education" :key="'ed' + i" class="parsed-card">
          <div>{{ ed.school || "—" }} · {{ ed.degree || "" }} {{ ed.major || "" }}</div>
          <div class="muted small">{{ ed.time || "" }}</div>
        </div>
      </div>

      <div class="parsed-sec" v-if="displayParsedStruct.skills?.length">
        <div class="parsed-sec-title">技能</div>
        <div class="parsed-tags">
          <span v-for="(s, i) in displayParsedStruct.skills" :key="'sk' + i" class="parsed-tag">{{ s }}</span>
        </div>
      </div>

      <div class="parsed-sec" v-if="displayParsedStruct.projects?.length">
        <div class="parsed-sec-title">项目经历</div>
        <div v-for="(pj, i) in displayParsedStruct.projects" :key="'pj' + i" class="parsed-card">
          <strong>{{ pj.name || "项目" }}</strong>
          <span v-if="pj.role" class="muted"> · {{ pj.role }}</span>
          <span v-if="pj.time" class="muted small"> {{ pj.time }}</span>
          <div class="parsed-desc">{{ pj.desc || "" }}</div>
        </div>
      </div>

      <div class="parsed-sec" v-if="displayParsedStruct.internships?.length">
        <div class="parsed-sec-title">实习经历</div>
        <div v-for="(it, i) in displayParsedStruct.internships" :key="'it' + i" class="parsed-card">
          <strong>{{ it.company || "公司" }}</strong>
          <span v-if="it.role" class="muted"> · {{ it.role }}</span>
          <span v-if="it.time" class="muted small"> {{ it.time }}</span>
          <div class="parsed-desc">{{ it.desc || "" }}</div>
        </div>
      </div>

        <div class="parsed-sec" v-if="displayParsedStruct.otherNotes">
          <div class="parsed-sec-title">其它</div>
          <div class="resume-panel-pre" style="max-height: 120px">{{ displayParsedStruct.otherNotes }}</div>
        </div>
      </div>

      <table class="data-table resume-table">
        <thead>
          <tr>
            <th>岗位</th>
            <th>格式</th>
            <th>大小(KB)</th>
            <th>上传时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in resumes"
            :key="row.resumeId"
            class="resume-table-row"
            @click="selectForParsed(row)"
          >
            <td>{{ postNameById(row.postId) }}</td>
            <td>{{ row.resumeFormat || "--" }}</td>
            <td>{{ sizeKb(row.resumeSize) }}</td>
            <td>{{ fmt(row.createTime) }}</td>
            <td>
              <span class="status-badge">{{ isOptimized(row) ? "已优化" : "待优化" }}</span>
            </td>
            <td class="resume-actions" @click.stop>
              <button class="btn btn-text resume-action-btn" type="button" @click="optimize(row.resumeId)">优化</button>
              <button
                class="btn btn-text resume-action-btn"
                type="button"
                :disabled="!isOptimized(row)"
                :title="isOptimized(row) ? '查看优化后的简历正文' : '请先完成优化'"
                @click="openOptimizePreview(row.resumeId)"
              >
                查看优化稿
              </button>
            </td>
          </tr>
          <tr v-if="!resumes.length">
            <td class="empty-cell" colspan="6">暂无简历数据，请先上传一份简历。</td>
          </tr>
        </tbody>
      </table>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>

    <Teleport to="body">
      <div v-if="showOptimizeModal" class="resume-modal-overlay" @click.self="closeOptimizeModal">
        <div class="resume-modal card">
          <h4 class="resume-modal-title">优化后简历预览</h4>
          <p class="muted small">可复制全文到本地进一步编辑或排版。</p>
          <pre class="resume-modal-pre">{{ optimizePreviewText }}</pre>
          <div class="resume-modal-actions">
            <button type="button" class="btn btn-outline" @click="copyOptimizePreview">复制全文</button>
            <button type="button" class="btn btn-outline" @click="closeOptimizeModal">关闭</button>
          </div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useAppStore } from "../stores/app";

type ParsedSnapshot = { resumeId: number; parsedJson: string; hint?: string };

const app = useAppStore();
const resumes = ref<any[]>([]);
const posts = ref<any[]>([]);
const errorText = ref("");
const highlightSuggestions = ref("");
const uploadPostId = ref(1);
const selectedFileName = ref("");
const filePickerRef = ref<HTMLInputElement | null>(null);
const uploading = ref(false);
const selectedResumeId = ref<number | null>(null);

const targetJobDescription = ref("");
const requiredAbilities = ref("");
const analyzingJob = ref(false);

/** 结构化解析：本地持久化，直到上传新简历覆盖 */
const persistedParsed = ref<ParsedSnapshot | null>(null);

const showOptimizeModal = ref(false);
const optimizePreviewText = ref("");

function lsKeyJob() {
  return `resume-job-panel:${app.uid}`;
}
function lsKeyParsed() {
  return `resume-parsed:${app.uid}`;
}

function saveParsedToLs() {
  try {
    if (!persistedParsed.value) {
      localStorage.removeItem(lsKeyParsed());
    } else {
      localStorage.setItem(lsKeyParsed(), JSON.stringify(persistedParsed.value));
    }
  } catch {
    // ignore
  }
}

const selectedResume = computed(() => {
  if (selectedResumeId.value == null) return null;
  return resumes.value.find((r) => Number(r.resumeId) === Number(selectedResumeId.value)) || null;
});

const displayParsedStruct = computed(() => {
  const raw = persistedParsed.value?.parsedJson || selectedResume.value?.parsedJson;
  if (!raw || !String(raw).trim()) return null;
  try {
    return JSON.parse(String(raw)) as Record<string, any>;
  } catch {
    return null;
  }
});

const hasParsedDisplay = computed(() => {
  const p = displayParsedStruct.value;
  if (!p || typeof p !== "object") return false;
  const bi = p.basicInfo;
  const hasBio = bi && (bi.name || bi.phone || bi.email || bi.location);
  return !!(
    hasBio ||
    (Array.isArray(p.education) && p.education.length) ||
    (Array.isArray(p.skills) && p.skills.length) ||
    (Array.isArray(p.projects) && p.projects.length) ||
    (Array.isArray(p.internships) && p.internships.length) ||
    (p.otherNotes && String(p.otherNotes).trim())
  );
});

/** 兼容 Jackson 默认驼峰与部分环境下的 snake_case */
function rowDiagnoseText(row: any): string {
  if (!row || typeof row !== "object") return "";
  const v = row.diagnoseResult ?? row.diagnose_result;
  return v != null && String(v).trim() ? String(v) : "";
}

function isOptimized(row: any): boolean {
  return Number(row?.isOptimizeApply) === 1;
}

/** 仅「优化」未「诊断」时库中无 diagnoseResult，用优化稿正文节选填充右侧说明区 */
async function loadOptimizedExcerptForRow(resumeId: number) {
  const rid = resumeId;
  try {
    const data = await app.apiRequest(`/api/resume/${rid}/optimized-preview`, "GET");
    if (Number(selectedResumeId.value) !== Number(rid)) return;
    const content = String(data?.content ?? "").trim();
    if (content) {
      const max = 1500;
      const excerpt = content.length > max ? content.slice(0, max) : content;
      highlightSuggestions.value =
        "【说明】未单独保存诊断全文时，下方为已生成的优化稿节选。\n\n" + excerpt;
    } else {
      highlightSuggestions.value =
        "已优化。正文节选为空，请点击「查看优化稿」加载全文。";
    }
  } catch {
    if (Number(selectedResumeId.value) !== Number(rid)) return;
    highlightSuggestions.value =
      "已优化。暂时无法加载正文节选，请点击「查看优化稿」查看。";
  }
}

function syncHighlightFromRow(row: any) {
  if (!row) return;
  const text = rowDiagnoseText(row);
  if (text) {
    highlightSuggestions.value = text;
    return;
  }
  if (isOptimized(row)) {
    highlightSuggestions.value = "正在加载优化稿节选…";
    void loadOptimizedExcerptForRow(row.resumeId);
  } else {
    highlightSuggestions.value = "";
  }
}

function selectForParsed(row: any) {
  selectedResumeId.value = row.resumeId;
  syncHighlightFromRow(row);
  if (row.parsedJson && String(row.parsedJson).trim()) {
    persistedParsed.value = {
      resumeId: row.resumeId,
      parsedJson: String(row.parsedJson)
    };
    saveParsedToLs();
  }
}

function postNameById(postId: any) {
  const post = posts.value.find((p) => String(p.postId) === String(postId));
  return post?.postName || "--";
}

async function loadResumes() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/user/${app.uid}`);
    resumes.value = Array.isArray(data) ? data : [];
    const sid = selectedResumeId.value;
    let row = sid != null ? resumes.value.find((r) => Number(r.resumeId) === Number(sid)) : null;
    if (!row && resumes.value.length) {
      row = resumes.value[0];
      selectedResumeId.value = row.resumeId;
    }
    if (row) syncHighlightFromRow(row);
  } catch (e: any) {
    resumes.value = [];
    const msg = e?.message || "查询简历失败";
    errorText.value = msg;
    app.showToast(msg);
  }
}

async function loadPosts() {
  try {
    posts.value = (await app.apiRequest("/api/post/list")) || [];
  } catch {
    posts.value = [];
  }
}

function openFilePicker() {
  errorText.value = "";
  filePickerRef.value?.click();
}

function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0] || null;
  selectedFileName.value = file?.name || "";
  if (!file) return;
  uploadResume(file).finally(() => {
    input.value = "";
  });
}

async function uploadResume(file: File) {
  errorText.value = "";
  uploading.value = true;
  try {
    const formData = new FormData();
    formData.append("userId", String(app.uid));
    formData.append("postId", String(uploadPostId.value));
    formData.append("file", file);

    const res = await fetch("/api/resume/upload", { method: "POST", body: formData });
    const text = await res.text();
    let data: any;
    try {
      data = text ? JSON.parse(text) : {};
    } catch {
      throw new Error("上传接口返回非 JSON，请确认后端已启动且走 Vite 代理 / 同源 8080");
    }
    if (data.code !== 200) throw new Error(data.message || "上传失败");
    app.showToast("上传成功");
    await loadResumes();
    const rid = data.data?.resumeId;
    if (rid != null) {
      selectedResumeId.value = Number(rid);
      const row = resumes.value.find((r) => Number(r.resumeId) === Number(selectedResumeId.value));
      if (row?.parsedJson && String(row.parsedJson).trim()) {
        persistedParsed.value = {
          resumeId: row.resumeId,
          parsedJson: String(row.parsedJson)
        };
      } else {
        persistedParsed.value = null;
      }
      saveParsedToLs();
      if (row) syncHighlightFromRow(row);
    }
  } catch (e: any) {
    const msg = e?.message || "上传失败";
    errorText.value = msg;
    app.showToast(msg);
  } finally {
    uploading.value = false;
  }
}

async function analyzeJobAbilities() {
  const jd = targetJobDescription.value.trim();
  if (!jd) {
    app.showToast("请先填写目标岗位描述");
    return;
  }
  errorText.value = "";
  analyzingJob.value = true;
  try {
    const data = await app.apiRequest("/api/resume/analyze-job", "POST", { jobDescription: jd });
    const text = String(data?.abilities ?? "").trim();
    requiredAbilities.value = text || "（未返回有效内容，请检查大模型配置或稍后重试）";
    app.showToast("岗位能力分析完成");
  } catch (e: any) {
    const msg = e?.message || "分析失败";
    errorText.value = msg;
    app.showToast(msg);
  } finally {
    analyzingJob.value = false;
  }
}

async function optimize(resumeId: number) {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/${resumeId}/optimize`, "POST");
    selectedResumeId.value = resumeId;
    app.showToast("优化完成");
    await loadResumes();
    const row = resumes.value.find((r) => Number(r.resumeId) === Number(resumeId));
    const fromList = rowDiagnoseText(row);
    if (fromList) highlightSuggestions.value = fromList;
    else {
      const fromPost = rowDiagnoseText(data);
      if (fromPost) highlightSuggestions.value = fromPost;
      else if (isOptimized(row)) {
        highlightSuggestions.value =
          "优化已完成。若此处无摘要，请点击「查看优化稿」阅读完整正文。";
      }
    }
  } catch (e: any) {
    const msg = e?.message || "优化失败";
    errorText.value = msg;
    app.showToast(msg);
  }
}

async function openOptimizePreview(resumeId: number) {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/resume/${resumeId}/optimized-preview`, "GET");
    optimizePreviewText.value = String(data?.content ?? "");
    showOptimizeModal.value = true;
  } catch (e: any) {
    const msg = e?.message || "无法加载优化稿";
    errorText.value = msg;
    app.showToast(msg);
  }
}

function closeOptimizeModal() {
  showOptimizeModal.value = false;
}

async function copyOptimizePreview() {
  const t = optimizePreviewText.value;
  if (!t.trim()) {
    app.showToast("暂无正文可复制");
    return;
  }
  try {
    await navigator.clipboard.writeText(t);
    app.showToast("已复制到剪贴板");
  } catch {
    app.showToast("复制失败，请手动选择正文复制");
  }
}

function sizeKb(v: any) {
  const kb = Number(v || 0) / 1024;
  return kb > 0 ? kb.toFixed(1) : "0.0";
}

function fmt(v: any) {
  if (!v) return "--";
  return String(v).replace("T", " ").slice(0, 16);
}

watch([targetJobDescription, requiredAbilities], () => {
  try {
    localStorage.setItem(
      lsKeyJob(),
      JSON.stringify({
        targetJobDescription: targetJobDescription.value,
        requiredAbilities: requiredAbilities.value
      })
    );
  } catch {
    // ignore
  }
});

onMounted(async () => {
  try {
    const raw = localStorage.getItem(lsKeyJob());
    if (raw) {
      const o = JSON.parse(raw);
      if (typeof o.targetJobDescription === "string") targetJobDescription.value = o.targetJobDescription;
      if (typeof o.requiredAbilities === "string") requiredAbilities.value = o.requiredAbilities;
    }
    const pr = localStorage.getItem(lsKeyParsed());
    if (pr) {
      persistedParsed.value = JSON.parse(pr) as ParsedSnapshot;
    }
  } catch {
    // ignore
  }
  await loadPosts();
  await loadResumes();
  const pid = persistedParsed.value?.resumeId;
  if (pid != null) {
    const row = resumes.value.find((r) => Number(r.resumeId) === Number(pid));
    if (row) {
      selectedResumeId.value = pid;
      syncHighlightFromRow(row);
    }
  }
});
</script>

<style scoped>
.resume-page {
  max-width: 1200px;
}

.resume-inner-scroll {
  max-height: min(76vh, 760px);
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.resume-split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 18px;
  align-items: stretch;
}

.resume-col {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.resume-col-left {
  min-height: 420px;
}

.resume-panel {
  border: 1px solid #e2eef7;
  border-radius: 14px;
  padding: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcff 100%);
  box-shadow: 0 2px 12px rgba(23, 58, 94, 0.06);
}

.resume-panel-grow {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 200px;
}

.resume-panel-full {
  flex: 1;
  min-height: 420px;
  display: flex;
  flex-direction: column;
}

.resume-panel-title {
  font-weight: 800;
  font-size: 15px;
  color: #173a5e;
  margin-bottom: 8px;
}

.resume-panel-hint {
  margin: 0 0 10px;
  font-size: 12px;
  color: #5a7a94;
  line-height: 1.5;
}

.resume-textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #cfe5f3;
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 13px;
  line-height: 1.6;
  color: #173a5e;
  resize: vertical;
  min-height: 140px;
  background: #fff;
}

.resume-textarea:focus {
  outline: none;
  border-color: #7ec8ea;
  box-shadow: 0 0 0 2px rgba(126, 200, 234, 0.25);
}

.resume-analyze-btn {
  margin-top: 10px;
  align-self: flex-start;
}

.resume-panel-body {
  flex: 1;
  font-size: 13px;
  line-height: 1.65;
  color: #173a5e;
  overflow: auto;
}

.resume-panel-pre {
  white-space: pre-wrap;
  word-break: break-word;
  max-height: min(52vh, 360px);
}

.resume-optimize-link {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #dfeef8;
  font-size: 13px;
}

.resume-optimize-link a {
  color: #1177c8;
  font-weight: 600;
}

.resume-table {
  margin-top: 4px;
}

.resume-table-row {
  cursor: pointer;
}

.resume-table-row:hover {
  background: rgba(23, 119, 200, 0.04);
}

.resume-parsed {
  margin-bottom: 16px;
  padding: 16px;
}

.parsed-grid {
  display: grid;
  gap: 12px;
}

.parsed-sec {
  margin-top: 12px;
}

.parsed-sec-title {
  font-weight: 800;
  font-size: 13px;
  color: #155985;
  margin-bottom: 8px;
}

.parsed-ul {
  margin: 0;
  padding-left: 18px;
  line-height: 1.7;
  font-size: 13px;
}

.parsed-card {
  border: 1px solid #e7f1f8;
  border-radius: 10px;
  padding: 10px;
  margin-bottom: 8px;
  background: #fff;
  font-size: 13px;
}

.parsed-desc {
  margin-top: 6px;
  white-space: pre-wrap;
  line-height: 1.6;
  color: #173a5e;
}

.parsed-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.parsed-tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(17, 119, 200, 0.08);
  border: 1px solid #cfe5f3;
  font-size: 12px;
  color: #173a5e;
}

.small {
  font-size: 12px;
}

.resume-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px 12px;
  white-space: normal;
  max-width: 320px;
}

.resume-actions .resume-action-btn {
  flex: 0 0 auto;
  white-space: nowrap;
}

.resume-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(23, 58, 94, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.resume-modal {
  max-width: 720px;
  width: 100%;
  max-height: 90vh;
  overflow: auto;
  padding: 18px;
}

.resume-modal-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #173a5e;
}

.resume-modal-pre {
  margin: 10px 0;
  padding: 12px;
  max-height: min(50vh, 400px);
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.55;
  background: #f8fcff;
  border: 1px solid #e2eef7;
  border-radius: 10px;
}

.resume-modal-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 920px) {
  .resume-split {
    grid-template-columns: 1fr;
  }

  .resume-panel-full {
    min-height: 280px;
  }
}
</style>
