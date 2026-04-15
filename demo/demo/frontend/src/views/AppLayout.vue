<template>
  <section class="app-shell">
    <header class="app-head">
      <div class="container">
        <div class="logo">
          <img class="logo-image" src="/logo1.png" alt="FaceAI Logo" />
          <img class="logo-text-image" src="/logo2.png" alt="FaceAI Interview" />
        </div>
        <div class="app-user-bar" ref="userMenuWrapRef">
          <button class="btn btn-outline" @click="logout">退出</button>
        </div>
      </div>
    </header>

    <div class="app-main">
      <aside class="sidebar">
        <div class="sidebar-groups">
          <div v-for="g in menuGroups" :key="g.key" class="sidebar-group">
            <button
              type="button"
              class="sidebar-group-title"
              :class="{ expanded: isExpanded(g.key) }"
              @click="toggleGroup(g.key)"
            >
              <span class="sidebar-group-title-left">
                <span class="sidebar-group-icon" v-html="getGroupIconSvg(g.key)"></span>
                <span class="sidebar-group-title-text">{{ g.title }}</span>
              </span>
              <span class="sidebar-group-caret">{{ isExpanded(g.key) ? "▲" : "▼" }}</span>
            </button>

            <div v-show="isExpanded(g.key)" class="sidebar-group-items">
              <template v-for="it in g.items" :key="it.key">
                <RouterLink
                  v-if="it.type === 'link'"
                  class="sidebar-link"
                  :class="{ active: isActive(it.path) }"
                  :to="it.path"
                >
                  <span class="sidebar-link-inner">
                    <span class="sidebar-link-icon" v-html="getItemIconSvg(it.key)"></span>
                    <span>{{ it.label }}</span>
                  </span>
                </RouterLink>
                <button
                  v-else
                  type="button"
                  class="sidebar-link sidebar-link-btn"
                  @click="it.onClick()"
                >
                  <span class="sidebar-link-inner">
                    <span class="sidebar-link-icon" v-html="getItemIconSvg(it.key)"></span>
                    <span>{{ it.label }}</span>
                  </span>
                </button>
              </template>
            </div>
          </div>
        </div>

        <div class="sidebar-user">
          <img
            v-if="sidebarAvatarUrl"
            class="sidebar-user-avatar sidebar-user-avatar-img"
            :src="sidebarAvatarUrl"
            :title="app.user.name"
            alt="用户头像"
          />
          <div v-else class="sidebar-user-avatar" :title="app.user.name">{{ avatarText }}</div>
          <div class="sidebar-user-meta">
            <div class="sidebar-user-name">{{ app.user.name }}</div>
          </div>
          <button type="button" class="sidebar-user-link" @click="goProfile">个人信息</button>
        </div>
      </aside>

      <main class="content">
        <RouterView />
      </main>
    </div>

  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const route = useRoute();
const router = useRouter();
const showUserMenu = ref(false);
const userMenuWrapRef = ref<HTMLElement | null>(null);

const menuGroups = [
  {
    key: "combat",
    title: "面试实战",
    items: [
      { key: "dashboard", type: "link", label: "工作面板", path: "/app/dashboard" },
      { key: "interview", type: "link", label: "模拟面试", path: "/app/interview" },
      { key: "reports", type: "link", label: "面试复盘", path: "/app/interview-reports" }
    ]
  },
  {
    key: "improve",
    title: "能力提升",
    items: [
      { key: "practice", type: "link", label: "专项训练", path: "/app/practice" },
      { key: "wrong", type: "link", label: "错题档案", path: "/app/wrong-book" },
      { key: "resources", type: "link", label: "学习资源", path: "/app/resources" },
      { key: "growth", type: "link", label: "成长数据", path: "/app/growth-data" }
    ]
  },
  {
    key: "job",
    title: "求职工具",
    items: [
      { key: "resume", type: "link", label: "简历优化", path: "/app/resume" },
      // 目前项目没有单独的“跨岗推荐”页面，先指向学习资源页（工作台入口也是如此）
      { key: "postmatch", type: "link", label: "跨岗推荐", path: "/app/post-match" }
    ]
  },
  {
    key: "me",
    title: "个人中心",
    items: [
      { key: "profile", type: "link", label: "个人资料", path: "/app/profile" },
      { key: "settings", type: "link", label: "系统设置", path: "/app/settings" },
      { key: "logout", type: "action", label: "安全登出", onClick: logout }
    ]
  }
] as const;

const expandedGroups = ref<Record<string, boolean>>({});
const sidebarAvatarUrl = ref("");
const avatarText = computed(() => {
  const raw = String(app.user?.name || "").trim();
  if (!raw) return "我";
  const first = raw[0] || "我";
  // 英文名取首字母大写；中文直接取首字
  return /[a-zA-Z]/.test(first) ? first.toUpperCase() : first;
});

function syncSidebarAvatar() {
  const uid = Number(app.uid || 0);
  if (!uid) {
    sidebarAvatarUrl.value = "";
    return;
  }
  sidebarAvatarUrl.value = localStorage.getItem(`faceai_avatar_${uid}`) || "";
}

function isActive(path: string) {
  return route.path === path;
}

function findGroupKeyByPath(path: string) {
  for (const g of menuGroups) {
    if (g.items.some((it: any) => it.type === "link" && it.path === path)) return g.key;
  }
  return null;
}

function toggleGroup(key: string) {
  expandedGroups.value[key] = !expandedGroups.value[key];
}

function isExpanded(key: string) {
  return expandedGroups.value[key] === true;
}

function getGroupIconSvg(key: string) {
  const common = `fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"`;
  switch (key) {
    case "combat":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M3 21h8" /><path d="m5 19 9.8-9.8a2.2 2.2 0 0 0-3.1-3.1L1.9 15.9a3 3 0 0 0-.8 1.4L0.6 21H4" /></svg>`;
    case "improve":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M9 3H5a2 2 0 0 0-2 2v4" /><path d="M15 3h4a2 2 0 0 1 2 2v4" /><path d="M21 15v4a2 2 0 0 1-2 2h-4" /><path d="M3 15v4a2 2 0 0 0 2 2h4" /><path d="M8 8h8v8H8z" /></svg>`;
    case "job":
      return `<svg viewBox="0 0 24 24" ${common}><rect x="3" y="7" width="18" height="12" rx="2" /><path d="M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2" /><path d="M3 12h18" /><path d="M10 12v2h4v-2" /></svg>`;
    case "me":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M12 12a4 4 0 1 0-4-4 4 4 0 0 0 4 4Z" /><path d="M4 21a8 8 0 0 1 16 0" /></svg>`;
    default:
      return `<svg viewBox="0 0 24 24" ${common}><path d="M12 2l9 5-9 5-9-5 9-5Z" /><path d="M3 12l9 5 9-5" /><path d="M3 17l9 5 9-5" /></svg>`;
  }
}

function getItemIconSvg(key: string) {
  const common = `fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"`;
  switch (key) {
    case "dashboard":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M3 11.5 12 4l9 7.5" /><path d="M5 10.5V20h14v-9.5" /></svg>`;
    case "interview":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M8 5a4 4 0 0 0 0 8h2l6 6v-6h1a4 4 0 0 0 0-8H8z" /></svg>`;
    case "reports":
      return `<svg viewBox="0 0 24 24" ${common}><rect x="3" y="5" width="18" height="14" rx="2" /><path d="M3 10h18" /><path d="M7 14h4" /></svg>`;
    case "practice":
      return `<svg viewBox="0 0 24 24" ${common}><rect x="5" y="4" width="14" height="16" rx="2" /><path d="M9 8h6" /><path d="M9 12h6" /><path d="M9 16h4" /></svg>`;
    case "wrong":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M9 3h10a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H9" /><path d="M9 7h8" /><path d="M9 11h8" /><path d="M9 15h6" /><path d="m3 10 2 2 4-4" /></svg>`;
    case "resources":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M4 5a3 3 0 0 1 3-1h4a4 4 0 0 1 4 4v12a4 4 0 0 0-4-4H7a3 3 0 0 0-3 1V5z" /><path d="M20 5a3 3 0 0 0-3-1h-4a4 4 0 0 0-4 4v12a4 4 0 0 1 4-4h4a3 3 0 0 1 3 1V5z" /></svg>`;
    case "growth":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M9 18h6" /><path d="M10 22h4" /><path d="M12 2a7 7 0 0 0-4 12.8c.9.6 1.5 1.5 1.7 2.5h4.6c.2-1 .8-1.9 1.7-2.5A7 7 0 0 0 12 2z" /></svg>`;
    case "resume":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M7 3h8l4 4v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" /><path d="M15 3v4h4" /><path d="M9 13h6" /><path d="M9 17h4" /></svg>`;
    case "postmatch":
      return `<svg viewBox="0 0 24 24" ${common}><circle cx="12" cy="12" r="9" /><path d="m15 9-5 2-2 5 5-2 2-5z" /><path d="M12 12l3-3" /></svg>`;
    case "profile":
      return `<svg viewBox="0 0 24 24" ${common}><rect x="3" y="4" width="18" height="16" rx="3" /><circle cx="9" cy="10" r="2.2" /><path d="M6.8 15a3.4 3.4 0 0 1 4.4 0" /><path d="M14 9h4" /><path d="M14 13h4" /></svg>`;
    case "settings":
      return `<svg viewBox="0 0 24 24" ${common}><circle cx="12" cy="12" r="3.2" /><path d="M19.4 15a1 1 0 0 0 .2 1.1l.1.1a1.8 1.8 0 1 1-2.5 2.5l-.1-.1a1 1 0 0 0-1.1-.2 1 1 0 0 0-.6.9V20a1.8 1.8 0 1 1-3.6 0v-.2a1 1 0 0 0-.6-.9 1 1 0 0 0-1.1.2l-.1.1a1.8 1.8 0 1 1-2.5-2.5l.1-.1a1 1 0 0 0 .2-1.1 1 1 0 0 0-.9-.6H4a1.8 1.8 0 1 1 0-3.6h.2a1 1 0 0 0 .9-.6 1 1 0 0 0-.2-1.1l-.1-.1a1.8 1.8 0 1 1 2.5-2.5l.1.1a1 1 0 0 0 1.1.2h0a1 1 0 0 0 .6-.9V4a1.8 1.8 0 1 1 3.6 0v.2a1 1 0 0 0 .6.9h0a1 1 0 0 0 1.1-.2l.1-.1a1.8 1.8 0 1 1 2.5 2.5l-.1.1a1 1 0 0 0-.2 1.1v0a1 1 0 0 0 .9.6H20a1.8 1.8 0 1 1 0 3.6h-.2a1 1 0 0 0-.9.6z" /></svg>`;
    case "logout":
      return `<svg viewBox="0 0 24 24" ${common}><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" /><path d="M10 17l5-5-5-5" /><path d="M15 12H3" /></svg>`;
    default:
      return `<svg viewBox="0 0 24 24" ${common}><circle cx="12" cy="12" r="8" /></svg>`;
  }
}

watch(
  () => route.path,
  (p) => {
    const activeGroupKey = findGroupKeyByPath(p);
    if (!activeGroupKey) return;
    // 路由变化时，只保证当前所在组是展开的，不主动收起其它已展开的组
    expandedGroups.value[activeGroupKey] = true;
  },
  { immediate: true }
);

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
  app.logout();
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
  syncSidebarAvatar();
  document.addEventListener("click", handleClickOutside);
  window.addEventListener("storage", syncSidebarAvatar);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
  window.removeEventListener("storage", syncSidebarAvatar);
});
watch(
  () => app.uid,
  () => syncSidebarAvatar(),
  { immediate: true }
);
watch(
  () => route.fullPath,
  () => syncSidebarAvatar()
);
</script>

