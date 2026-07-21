package com.example.demo.student.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Student;
import com.example.demo.entity.Course;
import com.example.demo.student.mapper.EnrollmentMapper;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.mapper.CourseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 孤儿数据清理 — 检测选课记录中引用已被删除的学生或课程
 */
@Component
public class OrphanCleanTask {

    private static final Logger log = LoggerFactory.getLogger(OrphanCleanTask.class);

    @Autowired private EnrollmentMapper enrollmentMapper;
    @Autowired private StudentMapper studentMapper;
    @Autowired private CourseMapper courseMapper;

    @Scheduled(cron = "0 30 4 * * ?")
    public void cleanOrphanEnrollments() {
        log.info("[OrphanClean] 开始检查孤儿数据...");

        var allEnrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>().ne(Enrollment::getRelStatus, 3));

        int orphanStudent = 0, orphanCourse = 0;
        for (Enrollment e : allEnrollments) {
            // 检查学生是否存在且未删除
            boolean studentValid = false;
            if (e.getStudentId() != null) {
                Student s = studentMapper.selectById(e.getStudentId());
                studentValid = s != null && s.getStudentDeleted() != null && s.getStudentDeleted() == 0;
            }
            // 检查课程是否存在且未删除
            boolean courseValid = false;
            if (e.getCourseId() != null) {
                Course c = courseMapper.selectById(e.getCourseId());
                courseValid = c != null && c.getCourseDeleted() != null && c.getCourseDeleted() == 0;
            }

            if (!studentValid && !courseValid) {
                log.warn("[OrphanClean] ⚠ 选课记录 relId={} 学生和课程均已无效", e.getId());
                orphanStudent++;
                orphanCourse++;
            } else if (!studentValid) {
                log.warn("[OrphanClean] ⚠ 选课记录 relId={} 学生(ID={})已删除", e.getId(), e.getStudentId());
                orphanStudent++;
            } else if (!courseValid) {
                log.warn("[OrphanClean] ⚠ 选课记录 relId={} 课程(ID={})已删除", e.getId(), e.getCourseId());
                orphanCourse++;
            }
        }

        if (orphanStudent == 0 && orphanCourse == 0) {
            log.info("[OrphanClean] 无孤儿数据");
        } else {
            log.warn("[OrphanClean] 发现孤儿数据: student={} course={}", orphanStudent, orphanCourse);
        }
    }
}
