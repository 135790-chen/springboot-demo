<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const route = useRoute()
const auth = useAuthStore()

const currentTitle = computed(() => (route.meta.title as string) || '首页')
const isAdmin = computed(() => auth.isAdmin)
const username = computed(() => auth.userInfo?.username || '')

async function handleLogout() {
  await auth.logout()
  window.location.reload()
}
</script>

<template>
  <header class="top-header">
    <div class="breadcrumb">
      <span>首页</span> &gt; <span class="current">{{ currentTitle }}</span>
    </div>
    <div class="header-right">
      <!-- 未登录 -->
      <template v-if="!auth.isLoggedIn">
        <el-button size="small" @click="$emit('openLogin')">登录</el-button>
        <el-button size="small" type="success" @click="$emit('openRegister')">注册</el-button>
      </template>
      <!-- 已登录 -->
      <template v-else>
        <span class="user-name">{{ username }}</span>
        <span class="role-tag" :class="isAdmin ? 'role-admin' : 'role-student'">
          {{ isAdmin ? '管理员' : '学生' }}
        </span>
        <a href="http://localhost:9411" target="_blank" class="link-btn">🔗 链路追踪</a>
        <el-button size="small" type="danger" @click="handleLogout">退出登录</el-button>
      </template>
    </div>
  </header>
</template>

<style scoped>
.top-header {
  height: 50px;
  min-height: 50px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(16px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid var(--border);
}
.breadcrumb { font-size: 13px; color: var(--text2); }
.breadcrumb .current { color: var(--text); font-weight: 520; }
.header-right { display: flex; align-items: center; gap: 10px; }
.user-name { font-size: 13px; color: var(--text); font-weight: 520; }
.role-tag {
  display: inline-block;
  padding: 1px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}
.role-admin { background: #fde8e8; color: #c53030; }
.role-student { background: #d4f5e9; color: #1f7c52; }
.link-btn { color: var(--accent); text-decoration: none; font-size: 13px; font-weight: 500; }
.link-btn:hover { color: var(--accent-hover); }
</style>
