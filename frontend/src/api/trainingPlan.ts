import http from './request'
import type { Result, PageResult, TrainingPlan } from '@/types/models'

export function getTrainingPlanPage(params: Record<string, string | number | undefined>) {
  return http.get<Result<PageResult<TrainingPlan>>>('/api/edu/training-plan/page', { params })
}

export function addTrainingPlan(data: Partial<TrainingPlan>) {
  return http.post<Result<TrainingPlan>>('/api/edu/training-plan', data)
}

export function updateTrainingPlan(data: Partial<TrainingPlan>) {
  return http.put<Result<TrainingPlan>>('/api/edu/training-plan', data)
}

export function deleteTrainingPlan(id: number) {
  return http.delete<Result<null>>(`/api/edu/training-plan/${id}`)
}

export function getPlanCourses(planId: number) {
  return http.get<Result<any[]>>(`/api/edu/training-plan/${planId}/courses`)
}

export function getPlanCoursesBySemester(planId: number) {
  return http.get<Result<{ groups: Record<string, any[]>; total: number }>>(`/api/edu/training-plan/${planId}/courses-by-semester`)
}
