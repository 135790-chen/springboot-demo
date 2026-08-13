package com.example.demo.message.consumer;

import com.example.demo.dto.EnrollmentMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnBean(KafkaTemplate.class)
public class EnrollmentConsumer {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentConsumer.class);

    private static final String TEACHING_SERVICE = "http://teaching-service";
    private static final String CONFIRM_PATH = "/api/edu/student-course/confirm";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "enrollment-topic", groupId = "enrollment-group")
    public void onEnrollment(String messageJson) {
        log.info("══════════════════════════════════════════");
        log.info("[Kafka-Consumer] 收到选课确认消息");
        log.info("[Kafka-Consumer] Topic : enrollment-topic");
        log.info("[Kafka-Consumer] 内容  : {}", messageJson);

        try {
            EnrollmentMessage msg = objectMapper.readValue(messageJson, EnrollmentMessage.class);

            String url = TEACHING_SERVICE + CONFIRM_PATH;
            log.info("[Kafka-Consumer] 第2步：回调学生服务确认选课 → {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EnrollmentMessage> entity = new HttpEntity<>(msg, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[Kafka-Consumer] ✅ 选课确认成功 studentId={} courseId={}",
                        msg.getStudentId(), msg.getCourseId());
            } else {
                log.error("[Kafka-Consumer] ❌ 选课确认失败 HTTP {} body={}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("[Kafka-Consumer] ❌ 处理选课消息异常: {}", e.getMessage(), e);
        }
        log.info("══════════════════════════════════════════");
    }
}
