package com.example.demo.teaching.controller;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RequirePermission;
import com.example.demo.entity.TimeSlot;
import com.example.demo.teaching.service.TimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "时间段管理", description = "查询可用时间段")
@RestController
@RequestMapping("/api/edu/timeslot")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @Operation(summary = "获取全部时间段")
    @RequirePermission("course:view")
    @GetMapping("/all")
    public Result<List<TimeSlot>> listAll() {
        return Result.success(timeSlotService.listAll());
    }
}
