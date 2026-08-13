package com.example.demo.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.Classroom;
import com.example.demo.teaching.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "教室管理", description = "教室增删改查")
@RestController
@RequestMapping("/api/edu/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Operation(summary = "分页查询教室")
    @RequirePermission("course:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Classroom> result = classroomService.getPage(page, size, keyword);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询教室详情")
    @RequirePermission("course:view")
    @GetMapping("/{id}")
    public Result<Classroom> getById(@PathVariable Long id) {
        Classroom c = classroomService.getById(id);
        return c != null ? Result.success(c) : Result.error(404, "教室不存在");
    }

    @Operation(summary = "新增教室")
    @RequirePermission("course:manage")
    @PostMapping
    public Result<Classroom> add(@RequestBody Classroom classroom) {
        return Result.success(classroomService.add(classroom));
    }

    @Operation(summary = "修改教室")
    @RequirePermission("course:manage")
    @PutMapping
    public Result<String> update(@RequestBody Classroom classroom) {
        if (classroom.getId() == null) return Result.error(400, "更新操作必须提供教室ID");
        return classroomService.update(classroom) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除教室（逻辑删除）")
    @RequirePermission("course:manage")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return classroomService.delete(id) ? Result.success("删除成功") : Result.error(404, "教室不存在");
    }
}
