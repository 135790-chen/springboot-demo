package com.example.demo.teaching.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.JwtUtil;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.dto.EnrollmentMessage;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.teaching.mapper.CourseMapper;
import com.example.demo.teaching.service.EnrollmentService;
import com.example.demo.teaching.service.StockService;
import com.example.demo.vo.StudentCourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "选课管理", description = "学生选课、退课、录入成绩、秒杀选课")
@RestController
@RequestMapping("/api/edu/student-course")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StockService stockService;
    @Autowired
    private JwtUtil jwtUtil;

    private Long extractStudentIdFromJwt(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        String token = auth.substring(7);
        if (!jwtUtil.validateToken(token)) return null;
        return jwtUtil.getStudentIdFromToken(token);
    }

    @Operation(summary = "秒杀课程列表", description = "返回选修课及 Redis 实时库存（学生用）")
    @RequirePermission("enrollment:view")
    @GetMapping("/seckill/list")
    public Result<List<Map<String, Object>>> seckillList(HttpServletRequest request) {
        Long myStudentId = (Long) request.getAttribute("studentId");
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getCourseStatus, 1)
                        .eq(Course::getCourseDeleted, 0)
                        .eq(Course::getCourseType, "elective"));
        List<Map<String, Object>> list = courses.stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            int max = c.getMaxStudents() != null ? c.getMaxStudents() : 100;
            m.put("courseId", c.getId());
            m.put("courseName", c.getCourseName());
            m.put("courseCode", c.getCourseCode());
            m.put("credit", c.getCredit());
            m.put("courseType", c.getCourseType());
            m.put("semester", c.getSemester());
            m.put("maxStudents", max);
            stockService.initStockIfAbsent(c.getId(), () -> max);
            int stock = stockService.getStock(c.getId());
            m.put("remaining", Math.max(0, stock));
            m.put("myStudentId", myStudentId);
            return m;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Operation(summary = "学生选课（普通）")
    @RequirePermission("enrollment:manage")
    @PostMapping
    public Result<Enrollment> add(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");
        if (studentId == null || courseId == null) {
            return Result.error(400, "studentId 和 courseId 不能为空");
        }
        return Result.success(enrollmentService.addEnrollment(studentId, courseId));
    }

    @Operation(summary = "秒杀选课", description = "学生从 JWT 取身份，Redis Lua 原子扣库存")
    @PostMapping("/seckill")
    public Result<String> seckill(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long courseId = body.get("courseId");
        if (courseId == null) {
            return Result.error(400, "courseId 不能为空");
        }
        Long studentId = extractStudentIdFromJwt(request);
        if (studentId == null) {
            return Result.error(403, "只有学生账号才能抢课，管理员请使用选课操作");
        }
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error(404, "课程不存在");
        }
        if (!"elective".equals(course.getCourseType())) {
            return Result.error(400, "只能抢选修课，必修课由管理员分配");
        }
        int code = enrollmentService.seckillEnrollment(studentId, courseId);
        if (code == 1) {
            return Result.success("预扣成功，订单处理中");
        } else if (code == -1) {
            return Result.error(409, "您已选此课程，请勿重复提交");
        } else {
            return Result.error(503, "课程已抢完，下次再来");
        }
    }

    @Operation(summary = "查询学生选课列表", description = "根据学生ID分页查询其所有选课记录（含课程和教师信息）")
    @RequirePermission("enrollment:view")
    @GetMapping("/student/{studentId}")
    public Result<Map<String, Object>> getStudentCourses(
            @PathVariable Long studentId,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Integer relStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentCourseVO> result = enrollmentService.getCoursesByStudentId(
                studentId, page, size, courseName, courseType, relStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "确认选课（内部回调）", description = "将预扣记录确认写入数据库")
    @PostMapping("/confirm")
    public Result<Enrollment> confirm(@RequestBody EnrollmentMessage message) {
        Enrollment enrollment = enrollmentService.confirmEnrollment(message);
        if (enrollment == null) {
            return Result.error(404, "找不到预扣记录");
        }
        return Result.success(enrollment);
    }

    @Operation(summary = "退课")
    @RequirePermission("enrollment:manage")
    @DeleteMapping("/{relId}")
    public Result<String> drop(@PathVariable Long relId) {
        return enrollmentService.dropEnrollment(relId)
                ? Result.success("退课成功") : Result.error(404, "选课记录不存在");
    }

    @Operation(summary = "录入成绩")
    @RequirePermission("score:input")
    @PutMapping("/{relId}/score")
    public Result<String> score(@PathVariable Long relId, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal score = body.get("score");
        if (score == null) return Result.error(400, "score 不能为空");
        return enrollmentService.updateScore(relId, score)
                ? Result.success("成绩录入成功") : Result.error(404, "选课记录不存在");
    }
}
