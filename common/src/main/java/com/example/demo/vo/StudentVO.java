package com.example.demo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生 VO — 分页查询结果，含关联班级名称和年级
 */
@Data
@NoArgsConstructor
public class StudentVO {

    private Long studentId;
    private String studentNo;
    private String studentName;
    private Integer gender;
    private String phone;
    private String email;
    private LocalDate birthday;
    private Long classId;
    private String className;       // 关联 edu_class.class_name
    private String grade;            // 关联 edu_class.grade
    private String enrollmentYear;
    private Integer studentStatus;
    private String studentRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
