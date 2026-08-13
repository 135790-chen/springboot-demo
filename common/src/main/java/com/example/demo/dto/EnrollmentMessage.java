package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka 选课消息 —— Redis 预扣成功后，异步写入数据库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentMessage {
    private String requestId;
    private Long studentId;
    private Long courseId;
    private Long timestamp;
}
