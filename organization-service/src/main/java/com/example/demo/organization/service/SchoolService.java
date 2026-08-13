package com.example.demo.organization.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.School;

import java.util.List;

public interface SchoolService {
    Page<School> getPage(int page, int size, String schoolName, String schoolCode, Integer schoolStatus);
    School getById(Long id);
    School add(School school);
    boolean update(School school);
    boolean delete(Long id);
    List<School> listAll();
}
