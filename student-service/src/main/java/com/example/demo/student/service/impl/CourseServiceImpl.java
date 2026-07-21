package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Course;
import com.example.demo.entity.Teacher;
import com.example.demo.student.mapper.CourseMapper;
import com.example.demo.student.mapper.EnrollmentMapper;
import com.example.demo.student.mapper.TeacherMapper;
import com.example.demo.student.service.CourseService;
import com.example.demo.vo.CourseStudentVO;
import com.example.demo.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired private CourseMapper courseMapper;
    @Autowired private TeacherMapper teacherMapper;
    @Autowired private EnrollmentMapper enrollmentMapper;

    @Override
    public Page<CourseVO> getPage(int page, int size, String courseCode, String courseName,
                                   String courseType, Long teacherId, String semester, Integer courseStatus) {
        return courseMapper.selectCourseVOPage(
                new Page<>(page, size), courseCode, courseName, courseType, teacherId, semester, courseStatus);
    }

    @Override
    public CourseVO getById(Long id) {
        return courseMapper.selectCourseVOById(id);
    }

    @Override
    @Transactional
    public Course add(Course course) {
        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public boolean update(Course course) {
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, course.getId());
        if (course.getCourseName() != null) wrapper.set(Course::getCourseName, course.getCourseName());
        if (course.getCourseCode() != null) wrapper.set(Course::getCourseCode, course.getCourseCode());
        if (course.getCredit() != null) wrapper.set(Course::getCredit, course.getCredit());
        if (course.getCourseHours() != null) wrapper.set(Course::getCourseHours, course.getCourseHours());
        if (course.getCourseType() != null) wrapper.set(Course::getCourseType, course.getCourseType());
        if (course.getTeacherId() != null) wrapper.set(Course::getTeacherId, course.getTeacherId());
        if (course.getSemester() != null) wrapper.set(Course::getSemester, course.getSemester());
        if (course.getCourseStatus() != null) wrapper.set(Course::getCourseStatus, course.getCourseStatus());
        if (course.getCourseRemark() != null) wrapper.set(Course::getCourseRemark, course.getCourseRemark());
        wrapper.set(Course::getGmtModified, java.time.LocalDateTime.now());
        return courseMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, id).set(Course::getCourseDeleted, 1);
        return courseMapper.update(null, wrapper) > 0;
    }

    @Override
    public Page<CourseStudentVO> getStudentsByCourseId(Long courseId, int page, int size,
                                                        String studentName, String studentNo, Integer relStatus) {
        return enrollmentMapper.selectCourseStudentVOPage(
                new Page<>(page, size), courseId, relStatus, studentName, studentNo);
    }
}
