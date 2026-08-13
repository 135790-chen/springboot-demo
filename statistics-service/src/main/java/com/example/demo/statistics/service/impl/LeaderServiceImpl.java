package com.example.demo.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.StatSnapshot;
import com.example.demo.statistics.mapper.StatMapper;
import com.example.demo.statistics.service.LeaderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LeaderServiceImpl implements LeaderService {

    private final StatMapper statMapper;

    public LeaderServiceImpl(StatMapper statMapper) {
        this.statMapper = statMapper;
    }

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // 从最新的统计快照中读取数据
        LambdaQueryWrapper<StatSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(StatSnapshot::getStatDate)
               .last("LIMIT 1");
        StatSnapshot latest = statMapper.selectOne(wrapper);

        // 真实数据应从各业务服务聚合获取，此处使用快照数据作为近似值
        // 如需精确数据，应注入 StudentMapper / TeacherMapper / CourseMapper 等并通过 Feign 或直接查询各业务库
        if (latest != null) {
            dashboard.put("totalStudents", latest.getTotalStudents());
            dashboard.put("totalTeachers", latest.getTotalTeachers());
            dashboard.put("totalCourses", latest.getTotalCourses());
            dashboard.put("totalEnrollments", latest.getTotalEnrollments());
            dashboard.put("avgScore", latest.getAvgScore());
            dashboard.put("failCount", latest.getFailCount());
        } else {
            // 无快照数据时返回占位值
            dashboard.put("totalStudents", 0);
            dashboard.put("totalTeachers", 0);
            dashboard.put("totalCourses", 0);
            dashboard.put("totalEnrollments", 0);
            dashboard.put("avgScore", BigDecimal.ZERO);
            dashboard.put("failCount", 0);
        }

        // 以下字段不在 StatSnapshot 中，使用占位值
        // 真实数据应从 student-service 的学院/专业/班级/毕业审核表中聚合
        dashboard.put("totalColleges", 0);
        dashboard.put("totalMajors", 0);
        dashboard.put("totalClasses", 0);
        dashboard.put("totalReviewed", 0);
        dashboard.put("totalPassed", 0);
        dashboard.put("graduationRate", BigDecimal.ZERO);

        return dashboard;
    }
}
