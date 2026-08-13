package com.example.demo.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.School;
import com.example.demo.organization.mapper.SchoolMapper;
import com.example.demo.organization.service.SchoolService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {

    private final SchoolMapper schoolMapper;

    public SchoolServiceImpl(SchoolMapper schoolMapper) {
        this.schoolMapper = schoolMapper;
    }

    @Override
    public Page<School> getPage(int page, int size, String schoolName, String schoolCode, Integer schoolStatus) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getSchoolDeleted, 0);
        if (schoolName != null && !schoolName.isEmpty()) wrapper.like(School::getSchoolName, schoolName);
        if (schoolCode != null && !schoolCode.isEmpty()) wrapper.like(School::getSchoolCode, schoolCode);
        if (schoolStatus != null) wrapper.eq(School::getSchoolStatus, schoolStatus);
        wrapper.orderByAsc(School::getSchoolCode);
        return schoolMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public School getById(Long id) {
        return schoolMapper.selectById(id);
    }

    @Override
    public School add(School school) {
        schoolMapper.insert(school);
        return school;
    }

    @Override
    public boolean update(School school) {
        return schoolMapper.updateById(school) > 0;
    }

    @Override
    public boolean delete(Long id) {
        School school = new School();
        school.setId(id);
        school.setSchoolDeleted(1);
        return schoolMapper.updateById(school) > 0;
    }

    @Override
    public List<School> listAll() {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getSchoolDeleted, 0);
        return schoolMapper.selectList(wrapper);
    }
}
