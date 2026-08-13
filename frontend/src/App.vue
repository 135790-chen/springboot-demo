<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/AppLayout.vue'

const auth = useAuthStore()

// 登录/注册弹窗
const loginVisible = ref(false)
const registerVisible = ref(false)
const loginForm = ref({ username: 'demo', password: '123456' })
const regForm = ref({ username: 'demo', password: '123456', inviteCode: '' })
const loginLoading = ref(false)
const regLoading = ref(false)

// 恢复会话
onMounted(() => {
  auth.restoreSession()
})

// 监听 401 事件（由 Axios 拦截器触发）
window.addEventListener('auth:unauthorized', () => {
  if (!loginVisible.value) {
    loginVisible.value = true
  }
})

async function handleLogin() {
  loginLoading.value = true
  try {
    const ok = await auth.login(loginForm.value.username, loginForm.value.password)
    if (ok) {
      loginVisible.value = false
      ElMessage.success('欢迎回来，' + (auth.userInfo?.username || ''))
    } else {
      ElMessage.error('登录失败')
    }
  } catch {
    ElMessage.error('登录失败，请检查网络')
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  if (!regForm.value.username || !regForm.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  regLoading.value = true
  try {
    const res = await auth.register(
      regForm.value.username,
      regForm.value.password,
      regForm.value.inviteCode || undefined
    )
    if (res.data.code === 200) {
      registerVisible.value = false
      ElMessage.success('注册成功，请登录')
    } else {
      ElMessage.error(res.data.message || '注册失败')
    }
  } catch {
    ElMessage.error('注册失败，请检查网络')
  } finally {
    regLoading.value = false
  }
}
</script>

<template>
  <AppLayout
    @open-login="loginVisible = true"
    @open-register="registerVisible = true"
  />

  <!-- 登录弹窗 -->
  <el-dialog v-model="loginVisible" title="🔐 登录" width="380px" :close-on-click-modal="false">
    <el-form label-position="top" @submit.prevent="handleLogin">
      <el-form-item label="用户名">
        <el-input v-model="loginForm.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="loginVisible = false">取消</el-button>
      <el-button type="primary" :loading="loginLoading" @click="handleLogin">登录</el-button>
    </template>
  </el-dialog>

  <!-- 注册弹窗 -->
  <el-dialog v-model="registerVisible" title="📝 注册" width="380px" :close-on-click-modal="false">
    <el-form label-position="top">
      <el-form-item label="用户名">
        <el-input v-model="regForm.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="regForm.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="管理员邀请码（选填）">
        <el-input v-model="regForm.inviteCode" placeholder="如有邀请码请填写" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="registerVisible = false">取消</el-button>
      <el-button type="success" :loading="regLoading" @click="handleRegister">注册</el-button>
    </template>
  </el-dialog>
</template>
