package com.example.demo.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Clazz;

import java.util.List;

/**
 * 班级业务逻辑接口
 */
public interface ClassService {

    Page<Clazz> getPage(int page, int size, String classCode, String className, String grade, String major, Integer classStatus);

    Clazz getById(Long id);

    Clazz add(Clazz clazz);

    boolean update(Clazz clazz);

    boolean delete(Long id);

    List<Clazz> listAll();
}
