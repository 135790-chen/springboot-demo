package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Clazz;
import com.example.demo.student.mapper.ClazzMapper;
import com.example.demo.student.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public Page<Clazz> getPage(int page, int size, String classCode, String className,
                               String grade, String major, Integer classStatus) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getClassDeleted, 0);
        if (classCode != null && !classCode.isEmpty()) wrapper.like(Clazz::getClassCode, classCode);
        if (className != null && !className.isEmpty()) wrapper.like(Clazz::getClassName, className);
        if (grade != null && !grade.isEmpty()) wrapper.eq(Clazz::getGrade, grade);
        if (major != null && !major.isEmpty()) wrapper.like(Clazz::getMajor, major);
        if (classStatus != null) wrapper.eq(Clazz::getClassStatus, classStatus);
        wrapper.orderByAsc(Clazz::getClassSort, Clazz::getId);
        return clazzMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Clazz getById(Long id) {
        return clazzMapper.selectById(id);
    }

    @Override
    @Transactional
    public Clazz add(Clazz clazz) {
        clazzMapper.insert(clazz);
        return clazz;
    }

    @Override
    @Transactional
    public boolean update(Clazz clazz) {
        LambdaUpdateWrapper<Clazz> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Clazz::getId, clazz.getId());
        if (clazz.getClassName() != null) wrapper.set(Clazz::getClassName, clazz.getClassName());
        if (clazz.getClassCode() != null) wrapper.set(Clazz::getClassCode, clazz.getClassCode());
        if (clazz.getGrade() != null) wrapper.set(Clazz::getGrade, clazz.getGrade());
        if (clazz.getMajor() != null) wrapper.set(Clazz::getMajor, clazz.getMajor());
        if (clazz.getClassSort() != null) wrapper.set(Clazz::getClassSort, clazz.getClassSort());
        if (clazz.getClassStatus() != null) wrapper.set(Clazz::getClassStatus, clazz.getClassStatus());
        if (clazz.getClassRemark() != null) wrapper.set(Clazz::getClassRemark, clazz.getClassRemark());
        wrapper.set(Clazz::getGmtModified, java.time.LocalDateTime.now());
        return clazzMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<Clazz> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Clazz::getId, id).set(Clazz::getClassDeleted, 1);
        return clazzMapper.update(null, wrapper) > 0;
    }

    @Override
    public List<Clazz> listAll() {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getClassDeleted, 0).eq(Clazz::getClassStatus, 1);
        return clazzMapper.selectList(wrapper);
    }
}
