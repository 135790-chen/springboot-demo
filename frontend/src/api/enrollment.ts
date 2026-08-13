import http from './request'
import type { Result, PageResult, Enrollment, SeckillCourse } from '@/types/models'

/** 秒杀课程列表 */
export function getSeckillList() {
  return http.get<Result<SeckillCourse[]>>('/api/edu/student-course/seckill/list')
}

/** 执行秒杀 */
export function doSeckill(courseId: number) {
  return http.post<Result<Enrollment>>('/api/edu/student-course/seckill', { courseId })
}

/** 管理员手动选课 */
export function doEnroll(studentId: number, courseId: number) {
  return http.post<Result<Enrollment>>('/api/edu/student-course', { studentId, courseId })
}

/** 退课 */
export function doDrop(relId: number) {
  return http.delete<Result<null>>(`/api/edu/student-course/${relId}`)
}

/** 录入成绩 */
export function doScore(relId: number, score: number) {
  return http.put<Result<Enrollment>>(`/api/edu/student-course/${relId}/score`, { score })
}
