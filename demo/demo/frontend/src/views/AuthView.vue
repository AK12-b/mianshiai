<template>
  <section class="auth-page">
    <div class="auth-stage">
      <div class="auth-deck">
        <!-- 底层：同一个“长框”表单（左登录 / 右注册）固定不动 -->
        <div class="auth-form-long">
          <!-- 登录半屏 -->
          <div class="auth-form-pane">
            <div class="auth-card auth-form-card">
              <h2 class="auth-title">LOGIN</h2>

              <div class="field">
                <label>邮箱</label>
                <input v-model.trim="app.loginForm.email" placeholder="请输入邮箱" />
              </div>
              <div class="field">
                <label>密码</label>
                <input v-model.trim="app.loginForm.password" type="password" placeholder="请输入密码" />
              </div>

              <div class="auth-row">
                <label class="auth-remember">
                  <input v-model="app.loginForm.remember" type="checkbox" />
                  记住密码
                </label>
                <button class="btn btn-text auth-forget">忘记密码？</button>
              </div>

              <button class="btn btn-primary auth-submit" :disabled="app.loading" @click="onLogin">
                {{ app.loading ? "登录中..." : "登录" }}
              </button>

              <div class="auth-bottom-links">
                <span class="muted">没有账号？</span>
                <button class="btn btn-text auth-link" @click="app.authTab = 'register'">立即注册</button>
                <span class="auth-divider">|</span>
                <RouterLink class="btn btn-text auth-link" to="/">返回介绍页</RouterLink>
              </div>

            </div>
          </div>

          <!-- 注册半屏 -->
          <div class="auth-form-pane">
            <div class="auth-card auth-form-card">
              <h2 class="auth-title">REGISTER</h2>

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


              <button class="btn btn-primary auth-submit" :disabled="app.loading" @click="onRegister">
                {{ app.loading ? "提交中..." : "注册" }}
              </button>

              <div class="auth-bottom-links">
                <span class="muted">已有账号？</span>
                <button class="btn btn-text auth-link" @click="app.authTab = 'login'">去登录</button>
                <span class="auth-divider">|</span>
                <RouterLink class="btn btn-text auth-link" to="/">返回介绍页</RouterLink>
              </div>

            </div>
          </div>
        </div>

        <!-- 顶层：滑动遮罩卡片（你圈的那块），登录遮右 / 注册遮左 -->
        <div class="auth-mask" :class="{ 'is-register': app.authTab === 'register' }">
          <div class="auth-card auth-hero-card">
            <div class="auth-hero-head">
              <div class="logo">
                <img class="logo-image" src="/logo1.png" alt="Logo" />
                <img class="logo-text-image" src="/logo2.png" alt="InterYou" />
              </div>
            </div>
            <div class="auth-hero-main">
              <div class="auth-hero-kicker">欢迎加入</div>
              <div class="auth-hero-title">InterYou 面小优</div>
              <div class="auth-hero-sub">开始模拟面试与专项练习</div>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const router = useRouter();
const route = useRoute();

watch(
  () => route.query.tab,
  (tab) => {
    app.authTab = tab === "register" ? "register" : "login";
  },
  { immediate: true }
);

async function onLogin() {
  const ok = await app.login();
  if (ok) router.push("/app/dashboard");
}

async function onRegister() {
  await app.register();
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 22px 14px;
  background-image: linear-gradient(180deg, rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0.18)), url("/background1.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.auth-stage {
  width: min(980px, 94vw);
  overflow: hidden;
  border-radius: 18px;
}

.auth-deck {
  position: relative;
  padding: 16px;
  --gap: 0px;
}

.auth-form-long {
  width: 100%;
  display: flex;
  gap: var(--gap);
  align-items: stretch;
  /* 裁剪下层卡片的阴影/圆角，避免遮罩时露边 */
  overflow: hidden;
  border-radius: 18px;
}

.auth-form-pane {
  min-width: 0;
  flex: 0 0 50%;
  width: 50%;
  display: flex;
}

.auth-mask {
  position: absolute;
  top: 16px;
  bottom: 16px;
  /* 固定半屏宽度：登录时在右边挡住注册；注册时滑到左边挡住登录
     轻微重叠 1px，避免露出缝隙 */
  /* 重叠加大，确保右侧完全盖住注册卡边缘/阴影 */
  left: -12px;
  width: calc(50% + 24px);
  transform: translateX(calc(100% - 10px));
  transition: transform 620ms cubic-bezier(0.22, 0.85, 0.18, 1);
  will-change: transform;
  z-index: 4;
  pointer-events: auto;
}

.auth-mask.is-register {
  transform: translateX(14px);
}

.auth-mask .auth-card {
  height: 100%;
  box-shadow: none;
  border: 1px solid rgba(122, 203, 237, 0.42);
}

.auth-card {
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(122, 203, 237, 0.42);
  box-shadow: 0 22px 46px rgba(16, 44, 72, 0.16);
  padding: 26px;
  min-height: 420px;
}

.auth-form-pane .auth-card {
  flex: 1 1 auto;
  height: 100%;
}

/* 登录/注册并在一起：中间无缝，仅保留外侧圆角 */
.auth-form-pane:first-child .auth-card {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  border-right-width: 0;
}
.auth-form-pane:last-child .auth-card {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-left-width: 0;
  box-shadow: none;
}

.auth-hero-card {
  /* 遮罩卡片需要完全遮挡下层表单：背景不透明 */
  background: #ffffff;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-hero-card::after {
  content: "";
  position: absolute;
  inset: -40%;
  background: radial-gradient(circle at 35% 35%, rgba(89, 188, 246, 0.30), transparent 55%),
    radial-gradient(circle at 70% 75%, rgba(231, 255, 201, 0.55), transparent 55%);
  transform: rotate(10deg);
  pointer-events: none;
}

.auth-hero-head,
.auth-hero-main,
.auth-hero-foot {
  position: relative;
  z-index: 1;
}

.auth-hero-head {
  position: absolute;
  top: 26px;
  left: 26px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.auth-hero-main {
  margin-top: 0;
  text-align: center;
}

.auth-hero-kicker {
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.8px;
  color: rgba(15, 39, 64, 0.70);
}

.auth-hero-title {
  font-size: 34px;
  font-weight: 900;
  margin-top: 8px;
  color: rgba(15, 39, 64, 0.95);
}

.auth-hero-sub {
  margin-top: 10px;
  color: rgba(15, 39, 64, 0.70);
  line-height: 1.7;
}

.auth-hero-foot {
  margin-top: 34px;
  font-size: 12px;
  color: rgba(15, 39, 64, 0.62);
}

.auth-form-card {
  background: rgba(255, 255, 255, 0.96);
}

.auth-title {
  font-size: 30px;
  margin: 0 0 16px;
  letter-spacing: 2px;
  color: rgba(15, 39, 64, 0.92);
  text-align: center;
}

.auth-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -2px 0 14px;
}

/* 登录框中标题下方内容整体下移，LOGIN 标题位置不变 */
.auth-form-pane:first-child .field:first-of-type {
  margin-top: 24px;
}

.auth-remember {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.auth-forget {
  font-size: 13px;
}

.auth-submit {
  width: 100%;
  padding: 12px 16px;
  border-radius: 999px;
  background: rgba(10, 12, 14, 0.92);
  color: #fff;
  border: 0;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.18);
}

.auth-submit:hover:not(:disabled) {
  background: rgba(10, 12, 14, 0.98);
  transform: translateY(-1px);
}

.auth-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-tip {
  font-size: 12px;
  margin-top: -2px;
}

.auth-link {
  font-size: 13px;
}

.auth-bottom-links {
  font-size: 13px;
}

.auth-bottom-links .muted {
  font-size: 13px;
}

.auth-agreement {
  text-align: center;
  font-size: 12px;
  margin: 14px 0 0;
}

@media (max-width: 920px) {
  .auth-stage {
    width: min(560px, 94vw);
  }
  .auth-deck {
    padding: 10px;
  }
  .auth-form-long {
    flex-direction: column;
  }
  .auth-form-pane + .auth-form-pane {
    margin-top: 12px;
  }
  .auth-mask {
    display: none;
  }
  .auth-card {
    min-height: unset;
  }
  .auth-hero-main {
    margin-top: 22px;
  }
}
</style>

