package com.example.demo.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.College;
import com.example.demo.entity.Major;
import com.example.demo.organization.mapper.CollegeMapper;
import com.example.demo.organization.mapper.MajorMapper;
import com.example.demo.organization.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Override
    public Page<College> getPage(int page, int size, String collegeName, String collegeCode, Integer collegeStatus) {
        LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(College::getCollegeDeleted, 0);
        if (collegeName != null && !collegeName.isEmpty())
            wrapper.like(College::getCollegeName, collegeName);
        if (collegeCode != null && !collegeCode.isEmpty())
            wrapper.like(College::getCollegeCode, collegeCode);
        if (collegeStatus != null)
            wrapper.eq(College::getCollegeStatus, collegeStatus);
        wrapper.orderByAsc(College::getCollegeCode);
        return collegeMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public College getById(Long id) {
        LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(College::getId, id).eq(College::getCollegeDeleted, 0);
        return collegeMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public College add(College college) {
        collegeMapper.insert(college);
        return college;
    }

    @Override
    @Transactional
    public boolean update(College college) {
        LambdaUpdateWrapper<College> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(College::getId, college.getId());
        if (college.getCollegeName() != null) wrapper.set(College::getCollegeName, college.getCollegeName());
        if (college.getCollegeCode() != null) wrapper.set(College::getCollegeCode, college.getCollegeCode());
        if (college.getCollegeStatus() != null) wrapper.set(College::getCollegeStatus, college.getCollegeStatus());
        if (college.getCollegeRemark() != null) wrapper.set(College::getCollegeRemark, college.getCollegeRemark());
        wrapper.set(College::getGmtModified, java.time.LocalDateTime.now());
        return collegeMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<College> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(College::getId, id).set(College::getCollegeDeleted, 1);
        return collegeMapper.update(null, wrapper) > 0;
    }

    @Override
    public List<College> listAll() {
        LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(College::getCollegeDeleted, 0).eq(College::getCollegeStatus, 1);
        return collegeMapper.selectList(wrapper);
    }

    @Override
    public List<Major> listMajorsByCollegeId(Long collegeId) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getCollegeId, collegeId)
               .eq(Major::getMajorDeleted, 0)
               .eq(Major::getMajorStatus, 1)
               .orderByAsc(Major::getMajorCode);
        return majorMapper.selectList(wrapper);
    }
}
