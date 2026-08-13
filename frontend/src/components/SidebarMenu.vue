<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

function navigate(path: string) {
  router.push(path)
}

interface MenuGroup {
  title: string
  items: MenuItem[]
}

interface MenuItem {
  path: string
  icon: string
  label: string
  adminOnly?: boolean
  visible?: boolean
}

const menuGroups = computed<MenuGroup[]>(() => [
  {
    title: '概览',
    items: [
      { path: '/dashboard', icon: '📊', label: '数据看板' }
    ]
  },
  {
    title: '业务管理',
    items: [
      { path: '/students', icon: '🎓', label: '学生管理' },
      { path: '/classes', icon: '🏫', label: '班级管理' },
      { path: '/teachers', icon: '👨‍🏫', label: '教师管理' },
      { path: '/courses', icon: '📖', label: '课程管理' },
      { path: '/enrollments', icon: '📝', label: '选课管理' }
    ]
  },
  {
    title: '教务管理',
    items: [
      { path: '/colleges', icon: '🏛️', label: '学院管理' },
      { path: '/majors', icon: '📚', label: '专业管理' },
      { path: '/training-plans', icon: '📋', label: '培养方案' },
      { path: '/graduation', icon: '🎓', label: '毕业审核' },
      { path: '/classrooms', icon: '🏫', label: '教室管理' },
      { path: '/schedule', icon: '🗓️', label: '排课管理' }
    ]
  },
  {
    title: '系统工具',
    items: [
      { path: '/kafka', icon: '📨', label: 'Kafka', adminOnly: true },
      { path: '/profile', icon: '👤', label: '我的信息' }
    ]
  }
])

function isVisible(item: MenuItem): boolean {
  if (item.adminOnly && !auth.isAdmin) return false
  return true
}
</script>

<template>
  <aside class="sidebar">
    <div class="sidebar-logo">
      <span class="logo-icon">📚</span>
      <span class="logo-text">智慧教务平台</span>
    </div>
    <nav class="sidebar-menu">
      <div v-for="group in menuGroups" :key="group.title" class="menu-group">
        <div class="menu-group-title">{{ group.title }}</div>
        <div
          v-for="item in group.items"
          :key="item.path"
          v-show="isVisible(item)"
          class="menu-item"
          :class="{ active: activeMenu === item.path }"
          @click="navigate(item.path)"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 212px;
  min-width: 212px;
  background: var(--surface);
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--border);
  height: 100vh;
}
.sidebar-logo {
  padding: 18px 18px 14px;
  display: flex;
  align-items: center;
  gap: 9px;
  border-bottom: 1px solid var(--border);
}
.logo-icon { font-size: 22px; }
.logo-text { font-size: 15px; font-weight: 700; white-space: nowrap; color: var(--accent); }
.sidebar-menu { flex: 1; padding: 10px 0; overflow-y: auto; }
.menu-group { margin-bottom: 2px; }
.menu-group-title {
  padding: 14px 16px 4px;
  font-size: 10px;
  color: var(--text3);
  text-transform: uppercase;
  letter-spacing: 0.6px;
  font-weight: 700;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px 12px;
  margin: 0 8px;
  cursor: pointer;
  transition: all 0.12s;
  font-size: 13px;
  color: var(--text2);
  border-radius: 7px;
  font-weight: 480;
}
.menu-item:hover { background: #f2f2f6; color: var(--text); }
.menu-item.active { background: #eeeffc; color: var(--accent); font-weight: 580; }
.menu-icon { font-size: 14px; width: 18px; text-align: center; }
</style>
