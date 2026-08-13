import http from './request'
import type { Result } from '@/types/models'

/** 发送 Kafka 消息 */
export function sendToKafka(data: {
  studentName: string
  birthday?: string | null
  grade: string
  email?: string
  phone?: string
}) {
  return http.post<Result<any>>('/kafka/receive-student', data)
}

/** 检查 Kafka 状态 */
export function checkKafkaStatus() {
  return http.get<Result<any>>('/kafka/status')
}
