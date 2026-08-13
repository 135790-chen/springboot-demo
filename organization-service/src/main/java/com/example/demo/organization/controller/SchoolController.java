package com.example.demo.organization.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.School;
import com.example.demo.organization.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学校管理", description = "学校的增删改查")
@RestController
@RequestMapping("/api/edu/school")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @Operation(summary = "分页查询学校")
    @RequirePermission("school:view")
    @GetMapping("/page")
    public Result<Page<School>> page(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String schoolCode,
            @RequestParam(required = false) Integer schoolStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<School> result = schoolService.getPage(page, size, schoolName, schoolCode, schoolStatus);
        return Result.success(result);
    }

    @Operation(summary = "查询学校详情")
    @RequirePermission("school:view")
    @GetMapping("/{schoolId}")
    public Result<School> getById(@PathVariable Long schoolId) {
        School school = schoolService.getById(schoolId);
        return school != null ? Result.success(school) : Result.error(404, "学校不存在");
    }

    @Operation(summary = "新增学校")
    @RequirePermission("school:manage")
    @PostMapping
    public Result<School> add(@RequestBody School school) {
        return Result.success(schoolService.add(school));
    }

    @Operation(summary = "修改学校")
    @RequirePermission("school:manage")
    @PutMapping
    public Result<Object> update(@RequestBody School school) {
        if (school.getId() == null) {
            return Result.error(400, "更新操作必须提供学校ID");
        }
        return schoolService.update(school) ? Result.success(true) : Result.error(400, "更新失败");
    }

    @Operation(summary = "删除学校")
    @RequirePermission("school:manage")
    @DeleteMapping("/{schoolId}")
    public Result<Boolean> delete(@PathVariable Long schoolId) {
        schoolService.delete(schoolId);
        return Result.success(true);
    }

    @Operation(summary = "查询所有学校列表")
    @RequirePermission("school:view")
    @GetMapping("/all")
    public Result<List<School>> listAll() {
        return Result.success(schoolService.listAll());
    }
}
