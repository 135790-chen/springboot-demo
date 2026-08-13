import http from './request'
import type { Result, PageResult, Course, Student } from '@/types/models'

export function getCoursePage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Course>>>('/api/edu/course/page', { params })
}

export function addCourse(data: Partial<Course>) {
  return http.post<Result<Course>>('/api/edu/course', data)
}

export function updateCourse(data: Partial<Course>) {
  return http.put<Result<Course>>('/api/edu/course', data)
}

export function deleteCourse(id: number) {
  return http.delete<Result<null>>(`/api/edu/course/${id}`)
}

export function getCourseStudents(courseId: number, params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Student>>>(`/api/edu/course/${courseId}/students`, { params })
}
