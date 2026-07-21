package com.example.demo.message.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

@Configuration
@ConditionalOnProperty(name = "kafka.embedded.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddedKafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedKafkaConfig.class);

    @Bean
    public EmbeddedKafkaKraftBroker embeddedKafkaBroker() {
        log.info("[EmbeddedKafka] 正在启动内嵌 Kafka Broker...");
        try {
            EmbeddedKafkaKraftBroker broker = new EmbeddedKafkaKraftBroker(1, 9092);
            broker.afterPropertiesSet();
            log.info("[EmbeddedKafka] 内嵌 Kafka Broker 启动成功！端口: localhost:9092 | KRaft 模式");
            log.info("[EmbeddedKafka] 完整消息链路已就绪: Producer → Topic → Consumer");
            return broker;
        } catch (Exception e) {
            log.warn("[EmbeddedKafka] 内嵌 Kafka 启动失败: {}", e.getMessage());
            log.warn("[EmbeddedKafka] 应用正常启动，Kafka 功能暂时不可用");
            return null;
        }
    }
}
