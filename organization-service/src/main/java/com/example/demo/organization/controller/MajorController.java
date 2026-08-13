package com.example.demo.organization.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.Major;
import com.example.demo.organization.service.MajorService;
import com.example.demo.vo.MajorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "专业管理", description = "专业增删改查")
@RestController
@RequestMapping("/api/edu/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @Operation(summary = "分页查询专业")
    @RequirePermission("major:view")
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(required = false) String majorName,
            @RequestParam(required = false) String majorCode,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Integer majorStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MajorVO> result = majorService.getPage(page, size, majorName, majorCode, collegeId, majorStatus);
        return Result.success(Map.of(
                "records", result.getRecords(),
                "total", result.getTotal(),
                "page", result.getCurrent(),
                "size", result.getSize()
        ));
    }

    @Operation(summary = "查询专业详情")
    @RequirePermission("major:view")
    @GetMapping("/{majorId}")
    public Result<MajorVO> getById(@PathVariable Long majorId) {
        MajorVO m = majorService.getById(majorId);
        return m != null ? Result.success(m) : Result.error(404, "专业不存在");
    }

    @Operation(summary = "新增专业")
    @RequirePermission("major:manage")
    @PostMapping
    public Result<Major> add(@Valid @RequestBody Major major) {
        return Result.success(majorService.add(major));
    }

    @Operation(summary = "修改专业")
    @RequirePermission("major:manage")
    @PutMapping
    public Result<String> update(@RequestBody Major major) {
        if (major.getId() == null) return Result.error(400, "更新操作必须提供专业ID");
        return majorService.update(major) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @Operation(summary = "删除专业（逻辑删除）")
    @RequirePermission("major:manage")
    @DeleteMapping("/{majorId}")
    public Result<String> delete(@PathVariable Long majorId) {
        return majorService.delete(majorId) ? Result.success("删除成功") : Result.error(404, "专业不存在");
    }
}
