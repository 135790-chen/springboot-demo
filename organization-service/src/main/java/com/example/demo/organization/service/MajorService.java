package com.example.demo.organization.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Major;
import com.example.demo.vo.MajorVO;

import java.util.List;

public interface MajorService {

    Page<MajorVO> getPage(int page, int size, String majorName, String majorCode, Long collegeId, Integer majorStatus);

    MajorVO getById(Long id);

    Major add(Major major);

    boolean update(Major major);

    boolean delete(Long id);

    List<Major> listAll();
}
