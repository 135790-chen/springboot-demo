package com.example.demo.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Student;
import com.example.demo.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学生数据访问层（Mapper）
 *
 * 继承 MyBatis-Plus 的 BaseMapper<Student> 后，自动获得基本 CRUD。
 * 多表关联查询使用 @Select + LEFT JOIN 直接返回 VO。
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生 + 班级名/年级（LEFT JOIN）
     */
    @Select("<script>" +
            "SELECT s.student_id AS studentId, s.student_no AS studentNo, s.student_name AS studentName, " +
            "s.gender AS gender, s.phone AS phone, s.email AS email, s.birthday AS birthday, " +
            "s.class_id AS classId, c.class_name AS className, c.grade AS grade, " +
            "s.enrollment_year AS enrollmentYear, s.student_status AS studentStatus, " +
            "s.student_remark AS studentRemark, s.gmt_create AS gmtCreate, s.gmt_modified AS gmtModified " +
            "FROM edu_student s LEFT JOIN edu_class c ON s.class_id = c.class_id AND c.class_deleted = 0 " +
            "WHERE s.student_deleted = 0 " +
            "<if test='studentNo != null and studentNo != \"\"'>AND s.student_no LIKE CONCAT('%', #{studentNo}, '%')</if> " +
            "<if test='studentName != null and studentName != \"\"'>AND s.student_name LIKE CONCAT('%', #{studentName}, '%')</if> " +
            "<if test='classId != null'>AND s.class_id = #{classId}</if> " +
            "<if test='gender != null'>AND s.gender = #{gender}</if> " +
            "<if test='studentStatus != null'>AND s.student_status = #{studentStatus}</if> " +
            "ORDER BY s.student_no</script>")
    Page<StudentVO> selectStudentVOPage(Page<StudentVO> page,
                                        @Param("studentNo") String studentNo,
                                        @Param("studentName") String studentName,
                                        @Param("classId") Long classId,
                                        @Param("gender") Integer gender,
                                        @Param("studentStatus") Integer studentStatus);

    /**
     * 查询单个学生详情 + 班级名/年级
     */
    @Select("SELECT s.student_id AS studentId, s.student_no AS studentNo, s.student_name AS studentName, " +
            "s.gender AS gender, s.phone AS phone, s.email AS email, s.birthday AS birthday, " +
            "s.class_id AS classId, c.class_name AS className, c.grade AS grade, " +
            "s.enrollment_year AS enrollmentYear, s.student_status AS studentStatus, " +
            "s.student_remark AS studentRemark, s.gmt_create AS gmtCreate, s.gmt_modified AS gmtModified " +
            "FROM edu_student s LEFT JOIN edu_class c ON s.class_id = c.class_id AND c.class_deleted = 0 " +
            "WHERE s.student_id = #{studentId} AND s.student_deleted = 0")
    StudentVO selectStudentVOById(@Param("studentId") Long studentId);
}
