package com.example.demo.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Classroom;

public interface ClassroomService {

    Page<Classroom> getPage(int page, int size, String keyword);

    Classroom add(Classroom classroom);

    boolean update(Classroom classroom);

    boolean delete(Long id);

    Classroom getById(Long id);
}
