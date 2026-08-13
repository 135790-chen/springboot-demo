package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("edu_course")
public class Course {

    @TableId(value = "course_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("course_name")
    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    @TableField("course_code")
    @NotBlank(message = "课程编码不能为空")
    private String courseCode;

    /** 学分 */
    private BigDecimal credit;

    /** 学时 */
    @TableField("course_hours")
    private Integer courseHours;

    /** 课程最大容量 */
    @TableField("max_students")
    private Integer maxStudents;

    /** 课程类型：required-必修 elective-选修 */
    @TableField("course_type")
    private String courseType;

    /** 授课教师ID */
    private Long teacherId;

    /** 开课学期（如：2026-2027-1） */
    private String semester;

    /** 状态：1-开课 0-停课 */
    @TableField("course_status")
    private Integer courseStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("course_deleted")
    private Integer courseDeleted;

    /** 备注 */
    @TableField("course_remark")
    private String courseRemark;

    /** 创建时间 */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
