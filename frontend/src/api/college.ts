import http from './request'
import type { Result, PageResult, College, Major } from '@/types/models'

export function getCollegePage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<College>>>('/api/edu/college/page', { params })
}

export function addCollege(data: Partial<College>) {
  return http.post<Result<College>>('/api/edu/college', data)
}

export function updateCollege(data: Partial<College>) {
  return http.put<Result<College>>('/api/edu/college', data)
}

export function deleteCollege(id: number) {
  return http.delete<Result<null>>(`/api/edu/college/${id}`)
}

export function getCollegeMajors(collegeId: number) {
  return http.get<Result<Major[]>>(`/api/edu/college/${collegeId}/majors`)
}
