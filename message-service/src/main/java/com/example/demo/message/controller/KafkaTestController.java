package com.example.demo.message.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Student;
import com.example.demo.message.service.KafkaProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Kafka 消息", description = "模拟外部系统推送学生数据，消息队列异步处理")
@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    @Configuration
    public static class RestTemplateConfig {
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) { return false; }
                @Override
                public void handleError(ClientHttpResponse response) {}
            });
            return restTemplate;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(KafkaTestController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String STUDENT_SERVICE = "http://student-service";

    @Operation(summary = "接收学生数据", description = "通过 Nacos 发现学生服务存库，再发 Kafka 消息")
    @PostMapping("/receive-student")
    public Result<?> receiveStudent(@Valid @RequestBody Student student, HttpServletRequest request) throws JsonProcessingException {
        try {
            String url = STUDENT_SERVICE + "/students";
            log.info("[Kafka-Flow] 第1步：通过 Nacos 调用学生服务 → {}", url);

            // 透传请求里的 Authorization header，避免 student-service 的认证拦截
            HttpHeaders headers = new HttpHeaders();
            String auth = request.getHeader("Authorization");
            if (auth != null) {
                headers.set("Authorization", auth);
            }
            HttpEntity<Student> entity = new HttpEntity<>(student, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("[Kafka-Flow] 学生服务返回: HTTP {}", response.getStatusCode());

            // 如果学生服务返回非 2xx（如重复提交），直接返回错误，不再发送 Kafka
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("[Kafka-Flow] 学生服务拒绝请求，中止 Kafka 发送: {}", response.getBody());
                return Result.error(response.getBody());
            }
        } catch (Exception e) {
            log.warn("[Kafka-Flow] 调用学生服务失败: {}", e.getMessage());
            return Result.error("学生服务不可用: " + e.getMessage());
        }

        String json = objectMapper.writeValueAsString(student);
        kafkaProducerService.send("student-topic", json);

        return Result.success(student);
    }

    @Operation(summary = "检查 Kafka 状态")
    @GetMapping("/status")
    public Result<String> status() {
        String info = "Nacos 服务发现 | 学生服务: " + STUDENT_SERVICE;
        if (kafkaProducerService.isKafkaAvailable()) {
            return Result.success("Kafka 已连接，消息队列正常工作 | " + info);
        } else {
            return Result.success("Kafka 模拟模式 — 消息输出到日志 | " + info);
        }
    }
}
