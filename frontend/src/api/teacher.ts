import http from './request'
import type { Result, PageResult, Teacher } from '@/types/models'

export function getTeacherPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Teacher>>>('/api/edu/teacher/page', { params })
}

export function addTeacher(data: Partial<Teacher>) {
  return http.post<Result<Teacher>>('/api/edu/teacher', data)
}

export function updateTeacher(data: Partial<Teacher>) {
  return http.put<Result<Teacher>>('/api/edu/teacher', data)
}

export function deleteTeacher(id: number) {
  return http.delete<Result<null>>(`/api/edu/teacher/${id}`)
}
