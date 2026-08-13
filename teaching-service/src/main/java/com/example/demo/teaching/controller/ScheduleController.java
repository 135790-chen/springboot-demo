package com.example.demo.teaching.controller;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.teaching.service.SchedulingService;
import com.example.demo.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "排课管理", description = "自动排课、排课结果查询")
@RestController
@RequestMapping("/api/edu/schedule")
public class ScheduleController {

    @Autowired
    private SchedulingService schedulingService;

    @Operation(summary = "触发自动排课", description = "贪心算法为指定学期生成排课方案，可限定专业")
    @RequirePermission("course:manage")
    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(
            @RequestParam String semester,
            @RequestParam(required = false) Long majorId) {
        Map<String, Object> result = schedulingService.generate(semester, majorId);
        return Result.success(result);
    }

    @Operation(summary = "清空某学期排课")
    @RequirePermission("course:manage")
    @DeleteMapping("/clear")
    public Result<String> clear(@RequestParam String semester) {
        int n = schedulingService.clear(semester);
        return Result.success("已清空 " + n + " 条排课记录（学期=" + semester + "）");
    }

    @Operation(summary = "分页查询排课结果")
    @RequirePermission("course:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long clazzId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(schedulingService.getPage(semester, clazzId, teacherId, page, size));
    }

    @Operation(summary = "按教师查询课表")
    @RequirePermission("course:view")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<ScheduleVO>> teacherSchedule(
            @PathVariable Long teacherId,
            @RequestParam String semester) {
        return Result.success(schedulingService.getByTeacher(teacherId, semester));
    }

    @Operation(summary = "按教室查询课表")
    @RequirePermission("course:view")
    @GetMapping("/classroom/{classroomId}")
    public Result<List<ScheduleVO>> classroomSchedule(
            @PathVariable Long classroomId,
            @RequestParam String semester) {
        return Result.success(schedulingService.getByClassroom(classroomId, semester));
    }

    @Operation(summary = "按班级查询课表")
    @RequirePermission("course:view")
    @GetMapping("/class/{clazzId}")
    public Result<List<ScheduleVO>> classSchedule(
            @PathVariable Long clazzId,
            @RequestParam String semester) {
        return Result.success(schedulingService.getByClass(clazzId, semester));
    }
}
