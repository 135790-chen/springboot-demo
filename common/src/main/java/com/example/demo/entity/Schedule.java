package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 排课结果实体
 */
@Data
@NoArgsConstructor
@TableName("edu_schedule")
public class Schedule {

    @TableId(value = "schedule_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("classroom_id")
    private Long classroomId;

    @TableField("time_slot_id")
    private Long timeSlotId;

    @TableField("clazz_id")
    private Long clazzId;

    @TableField("semester")
    private String semester;

    @TableField("week_start")
    private Integer weekStart;

    @TableField("week_end")
    private Integer weekEnd;

    @TableField("schedule_status")
    private Integer scheduleStatus;

    @TableField("schedule_deleted")
    private Integer scheduleDeleted;

    @TableField("schedule_remark")
    private String scheduleRemark;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
