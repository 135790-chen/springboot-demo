package com.example.demo.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Student;
import com.example.demo.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 学生数据访问层（Mapper）
 *
 * 继承 MyBatis-Plus 的 BaseMapper<Student> 后，自动获得基本 CRUD。
 * 多表关联查询定义在 resources/mapper/StudentMapper.xml（LEFT JOIN 返回 VO）。
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生 + 班级名/年级（LEFT JOIN）
     */
    Page<StudentVO> selectStudentVOPage(Page<StudentVO> page,
                                        @Param("studentNo") String studentNo,
                                        @Param("studentName") String studentName,
                                        @Param("classId") Long classId,
                                        @Param("gender") Integer gender,
                                        @Param("studentStatus") Integer studentStatus,
                                        @Param("grade") String grade,
                                        @Param("className") String className);

    /**
     * 查询单个学生详情 + 班级名/年级
     */
    StudentVO selectStudentVOById(@Param("studentId") Long studentId);
}
