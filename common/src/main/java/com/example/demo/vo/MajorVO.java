package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业视图对象 — 包含所属学院名称
 */
@Data
public class MajorVO {

    private Long id;
    private String majorName;
    private String majorCode;
    private Long collegeId;

    /** 学院名称（JOIN edu_college） */
    private String collegeName;

    private Integer majorStatus;
    private String majorRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
