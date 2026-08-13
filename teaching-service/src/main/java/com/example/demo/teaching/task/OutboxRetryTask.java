package com.example.demo.teaching.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.EnrollmentMessage;
import com.example.demo.entity.EnrollmentOutbox;
import com.example.demo.teaching.mapper.EnrollmentOutboxMapper;
import com.example.demo.teaching.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 本地表重试任务 — 每 30 秒扫描 PENDING 记录，重新投递 Kafka
 *
 * 重试策略：
 * - 只处理创建超过 1 分钟的记录（给首次投递留足时间）
 * - 最大重试 3 次
 * - 超出最大重试 → 标记 FAILED + 回滚 Redis 库存
 */
@Component
public class OutboxRetryTask {

    private static final Logger log = LoggerFactory.getLogger(OutboxRetryTask.class);
    private static final int MAX_RETRY = 3;
    private static final int RETRY_DELAY_MINUTES = 1;
    private static final String MESSAGE_SERVICE_URL = "http://message-service:8083/kafka/enrollment";

    @Autowired
    private EnrollmentOutboxMapper outboxMapper;

    @Autowired
    private StockService stockService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(cron = "*/30 * * * * *")
    public void retryPending() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(RETRY_DELAY_MINUTES);

        LambdaQueryWrapper<EnrollmentOutbox> query = new LambdaQueryWrapper<>();
        query.eq(EnrollmentOutbox::getStatus, 0)
             .lt(EnrollmentOutbox::getGmtCreate, threshold)
             .last("LIMIT 100");

        List<EnrollmentOutbox> pendingList = outboxMapper.selectList(query);
        if (pendingList.isEmpty()) return;

        log.info("[OutboxRetry] 发现 {} 条待重试记录", pendingList.size());

        for (EnrollmentOutbox outbox : pendingList) {
            try {
                // 重新构造 Kafka 消息并投递
                EnrollmentMessage msg = new EnrollmentMessage();
                msg.setRequestId(outbox.getRequestId());
                msg.setStudentId(outbox.getStudentId());
                msg.setCourseId(outbox.getCourseId());
                msg.setTimestamp(System.currentTimeMillis());

                String json = objectMapper.writeValueAsString(msg);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);

                restTemplate.postForEntity(MESSAGE_SERVICE_URL, entity, String.class);

                // 投递成功 → 标记 SUCCESS
                outbox.setStatus(1);
                outbox.setRetryCount(outbox.getRetryCount() + 1);
                outboxMapper.updateById(outbox);
                log.info("[OutboxRetry] 重试成功 outboxId={} retryCount={}",
                        outbox.getId(), outbox.getRetryCount());

            } catch (Exception e) {
                int newCount = outbox.getRetryCount() + 1;
                log.error("[OutboxRetry] 重试失败 outboxId={} retryCount={}/{} error={}",
                        outbox.getId(), newCount, MAX_RETRY, e.getMessage());

                if (newCount >= MAX_RETRY) {
                    // 超出最大重试 → 标记 FAILED + 回滚 Redis 库存
                    outbox.setStatus(2);
                    outbox.setRetryCount(newCount);
                    outbox.setErrorMsg(e.getMessage());
                    outboxMapper.updateById(outbox);

                    stockService.rollbackStock(outbox.getCourseId(), outbox.getStudentId());
                    log.warn("[OutboxRetry] 已达最大重试次数，回滚库存 outboxId={}", outbox.getId());
                } else {
                    outbox.setRetryCount(newCount);
                    outbox.setErrorMsg(e.getMessage());
                    outboxMapper.updateById(outbox);
                }
            }
        }
    }
}
