package com.example.demo.student.controller;

import com.example.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "统计管理", description = "教育数据统计查询")
@RestController
@RequestMapping("/api/edu/stat")
public class StatController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Operation(summary = "查询最近 30 天统计快照")
    @GetMapping("/recent")
    public Result<List<Map<String, Object>>> recent() {
        String sql = """
                SELECT stat_id, stat_date, total_students, total_teachers, total_courses,
                       total_enrollments, avg_score, fail_count, gmt_create
                FROM edu_stat ORDER BY stat_date DESC LIMIT 30
                """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return Result.success(rows);
    }

    @Operation(summary = "查询最新统计快照")
    @GetMapping("/latest")
    public Result<Map<String, Object>> latest() {
        String sql = """
                SELECT stat_id, stat_date, total_students, total_teachers, total_courses,
                       total_enrollments, avg_score, fail_count, gmt_create
                FROM edu_stat ORDER BY stat_date DESC LIMIT 1
                """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return Result.success(rows.isEmpty() ? null : rows.get(0));
    }
}
