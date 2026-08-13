<script setup lang="ts">
import SidebarMenu from './SidebarMenu.vue'
import TopHeader from './TopHeader.vue'

const emit = defineEmits<{
  'openLogin': []
  'openRegister': []
}>()
</script>

<template>
  <div class="app-layout">
    <SidebarMenu />
    <div class="main-area">
      <TopHeader
        @open-login="emit('openLogin')"
        @open-register="emit('openRegister')"
      />
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 22px 24px;
}
</style>
