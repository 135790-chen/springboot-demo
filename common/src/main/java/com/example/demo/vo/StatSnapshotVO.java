package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计快照 VO
 */
@Data
@NoArgsConstructor
public class StatSnapshotVO {

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