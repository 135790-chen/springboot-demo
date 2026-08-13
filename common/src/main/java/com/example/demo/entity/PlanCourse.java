package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 培养方案-课程关联实体
 */
@Data
@NoArgsConstructor
@TableName("edu_plan_course")
public class PlanCourse {

    @TableId(value = "rel_id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 培养方案ID */
    @TableField("plan_id")
    @NotNull(message = "方案ID不能为空")
    private Long planId;

    /** 课程ID */
    @TableField("course_id")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 课程类别：REQUIRED-必修、MAJOR_ELECTIVE-专业选修、GENERAL_ELECTIVE-通识选修
     */
    @TableField("course_category")
    @NotBlank(message = "课程类别不能为空")
    private String courseCategory;

    /** 是否必选：1-必选 0-可选 */
    @TableField("is_required")
    private Integer isRequired;

    /** 学期顺序：1=第一学期, 2=第二学期, ... NULL=未指定 */
    @TableField("semester_order")
    private Integer semesterOrder;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;
}
