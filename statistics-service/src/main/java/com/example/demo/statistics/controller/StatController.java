package com.example.demo.statistics.controller;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.StatSnapshot;
import com.example.demo.statistics.service.StatService;
import com.example.demo.statistics.task.StatSnapshotTask;
import com.example.demo.vo.StatSnapshotVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "统计管理", description = "教育数据统计查询")
@RestController
@RequestMapping("/api/edu/stat")
public class StatController {

    @Autowired
    private StatService statService;

    @Autowired
    private StatSnapshotTask statSnapshotTask;

    @Operation(summary = "查询最近 30 天统计快照")
    @GetMapping("/recent")
    @RequirePermission("stat:view")
    public Result<List<StatSnapshotVO>> recent() {
        return Result.success(statService.getRecent30());
    }

    @Operation(summary = "查询最新统计快照")
    @GetMapping("/latest")
    @RequirePermission("stat:view")
    public Result<StatSnapshotVO> latest() {
        StatSnapshotVO latest = statService.getLatest();
        if (latest == null) {
            // 无快照时自动生成一份
            StatSnapshot snapshot = statSnapshotTask.generateSnapshot();
            latest = statService.save(snapshot);
        }
        return Result.success(latest);
    }

    @Operation(summary = "手动触发统计快照（从各服务实时聚合数据）")
    @PostMapping("/snapshot")
    @RequirePermission("stat:view")
    public Result<StatSnapshotVO> triggerSnapshot() {
        StatSnapshot snapshot = statSnapshotTask.generateSnapshot();
        StatSnapshotVO vo = statService.save(snapshot);
        return Result.success(vo);
    }
}
