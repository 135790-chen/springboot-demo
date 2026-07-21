package com.example.demo.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Course;
import com.example.demo.vo.CourseStudentVO;
import com.example.demo.vo.CourseVO;

public interface CourseService {

    Page<CourseVO> getPage(int page, int size, String courseCode, String courseName, String courseType,
                           Long teacherId, String semester, Integer courseStatus);

    CourseVO getById(Long id);

    Course add(Course course);

    boolean update(Course course);

    boolean delete(Long id);

    Page<CourseStudentVO> getStudentsByCourseId(Long courseId, int page, int size,
                                                 String studentName, String studentNo, Integer relStatus);
}
