package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.demo.entity.*;
import com.example.demo.student.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 数据统计快照 — 每天 23:59 记录教育数据概况
 */
@Component
public class StatSnapshotTask {

    private static final Logger log = LoggerFactory.getLogger(StatSnapshotTask.class);

    @Autowired private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 59 23 * * ?")
    public void takeSnapshot() {
        LocalDate today = LocalDate.now();
        log.info("[StatSnapshot] 开始生成统计快照 {}", today);

        // 统计各项指标
        int totalStudents = count("edu_student", "student_status = 1 AND student_deleted = 0");
        int totalTeachers = count("edu_teacher", "teacher_status = 1 AND teacher_deleted = 0");
        int totalCourses  = count("edu_course", "course_status = 1 AND course_deleted = 0");
        int totalEnrollments = count("edu_student_course", "rel_status = 1");

        // 平均分（仅已修完有成绩的）
        BigDecimal avgScore = null;
        try {
            var result = jdbcTemplate.queryForObject(
                    "SELECT AVG(score) FROM edu_student_course WHERE rel_status = 2 AND score IS NOT NULL",
                    BigDecimal.class);
            if (result != null) {
                avgScore = result.setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {}

        // 不及格人次
        int failCount = 0;
        try {
            var fc = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM edu_student_course WHERE rel_status = 2 AND score < 60 AND score IS NOT NULL",
                    Integer.class);
            failCount = fc != null ? fc : 0;
        } catch (Exception ignored) {}

        // UPSERT（存在则更新，不存在则插入）
        String sql = """
                INSERT INTO edu_stat (stat_id, stat_date, total_students, total_teachers, total_courses,
                    total_enrollments, avg_score, fail_count, gmt_create)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
                ON DUPLICATE KEY UPDATE
                    total_students = VALUES(total_students),
                    total_teachers = VALUES(total_teachers),
                    total_courses = VALUES(total_courses),
                    total_enrollments = VALUES(total_enrollments),
                    avg_score = VALUES(avg_score),
                    fail_count = VALUES(fail_count)
                """;
        jdbcTemplate.update(sql, IdWorker.getId(), today,
                totalStudents, totalTeachers, totalCourses, totalEnrollments, avgScore, failCount);

        log.info("[StatSnapshot] 快照完成: 学生{} 教师{} 课程{} 选课{} 均分{} 挂科{}",
                totalStudents, totalTeachers, totalCourses, totalEnrollments, avgScore, failCount);
    }

    private int count(String table, String where) {
        try {
            var c = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + table + " WHERE " + where, Integer.class);
            return c != null ? c : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
