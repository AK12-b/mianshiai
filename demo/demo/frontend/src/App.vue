<template>
  <main>
    <div
      v-if="app.toast"
      style="position: fixed; top: 14px; left: 50%; transform: translateX(-50%); z-index: 9999; background: #11395f; color: #fff; padding: 10px 16px; border-radius: 10px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2)"
    >
      {{ app.toast }}
    </div>
    <RouterView />
  </main>
</template>

<script setup lang="ts">
import { useAppStore } from "./stores/app";

const app = useAppStore();
</script>

