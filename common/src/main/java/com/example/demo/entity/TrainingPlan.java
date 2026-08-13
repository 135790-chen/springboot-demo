package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 培养方案实体
 */
@Data
@NoArgsConstructor
@TableName("edu_training_plan")
public class TrainingPlan {

    @TableId(value = "plan_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("plan_name")
    @NotBlank(message = "方案名称不能为空")
    private String planName;

    /** 专业ID */
    @TableField("major_id")
    @NotNull(message = "专业ID不能为空")
    private Long majorId;

    /** 适用年级（入学年份） */
    @NotBlank(message = "年级不能为空")
    private String grade;

    /** 版本号 */
    private Integer version;

    /** 必修课最低总学分 */
    @TableField("total_required_credits")
    @NotNull(message = "必修总学分不能为空")
    private BigDecimal totalRequiredCredits;

    /** 专业选修课最低学分 */
    @TableField("major_elective_min_credits")
    @NotNull(message = "专业选修最低学分不能为空")
    private BigDecimal majorElectiveMinCredits;

    /** 通识选修课最低学分 */
    @TableField("general_elective_min_credits")
    @NotNull(message = "通识选修最低学分不能为空")
    private BigDecimal generalElectiveMinCredits;

    /** 状态：1-启用 0-停用 */
    @TableField("plan_status")
    private Integer planStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("plan_deleted")
    private Integer planDeleted;

    /** 备注 */
    @TableField("plan_remark")
    private String planRemark;

    /** 创建时间 */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
