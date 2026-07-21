package com.example.demo.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Clazz;
import com.example.demo.student.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "班级管理", description = "班级增删改查")
@RestController
@RequestMapping("/api/edu/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @Operation(summary = "分页查询班级")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String classCode,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer classStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Clazz> result = classService.getPage(page, size, classCode, className, grade, major, classStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询班级详情")
    @GetMapping("/{classId}")
    public Result<Clazz> getById(@PathVariable Long classId) {
        Clazz c = classService.getById(classId);
        return c != null ? Result.success(c) : Result.error(404, "班级不存在");
    }

    @Operation(summary = "新增班级")
    @PostMapping
    public Result<Clazz> add(@Valid @RequestBody Clazz clazz) {
        return Result.success(classService.add(clazz));
    }

    @Operation(summary = "修改班级")
    @PutMapping
    public Result<String> update(@RequestBody Clazz clazz) {
        if (clazz.getId() == null) return Result.error(400, "更新操作必须提供班级ID");
        return classService.update(clazz) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除班级（逻辑删除）")
    @DeleteMapping("/{classId}")
    public Result<String> delete(@PathVariable Long classId) {
        return classService.delete(classId) ? Result.success("删除成功") : Result.error(404, "班级不存在");
    }
}
