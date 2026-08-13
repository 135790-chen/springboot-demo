package com.example.demo.statistics.service;

import java.util.Map;

/**
 * 校领导数据驾驶舱服务
 */
public interface LeaderService {

    /**
     * 获取全校数据概览
     * @return 包含学生数、教师数、课程数、学院数、专业数、毕业率等统计
     */
    Map<String, Object> getDashboard();
}
