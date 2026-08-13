package com.example.demo.teaching.service;

import com.example.demo.entity.GraduationResult;
import com.example.demo.vo.GraduationResultVO;

import java.util.List;

public interface GraduationService {

    GraduationResultVO checkGraduation(Long studentId);

    List<GraduationResult> getHistory(Long studentId);
}
