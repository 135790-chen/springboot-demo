import http from './request'
import type { Result, GraduationResult } from '@/types/models'

/** 毕业审核 */
export function checkGraduation(studentId: number) {
  return http.get<Result<GraduationResult>>(`/api/edu/graduation/check/${studentId}`)
}

/** 历史审核记录 */
export function getGraduationHistory(studentId: number) {
  return http.get<Result<any[]>>(`/api/edu/graduation/results/${studentId}`)
}
