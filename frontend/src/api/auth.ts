import http from './request'
import type { Result, LoginResult, UserInfo } from '@/types/models'

/** 登录 */
export function login(username: string, password: string) {
  return http.post<Result<LoginResult>>('/auth/login', { username, password })
}

/** 注册 */
export function register(username: string, password: string, inviteCode?: string) {
  return http.post<Result<null>>('/auth/register', { username, password, inviteCode })
}

/** 登出 */
export function logout() {
  return http.post<Result<null>>('/auth/logout')
}

/** 获取当前用户信息 */
export function getMyInfo() {
  return http.get<Result<UserInfo>>('/auth/me')
}

/** 注销账号 */
export function deleteAccount() {
  return http.delete<Result<null>>('/auth/account')
}
