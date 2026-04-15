<template>
  <div class="landing-wrap">
    <section class="landing-page">
      <header class="landing-top">
        <div class="container landing-nav">
          <div class="logo landing-logo">
            <img class="logo-image" src="/logo1.png" alt="Logo" />
            <img class="logo-text-image landing-logo-text" src="/logo2.png" alt="InterYou" />
          </div>

          <nav class="landing-menu">
          <button class="landing-menu-item" type="button" @click="scrollToSection('landing-features')">功能介绍</button>
          <button class="landing-menu-item" type="button" @click="scrollToSection('landing-team')">我们的团队</button>
          </nav>

          <div class="landing-actions">
            <RouterLink class="landing-pill landing-pill-dark" to="/auth">登录</RouterLink>
            <RouterLink class="landing-pill landing-pill-light" to="/auth?tab=register">注册</RouterLink>
          </div>
        </div>
      </header>

      <main class="container landing-hero">
        <div class="landing-hero-inner">
          <div class="landing-brand-line">
            <img class="landing-brand-text" src="/logo.png" alt="InterYou 面小优" />
          </div>
          <p class="landing-desc">
            专注校招求职的 AI 模拟面试平台，支持实战模拟、专项训练、智能<br />
            报告复盘与针对性能力提升。
          </p>
          <RouterLink class="landing-cta" to="/auth">立即使用</RouterLink>
        </div>
      </main>
    </section>

    <section id="landing-features" class="landing-section landing-feature-section">
      <div class="container landing-panel">
        <h2 class="landing-sec-title">功能介绍</h2>
        <div class="feature-grid">
          <article v-for="item in featureCards" :key="item.title" class="feature-card">
            <img class="feature-icon" :src="item.image" :alt="item.title" />
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </article>
        </div>
      </div>
    </section>

    <section id="landing-team" class="landing-section landing-team-section">
      <div class="container landing-panel">
        <h2 class="landing-sec-title">我们的团队</h2>
        <p class="landing-sec-sub">从学生视角出发，打造真正适合计算机专业的互动式求职工具</p>
        <div class="team-grid">
          <article v-for="item in teamMembers" :key="item.name" class="team-card">
            <img class="team-avatar" :src="item.image" :alt="item.name" />
            <h4>{{ item.name }}</h4>
            <p>{{ item.role }}</p>
          </article>
        </div>

        <footer class="landing-footer">
          <div class="footer-brand">
            <img class="footer-logo" src="/logo.png" alt="InterYou" />
            <p>专注校招求职的 AI 模拟面试平台，支持实战模拟、专项训练、智能报告复盘与针对性能力提升。</p>
          </div>
          <div class="footer-col">
            <h5>Services</h5>
            <a href="javascript:void(0)">面试模拟</a>
            <a href="javascript:void(0)">专项训练</a>
            <a href="javascript:void(0)">复盘分析</a>
            <a href="javascript:void(0)">能力提升</a>
          </div>
          <div class="footer-col">
            <h5>About Us</h5>
            <a href="javascript:void(0)">关于面小优</a>
            <a href="javascript:void(0)">用户指南</a>
            <a href="javascript:void(0)">隐私政策</a>
            <a href="javascript:void(0)">服务条款</a>
          </div>
          <div class="footer-col">
            <h5>Contact Info</h5>
            <a href="mailto:1403191941@qq.com">1403191941@qq.com</a>
          </div>
          <div class="footer-copyright">© 2026 Interyou All Rights Reserved. 面小优 版权所有</div>
        </footer>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const featureCards = [
  { title: "模拟面试", image: "/keji/ke1.png", desc: "AI 面试官实时对话，深度追问，1:1 还原校招技术面试场景。" },
  { title: "面试复盘", image: "/keji/ke2.png", desc: "自动生成面试记录与分析，查看历史表现，查漏补缺，稳步提升。" },
  { title: "专项训练", image: "/keji/ke3.png", desc: "根据薄弱知识点定向刷题，强化核心技能，针对性突破面试难点。" },
  { title: "错题档案", image: "/keji/ke4.png", desc: "自动收集练习错题，支持反复复习，让错误不再重复出现。" },
  { title: "学习资源", image: "/keji/ke5.png", desc: "提供岗位相关知识点与面试资料，辅助系统学习，夯实技术基础。" },
  { title: "成长数据", image: "/keji/ke6.png", desc: "可视化展示能力变化曲线，清晰看到进步，让提升有迹可循。" },
  { title: "简历优化", image: "/keji/ke7.png", desc: "AI 智能诊断简历问题，自动优化表达，提高简历通过率。" },
  { title: "跨岗推荐", image: "/keji/ke8.png", desc: "基于能力画像智能匹配适合岗位，明确提升方向，拓宽求职机会。" }
];

const teamMembers = [
  { name: "陈芳芳", role: "后端工程师", image: "/people/p1.png" },
  { name: "郭伟薇", role: "前端工程师", image: "/people/p2.png" },
  { name: "林若馨", role: "大模型工程师", image: "/people/p3.png" },
  { name: "林佳仪", role: "UI设计师", image: "/people/p4.png" }
];

function scrollToSection(id: string) {
  const target = document.getElementById(id);
  if (!target) return;
  target.scrollIntoView({ behavior: "smooth", block: "start" });
}
</script>

<style scoped>
.landing-wrap {
  background: #f5f6f8;
}
.landing-page {
  min-height: 100vh;
  position: relative;
  background-image: linear-gradient(180deg, rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0.18)), url("/background1.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.landing-top {
  height: 78px;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 2;
}
.landing-nav {
  display: flex;
  align-items: center;
  gap: 16px;
}
.landing-logo-text {
  transform: translateY(7px);
}
.landing-menu {
  display: flex;
  gap: 14px;
  align-items: center;
  justify-content: flex-start;
  margin-left: 12px;
  font-size: 12px;
  color: rgba(15, 39, 64, 0.72);
}
.landing-menu-item {
  color: inherit;
  text-decoration: none;
  padding: 6px 8px;
  border-radius: 999px;
  border: 0;
  background: transparent;
  cursor: pointer;
  font: inherit;
}
.landing-actions {
  display: flex;
  justify-content: flex-end;
  margin-left: auto;
  gap: 10px;
}
.landing-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
}
.landing-pill-dark {
  background: rgba(20, 24, 30, 0.82);
  color: #fff;
}
.landing-pill-light {
  background: rgba(255, 255, 255, 0.72);
  color: rgba(15, 39, 64, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.75);
}
.landing-hero {
  min-height: calc(100vh - 78px);
  display: grid;
  place-items: center;
  position: relative;
  z-index: 2;
}
.landing-hero-inner {
  text-align: center;
  padding: 24px 10px 60px;
}
.landing-brand-text {
  width: min(560px, 86vw);
  height: auto;
  display: block;
  margin: 0 auto;
}
.landing-desc {
  margin: 24px 0 44px;
  color: rgba(10, 14, 18, 0.82);
  font-size: 16px;
  line-height: 1.75;
}
.landing-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-top: 14px;
  height: 60px;
  padding: 0 54px;
  border-radius: 999px;
  background: rgba(10, 12, 14, 0.92);
  color: #fff;
  text-decoration: none;
  font-weight: 900;
  font-size: 20px;
}

.landing-section {
  padding: 20px 0;
}
.landing-panel {
  background: #fff;
  padding: 44px 28px 44px;
  width: 100%;
}
.landing-sec-title {
  margin: 0;
  text-align: center;
  font-size: 42px;
  font-weight: 900;
  color: #1c2340;
}
.landing-sec-sub {
  margin: 6px 0 0;
  text-align: center;
  font-size: 12px;
  color: #7d8398;
}

.feature-grid {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 204px));
  justify-content: center;
  column-gap: 62px;
  row-gap: 70px;
}
.feature-card {
  background: #fcfcfd;
  border: 0;
  box-shadow: 0 10px 24px rgba(16, 44, 72, 0.10);
  padding: 18px 12px 16px;
  text-align: center;
  display: grid;
  grid-template-rows: 146px 64px 1fr;
  align-items: start;
  justify-items: center;
}
.feature-icon {
  width: 194px;
  height: 136px;
  object-fit: contain;
  object-position: center center;
  display: block;
  margin: 0 auto;
  align-self: center;
}
.feature-card h3 {
  margin: 6px 0 6px;
  font-size: 22px;
  line-height: 1.28;
  font-weight: 800;
  color: #161a2f;
  min-height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.feature-card p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  font-weight: 500;
  color: #2f344a;
  min-height: 142px;
}
.landing-feature-section .landing-panel {
  min-height: 860px;
}

.team-grid {
  margin: 40px auto 52px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 40px;
  max-width: 1160px;
}
.team-card {
  background: #fcfcfd;
  border: 0;
  box-shadow: 0 8px 20px rgba(16, 44, 72, 0.08);
  text-align: center;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.team-avatar {
  width: 162px;
  height: 162px;
  object-fit: contain;
  object-position: center center;
  display: block;
  margin: 0 auto;
}
/* 第一张源图留白较多，单独放大以对齐视觉尺寸 */
.team-card:first-child .team-avatar {
  width: 166px;
  height: 166px;
  transform: translateY(4px);
}
.team-card h4 {
  margin: 12px 0 5px;
  font-size: 21px;
  font-weight: 800;
  line-height: 1.25;
  min-height: 26px;
}
.team-card p {
  margin: 0;
  color: #6e7387;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.3;
  min-height: 20px;
}
/* 第一张卡片文字强制与其余三张一致 */
.team-card:first-child h4 {
  font-size: 21px;
  font-weight: 800;
  line-height: 1.25;
  min-height: 26px;
}
.team-card:first-child p {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.3;
  min-height: 20px;
}
/* 第一张卡片文字整体上移一点点 */
.team-card:first-child h4,
.team-card:first-child p {
  transform: translateY(-3px);
}

.landing-footer {
  border-top: 1px solid #eceef3;
  margin-top: 18px;
  padding-top: 30px;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.2fr;
  gap: 20px;
  width: min(1080px, 92%);
  margin-left: auto;
  margin-right: auto;
}
.footer-brand p {
  margin: 12px 0 0;
  color: #50566f;
  font-size: 13px;
  line-height: 1.75;
  max-width: 360px;
}
.footer-logo {
  width: 165px;
  height: auto;
}
.footer-col h5 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #1f2440;
}
.footer-col a {
  display: block;
  color: #565c73;
  font-size: 13px;
  line-height: 1.9;
  text-decoration: none;
}
.footer-copyright {
  grid-column: 1 / -1;
  border-top: 1px solid #eceef3;
  margin-top: 16px;
  padding-top: 14px;
  text-align: center;
  color: #8b90a3;
  font-size: 12px;
}

.landing-team-section .landing-panel {
  min-height: 860px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.landing-team-section .landing-sec-title {
  margin-top: 16px;
}

@media (max-width: 1100px) {
  .landing-sec-title { font-size: 36px; }
  .feature-card h3 { font-size: 19px; }
  .feature-card p { font-size: 13px; }
  .feature-icon { width: 140px; height: 98px; }
}
@media (max-width: 900px) {
  .landing-nav { grid-template-columns: 1fr 1fr; }
  .landing-menu { display: none; }
  .feature-grid, .team-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .landing-footer { grid-template-columns: 1fr 1fr; }
  /* 小屏下统一所有功能图尺寸与位置，避免标题高度错位 */
  .feature-icon {
    width: 170px;
    height: 116px;
  }
  /* 手机端功能卡片文字等高 */
  .feature-card h3 {
    min-height: 58px;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1.2;
  }
  .feature-card p {
    min-height: 136px;
    line-height: 1.55;
  }
}
</style>

