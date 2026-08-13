package com.example.demo.teaching.controller;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.GraduationResult;
import com.example.demo.teaching.service.GraduationService;
import com.example.demo.vo.GraduationResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "毕业审核", description = "毕业资格审核及历史查询")
@RestController
@RequestMapping("/api/edu/graduation")
public class GraduationController {

    @Autowired
    private GraduationService graduationService;

    @Operation(summary = "毕业资格审核", description = "根据培养方案规则逐一比对学分要求，返回审核结果")
    @RequirePermission("graduation:review")
    @GetMapping("/check/{studentId}")
    public Result<GraduationResultVO> check(@PathVariable Long studentId) {
        return Result.success(graduationService.checkGraduation(studentId));
    }

    @Operation(summary = "查询历史审核记录")
    @RequirePermission("graduation:view")
    @GetMapping("/results/{studentId}")
    public Result<List<GraduationResult>> results(@PathVariable Long studentId) {
        return Result.success(graduationService.getHistory(studentId));
    }
}
