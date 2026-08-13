package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育统计快照实体
 */
@Data
@NoArgsConstructor
@TableName("edu_stat")
public class StatSnapshot {

    @TableId(type = IdType.ASSIGN_ID)
    private Long statId;
    private LocalDate statDate;
    private Integer totalStudents;
    private Integer totalTeachers;
    private Integer totalCourses;
    private Integer totalEnrollments;
    private BigDecimal avgScore;
    private Integer failCount;
    private LocalDateTime gmtCreate;
}