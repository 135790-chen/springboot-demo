package com.example.demo.statistics.task;

import com.example.demo.common.JwtUtil;
import com.example.demo.entity.StatSnapshot;
import com.example.demo.statistics.mapper.StatMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 数据统计快照 — 每天 23:59 通过 REST API 聚合各服务数据
 *
 * <p>使用 @LoadBalanced RestTemplate + Nacos 服务发现调用其他服务，
 * 通过系统 JWT Token 进行服务间认证。</p>
 */
@Component
public class StatSnapshotTask {

    private static final Logger log = LoggerFactory.getLogger(StatSnapshotTask.class);

    @Autowired
    private StatMapper statMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${stat.system-user-id:2}")
    private Long systemUserId;

    @Value("${stat.system-username:system}")
    private String systemUsername;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(cron = "0 59 23 * * ?")
    public void takeSnapshot() {
        log.info("[StatSnapshot] 开始生成统计快照...");
        StatSnapshot snapshot = generateSnapshot();
        statMapper.insert(snapshot);
        log.info("[StatSnapshot] 快照完成: statId={} statDate={} students={} teachers={} courses={}",
                snapshot.getStatId(), snapshot.getStatDate(),
                snapshot.getTotalStudents(), snapshot.getTotalTeachers(), snapshot.getTotalCourses());
    }

    /**
     * 生成当前统计快照（供定时任务和手动触发共用）
     */
    public StatSnapshot generateSnapshot() {
        String token = generateSystemToken();

        StatSnapshot snapshot = new StatSnapshot();
        snapshot.setStatDate(LocalDate.now());
        snapshot.setTotalStudents(queryTotal(token, "http://student-service/api/edu/student/page"));
        snapshot.setTotalTeachers(queryTotal(token, "http://teaching-service/api/edu/teacher/page"));
        snapshot.setTotalCourses(queryTotal(token, "http://teaching-service/api/edu/course/page"));
        snapshot.setTotalEnrollments(0);
        snapshot.setAvgScore(BigDecimal.ZERO);
        snapshot.setFailCount(0);

        return snapshot;
    }

    private int queryTotal(String token, String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(
                    url + "?page=1&size=1", HttpMethod.GET, entity, String.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                JsonNode root = objectMapper.readTree(resp.getBody());
                JsonNode data = root.get("data");
                if (data != null && data.has("total")) {
                    return data.get("total").asInt();
                }
            }
        } catch (Exception e) {
            log.warn("[StatSnapshot] 查询 {} 失败: {}", url, e.getMessage());
        }
        return 0;
    }

    private String generateSystemToken() {
        List<String> permissions = List.of(
                "student:view", "teacher:view", "course:view",
                "user:view", "enrollment:view", "stat:view", "dashboard:view"
        );
        return jwtUtil.generateToken(systemUserId, systemUsername, "admin", null, permissions);
    }
}
