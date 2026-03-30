<template>
  <section class="auth-layout">
    <aside class="auth-brand">
      <div class="brand-top">
        <div class="logo" style="color: #fff">
          <span class="logo-mark"></span>
          <span>FaceAI Interview</span>
        </div>
      </div>
      <div class="brand-main">
        <h1>在同一个平台完成模拟面试、专项练习与能力提升</h1>
        <p>模拟面试 · 智能报告 · 错题复盘 · 简历优化</p>
      </div>
      <div class="brand-foot">© 2026 FaceAI Interview. All rights reserved.</div>
    </aside>

    <section class="auth-form-wrap">
      <div class="auth-card">
        <h2>登录你的面试账户</h2>

        <div class="auth-tabs">
          <button class="auth-tab" :class="{ active: app.authTab === 'login' }" @click="app.authTab = 'login'">登录</button>
          <button class="auth-tab" :class="{ active: app.authTab === 'register' }" @click="app.authTab = 'register'">注册</button>
        </div>

        <div v-if="app.authTab === 'login'">
          <div class="field">
            <label>邮箱</label>
            <input v-model.trim="app.loginForm.email" placeholder="输入手机号或邮箱（当前使用邮箱）" />
          </div>
          <div class="field">
            <label>密码</label>
            <input v-model.trim="app.loginForm.password" type="password" placeholder="请输入密码" />
          </div>
          <div class="auth-row">
            <label style="display: flex; gap: 8px; align-items: center; font-size: 13px">
              <input v-model="app.loginForm.remember" type="checkbox" />记住账号
            </label>
            <button class="btn btn-text" style="font-size: 13px">忘记密码？</button>
          </div>
          <button class="btn btn-primary" style="width: 100%" :disabled="app.loading" @click="onLogin">
            {{ app.loading ? "登录中..." : "登录" }}
          </button>
          <div class="auth-bottom-links">
            <span class="muted">没有账号？</span>
            <button class="btn btn-text" style="font-size: 13px" @click="app.authTab = 'register'">立即注册</button>
            <span class="auth-divider">|</span>
            <RouterLink class="btn btn-text" style="font-size: 13px" to="/">返回介绍页</RouterLink>
          </div>
        </div>

        <div v-else>
          <div class="field">
            <label>用户名</label>
            <input v-model.trim="app.registerForm.userName" placeholder="请输入用户名" />
          </div>
          <div class="field">
            <label>邮箱</label>
            <input v-model.trim="app.registerForm.email" placeholder="请输入邮箱" />
          </div>
          <div class="field">
            <label>密码</label>
            <input v-model.trim="app.registerForm.password" type="password" placeholder="请输入密码" />
          </div>
          <p class="muted" style="font-size: 12px; margin-top: -2px">只需填写这三项，注册完成即可登录。</p>
          <button class="btn btn-primary" style="width: 100%" :disabled="app.loading" @click="onRegister">
            {{ app.loading ? "提交中..." : "注册" }}
          </button>
          <div class="auth-bottom-links">
            <span class="muted">已有账号？</span>
            <button class="btn btn-text" style="font-size: 13px" @click="app.authTab = 'login'">去登录</button>
            <span class="auth-divider">|</span>
            <RouterLink class="btn btn-text" style="font-size: 13px" to="/">返回介绍页</RouterLink>
          </div>
        </div>

        <p class="muted auth-agreement">点击按钮表示已阅读并同意服务条款</p>
      </div>
    </section>
  </section>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const router = useRouter();

async function onLogin() {
  const ok = await app.login();
  if (ok) router.push("/app/dashboard");
}

async function onRegister() {
  await app.register();
}
</script>

