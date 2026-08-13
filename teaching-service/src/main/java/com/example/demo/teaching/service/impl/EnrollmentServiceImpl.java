package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.EnrollmentMessage;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.EnrollmentOutbox;
import com.example.demo.teaching.mapper.CourseMapper;
import com.example.demo.teaching.mapper.EnrollmentMapper;
import com.example.demo.teaching.mapper.EnrollmentOutboxMapper;
import com.example.demo.teaching.service.EnrollmentService;
import com.example.demo.teaching.service.StockService;
import com.example.demo.vo.StudentCourseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    @Autowired private EnrollmentMapper enrollmentMapper;
    @Autowired private CourseMapper courseMapper;
    @Autowired private StockService stockService;
    @Autowired private EnrollmentOutboxMapper outboxMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MESSAGE_SERVICE_URL = "http://message-service:8083/kafka/enrollment";

    @Override
    @Transactional
    public Enrollment addEnrollment(Long studentId, Long courseId) {
        LambdaQueryWrapper<Enrollment> check = new LambdaQueryWrapper<>();
        check.eq(Enrollment::getStudentId, studentId)
             .eq(Enrollment::getCourseId, courseId)
             .ne(Enrollment::getRelStatus, 3);
        if (enrollmentMapper.selectCount(check) > 0) {
            throw new IllegalArgumentException("该学生已选此课程");
        }
        Enrollment e = new Enrollment();
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        e.setRelStatus(1);
        e.setConfirmStatus(1);
        enrollmentMapper.insert(e);
        return e;
    }

    @Override
    @Transactional
    public boolean dropEnrollment(Long relId) {
        LambdaUpdateWrapper<Enrollment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Enrollment::getId, relId).set(Enrollment::getRelStatus, 3);
        return enrollmentMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean updateScore(Long relId, BigDecimal score) {
        LambdaUpdateWrapper<Enrollment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Enrollment::getId, relId)
               .set(Enrollment::getScore, score)
               .set(Enrollment::getRelStatus, 2)
               .set(Enrollment::getGmtModified, java.time.LocalDateTime.now());
        return enrollmentMapper.update(null, wrapper) > 0;
    }

    @Override
    public Page<StudentCourseVO> getCoursesByStudentId(Long studentId, int page, int size,
                                                        String courseName, String courseType, Integer relStatus) {
        return enrollmentMapper.selectStudentCourseVOPage(
                new Page<>(page, size), studentId, relStatus, courseName, courseType);
    }

    @Override
    @Transactional
    public int seckillEnrollment(Long studentId, Long courseId) {
        stockService.initStockIfAbsent(courseId, () -> {
            Course course = courseMapper.selectById(courseId);
            int max = course != null && course.getMaxStudents() != null ? course.getMaxStudents() : 100;
            LambdaQueryWrapper<Enrollment> countQuery = new LambdaQueryWrapper<>();
            countQuery.eq(Enrollment::getCourseId, courseId)
                      .eq(Enrollment::getConfirmStatus, 1)
                      .ne(Enrollment::getRelStatus, 3);
            long enrolled = enrollmentMapper.selectCount(countQuery);
            return Math.max(0, max - (int) enrolled);
        });

        String requestId = UUID.randomUUID().toString();
        int result = stockService.tryDeduct(courseId, studentId, requestId);

        if (result == 1) {
            // 在同一事务内：写 enrollment(预扣) + outbox(PENDING)
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourseId(courseId);
            enrollment.setRelStatus(1);
            enrollment.setConfirmStatus(0);  // 0=预扣待确认
            enrollmentMapper.insert(enrollment);

            EnrollmentOutbox outbox = new EnrollmentOutbox();
            outbox.setRequestId(requestId);
            outbox.setStudentId(studentId);
            outbox.setCourseId(courseId);
            outbox.setStatus(0);  // 0=PENDING
            outbox.setRetryCount(0);
            outboxMapper.insert(outbox);

            log.info("[秒杀选课] 预扣成功 studentId={} courseId={} relId={} outboxId={}",
                    studentId, courseId, enrollment.getId(), outbox.getId());

            // 异步投递 Kafka（失败不影响主流程，定时任务会重试）
            try {
                sendEnrollmentKafka(requestId, studentId, courseId);
            } catch (Exception e) {
                log.warn("[秒杀选课] Kafka 发送失败，等待定时任务重试 outboxId={}: {}",
                        outbox.getId(), e.getMessage());
            }
        }

        return result;
    }

    /**
     * 通过 message-service REST 接口异步投递 Kafka 选课消息
     */
    private void sendEnrollmentKafka(String requestId, Long studentId, Long courseId) throws Exception {
        EnrollmentMessage msg = new EnrollmentMessage();
        msg.setRequestId(requestId);
        msg.setStudentId(studentId);
        msg.setCourseId(courseId);
        msg.setTimestamp(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(msg), headers);

        restTemplate.postForEntity(MESSAGE_SERVICE_URL, entity, String.class);
        log.info("[秒杀选课] Kafka 投递成功 studentId={} courseId={}", studentId, courseId);
    }

    @Override
    public Enrollment confirmEnrollment(EnrollmentMessage message) {
        LambdaQueryWrapper<Enrollment> query = new LambdaQueryWrapper<>();
        query.eq(Enrollment::getStudentId, message.getStudentId())
             .eq(Enrollment::getCourseId, message.getCourseId())
             .eq(Enrollment::getConfirmStatus, 0)
             .orderByDesc(Enrollment::getGmtCreate)
             .last("LIMIT 1");
        Enrollment enrollment = enrollmentMapper.selectOne(query);
        if (enrollment == null) return null;
        enrollment.setConfirmStatus(1);
        enrollmentMapper.updateById(enrollment);

        // 更新 outbox 为 SUCCESS
        LambdaQueryWrapper<EnrollmentOutbox> outboxQuery = new LambdaQueryWrapper<>();
        outboxQuery.eq(EnrollmentOutbox::getRequestId, message.getRequestId());
        EnrollmentOutbox outbox = outboxMapper.selectOne(outboxQuery);
        if (outbox != null) {
            outbox.setStatus(1);  // 1=SUCCESS
            outboxMapper.updateById(outbox);
        }

        stockService.confirmDedup(message.getCourseId(), message.getStudentId());
        log.info("[选课确认] confirmStatus 已确认 studentId={} courseId={} outboxId={}",
                message.getStudentId(), message.getCourseId(),
                outbox != null ? outbox.getId() : null);
        return enrollment;
    }
}
