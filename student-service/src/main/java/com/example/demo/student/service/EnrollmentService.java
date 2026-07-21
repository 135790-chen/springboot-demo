package com.example.demo.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Enrollment;
import com.example.demo.vo.StudentCourseVO;

import java.math.BigDecimal;

public interface EnrollmentService {

    Enrollment addEnrollment(Long studentId, Long courseId);

    boolean dropEnrollment(Long relId);

    boolean updateScore(Long relId, BigDecimal score);

    Page<StudentCourseVO> getCoursesByStudentId(Long studentId, int page, int size,
                                                 String courseName, String courseType, Integer relStatus);
}
