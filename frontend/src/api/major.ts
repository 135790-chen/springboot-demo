import http from './request'
import type { Result, PageResult, Major } from '@/types/models'

export function getMajorPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Major>>>('/api/edu/major/page', { params })
}

export function addMajor(data: Partial<Major>) {
  return http.post<Result<Major>>('/api/edu/major', data)
}

export function updateMajor(data: Partial<Major>) {
  return http.put<Result<Major>>('/api/edu/major', data)
}

export function deleteMajor(id: number) {
  return http.delete<Result<null>>(`/api/edu/major/${id}`)
}
