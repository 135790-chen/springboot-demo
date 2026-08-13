package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.PlanCourse;
import com.example.demo.vo.PlanCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlanCourseMapper extends BaseMapper<PlanCourse> {

    List<PlanCourse> selectByPlanId(@Param("planId") Long planId);

    /** 查询方案课程（含课程名称，按学期排序） */
    List<PlanCourseVO> selectPlanCourseVOsByPlanId(@Param("planId") Long planId);
}
