package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Student;
import com.example.demo.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    Page<StudentVO> selectStudentVOPage(Page<StudentVO> page,
                                        @Param("studentNo") String studentNo,
                                        @Param("studentName") String studentName,
                                        @Param("classId") Long classId,
                                        @Param("gender") Integer gender,
                                        @Param("studentStatus") Integer studentStatus,
                                        @Param("grade") String grade,
                                        @Param("className") String className);

    StudentVO selectStudentVOById(@Param("studentId") Long studentId);
}
