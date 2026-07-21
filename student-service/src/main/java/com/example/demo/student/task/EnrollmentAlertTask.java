package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.student.mapper.CourseMapper;
import com.example.demo.student.mapper.EnrollmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 选课人数预警 — 检查在读人数过少的课程（可能需停课）
 */
@Component
public class EnrollmentAlertTask {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentAlertTask.class);
    private static final int MIN_STUDENTS = 5;

    @Autowired private CourseMapper courseMapper;
    @Autowired private EnrollmentMapper enrollmentMapper;

    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void checkLowEnrollment() {
        log.info("[EnrollmentAlert] 开始检查低选课人数课程...");

        var courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getCourseStatus, 1)
                        .eq(Course::getCourseDeleted, 0));

        int warned = 0;
        for (Course c : courses) {
            long count = enrollmentMapper.selectCount(
                    new LambdaQueryWrapper<Enrollment>()
                            .eq(Enrollment::getCourseId, c.getId())
                            .eq(Enrollment::getRelStatus, 1)); // 在读
            if (count < MIN_STUDENTS) {
                log.warn("[EnrollmentAlert] ⚠ 课程「{}」(ID={}) 选课人数仅 {} 人，低于阈值 {}",
                        c.getCourseName(), c.getId(), count, MIN_STUDENTS);
                warned++;
            }
        }

        if (warned == 0) {
            log.info("[EnrollmentAlert] 所有课程选课人数正常");
        } else {
            log.warn("[EnrollmentAlert] 共 {} 门课程低于最低选课人数", warned);
        }
    }
}
