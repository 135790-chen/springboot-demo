package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Course;
import com.example.demo.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    Page<CourseVO> selectCourseVOPage(Page<CourseVO> page,
                                       @Param("courseCode") String courseCode,
                                       @Param("courseName") String courseName,
                                       @Param("courseType") String courseType,
                                       @Param("teacherId") Long teacherId,
                                       @Param("semester") String semester,
                                       @Param("courseStatus") Integer courseStatus);

    CourseVO selectCourseVOById(@Param("courseId") Long courseId);
}
