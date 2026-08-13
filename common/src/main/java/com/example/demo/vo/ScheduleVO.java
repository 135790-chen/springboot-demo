package com.example.demo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 排课结果视图对象 — 多表 JOIN 扁平化展示
 */
@Data
public class ScheduleVO {

    private Long scheduleId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private BigDecimal credit;
    private Long teacherId;
    private String teacherName;
    private Long classroomId;
    private String classroomName;
    private String classroomCode;
    private String classroomType;
    private Integer capacity;
    private Long timeSlotId;
    private String slotName;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private Long clazzId;
    private String className;
    private String semester;
    private Integer weekStart;
    private Integer weekEnd;
    private Integer scheduleStatus;
    private LocalDateTime gmtCreate;
}
