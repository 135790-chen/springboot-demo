/** 统一响应格式 */
export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页响应 */
export interface PageResult<T> {
  page: number
  size: number
  total: number
  records: T[]
}
