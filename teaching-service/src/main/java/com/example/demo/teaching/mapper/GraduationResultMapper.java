package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.GraduationResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GraduationResultMapper extends BaseMapper<GraduationResult> {

    List<GraduationResult> selectByStudentId(@Param("studentId") Long studentId);
}
