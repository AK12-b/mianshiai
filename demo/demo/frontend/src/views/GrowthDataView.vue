<template>
  <section class="card">
    <div class="dashboard-section-title">
      <h3 style="margin-top: 0">成长数据</h3>
      <button class="btn btn-outline" @click="loadGrowth">刷新数据</button>
    </div>
    <pre class="api-output">{{ output }}</pre>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useAppStore } from "../stores/app";

const app = useAppStore();
const output = ref("");

async function loadGrowth() {
  const data = await app.apiRequest(`/api/ability-growth/user/${app.uid}`);
  output.value = app.pretty(data);
}

onMounted(() => {
  loadGrowth();
});
</script>

