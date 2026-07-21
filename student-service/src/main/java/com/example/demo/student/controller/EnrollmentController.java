package com.example.demo.student.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Enrollment;
import com.example.demo.student.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "选课管理", description = "学生选课、退课、录入成绩")
@RestController
@RequestMapping("/api/edu/student-course")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Operation(summary = "学生选课")
    @PostMapping
    public Result<Enrollment> add(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");
        if (studentId == null || courseId == null) {
            return Result.error(400, "studentId 和 courseId 不能为空");
        }
        return Result.success(enrollmentService.addEnrollment(studentId, courseId));
    }

    @Operation(summary = "退课")
    @DeleteMapping("/{relId}")
    public Result<String> drop(@PathVariable Long relId) {
        return enrollmentService.dropEnrollment(relId)
                ? Result.success("退课成功") : Result.error(404, "选课记录不存在");
    }

    @Operation(summary = "录入成绩")
    @PutMapping("/{relId}/score")
    public Result<String> score(@PathVariable Long relId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal score = body.get("score");
        if (score == null) return Result.error(400, "score 不能为空");
        return enrollmentService.updateScore(relId, score)
                ? Result.success("成绩录入成功") : Result.error(404, "选课记录不存在");
    }
}
