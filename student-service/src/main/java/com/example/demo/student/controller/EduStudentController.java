package com.example.demo.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.Student;
import com.example.demo.student.service.StudentService;
import com.example.demo.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "学生管理（edu）", description = "学生增删改查（含班级关联）")
@RestController
@RequestMapping("/api/edu/student")
public class EduStudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "分页查询学生（含班级名称）")
    @RequirePermission("student:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer studentStatus,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String className,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentVO> result = studentService.getStudentPage(
                page, size, studentNo, studentName, classId, gender, studentStatus,
                grade, className);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询学生详情（含班级名称）")
    @RequirePermission("student:view")
    @GetMapping("/{studentId}")
    public Result<StudentVO> getById(@PathVariable Long studentId) {
        StudentVO vo = studentService.getStudentVOById(studentId);
        return vo != null ? Result.success(vo) : Result.error(404, "学生不存在");
    }

    @Operation(summary = "新增学生")
    @RequirePermission("student:manage")
    @PostMapping
    public Result<Student> add(@Valid @RequestBody Student student) {
        return Result.success(studentService.addStudent(student));
    }

    @Operation(summary = "修改学生")
    @RequirePermission("student:manage")
    @PutMapping
    public Result<String> update(@RequestBody Student student) {
        if (student.getId() == null) return Result.error(400, "更新操作必须提供学生ID");
        return studentService.updateStudent(student) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除学生（逻辑删除）")
    @RequirePermission("student:manage")
    @DeleteMapping("/{studentId}")
    public Result<String> delete(@PathVariable Long studentId) {
        return studentService.deleteStudent(studentId)
                ? Result.success("删除成功") : Result.error(404, "学生不存在");
    }
}
