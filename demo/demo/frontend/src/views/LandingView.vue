<template>
  <section class="intro-page">
    <header class="intro-top">
      <div class="container intro-nav">
        <div class="logo">
          <span class="logo-mark"></span>
          <span>FaceAI Interview</span>
        </div>
        <div class="intro-links">
          <button class="btn btn-text">帮助中心</button>
          <button class="btn btn-text">联系我们</button>
        </div>
      </div>
    </header>
    <main class="container intro-main">
      <p class="intro-tip">面向校招求职场景的 AI 模拟面试系统，支持模拟面试、专项练习、报告复盘与能力提升。</p>
      <h1 class="intro-title">FaceAI</h1>
      <h2 class="intro-subtitle">AI模拟面试与能力提升平台</h2>
      <div class="intro-cards">
        <article class="intro-card">
          <h3>开始对话</h3>
          <p>与 AI 面试官实时交流，开启拟真的求职训练流程</p>
          <RouterLink class="btn btn-primary" to="/auth">开始对话</RouterLink>
        </article>
        <article class="intro-card">
          <h3>进入平台</h3>
          <p>登录后可进行专项练习、错题复盘和面试报告分析</p>
          <RouterLink class="btn btn-outline" to="/auth">立即登录</RouterLink>
        </article>
      </div>
    </main>
  </section>
</template>

<script setup lang="ts"></script>

