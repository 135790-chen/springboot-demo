package com.example.demo.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.TrainingPlan;
import com.example.demo.vo.TrainingPlanVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainingPlanMapper extends BaseMapper<TrainingPlan> {

    TrainingPlan selectByMajorAndGrade(@Param("majorId") Long majorId, @Param("grade") String grade);

    Page<TrainingPlanVO> selectTrainingPlanVOPage(Page<TrainingPlanVO> page,
                                                    @Param("majorId") Long majorId,
                                                    @Param("grade") String grade,
                                                    @Param("planStatus") Integer planStatus);

    TrainingPlanVO selectTrainingPlanVOById(@Param("planId") Long planId);
}
