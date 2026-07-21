package com.example.demo.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Teacher;

public interface TeacherService {

    Page<Teacher> getPage(int page, int size, String teacherNo, String teacherName, String title, Integer teacherStatus);

    Teacher getById(Long id);

    Teacher add(Teacher teacher);

    boolean update(Teacher teacher);

    boolean delete(Long id);
}
