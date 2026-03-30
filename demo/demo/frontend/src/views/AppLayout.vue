<template>
  <section class="app-shell">
    <header class="app-head">
      <div class="container">
        <div class="logo">
          <span class="logo-mark"></span>
          <span>FaceAI Interview</span>
        </div>
        <div class="app-user-bar" ref="userMenuWrapRef">
          <span class="tag">{{ app.user.role }}</span>
          <button class="btn app-user-name" @click.stop="toggleUserMenu">
            {{ app.user.name }}
            <span class="user-name-arrow">▼</span>
          </button>
          <div v-if="showUserMenu" class="user-dropdown card">
            <div class="user-dropdown-title">{{ app.user.name }}</div>
            <button class="user-dropdown-item" @click="goProfile">个人信息</button>
            <button class="user-dropdown-item" @click="goSettings">设置</button>
          </div>
          <button class="btn btn-outline" @click="logout">退出</button>
        </div>
      </div>
    </header>

    <div class="app-main">
      <aside class="sidebar">
        <h4 class="menu-title">核心功能</h4>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/dashboard') }" to="/app/dashboard">个人工作台</RouterLink>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/interview') }" to="/app/interview">模拟面试配置</RouterLink>

        <h4 class="menu-title">能力提升</h4>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/practice') }" to="/app/practice">专项练习</RouterLink>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/wrong-book') }" to="/app/wrong-book">错题本</RouterLink>

        <h4 class="menu-title">创新功能</h4>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/resume') }" to="/app/resume">简历优化</RouterLink>

        <h4 class="menu-title">学习与成长</h4>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/resources') }" to="/app/resources">学习资源</RouterLink>
        <RouterLink class="menu-item" :class="{ active: isActive('/app/growth-data') }" to="/app/growth-data">成长数据</RouterLink>
      </aside>

      <main class="content">
        <RouterView />
      </main>
    </div>

    <footer class="app-foot">
      <div class="container">
        <span>客服微信/QQ：FaceAI-Support</span>
        <span>© 2026 AI模拟面试平台</span>
        <button class="btn btn-text">意见反馈</button>
      </div>
    </footer>
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();
const showUserMenu = ref(false);
const userMenuWrapRef = ref<HTMLElement | null>(null);

function isActive(path: string) {
  return route.path === path;
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value;
}

function goProfile() {
  showUserMenu.value = false;
  router.push("/app/profile");
}

function goSettings() {
  showUserMenu.value = false;
  router.push("/app/settings");
}

function logout() {
  app.user.userId = null;
  showUserMenu.value = false;
  router.push("/");
}

function handleClickOutside(event: MouseEvent) {
  if (!showUserMenu.value || !userMenuWrapRef.value) return;
  const target = event.target as Node;
  if (!userMenuWrapRef.value.contains(target)) {
    showUserMenu.value = false;
  }
}

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>

