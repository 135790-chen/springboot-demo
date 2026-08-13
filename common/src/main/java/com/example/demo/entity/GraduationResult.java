package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 毕业审核结果实体
 */
@Data
@NoArgsConstructor
@TableName("edu_graduation_result")
public class GraduationResult {

    @TableId(value = "result_id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 学生ID */
    @TableField("student_id")
    private Long studentId;

    /** 培养方案ID */
    @TableField("plan_id")
    private Long planId;

    /** 已获总学分 */
    @TableField("total_earned_credits")
    private BigDecimal totalEarnedCredits;

    /** 必修已获学分 */
    @TableField("required_earned_credits")
    private BigDecimal requiredEarnedCredits;

    /** 专业选修已获学分 */
    @TableField("major_elective_earned_credits")
    private BigDecimal majorElectiveEarnedCredits;

    /** 通识选修已获学分 */
    @TableField("general_elective_earned_credits")
    private BigDecimal generalElectiveEarnedCredits;

    /** 是否通过：1-通过 0-不通过 */
    private Integer passed;

    /** 缺失项明细（JSON格式）*/
    @TableField("missing_items")
    private String missingItems;

    /** 审核时间 */
    @TableField("review_time")
    private LocalDateTime reviewTime;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;
}
