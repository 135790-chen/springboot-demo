package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 时间段实体 — 定义每周固定时段
 */
@Data
@NoArgsConstructor
@TableName("edu_time_slot")
public class TimeSlot {

    @TableId(value = "slot_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("slot_name")
    private String slotName;

    /** 1=周一 … 7=周日 */
    @TableField("day_of_week")
    private Integer dayOfWeek;

    /** 开始节次 (1-12) */
    @TableField("start_period")
    private Integer startPeriod;

    /** 结束节次 (1-12) */
    @TableField("end_period")
    private Integer endPeriod;

    @TableField("slot_status")
    private Integer slotStatus;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
}
