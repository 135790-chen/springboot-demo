<script setup lang="ts">
defineProps<{
  icon: string
  iconBg: string  // e.g. '#e6f7ff'
  value: string | number
  label: string
}>()
</script>

<template>
  <div class="stat-card">
    <div class="stat-icon" :style="{ background: iconBg }">
      {{ icon }}
    </div>
    <div class="stat-info">
      <div class="stat-num">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
    </div>
  </div>
</template>

<style scoped>
.stat-card {
  background: var(--surface);
  border-radius: var(--radius);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--border);
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}
.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
  line-height: 1.1;
}
.stat-label {
  font-size: 13px;
  color: var(--text3);
  margin-top: 2px;
}
</style>
