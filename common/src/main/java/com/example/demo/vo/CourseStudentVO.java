package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程选课学生 VO — 三表关联结果（edu_student_course + edu_student + edu_class）
 */
@Data
@NoArgsConstructor
public class CourseStudentVO {

    private Long relId;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Integer gender;
    private String className;        // 关联 edu_class.class_name
    private BigDecimal score;
    private Integer relStatus;       // 1-在读 2-已修完 3-退课
    private LocalDateTime gmtCreate;
}
