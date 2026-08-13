import http from './request'
import type { Result, StatSnapshot } from '@/types/models'

/** 最新统计快照 */
export function getLatestStat() {
  return http.get<Result<StatSnapshot>>('/api/edu/stat/latest')
}

/** 近期统计趋势 */
export function getRecentStats() {
  return http.get<Result<StatSnapshot[]>>('/api/edu/stat/recent')
}
