package com.example.demo.teaching.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.PlanCourse;
import com.example.demo.entity.TrainingPlan;
import com.example.demo.teaching.mapper.PlanCourseMapper;
import com.example.demo.teaching.service.TrainingPlanService;
import com.example.demo.vo.PlanCourseVO;
import com.example.demo.vo.TrainingPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "培养方案管理", description = "培养方案增删改查、关联课程查看")
@RestController
@RequestMapping("/api/edu/training-plan")
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @Autowired
    private PlanCourseMapper planCourseMapper;

    @Operation(summary = "分页查询培养方案（含专业名称）")
    @RequirePermission("training_plan:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) Integer planStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TrainingPlanVO> result = trainingPlanService.getPage(page, size, majorId, grade, planStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询培养方案详情")
    @RequirePermission("training_plan:view")
    @GetMapping("/{planId}")
    public Result<TrainingPlanVO> getById(@PathVariable Long planId) {
        TrainingPlanVO vo = trainingPlanService.getById(planId);
        return vo != null ? Result.success(vo) : Result.error(404, "培养方案不存在");
    }

    @Operation(summary = "新增培养方案")
    @RequirePermission("training_plan:manage")
    @PostMapping
    public Result<TrainingPlan> add(@RequestBody TrainingPlan plan) {
        return Result.success(trainingPlanService.add(plan));
    }

    @Operation(summary = "修改培养方案")
    @RequirePermission("training_plan:manage")
    @PutMapping
    public Result<String> update(@RequestBody TrainingPlan plan) {
        if (plan.getId() == null) return Result.error(400, "更新操作必须提供方案ID");
        return trainingPlanService.update(plan) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除培养方案（逻辑删除）")
    @RequirePermission("training_plan:manage")
    @DeleteMapping("/{planId}")
    public Result<String> delete(@PathVariable Long planId) {
        return trainingPlanService.delete(planId) ? Result.success("删除成功") : Result.error(404, "培养方案不存在");
    }

    @Operation(summary = "查询方案关联的课程列表")
    @RequirePermission("training_plan:view")
    @GetMapping("/{planId}/courses")
    public Result<List<PlanCourse>> courses(@PathVariable Long planId) {
        List<PlanCourse> courses = planCourseMapper.selectByPlanId(planId);
        return Result.success(courses);
    }

    @Operation(summary = "按学期分组查询方案课程（含课程名，体现时间逻辑顺序）")
    @RequirePermission("training_plan:view")
    @GetMapping("/{planId}/courses-by-semester")
    public Result<Map<String, Object>> coursesBySemester(@PathVariable Long planId) {
        List<PlanCourseVO> vos = planCourseMapper.selectPlanCourseVOsByPlanId(planId);
        // 按 semesterOrder 分组（null → "未分类"）
        LinkedHashMap<String, List<PlanCourseVO>> grouped = vos.stream()
                .collect(Collectors.groupingBy(
                        vo -> vo.getSemesterOrder() != null ? "第" + vo.getSemesterOrder() + "学期" : "未分类",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        return Result.success(Map.of(
                "groups", grouped,
                "total", vos.size()
        ));
    }
}