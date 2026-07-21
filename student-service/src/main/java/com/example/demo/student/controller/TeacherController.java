package com.example.demo.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Teacher;
import com.example.demo.student.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "教师管理", description = "教师增删改查")
@RestController
@RequestMapping("/api/edu/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Operation(summary = "分页查询教师")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer teacherStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Teacher> result = teacherService.getPage(page, size, teacherNo, teacherName, title, teacherStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询教师详情")
    @GetMapping("/{teacherId}")
    public Result<Teacher> getById(@PathVariable Long teacherId) {
        Teacher t = teacherService.getById(teacherId);
        return t != null ? Result.success(t) : Result.error(404, "教师不存在");
    }

    @Operation(summary = "新增教师")
    @PostMapping
    public Result<Teacher> add(@Valid @RequestBody Teacher teacher) {
        return Result.success(teacherService.add(teacher));
    }

    @Operation(summary = "修改教师")
    @PutMapping
    public Result<String> update(@RequestBody Teacher teacher) {
        if (teacher.getId() == null) return Result.error(400, "更新操作必须提供教师ID");
        return teacherService.update(teacher) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除教师（逻辑删除）")
    @DeleteMapping("/{teacherId}")
    public Result<String> delete(@PathVariable Long teacherId) {
        return teacherService.delete(teacherId) ? Result.success("删除成功") : Result.error(404, "教师不存在");
    }
}
