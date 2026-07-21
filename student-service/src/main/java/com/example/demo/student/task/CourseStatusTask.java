package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.entity.Course;
import com.example.demo.student.mapper.CourseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 课程状态自动切换 — 学期结束后关闭课程
 * 春季学期(-2) 8月后自动关闭，秋季学期(-1) 3月后自动关闭
 */
@Component
public class CourseStatusTask {

    private static final Logger log = LoggerFactory.getLogger(CourseStatusTask.class);

    @Autowired private CourseMapper courseMapper;

    @Scheduled(cron = "0 0 2 1 * ?")
    public void autoCloseCourses() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        String closeSemester;
        if (month >= 3 && month <= 7) {
            // 3-7月：关闭上学期的秋季学期 (year-1)-(year)-1
            closeSemester = (year - 1) + "-" + year + "-1";
        } else if (month >= 8 || month <= 2) {
            // 8-2月：关闭上学期的春季学期 year-(year+1)-2 或 (year-1)-year-2
            if (month >= 8) {
                closeSemester = (year - 1) + "-" + year + "-2";
            } else {
                closeSemester = (year - 2) + "-" + (year - 1) + "-2";
            }
        } else {
            return; // shouldn't happen
        }

        log.info("[CourseStatus] 检查需关闭的学期: {}", closeSemester);

        var wrapper = new LambdaUpdateWrapper<Course>()
                .eq(Course::getSemester, closeSemester)
                .eq(Course::getCourseStatus, 1)   // 开课
                .eq(Course::getCourseDeleted, 0)
                .set(Course::getCourseStatus, 0);  // 停课
        int updated = courseMapper.update(null, wrapper);

        if (updated > 0) {
            log.info("[CourseStatus] 自动关闭 {} 门课程 (学期: {})", updated, closeSemester);
        }
    }
}
