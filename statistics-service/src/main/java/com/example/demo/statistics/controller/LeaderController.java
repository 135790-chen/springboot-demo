package com.example.demo.statistics.controller;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.statistics.service.LeaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 校领导数据驾驶舱
 */
@RestController
@RequestMapping("/api/leader")
public class LeaderController {

    private final LeaderService leaderService;

    public LeaderController(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    @GetMapping("/dashboard")
    @RequirePermission("dashboard:view")
    public Result<?> dashboard() {
        return Result.success(leaderService.getDashboard());
    }
}
