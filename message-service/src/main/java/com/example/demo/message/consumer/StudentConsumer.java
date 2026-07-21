package com.example.demo.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
public class StudentConsumer {

    private static final Logger log = LoggerFactory.getLogger(StudentConsumer.class);

    @KafkaListener(topics = "student-topic", groupId = "student-group")
    public void consume(String message) {
        log.info("══════════════════════════════════════════");
        log.info("[Kafka-Consumer] 收到学生数据消息");
        log.info("[Kafka-Consumer] Topic : student-topic");
        log.info("[Kafka-Consumer] 内容  : {}", message);
        log.info("[Kafka-Consumer] 模拟处理：已保存到下游系统（ES/数据仓库/通知服务）");
        log.info("══════════════════════════════════════════");
    }
}
