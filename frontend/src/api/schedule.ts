import http from './request'
import type { Result, PageResult, ScheduleEntry, TimeSlot } from '@/types/models'

export function getTimeSlots() {
  return http.get<Result<TimeSlot[]>>('/api/edu/timeslot/all')
}

export function generateSchedule(semester: string, majorId?: number) {
  const params: Record<string, string | number> = { semester }
  if (majorId) params.majorId = majorId
  return http.post<Result<{ success: boolean; scheduledCount: number; failedCount: number; failedCourses: string[]; message: string }>>('/api/edu/schedule/generate', null, { params })
}

export function clearSchedule(semester: string) {
  return http.delete<Result<string>>('/api/edu/schedule/clear', { params: { semester } })
}

export function getSchedulePage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<ScheduleEntry>>>('/api/edu/schedule/page', { params })
}

export function getTeacherSchedule(teacherId: number, semester: string) {
  return http.get<Result<ScheduleEntry[]>>(`/api/edu/schedule/teacher/${teacherId}`, { params: { semester } })
}

export function getClassroomSchedule(classroomId: number, semester: string) {
  return http.get<Result<ScheduleEntry[]>>(`/api/edu/schedule/classroom/${classroomId}`, { params: { semester } })
}

export function getClassSchedule(clazzId: number, semester: string) {
  return http.get<Result<ScheduleEntry[]>>(`/api/edu/schedule/class/${clazzId}`, { params: { semester } })
}
