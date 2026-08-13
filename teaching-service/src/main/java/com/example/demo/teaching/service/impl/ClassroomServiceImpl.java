package com.example.demo.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Classroom;
import com.example.demo.teaching.mapper.ClassroomMapper;
import com.example.demo.teaching.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public Page<Classroom> getPage(int page, int size, String keyword) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Classroom::getClassroomDeleted, 0);
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Classroom::getClassroomName, keyword)
                    .or().like(Classroom::getClassroomCode, keyword)
                    .or().like(Classroom::getBuilding, keyword));
        }
        wrapper.orderByAsc(Classroom::getBuilding, Classroom::getFloor, Classroom::getClassroomName);
        return classroomMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public Classroom add(Classroom classroom) {
        classroom.setClassroomDeleted(0);
        classroom.setClassroomStatus(1);
        classroomMapper.insert(classroom);
        return classroom;
    }

    @Override
    @Transactional
    public boolean update(Classroom classroom) {
        return classroomMapper.updateById(classroom) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Classroom c = new Classroom();
        c.setId(id);
        c.setClassroomDeleted(1);
        return classroomMapper.updateById(c) > 0;
    }

    @Override
    public Classroom getById(Long id) {
        return classroomMapper.selectById(id);
    }
}
