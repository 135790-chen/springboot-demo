package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程 VO — 分页查询结果，含关联教师姓名
 */
@Data
@NoArgsConstructor
public class CourseVO {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private Integer courseHours;
    private String courseType;
    private Long teacherId;
    private String teacherName;      // 关联 edu_teacher.teacher_name
    private String semester;
    private Integer courseStatus;
    private String courseRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
