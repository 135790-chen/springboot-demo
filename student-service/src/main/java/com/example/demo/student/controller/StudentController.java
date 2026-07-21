package com.example.demo.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Student;
import com.example.demo.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "学生管理", description = "学生增删改查、按年级筛选、按姓名搜索")
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "查询所有学生")
    @GetMapping
    public Result<List<Student>> list() {
        List<Student> students = studentService.getAllStudents();
        return Result.success(students);
    }

    @Operation(summary = "分页查询学生")
    @GetMapping("/page")
    public Result<Map<String, Object>> listByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Student> result = studentService.getStudentsByPage(page, size);
        return Result.success(Map.of(
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize(),
                "rows", result.getRecords()
        ));
    }

    @Operation(summary = "根据 ID 查询学生")
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return Result.error(404, "学生不存在，ID: " + id);
        }
        return Result.success(student);
    }

    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Student> add(@Valid @RequestBody Student student) {
        return Result.success(studentService.addStudent(student));
    }

    @Operation(summary = "更新学生", description = "必须提供学生 ID")
    @PutMapping
    public Result<String> update(@RequestBody Student student) {
        if (student.getId() == null) {
            return Result.error(400, "更新操作必须提供学生 ID");
        }
        boolean success = studentService.updateStudent(student);
        return success ? Result.success("更新成功") : Result.error("更新失败，学生不存在");
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = studentService.deleteStudent(id);
        return success ? Result.success("删除成功") : Result.error(404, "删除失败，学生不存在，ID: " + id);
    }

    @Operation(summary = "按年级筛选学生")
    @GetMapping("/grade/{grade}")
    public Result<List<Student>> listByGrade(@PathVariable String grade) {
        List<Student> students = studentService.getStudentsByGrade(grade);
        return Result.success(students);
    }

    @Operation(summary = "按姓名模糊搜索")
    @GetMapping("/search")
    public Result<List<Student>> search(@RequestParam String keyword) {
        List<Student> students = studentService.searchStudentsByName(keyword);
        return Result.success(students);
    }
}
