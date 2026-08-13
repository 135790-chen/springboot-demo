package com.example.demo.vo;

import lombok.Data;

/**
 * 培养方案课程关联视图（含课程基本信息）
 */
@Data
public class PlanCourseVO {

    private Long relId;
    private Long planId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String courseCategory;
    private Integer isRequired;
    private Integer semesterOrder;
}
