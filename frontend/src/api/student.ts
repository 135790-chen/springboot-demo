import http from './request'
import type { Result, PageResult, Student, Enrollment } from '@/types/models'

/** 学生分页查询 */
export function getStudentPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<Student>>>('/api/edu/student/page', { params })
}

/** 新增学生 */
export function addStudent(data: Partial<Student>) {
  return http.post<Result<Student>>('/api/edu/student', data)
}

/** 更新学生 */
export function updateStudent(data: Partial<Student>) {
  return http.put<Result<Student>>('/api/edu/student', data)
}

/** 删除学生（逻辑删除） */
export function deleteStudent(id: number) {
  return http.delete<Result<null>>(`/api/edu/student/${id}`)
}

/** 查询学生的选课列表 */
export function getStudentCourses(studentId: number, params: Record<string, number | undefined>) {
  return http.get<Result<PageResult<Enrollment>>>(`/api/edu/student-course/student/${studentId}`, { params })
}
