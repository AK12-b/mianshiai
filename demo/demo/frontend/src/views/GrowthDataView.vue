<template>
  <section class="growth-wrap">
    <div class="card">
      <div class="growth-head">
        <h3>成长数据</h3>
        <div class="toolbar" style="margin: 0">
          <select v-model.number="activeCycle">
            <option :value="1">按周</option>
            <option :value="2">按月</option>
            <option :value="3">按年</option>
          </select>
          <select v-model="activePostId">
            <option value="">全部岗位</option>
            <option v-for="p in posts" :key="p.postId" :value="String(p.postId)">{{ p.postName }}</option>
          </select>
          <button class="btn btn-outline" @click="loadAll">刷新数据</button>
        </div>
      </div>
      <div class="growth-kpi-grid">
        <article class="growth-kpi-card">
          <div class="growth-kpi-top"><div class="muted">面试总次数</div><span class="growth-kpi-icon">🎯</span></div>
          <div class="growth-kpi-value">{{ summary.interviewCount }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="growth-kpi-top"><div class="muted">面试平均分</div><span class="growth-kpi-icon">📈</span></div>
          <div class="growth-kpi-value">{{ summary.avgScore }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="growth-kpi-top"><div class="muted">专项练习总题数</div><span class="growth-kpi-icon">📝</span></div>
          <div class="growth-kpi-value">{{ practiceSummary.totalQuestions }}</div>
        </article>
        <article class="growth-kpi-card">
          <div class="growth-kpi-top"><div class="muted">累计训练时长</div><span class="growth-kpi-icon">⏱️</span></div>
          <div class="growth-kpi-value">{{ totalTrainingDurationText }}</div>
        </article>
      </div>
    </div>

    <div class="growth-main-grid">
      <div class="growth-row-top">
        <div class="card">
          <div class="dashboard-section-title"><h3>短板变化和针对性建议</h3></div>
          <div class="growth-note-item">
            <span class="muted growth-key-label">当前短板</span>
            <span class="growth-value-normal">{{ latestRow?.weakPoint || "--" }}</span>
          </div>
          <div class="growth-note-item">
            <span class="muted growth-key-label">近3次短板变化</span>
            <span class="growth-value-normal">{{ weaknessTrendText }}</span>
          </div>
          <div class="growth-note-item">
            <span class="muted growth-key-label">针对性建议</span>
            <span class="growth-value-normal">{{ latestRow?.growthSuggest || "--" }}</span>
          </div>
        </div>
        <div class="card">
          <div class="dashboard-section-title"><h3>阶段性成长复盘</h3></div>
          <div class="growth-note-item">
            <span class="muted growth-key-label">{{ cycleLabel }}总结</span>
            <span class="growth-value-normal">{{ cycleSummaryText }}</span>
          </div>
          <div class="growth-note-item">
            <span class="muted growth-key-label">进步亮点</span>
            <span class="growth-value-normal">{{ progressHighlightText }}</span>
          </div>
        </div>
        <div class="card">
          <div class="dashboard-section-title"><h3>能力维度雷达图</h3></div>
          <div class="radar-wrap">
            <svg viewBox="0 0 580 440" class="radar-svg">
              <polygon v-for="(ring, idx) in radarRings" :key="idx" :points="ring" class="radar-ring" />
              <line v-for="(axis, idx) in radarAxes" :key="'a'+idx" :x1="axis.x1" :y1="axis.y1" :x2="axis.x2" :y2="axis.y2" class="radar-axis" />
              <text v-for="t in radarScaleTicks" :key="t.value" :x="t.x" :y="t.y" class="radar-tick">{{ t.value }}</text>
              <polygon :points="radarPoints" class="radar-fill" />
              <polyline :points="radarPoints + ' ' + radarPoints.split(' ')[0]" class="radar-line" />
              <g v-for="lbl in radarLabelPoints" :key="lbl.key">
                <text :x="lbl.labelX" :y="lbl.labelY" class="radar-label" :text-anchor="lbl.labelAnchor">{{ lbl.label }}</text>
                <text :x="lbl.valueX" :y="lbl.valueY" class="radar-label-value" :text-anchor="lbl.valueAnchor">{{ lbl.value }}</text>
              </g>
            </svg>
          </div>
        </div>
      </div>
      <div class="growth-row-bottom">
        <div class="card">
          <div class="dashboard-section-title"><h3>成长曲线</h3></div>
          <div class="line-wrap">
            <svg viewBox="0 0 860 320" class="line-svg">
              <line v-for="y in [40,90,140,190,240,290]" :key="y" x1="56" :y1="y" x2="820" :y2="y" class="line-grid" />
              <text v-for="s in stageAxis" :key="s.label" x="12" :y="s.y + 4" class="line-y-stage">{{ s.label }}</text>
            <template v-for="line in lineSeries" :key="line.key">
              <polyline v-for="(seg, sidx) in line.segments" :key="line.key + '-seg-' + sidx" :points="seg.points" :stroke="line.color" class="line-poly" />
              <circle
                v-for="(p, idx) in line.dotPoints"
                :key="line.key + '-' + idx"
                :cx="p.x"
                :cy="p.y"
                r="3"
                :fill="line.color"
              />
            </template>
              <text v-for="(x, idx) in lineXAxis" :key="x + idx" :x="xPos(idx)" y="310" class="line-x-label">{{ x }}</text>
            </svg>
            <div class="line-legend">
              <span v-for="line in lineSeries" :key="line.key" class="line-legend-item">
                <i :style="{ background: line.color }"></i>{{ line.label }}
              </span>
            </div>
          </div>
        </div>
        <div class="card">
          <div class="dashboard-section-title"><h3>错题统计</h3></div>
          <div class="word-cloud word-cloud-center" v-if="wrongWordCloudItems.length">
            <span
              v-for="w in wrongWordCloudItems"
              :key="w.name"
              class="word-cloud-item"
              :style="{ fontSize: `${w.fontSize}px`, color: w.color }"
              :title="w.name"
            >{{ w.name }}</span>
          </div>
          <div v-else class="muted">暂无错题数据</div>
        </div>
      </div>
    </div>

    <pre v-if="errorText" class="api-output">{{ errorText }}</pre>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const rows = ref<any[]>([]);
const interviewTrendRows = ref<any[]>([]);
const posts = ref<any[]>([]);
const activeCycle = ref(1); // 1周 2月 3年
const activePostId = ref("");
const errorText = ref("");
const dailyWeekTrend = ref<Map<string, any>>(new Map()); // yyyy-MM-dd -> row
const radarMetricRow = ref<any | null>(null);
const wrongKnowledgeLabels = ref<string[]>([]);
const wrongKnowledgeStats = ref<{ name: string; count: number }[]>([]);

const wrongWordCloudItems = computed(() => {
  const colors = ["#2b6cb0", "#2f855a", "#c05621", "#553c9a", "#b7791f", "#2c5282"];
  const list = wrongKnowledgeStats.value.slice().filter((x) => x && x.name);
  if (!list.length) return [];
  const max = Math.max(1, ...list.map((x) => Number(x.count || 0)));
  const minFont = 14;
  const maxFont = 30;
  return list.map((x, idx) => {
    const c = Math.max(1, Number(x.count || 1));
    const ratio = c / max;
    return {
      name: x.name,
      count: c,
      color: colors[idx % colors.length],
      fontSize: Math.round(minFont + (maxFont - minFont) * ratio)
    };
  });
});

const currentCycleRows = computed(() => {
  return rows.value
    .filter((r) => {
    const cycleOk = Number(r.statisticsCycle) === activeCycle.value;
    const postOk = !activePostId.value || String(r.postId ?? "") === activePostId.value;
    return cycleOk && postOk;
    })
    .slice()
    .sort((a, b) => String(b?.cycleTime || "").localeCompare(String(a?.cycleTime || "")));
});
const fallbackPostRows = computed(() => {
  return rows.value
    .filter((r) => !activePostId.value || String(r?.postId ?? "") === activePostId.value)
    .slice()
    .sort((a, b) => String(b?.cycleTime || "").localeCompare(String(a?.cycleTime || "")));
});
const displayRows = computed(() => {
  // 能力表优先按当前周期读取；若该周期为空，则回退到能力表该岗位的全部数据，避免页面全 0
  return currentCycleRows.value.length ? currentCycleRows.value : fallbackPostRows.value;
});

const latestRow = computed(() => displayRows.value[0] || rows.value[0] || null);
const aggregatedCycleRows = computed(() => {
  const map = new Map<string, any>();
  for (const r of displayRows.value) {
    const key = String(r?.cycleTime || "").trim();
    if (!key) continue;
    const weight = Math.max(0, Number(r?.interviewCount || 0));
    if (!map.has(key)) {
      map.set(key, {
        cycleTime: key,
        interviewCount: 0,
        practiceQuestionCount: 0,
        totalTrainDuration: 0,
        wrongQuestionCount: 0,
        _w: 0,
        _avgScore: 0,
        _techScore: 0,
        _knowledgeDepthScore: 0,
        _logicScore: 0,
        _expressScore: 0,
        _matchScore: 0
      });
    }
    const g = map.get(key);
    g.interviewCount += weight;
    g.practiceQuestionCount += Number(r?.practiceQuestionCount || 0);
    g.totalTrainDuration += Number(r?.totalTrainDuration || 0);
    g.wrongQuestionCount += Number(r?.wrongQuestionCount || 0);
    g._w += weight;
    g._avgScore += Number(r?.avgScore || 0) * weight;
    g._techScore += Number(r?.techScore ?? r?.avgScore ?? 0) * weight;
    g._knowledgeDepthScore += Number(r?.knowledgeDepthScore ?? r?.avgScore ?? 0) * weight;
    g._logicScore += Number(r?.logicScore ?? r?.avgScore ?? 0) * weight;
    g._expressScore += Number(r?.expressScore ?? r?.avgScore ?? 0) * weight;
    g._matchScore += Number(r?.matchScore ?? r?.avgScore ?? 0) * weight;
  }
  return Array.from(map.values())
    .map((g) => {
      const w = g._w > 0 ? g._w : 1;
      return {
        cycleTime: g.cycleTime,
        interviewCount: g.interviewCount,
        practiceQuestionCount: g.practiceQuestionCount,
        totalTrainDuration: g.totalTrainDuration,
        wrongQuestionCount: g.wrongQuestionCount,
        avgScore: Number((g._avgScore / w).toFixed(1)),
        techScore: Number((g._techScore / w).toFixed(1)),
        knowledgeDepthScore: Number((g._knowledgeDepthScore / w).toFixed(1)),
        logicScore: Number((g._logicScore / w).toFixed(1)),
        expressScore: Number((g._expressScore / w).toFixed(1)),
        matchScore: Number((g._matchScore / w).toFixed(1))
      };
    })
    .sort((a, b) => String(a.cycleTime).localeCompare(String(b.cycleTime)));
});
const trendCycleRows = computed(() => {
  return interviewTrendRows.value
    .filter((r) => {
      const cycleOk = Number(r?.statisticsCycle) === activeCycle.value;
      const postOk = !activePostId.value || String(r?.postId ?? "") === activePostId.value;
      return cycleOk && postOk;
    })
    .slice()
    .sort((a, b) => String(b?.cycleTime || "").localeCompare(String(a?.cycleTime || "")));
});
const trendFallbackPostRows = computed(() => {
  return interviewTrendRows.value
    .filter((r) => !activePostId.value || String(r?.postId ?? "") === activePostId.value)
    .slice()
    .sort((a, b) => String(b?.cycleTime || "").localeCompare(String(a?.cycleTime || "")));
});
const latestRadarSourceRow = computed(() => trendCycleRows.value[0] || trendFallbackPostRows.value[0] || null);
const aggregatedTrendCycleRows = computed(() => {
  const map = new Map<string, any>();
  for (const r of trendCycleRows.value) {
    const key = String(r?.cycleTime || "").trim();
    if (!key) continue;
    const weight = Math.max(0, Number(r?.interviewCount || 0));
    if (!map.has(key)) {
      map.set(key, {
        cycleTime: key,
        interviewCount: 0,
        _w: 0,
        _avgScore: 0,
        _techScore: 0,
        _knowledgeDepthScore: 0,
        _logicScore: 0,
        _expressScore: 0,
        _matchScore: 0
      });
    }
    const g = map.get(key);
    g.interviewCount += weight;
    g._w += weight;
    g._avgScore += Number(r?.avgScore || 0) * weight;
    g._techScore += Number(r?.techScore ?? r?.avgScore ?? 0) * weight;
    g._knowledgeDepthScore += Number(r?.knowledgeDepthScore ?? r?.avgScore ?? 0) * weight;
    g._logicScore += Number(r?.logicScore ?? r?.avgScore ?? 0) * weight;
    g._expressScore += Number(r?.expressScore ?? r?.avgScore ?? 0) * weight;
    g._matchScore += Number(r?.matchScore ?? r?.avgScore ?? 0) * weight;
  }
  return Array.from(map.values())
    .map((g) => {
      const w = g._w > 0 ? g._w : 1;
      return {
        cycleTime: g.cycleTime,
        interviewCount: g.interviewCount,
        avgScore: Number((g._avgScore / w).toFixed(1)),
        techScore: Number((g._techScore / w).toFixed(1)),
        knowledgeDepthScore: Number((g._knowledgeDepthScore / w).toFixed(1)),
        logicScore: Number((g._logicScore / w).toFixed(1)),
        expressScore: Number((g._expressScore / w).toFixed(1)),
        matchScore: Number((g._matchScore / w).toFixed(1))
      };
    })
    .sort((a, b) => String(a.cycleTime).localeCompare(String(b.cycleTime)));
});

const summary = computed(() => {
  const list = displayRows.value;
  if (!list.length) {
    return {
      interviewCount: 0,
      avgScore: "0"
    };
  }
  const interviewCount = list.reduce((s, r) => s + safeInterviewCount(r), 0);
  const totalWeight = interviewCount;
  const weightedSum = list.reduce((s, r) => s + Number(r?.avgScore || 0) * safeInterviewCount(r), 0);
  const avgScore = totalWeight > 0 ? (weightedSum / totalWeight).toFixed(1) : "0";
  return { interviewCount, avgScore };
});
const cycleLabel = computed(() => (activeCycle.value === 3 ? "年度" : activeCycle.value === 2 ? "月度" : "周度"));
const practiceSummary = computed(() => {
  const totalQuestions = displayRows.value.reduce((s, x) => s + Number(x?.practiceQuestionCount || 0), 0);
  return { totalQuestions };
});
const totalTrainingDurationMinutes = computed(() => {
  return displayRows.value.reduce((s, x) => s + Number(x?.totalTrainDuration || 0), 0);
});
const totalTrainingDurationText = computed(() => {
  const mins = totalTrainingDurationMinutes.value;
  if (mins < 60) return `${mins} 分钟`;
  const h = Math.floor(mins / 60);
  const m = mins % 60;
  return m ? `${h}小时${m}分` : `${h}小时`;
});
const weaknessTrendText = computed(() => {
  const top3 = displayRows.value.slice(0, 3).map((x) => String(x?.weakPoint || "").trim()).filter(Boolean);
  return top3.length ? top3.join(" → ") : "--";
});
const cycleSummaryText = computed(() => {
  const list = displayRows.value;
  if (!list.length) return "暂无数据";
  const interviewCount = list.reduce((s, r) => s + safeInterviewCount(r), 0);
  const avg = Number(summary.value.avgScore || 0).toFixed(1);
  const best = list.reduce((m, r) => Math.max(m, Number(r?.avgScore || 0)), 0).toFixed(1);
  const weakness = list.map((r) => String(r?.weakPoint || "").trim()).filter(Boolean);
  const firstWeak = weakness[weakness.length - 1] || "";
  const latestWeak = weakness[0] || "";
  const improveRate = firstWeak && latestWeak && firstWeak !== latestWeak ? "20%" : "10%";
  const kpCount = list.reduce((s, r) => s + Number(r?.practiceQuestionCount || 0), 0);
  return `训练${interviewCount}次，均分${avg}，最高${best}，短板改善率${improveRate}，新增掌握知识点${kpCount}个`;
});
const progressHighlightText = computed(() => {
  const list = displayRows.value;
  if (list.length < 2) return "本阶段完成持续训练，核心能力保持稳定。";
  const latest = list[0];
  const old = list[list.length - 1];
  const dTech = Math.round(Number(latest?.techScore || 0) - Number(old?.techScore || 0));
  const dExpr = Math.round(Number(latest?.expressScore || 0) - Number(old?.expressScore || 0));
  return `${cycleLabel.value}技术正确性得分提升${Math.max(0, dTech)}%，表达能力提升${Math.max(0, dExpr)}%。`;
});

const metric = computed(() => {
  // 雷达图专用：优先读取“按当前周期聚合”的面试评估真实维度分
  const source = radarMetricRow.value || latestRadarSourceRow.value || latestRow.value;
  const scoreBase = Number(source?.avgScore || 0);
  return {
    tech: clamp(Number(source?.techScore ?? scoreBase)),
    depth: clamp(Number(source?.knowledgeDepthScore ?? scoreBase)),
    logic: clamp(Number(source?.logicScore ?? scoreBase)),
    match: clamp(Number(source?.matchScore ?? scoreBase)),
    expression: clamp(Number(source?.expressScore ?? scoreBase))
  };
});
const radarLegend = computed(() => [
  { key: "tech", label: "技术正确性", value: metric.value.tech },
  { key: "depth", label: "知识深度", value: metric.value.depth },
  { key: "logic", label: "逻辑严谨性", value: metric.value.logic },
  { key: "expression", label: "表达能力", value: metric.value.expression },
  { key: "match", label: "岗位匹配度", value: metric.value.match }
]);

const RADAR_CENTER_X = 280;
const RADAR_CENTER_Y = 210; // 整体下移，避免顶部遮挡
const RADAR_MAX_R = 170; // 缩小半径，保证 0~100 刻度在 viewBox 内

const radarPoints = computed(() => {
  const vals = [metric.value.tech, metric.value.depth, metric.value.logic, metric.value.expression, metric.value.match];
  return vals
    .map((v, idx) => {
      const angle = (-90 + idx * 72) * (Math.PI / 180);
      const r = (clamp(v) / 100) * RADAR_MAX_R;
      const x = RADAR_CENTER_X + Math.cos(angle) * r;
      const y = RADAR_CENTER_Y + Math.sin(angle) * r;
      return `${x},${y}`;
    })
    .join(" ");
});
const radarLabelPoints = computed(() => {
  const labels = radarLegend.value;
  return labels.map((it, idx) => {
    const angle = (-90 + idx * 72) * (Math.PI / 180);
    const bx = RADAR_CENTER_X + Math.cos(angle) * (RADAR_MAX_R + 30);
    const by = RADAR_CENTER_Y + Math.sin(angle) * (RADAR_MAX_R + 30);
    const isRight = Math.cos(angle) >= 0.15;
    const isLeft = Math.cos(angle) <= -0.15;
    const isTop = Math.sin(angle) < -0.7;
    const isBottom = Math.sin(angle) > 0.7;
    const y = isTop ? by + 18 : isBottom ? by - 3 : by;
    const yOffsetMap = [-6, -2, 6, 6, -8];
    const xOffsetMap = [0, 0, 0, 0, -2];
    const labelShift = isRight ? -6 : isLeft ? 6 : 0;
    // 拉开「逻辑严谨性」(idx=2) 的标签与数值间距，避免贴太近
    const valueGapMap = [64, 64, 92, -66, 64];
    const valueGap = valueGapMap[idx] ?? (isRight ? 86 : isLeft ? -86 : 64);
    const valueBelow = it.key === "depth" || it.key === "match";
    const baseLabelY = y + (yOffsetMap[idx] || 0);
    const labelAnchor = valueBelow ? "middle" : isRight ? "start" : isLeft ? "end" : "middle";
    const valueAnchor = valueBelow ? "middle" : isRight ? "start" : isLeft ? "end" : "middle";
    const labelX = valueBelow ? bx + (xOffsetMap[idx] || 0) : bx + labelShift + (xOffsetMap[idx] || 0);
    return {
      key: it.key,
      label: it.label,
      value: `${it.value}`,
      labelY: baseLabelY,
      valueY: valueBelow ? baseLabelY + 24 : baseLabelY + (isTop ? -2 : 0),
      labelX,
      valueX: valueBelow ? labelX : bx + valueGap + (xOffsetMap[idx] || 0),
      labelAnchor,
      valueAnchor
    };
  });
});
const radarRings = computed(() => {
  const levels = [0.2, 0.4, 0.6, 0.8, 1];
  return levels.map((lv) =>
    [0, 1, 2, 3, 4]
      .map((idx) => {
        const angle = (-90 + idx * 72) * (Math.PI / 180);
        const x = RADAR_CENTER_X + Math.cos(angle) * RADAR_MAX_R * lv;
        const y = RADAR_CENTER_Y + Math.sin(angle) * RADAR_MAX_R * lv;
        return `${x},${y}`;
      })
      .join(" ")
  );
});
const radarAxes = computed(() => {
  return [0, 1, 2, 3, 4].map((idx) => {
    const angle = (-90 + idx * 72) * (Math.PI / 180);
    return {
      x1: RADAR_CENTER_X,
      y1: RADAR_CENTER_Y,
      x2: RADAR_CENTER_X + Math.cos(angle) * RADAR_MAX_R,
      y2: RADAR_CENTER_Y + Math.sin(angle) * RADAR_MAX_R
    };
  });
});
const radarScaleTicks = computed(() => {
  const values = [20, 40, 60, 80, 100];
  return values.map((v) => {
    const y = RADAR_CENTER_Y - (RADAR_MAX_R * v) / 100;
    return { value: v, x: RADAR_CENTER_X + 8, y: y + 4 };
  });
});
async function loadWrongKnowledgeLabels() {
  try {
    const postId = activePostId.value ? Number(activePostId.value) : 0;
    const data = await app.apiRequest(
      `/api/wrong-question/user/${app.uid}/knowledge-points?cycle=${activeCycle.value}&postId=${postId}&limit=18`
    );
    wrongKnowledgeLabels.value = Array.isArray(data) ? data.map((x) => String(x || "").trim()).filter(Boolean) : [];
  } catch {
    wrongKnowledgeLabels.value = [];
  }
}

async function loadWrongKnowledgeStats() {
  try {
    const postId = activePostId.value ? Number(activePostId.value) : 0;
    const data = await app.apiRequest(
      `/api/wrong-question/user/${app.uid}/knowledge-point-stats?cycle=${activeCycle.value}&postId=${postId}&limit=18`
    );
    wrongKnowledgeStats.value = Array.isArray(data)
      ? data
          .map((x: any) => ({ name: String(x?.name || "").trim(), count: Number(x?.count || 0) }))
          .filter((x: any) => x.name && x.count > 0)
      : [];
  } catch {
    wrongKnowledgeStats.value = [];
  }
}

const lineChartPack = computed(() => {
  if (activeCycle.value === 1) {
    const labels: string[] = [];
    const packed: any[] = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    for (let i = 6; i >= 0; i--) {
      const d = addDays(today, -i);
      const key = dateKey(d);
      labels.push(dateLabel(d));
      packed.push(dailyWeekTrend.value.get(key) || null);
    }
    return { labels, rows: packed };
  }
  if (activeCycle.value === 2) {
    // 最近 6 个月（含本月）
    const byMonth = new Map<string, any>();
    aggregatedTrendCycleRows.value.forEach((r) => {
      const k = String(r?.cycleTime || "").trim();
      if (k) byMonth.set(k, r);
    });
    const labels: string[] = [];
    const packed: any[] = [];
    const now = new Date();
    now.setDate(1);
    now.setHours(0, 0, 0, 0);
    for (let i = 5; i >= 0; i--) {
      const d = addMonths(now, -i);
      const key = monthKey(d);
      labels.push(key);
      packed.push(byMonth.get(key) || null);
    }
    return { labels, rows: packed };
  }
  // 最近 5 年（含今年）
  const byYear = new Map<string, any>();
  aggregatedTrendCycleRows.value.forEach((r) => {
    const k = String(r?.cycleTime || "").trim();
    if (k) byYear.set(k, r);
  });
  const labels: string[] = [];
  const packed: any[] = [];
  const thisYear = new Date().getFullYear();
  for (let y = thisYear - 4; y <= thisYear; y++) {
    const key = String(y);
    labels.push(`${key}年`);
    packed.push(byYear.get(key) || null);
  }
  return { labels, rows: packed };
});
const lineXAxis = computed(() => lineChartPack.value.labels);
const lineRowsForSeries = computed(() => lineChartPack.value.rows);
function xPos(idx: number) {
  const total = Math.max(1, lineXAxis.value.length - 1);
  return 56 + (764 / total) * idx;
}
function yPos(v: number) {
  return 290 - (clamp(v) / 100) * 250;
}
const stageAxis = [
  { label: "100", y: yPos(100) },
  { label: "80", y: yPos(80) },
  { label: "60", y: yPos(60) },
  { label: "40", y: yPos(40) },
  { label: "20", y: yPos(20) }
];
const lineSeries = computed(() => {
  const rowsAsc = lineRowsForSeries.value;
  function build(key: string, label: string, color: string, getter: (r: any) => number | null | undefined) {
    const segments: { points: string }[] = [];
    const dotPoints: { x: number; y: number }[] = [];

    let cur: string[] = [];
    rowsAsc.forEach((r, idx) => {
      const v = r == null ? null : getter(r);
      if (v == null || Number.isNaN(Number(v))) {
        if (cur.length >= 2) segments.push({ points: cur.join(" ") });
        cur = [];
        return;
      }
      const p = { x: xPos(idx), y: yPos(Number(v)) };
      cur.push(`${p.x},${p.y}`);
      dotPoints.push(p);
    });
    if (cur.length >= 2) segments.push({ points: cur.join(" ") });

    return { key, label, color, segments, dotPoints };
  }
  return [
    build("tech", "技术正确性", "#3AAEFF", (r) => Number(r.techScore ?? r.avgScore ?? 0)),
    build("depth", "知识深度", "#5B8FF9", (r) => Number(r.knowledgeDepthScore ?? r.avgScore ?? 0)),
    build("logic", "逻辑严谨性", "#4BC0A0", (r) => Number(r.logicScore ?? r.avgScore ?? 0)),
    build("expression", "表达能力", "#F59E66", (r) => Number(r.expressScore ?? r.avgScore ?? 0)),
    build("match", "岗位匹配度", "#8B7CF6", (r) => Number(r.matchScore ?? r.avgScore ?? 0)),
    build("avg", "面试平均分", "#1F3854", (r) => Number(r.avgScore || 0))
  ];
});

function clamp(value: number) {
  return Math.max(0, Math.min(100, Math.round(value)));
}

function safeNum(v: any) {
  return Number(v || 0);
}
function safeInterviewCount(r: any) {
  const n = Number(r?.interviewCount);
  if (Number.isFinite(n) && n > 0) return n;
  const hasSignal =
    Number(r?.avgScore || 0) > 0 ||
    Number(r?.practiceQuestionCount || 0) > 0 ||
    Number(r?.totalTrainDuration || 0) > 0;
  return hasSignal ? 1 : 0;
}

function addDays(base: Date, delta: number) {
  const d = new Date(base);
  d.setDate(d.getDate() + delta);
  return d;
}
function addMonths(base: Date, delta: number) {
  const d = new Date(base);
  d.setMonth(d.getMonth() + delta);
  return d;
}
function dateKey(d: Date) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}
function dateLabel(d: Date) {
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${m}-${day}`;
}
function monthKey(d: Date) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  return `${y}-${m}`;
}

async function loadGrowth() {
  errorText.value = "";
  try {
    const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}`);
    rows.value = Array.isArray(data) ? data : [];
  } catch (e: any) {
    rows.value = [];
    errorText.value = e?.message || "成长数据加载失败";
  }
}
async function loadInterviewTrend() {
  try {
    const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}/dimension-trend`);
    interviewTrendRows.value = Array.isArray(data) ? data : [];
  } catch {
    interviewTrendRows.value = [];
  }
}

async function loadPosts() {
  try {
    const data = await app.apiRequest("/api/post/list");
    posts.value = Array.isArray(data) ? data : [];
  } catch {
    posts.value = [];
  }
}
async function loadRadarMetric() {
  try {
    const postId = activePostId.value ? Number(activePostId.value) : 0;
    const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}/radar-metric?cycle=${activeCycle.value}&postId=${postId}`);
    radarMetricRow.value = data && typeof data === "object" ? data : null;
  } catch {
    radarMetricRow.value = null;
  }
}
async function loadDailyWeekTrend() {
  if (activeCycle.value !== 1) return;
  try {
    const postId = activePostId.value ? Number(activePostId.value) : 0;
    const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}/daily-trend?postId=${postId}`);
    const list = Array.isArray(data) ? data : [];
    const map = new Map<string, any>();
    list.forEach((r: any) => {
      const k = String(r?.date || "").trim();
      if (!k) return;
      map.set(k, r);
    });
    dailyWeekTrend.value = map;
  } catch {
    dailyWeekTrend.value = new Map();
  }
}
async function loadAll() {
  await Promise.all([
    loadGrowth(),
    loadInterviewTrend(),
    loadPosts(),
    loadRadarMetric(),
    loadWrongKnowledgeLabels(),
    loadWrongKnowledgeStats()
  ]);
  await loadDailyWeekTrend();
}

onMounted(loadAll);

watch([activeCycle, activePostId], () => {
  loadRadarMetric();
  loadWrongKnowledgeLabels();
  loadWrongKnowledgeStats();
  if (activeCycle.value === 1) {
    loadDailyWeekTrend();
  }
});

function formatCycleLabel(cycleTime: string) {
  if (!cycleTime || cycleTime === "--") return "--";
  if (activeCycle.value === 1) return cycleTime; // 例如 2026-W15
  if (activeCycle.value === 2) return cycleTime; // 例如 2026-04
  if (activeCycle.value === 3) return `${cycleTime}年`;
  return cycleTime;
}
</script>

<style scoped>
.growth-wrap {
  display: grid;
  gap: 12px;
}

.growth-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.growth-head h3 {
  margin: 0;
}

.growth-kpi-grid { display: grid; grid-template-columns: repeat(4, minmax(140px, 1fr)); gap: 10px; }

.growth-kpi-card {
  border: 1px solid #e2eef7;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}
.growth-kpi-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.growth-kpi-icon {
  font-size: 36px;
  color: #5e7fa1;
  line-height: 1;
}

.growth-kpi-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #14466f;
}

.growth-main-grid { display: grid; gap: 12px; }
.growth-row-top {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
  align-items: stretch;
  margin-top: 10px;
}
.growth-row-bottom { display: grid; grid-template-columns: 2fr 1fr; gap: 12px; }
.growth-row-top .card {
  padding: 6px 8px 8px;
  display: flex;
  flex-direction: column;
  min-height: 320px;
  height: 100%;
}
.growth-row-top .dashboard-section-title {
  margin-bottom: 6px;
  flex-shrink: 0;
  padding-left: 8px;
  margin-top: 8px;
}
.growth-row-top .growth-note-item {
  padding: 4px 0;
  padding-left: 8px;
}
.growth-row-top .card:last-child {
  overflow: visible;
}
.growth-row-top .radar-wrap {
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 10px;
  padding-bottom: 14px;
  box-sizing: border-box;
  overflow: visible;
}
.growth-row-top .radar-svg {
  transform: none;
  height: min(320px, 38vh);
  margin-top: 0;
}

.radar-wrap {
  display: grid;
  grid-template-columns: 1fr;
  gap: 6px;
  align-items: start;
}
.radar-svg {
  width: 100%;
  /* viewBox=580x440，给标签留足高度，避免底部被裁切 */
  height: min(420px, 46vh);
  max-height: 100%;
}
.radar-ring {
  fill: none;
  stroke: #c3d7ea;
  stroke-width: 1;
}
.radar-axis {
  stroke: #d2e1ef;
  stroke-width: 1;
}
.radar-fill {
  fill: rgba(58, 174, 255, 0.18);
}
.radar-line {
  fill: none;
  stroke: #3aaeff;
  stroke-width: 2.4;
}
.radar-label {
  font-size: 15px;
  fill: #1f3854;
}
.radar-label-value {
  font-size: 15px;
  fill: #1f3854;
  font-weight: 700;
}
.radar-tick {
  font-size: 14px;
  fill: #8aa3b7;
  user-select: none;
}
.line-wrap {
  display: grid;
  gap: 10px;
}
.line-svg {
  width: 100%;
  height: 320px;
  background: #fff;
  border: 1px solid #e3eef8;
  border-radius: 12px;
}
.line-grid {
  stroke: #e9f3fb;
  stroke-width: 1;
}
.line-poly {
  fill: none;
  stroke-width: 2.5;
}
.line-x-label {
  font-size: 12px;
  fill: #6a8298;
  text-anchor: middle;
}
.line-y-stage {
  font-size: 12px;
  fill: #6a8298;
}
.line-legend {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}
.line-legend-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #4d667d;
}
.line-legend-item i {
  width: 18px;
  height: 3px;
  border-radius: 999px;
  display: inline-block;
}

.growth-note-item {
  padding: 10px 0;
  border-bottom: 1px dashed #dfeef8;
  display: grid;
  gap: 4px;
}

.growth-note-item:last-child {
  border-bottom: 0;
}
.growth-key-label {
  font-weight: 700;
  color: #1f3854;
}
.growth-value-normal {
  font-weight: 400;
  color: #1f3854;
  line-height: 1.6;
}
.word-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  align-items: baseline;
  line-height: 1.5;
}
.word-cloud-center {
  min-height: 220px;
  height: 100%;
  justify-content: center;
  align-content: center;
  align-items: center;
}
.word-cloud-item {
  font-weight: 700;
  cursor: default;
  white-space: nowrap;
}

@media (max-width: 1000px) {
  .growth-head {
    flex-direction: column;
    align-items: flex-start;
  }
  .growth-kpi-grid {
    grid-template-columns: repeat(2, minmax(140px, 1fr));
  }
  .growth-row-top,
  .growth-row-bottom {
    grid-template-columns: 1fr;
  }
  .radar-wrap {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .growth-kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>

