package com.example.demo.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Course;
import com.example.demo.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 分页查询课程 + 教师姓名（LEFT JOIN）
     */
    @Select("<script>" +
            "SELECT c.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, " +
            "c.credit AS credit, c.course_hours AS courseHours, c.course_type AS courseType, " +
            "c.teacher_id AS teacherId, t.teacher_name AS teacherName, " +
            "c.semester AS semester, c.course_status AS courseStatus, " +
            "c.course_remark AS courseRemark, c.gmt_create AS gmtCreate, c.gmt_modified AS gmtModified " +
            "FROM edu_course c LEFT JOIN edu_teacher t ON c.teacher_id = t.teacher_id AND t.teacher_deleted = 0 " +
            "WHERE c.course_deleted = 0 " +
            "<if test='courseCode != null and courseCode != \"\"'>AND c.course_code LIKE CONCAT('%', #{courseCode}, '%')</if> " +
            "<if test='courseName != null and courseName != \"\"'>AND c.course_name LIKE CONCAT('%', #{courseName}, '%')</if> " +
            "<if test='courseType != null and courseType != \"\"'>AND c.course_type = #{courseType}</if> " +
            "<if test='teacherId != null'>AND c.teacher_id = #{teacherId}</if> " +
            "<if test='semester != null and semester != \"\"'>AND c.semester = #{semester}</if> " +
            "<if test='courseStatus != null'>AND c.course_status = #{courseStatus}</if> " +
            "ORDER BY c.course_code</script>")
    Page<CourseVO> selectCourseVOPage(Page<CourseVO> page,
                                       @Param("courseCode") String courseCode,
                                       @Param("courseName") String courseName,
                                       @Param("courseType") String courseType,
                                       @Param("teacherId") Long teacherId,
                                       @Param("semester") String semester,
                                       @Param("courseStatus") Integer courseStatus);

    /**
     * 查询单个课程详情 + 教师姓名
     */
    @Select("SELECT c.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, " +
            "c.credit AS credit, c.course_hours AS courseHours, c.course_type AS courseType, " +
            "c.teacher_id AS teacherId, t.teacher_name AS teacherName, " +
            "c.semester AS semester, c.course_status AS courseStatus, " +
            "c.course_remark AS courseRemark, c.gmt_create AS gmtCreate, c.gmt_modified AS gmtModified " +
            "FROM edu_course c LEFT JOIN edu_teacher t ON c.teacher_id = t.teacher_id AND t.teacher_deleted = 0 " +
            "WHERE c.course_id = #{courseId} AND c.course_deleted = 0")
    CourseVO selectCourseVOById(@Param("courseId") Long courseId);
}
