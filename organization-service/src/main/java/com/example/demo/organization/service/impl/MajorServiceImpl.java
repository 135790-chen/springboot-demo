package com.example.demo.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Major;
import com.example.demo.organization.mapper.MajorMapper;
import com.example.demo.organization.service.MajorService;
import com.example.demo.vo.MajorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    private MajorMapper majorMapper;

    @Override
    public Page<MajorVO> getPage(int page, int size, String majorName, String majorCode, Long collegeId, Integer majorStatus) {
        return majorMapper.selectMajorVOPage(
                new Page<>(page, size), majorName, majorCode, collegeId, majorStatus);
    }

    @Override
    public MajorVO getById(Long id) {
        return majorMapper.selectMajorVOById(id);
    }

    @Override
    @Transactional
    public Major add(Major major) {
        majorMapper.insert(major);
        return major;
    }

    @Override
    @Transactional
    public boolean update(Major major) {
        LambdaUpdateWrapper<Major> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Major::getId, major.getId());
        if (major.getMajorName() != null) wrapper.set(Major::getMajorName, major.getMajorName());
        if (major.getMajorCode() != null) wrapper.set(Major::getMajorCode, major.getMajorCode());
        if (major.getCollegeId() != null) wrapper.set(Major::getCollegeId, major.getCollegeId());
        if (major.getMajorStatus() != null) wrapper.set(Major::getMajorStatus, major.getMajorStatus());
        if (major.getMajorRemark() != null) wrapper.set(Major::getMajorRemark, major.getMajorRemark());
        wrapper.set(Major::getGmtModified, java.time.LocalDateTime.now());
        return majorMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<Major> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Major::getId, id).set(Major::getMajorDeleted, 1);
        return majorMapper.update(null, wrapper) > 0;
    }

    @Override
    public List<Major> listAll() {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getMajorDeleted, 0).eq(Major::getMajorStatus, 1);
        return majorMapper.selectList(wrapper);
    }
}
