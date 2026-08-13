package com.example.demo.organization.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.College;
import com.example.demo.entity.Major;
import com.example.demo.organization.service.CollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "学院管理", description = "学院增删改查")
@RestController
@RequestMapping("/api/edu/college")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @Operation(summary = "分页查询学院")
    @RequirePermission("college:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String collegeName,
            @RequestParam(required = false) String collegeCode,
            @RequestParam(required = false) Integer collegeStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<College> result = collegeService.getPage(page, size, collegeName, collegeCode, collegeStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询学院详情")
    @RequirePermission("college:view")
    @GetMapping("/{collegeId}")
    public Result<College> getById(@PathVariable Long collegeId) {
        College c = collegeService.getById(collegeId);
        return c != null ? Result.success(c) : Result.error(404, "学院不存在");
    }

    @Operation(summary = "新增学院")
    @RequirePermission("college:manage")
    @PostMapping
    public Result<College> add(@Valid @RequestBody College college) {
        return Result.success(collegeService.add(college));
    }

    @Operation(summary = "修改学院")
    @RequirePermission("college:manage")
    @PutMapping
    public Result<String> update(@RequestBody College college) {
        if (college.getId() == null) return Result.error(400, "更新操作必须提供学院ID");
        return collegeService.update(college) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除学院（逻辑删除）")
    @RequirePermission("college:manage")
    @DeleteMapping("/{collegeId}")
    public Result<String> delete(@PathVariable Long collegeId) {
        return collegeService.delete(collegeId) ? Result.success("删除成功") : Result.error(404, "学院不存在");
    }

    @Operation(summary = "查询学院下的专业列表")
    @RequirePermission("college:view")
    @GetMapping("/{collegeId}/majors")
    public Result<List<Major>> listMajors(@PathVariable Long collegeId) {
        return Result.success(collegeService.listMajorsByCollegeId(collegeId));
    }
}
