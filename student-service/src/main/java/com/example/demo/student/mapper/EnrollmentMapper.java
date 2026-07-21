package com.example.demo.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Enrollment;
import com.example.demo.vo.CourseStudentVO;
import com.example.demo.vo.StudentCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {

    /**
     * 查询学生已选课程 — 三表 JOIN（enrollment + course + teacher）
     */
    @Select("<script>" +
            "SELECT e.rel_id AS relId, c.course_id AS courseId, c.course_code AS courseCode, " +
            "c.course_name AS courseName, c.credit AS credit, c.course_type AS courseType, " +
            "c.teacher_id AS teacherId, t.teacher_name AS teacherName, c.semester AS semester, " +
            "e.score AS score, e.rel_status AS relStatus " +
            "FROM edu_student_course e " +
            "JOIN edu_course c ON e.course_id = c.course_id AND c.course_deleted = 0 " +
            "LEFT JOIN edu_teacher t ON c.teacher_id = t.teacher_id AND t.teacher_deleted = 0 " +
            "WHERE e.student_id = #{studentId} " +
            "<if test='relStatus != null'>AND e.rel_status = #{relStatus}</if> " +
            "<if test='courseName != null and courseName != \"\"'>AND c.course_name LIKE CONCAT('%', #{courseName}, '%')</if> " +
            "<if test='courseType != null and courseType != \"\"'>AND c.course_type = #{courseType}</if> " +
            "ORDER BY c.course_code</script>")
    Page<StudentCourseVO> selectStudentCourseVOPage(Page<StudentCourseVO> page,
                                                     @Param("studentId") Long studentId,
                                                     @Param("relStatus") Integer relStatus,
                                                     @Param("courseName") String courseName,
                                                     @Param("courseType") String courseType);

    /**
     * 查询课程选课学生 — 三表 JOIN（enrollment + student + class）
     */
    @Select("<script>" +
            "SELECT e.rel_id AS relId, s.student_id AS studentId, s.student_no AS studentNo, " +
            "s.student_name AS studentName, s.gender AS gender, cl.class_name AS className, " +
            "e.score AS score, e.rel_status AS relStatus, e.gmt_create AS gmtCreate " +
            "FROM edu_student_course e " +
            "JOIN edu_student s ON e.student_id = s.student_id AND s.student_deleted = 0 " +
            "LEFT JOIN edu_class cl ON s.class_id = cl.class_id AND cl.class_deleted = 0 " +
            "WHERE e.course_id = #{courseId} " +
            "<if test='relStatus != null'>AND e.rel_status = #{relStatus}</if> " +
            "<if test='studentName != null and studentName != \"\"'>AND s.student_name LIKE CONCAT('%', #{studentName}, '%')</if> " +
            "<if test='studentNo != null and studentNo != \"\"'>AND s.student_no LIKE CONCAT('%', #{studentNo}, '%')</if> " +
            "ORDER BY s.student_no</script>")
    Page<CourseStudentVO> selectCourseStudentVOPage(Page<CourseStudentVO> page,
                                                     @Param("courseId") Long courseId,
                                                     @Param("relStatus") Integer relStatus,
                                                     @Param("studentName") String studentName,
                                                     @Param("studentNo") String studentNo);
}
