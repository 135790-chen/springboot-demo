package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Enrollment;
import com.example.demo.student.mapper.EnrollmentMapper;
import com.example.demo.student.service.EnrollmentService;
import com.example.demo.vo.StudentCourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired private EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public Enrollment addEnrollment(Long studentId, Long courseId) {
        // 检查是否已选
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
}
