package com.example.demo.organization.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.College;
import com.example.demo.entity.Major;

import java.util.List;

public interface CollegeService {

    Page<College> getPage(int page, int size, String collegeName, String collegeCode, Integer collegeStatus);

    College getById(Long id);

    College add(College college);

    boolean update(College college);

    boolean delete(Long id);

    List<College> listAll();

    /** 查询学院下的所有活跃专业 */
    List<Major> listMajorsByCollegeId(Long collegeId);
}
