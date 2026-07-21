package com.example.demo.message.service.impl;

import com.example.demo.message.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(String topic, String message) {
        if (kafkaTemplate == null) {
            simulateMode(topic, message);
            return;
        }
        try {
            log.info("[Kafka-Producer] 📨 发送消息 → Topic: {}", topic);
            kafkaTemplate.send(topic, message);
            log.info("[Kafka-Producer] ✅ 消息发送成功");
        } catch (Exception e) {
            log.warn("[Kafka-Producer] ⚠️ 发送失败 ({}), 降级为模拟模式", e.getMessage());
            simulateMode(topic, message);
        }
    }

    @Override
    public boolean isKafkaAvailable() {
        return kafkaTemplate != null;
    }

    private void simulateMode(String topic, String message) {
        String preview = message.length() > 50 ? message.substring(0, 47) + "..." : message;
        log.info("┌──────────────────────────────────────────┐");
        log.info("│ 📨 [Kafka-模拟] 消息已生产               │");
        log.info("│ Topic : {:<32} │", topic);
        log.info("│ 内容  : {:<32} │", preview);
        log.info("│ 说明  : Kafka 未连接，消息输出到日志     │");
        log.info("└──────────────────────────────────────────┘");
    }
}
