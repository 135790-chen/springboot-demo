package com.example.demo.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Teacher;
import com.example.demo.student.mapper.TeacherMapper;
import com.example.demo.student.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Page<Teacher> getPage(int page, int size, String teacherNo, String teacherName,
                                  String title, Integer teacherStatus) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherDeleted, 0);
        if (teacherNo != null && !teacherNo.isEmpty()) wrapper.like(Teacher::getTeacherNo, teacherNo);
        if (teacherName != null && !teacherName.isEmpty()) wrapper.like(Teacher::getTeacherName, teacherName);
        if (title != null && !title.isEmpty()) wrapper.eq(Teacher::getTitle, title);
        if (teacherStatus != null) wrapper.eq(Teacher::getTeacherStatus, teacherStatus);
        wrapper.orderByAsc(Teacher::getTeacherNo);
        return teacherMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.selectById(id);
    }

    @Override
    @Transactional
    public Teacher add(Teacher teacher) {
        teacherMapper.insert(teacher);
        return teacher;
    }

    @Override
    @Transactional
    public boolean update(Teacher teacher) {
        LambdaUpdateWrapper<Teacher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Teacher::getId, teacher.getId());
        if (teacher.getTeacherName() != null) wrapper.set(Teacher::getTeacherName, teacher.getTeacherName());
        if (teacher.getTeacherNo() != null) wrapper.set(Teacher::getTeacherNo, teacher.getTeacherNo());
        if (teacher.getGender() != null) wrapper.set(Teacher::getGender, teacher.getGender());
        if (teacher.getPhone() != null) wrapper.set(Teacher::getPhone, teacher.getPhone());
        if (teacher.getEmail() != null) wrapper.set(Teacher::getEmail, teacher.getEmail());
        if (teacher.getTitle() != null) wrapper.set(Teacher::getTitle, teacher.getTitle());
        if (teacher.getTeacherStatus() != null) wrapper.set(Teacher::getTeacherStatus, teacher.getTeacherStatus());
        if (teacher.getTeacherRemark() != null) wrapper.set(Teacher::getTeacherRemark, teacher.getTeacherRemark());
        wrapper.set(Teacher::getGmtModified, java.time.LocalDateTime.now());
        return teacherMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        LambdaUpdateWrapper<Teacher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Teacher::getId, id).set(Teacher::getTeacherDeleted, 1);
        return teacherMapper.update(null, wrapper) > 0;
    }
}
