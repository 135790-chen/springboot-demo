import http from './request'
import type { Result, PageResult, ClassInfo } from '@/types/models'

export function getClassPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<ClassInfo>>>('/api/edu/class/page', { params })
}

export function addClass(data: Partial<ClassInfo>) {
  return http.post<Result<ClassInfo>>('/api/edu/class', data)
}

export function updateClass(data: Partial<ClassInfo>) {
  return http.put<Result<ClassInfo>>('/api/edu/class', data)
}

export function deleteClass(id: number) {
  return http.delete<Result<null>>(`/api/edu/class/${id}`)
}
