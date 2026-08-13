package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Enrollment;
import com.example.demo.vo.CourseStudentVO;
import com.example.demo.vo.StudentCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {

    Page<StudentCourseVO> selectStudentCourseVOPage(Page<StudentCourseVO> page,
                                                     @Param("studentId") Long studentId,
                                                     @Param("relStatus") Integer relStatus,
                                                     @Param("courseName") String courseName,
                                                     @Param("courseType") String courseType);

    Page<CourseStudentVO> selectCourseStudentVOPage(Page<CourseStudentVO> page,
                                                     @Param("courseId") Long courseId,
                                                     @Param("relStatus") Integer relStatus,
                                                     @Param("studentName") String studentName,
                                                     @Param("studentNo") String studentNo);
}
