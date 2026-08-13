package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.TrainingPlan;
import com.example.demo.teaching.mapper.TrainingPlanMapper;
import com.example.demo.teaching.service.TrainingPlanService;
import com.example.demo.vo.TrainingPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingPlanServiceImpl implements TrainingPlanService {

    @Autowired
    private TrainingPlanMapper trainingPlanMapper;

    @Override
    public Page<TrainingPlanVO> getPage(int page, int size, Long majorId, String grade, Integer planStatus) {
        return trainingPlanMapper.selectTrainingPlanVOPage(
                new Page<>(page, size), majorId, grade, planStatus);
    }

    @Override
    public TrainingPlanVO getById(Long id) {
        return trainingPlanMapper.selectTrainingPlanVOById(id);
    }

    @Override
    @Transactional
    public TrainingPlan add(TrainingPlan plan) {
        trainingPlanMapper.insert(plan);
        return plan;
    }

    @Override
    @Transactional
    public boolean update(TrainingPlan plan) {
        LambdaUpdateWrapper<TrainingPlan> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TrainingPlan::getId, plan.getId());
        if (plan.getPlanName() != null) wrapper.set(TrainingPlan::getPlanName, plan.getPlanName());
        if (plan.getMajorId() != null) wrapper.set(TrainingPlan::getMajorId, plan.getMajorId());
        if (plan.getGrade() != null) wrapper.set(TrainingPlan::getGrade, plan.getGrade());
        if (plan.getVersion() != null) wrapper.set(TrainingPlan::getVersion, plan.getVersion());
        if (plan.getTotalRequiredCredits() != null) wrapper.set(TrainingPlan::getTotalRequiredCredits, plan.getTotalRequiredCredits());
        if (plan.getMajorElectiveMinCredits() != null) wrapper.set(TrainingPlan::getMajorElectiveMinCredits, plan.getMajorElectiveMinCredits());
        if (plan.getGeneralElectiveMinCredits() != null) wrapper.set(TrainingPlan::getGeneralElectiveMinCredits, plan.getGeneralElectiveMinCredits());
        if (plan.getPlanStatus() != null) wrapper.set(TrainingPlan::getPlanStatus, plan.getPlanStatus());
        if (plan.getPlanRemark() != null) wrapper.set(TrainingPlan::getPlanRemark, plan.getPlanRemark());
        wrapper.set(TrainingPlan::getGmtModified, java.time.LocalDateTime.now());
        return trainingPlanMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<TrainingPlan> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TrainingPlan::getId, id).set(TrainingPlan::getPlanDeleted, 1);
        return trainingPlanMapper.update(null, wrapper) > 0;
    }

    @Override
    public List<TrainingPlan> listAll() {
        LambdaQueryWrapper<TrainingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingPlan::getPlanDeleted, 0).eq(TrainingPlan::getPlanStatus, 1);
        return trainingPlanMapper.selectList(wrapper);
    }
}
