package com.example.demo.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Course;
import com.example.demo.student.service.CourseService;
import com.example.demo.vo.CourseStudentVO;
import com.example.demo.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "课程管理", description = "课程增删改查、选课学生列表")
@RestController
@RequestMapping("/api/edu/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "分页查询课程（含教师姓名）")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer courseStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseVO> result = courseService.getPage(page, size, courseCode, courseName,
                courseType, teacherId, semester, courseStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询课程详情（含教师姓名）")
    @GetMapping("/{courseId}")
    public Result<CourseVO> getById(@PathVariable Long courseId) {
        CourseVO vo = courseService.getById(courseId);
        return vo != null ? Result.success(vo) : Result.error(404, "课程不存在");
    }

    @Operation(summary = "新增课程")
    @PostMapping
    public Result<Course> add(@Valid @RequestBody Course course) {
        return Result.success(courseService.add(course));
    }

    @Operation(summary = "修改课程")
    @PutMapping
    public Result<String> update(@RequestBody Course course) {
        if (course.getId() == null) return Result.error(400, "更新操作必须提供课程ID");
        return courseService.update(course) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除课程（逻辑删除）")
    @DeleteMapping("/{courseId}")
    public Result<String> delete(@PathVariable Long courseId) {
        return courseService.delete(courseId) ? Result.success("删除成功") : Result.error(404, "课程不存在");
    }

    @Operation(summary = "查询课程下的选课学生（三表关联）")
    @GetMapping("/{courseId}/students")
    public Result<Map<String, Object>> students(
            @PathVariable Long courseId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) Integer relStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseStudentVO> result = courseService.getStudentsByCourseId(
                courseId, page, size, studentName, studentNo, relStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }
}
