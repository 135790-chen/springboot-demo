package com.example.demo.message.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KafkaProducerServiceImpl 单元测试
 * <p>
 * 注：KafkaTemplate 在 Java 25 下无法 Mockito mock，本测试仅覆盖 Kafka 不可用时的降级路径。
 * Kafka 可用时的发送逻辑由集成测试覆盖。
 */
class KafkaProducerServiceImplTest {

    // ── isKafkaAvailable ──

    @Test
    void isKafkaAvailable_withoutKafka_returnsFalse() {
        KafkaProducerServiceImpl service = new KafkaProducerServiceImpl();
        // kafkaTemplate 为 null（未注入）→ 不可用
        assertFalse(service.isKafkaAvailable());
    }

    // ── send（Kafka 不可用，降级到 simulateMode） ──

    @Test
    void send_kafkaUnavailable_doesNotThrow() {
        KafkaProducerServiceImpl service = new KafkaProducerServiceImpl();

        // Kafka 不可用时不应抛异常，仅日志输出
        assertDoesNotThrow(() -> service.send("test-topic", "hello kafka"));
        assertDoesNotThrow(() -> service.send("another-topic", ""));
        assertDoesNotThrow(() -> service.send("long-message-topic",
                "this is a very long message that exceeds fifty characters to test the truncation logic"));
    }

    // ── 覆盖构造函数 ──

    @Test
    void newInstance_defaultState_isNotAvailable() {
        KafkaProducerServiceImpl service = new KafkaProducerServiceImpl();
        assertNotNull(service);
        assertFalse(service.isKafkaAvailable());
    }
}