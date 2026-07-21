package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Student;
import com.example.demo.student.mapper.CourseMapper;
import com.example.demo.student.mapper.EnrollmentMapper;
import com.example.demo.student.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 挂科预警 — 检测有不及格记录的学生
 */
@Component
public class FailAlertTask {

    private static final Logger log = LoggerFactory.getLogger(FailAlertTask.class);
    private static final BigDecimal PASS_SCORE = new BigDecimal("60");

    @Autowired private EnrollmentMapper enrollmentMapper;
    @Autowired private StudentMapper studentMapper;
    @Autowired private CourseMapper courseMapper;

    @Scheduled(cron = "0 10 10 * * MON-FRI")
    public void checkFailures() {
        log.info("[FailAlert] 开始检查挂科情况...");

        var fails = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                        .lt(Enrollment::getScore, PASS_SCORE)
                        .isNotNull(Enrollment::getScore)
                        .eq(Enrollment::getRelStatus, 2)); // 已修完但有成绩的

        if (fails.isEmpty()) {
            log.info("[FailAlert] 无挂科记录");
            return;
        }

        // 批量查学生和课程名
        var studentIds = fails.stream().map(Enrollment::getStudentId).distinct().toList();
        var courseIds = fails.stream().map(Enrollment::getCourseId).distinct().toList();
        Map<Long, String> studentNames = studentMapper.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, Student::getStudentName, (a, b) -> a));
        Map<Long, String> courseNames = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Course::getCourseName, (a, b) -> a));

        for (Enrollment e : fails) {
            log.warn("[FailAlert] ⚠ {} (ID={}) — 课程「{}」成绩: {} 分",
                    studentNames.getOrDefault(e.getStudentId(), "未知"),
                    e.getStudentId(),
                    courseNames.getOrDefault(e.getCourseId(), "未知"),
                    e.getScore());
        }
        log.warn("[FailAlert] 共 {} 人次不及格", fails.size());
    }
}
