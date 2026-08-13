import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '@/api/auth'
import type { UserInfo } from '@/types/models'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  /** 解析 JWT 获取用户信息 */
  function parseJwt(t: string): UserInfo | null {
    try {
      const payload = JSON.parse(atob(t.split('.')[1]))
      return {
        userId: payload.userId,
        username: payload.username,
        role: payload.role || 'student',
        studentId: payload.studentId
      }
    } catch {
      return null
    }
  }

  /** 登录 */
  async function login(username: string, password: string): Promise<boolean> {
    const res = await authApi.login(username, password)
    if (res.data.code === 200 && res.data.data?.token) {
      token.value = res.data.data.token
      localStorage.setItem('token', token.value)
      userInfo.value = parseJwt(token.value)
      return true
    }
    return false
  }

  /** 登出 */
  async function logout(): Promise<void> {
    try {
      await authApi.logout()
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  /** 注册 */
  async function register(username: string, password: string, inviteCode?: string) {
    return authApi.register(username, password, inviteCode)
  }

  /** 从 localStorage 恢复会话 */
  function restoreSession() {
    if (token.value) {
      userInfo.value = parseJwt(token.value)
    }
  }

  /** 获取用户信息 */
  async function fetchUserInfo() {
    const res = await authApi.getMyInfo()
    if (res.data.code === 200) {
      userInfo.value = res.data.data
    }
  }

  /** 注销账号 */
  async function deleteAccount(): Promise<boolean> {
    const res = await authApi.deleteAccount()
    if (res.data.code === 200) {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      return true
    }
    return false
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    register,
    restoreSession,
    fetchUserInfo,
    deleteAccount
  }
})
