import http from './request'
import type { Result, PageResult, Classroom } from '@/types/models'

export function getClassroomPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Classroom>>>('/api/edu/classroom/page', { params })
}

export function getClassroomById(id: number) {
  return http.get<Result<Classroom>>(`/api/edu/classroom/${id}`)
}

export function addClassroom(data: Partial<Classroom>) {
  return http.post<Result<Classroom>>('/api/edu/classroom', data)
}

export function updateClassroom(data: Partial<Classroom>) {
  return http.put<Result<Classroom>>('/api/edu/classroom', data)
}

export function deleteClassroom(id: number) {
  return http.delete<Result<null>>(`/api/edu/classroom/${id}`)
}
