<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const infoResult = ref('')
const loading = ref(false)

async function loadMyInfo() {
  loading.value = true
  try {
    await auth.fetchUserInfo()
    infoResult.value = auth.userInfo ? JSON.stringify(auth.userInfo, null, 2) : '未获取到用户信息'
  } catch {
    infoResult.value = '获取用户信息失败'
  } finally {
    loading.value = false
  }
}

async function handleDeleteAccount() {
  try {
    await ElMessageBox.confirm('确定要注销账号吗？此操作不可撤销！', '确认注销', { type: 'warning', confirmButtonText: '确认注销', cancelButtonText: '取消' })
    const ok = await auth.deleteAccount()
    if (ok) {
      ElMessage.success('账号已注销')
      window.location.reload()
    }
  } catch { /* cancelled */ }
}

onMounted(() => loadMyInfo())
</script>

<template>
  <div class="page">
    <div class="card">
      <div class="card-title">👤 我的信息</div>
      <div style="margin-top:12px;display:flex;gap:8px">
        <el-button type="primary" size="small" :loading="loading" @click="loadMyInfo">刷新信息</el-button>
        <el-button type="danger" size="small" @click="handleDeleteAccount">注销账号</el-button>
      </div>
      <div v-if="infoResult" class="result-box" style="margin-top:12px">
        <div style="margin-bottom:8px"><strong>用户名：</strong>{{ auth.userInfo?.username || '-' }}</div>
        <div style="margin-bottom:8px"><strong>角色：</strong>{{ auth.isAdmin ? '管理员' : '学生' }}</div>
        <div style="margin-bottom:8px"><strong>用户ID：</strong>{{ auth.userInfo?.userId ?? '-' }}</div>
        <div v-if="auth.userInfo?.studentId"><strong>学生ID：</strong>{{ auth.userInfo?.studentId }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card { background: var(--surface); border-radius: var(--radius); padding: 20px 22px; margin-bottom: 14px; box-shadow: 0 0 0 1px rgba(0,0,0,.03), 0 2px 6px rgba(0,0,0,.04); border: 1px solid var(--border); }
.card-title { font-size: 14px; font-weight: 620; color: var(--text); display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.result-box { background: #f9f9fb; padding: 14px; border-radius: 7px; font-size: 13px; border: 1px solid var(--border); }
</style>
