package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生选课 VO — 三表关联结果（edu_student_course + edu_course + edu_teacher）
 */
@Data
@NoArgsConstructor
public class StudentCourseVO {

    private Long relId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private String courseType;
    private Long teacherId;
    private String teacherName;      // 关联 edu_teacher.teacher_name
    private String semester;
    private BigDecimal score;
    private Integer relStatus;       // 1-在读 2-已修完 3-退课
}
