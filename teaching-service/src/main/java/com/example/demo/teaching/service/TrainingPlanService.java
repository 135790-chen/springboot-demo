package com.example.demo.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.TrainingPlan;
import com.example.demo.vo.TrainingPlanVO;

import java.util.List;

public interface TrainingPlanService {

    Page<TrainingPlanVO> getPage(int page, int size, Long majorId, String grade, Integer planStatus);

    TrainingPlanVO getById(Long id);

    TrainingPlan add(TrainingPlan plan);

    boolean update(TrainingPlan plan);

    boolean delete(Long id);

    List<TrainingPlan> listAll();
}
