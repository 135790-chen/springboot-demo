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

import java.time.LocalDateTime;

/**
 * 专业实体
 */
@Data
@NoArgsConstructor
@TableName("edu_major")
public class Major {

    @TableId(value = "major_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("major_name")
    @NotBlank(message = "专业名称不能为空")
    private String majorName;

    @TableField("major_code")
    @NotBlank(message = "专业编码不能为空")
    private String majorCode;

    /** 所属学院ID */
    @TableField("college_id")
    @NotNull(message = "学院ID不能为空")
    private Long collegeId;

    /** 专业负责人ID（关联 sys_user） */
    @TableField("director_id")
    private Long directorId;

    /** 状态：1-正常 0-禁用 */
    @TableField("major_status")
    private Integer majorStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("major_deleted")
    private Integer majorDeleted;

    /** 备注 */
    @TableField("major_remark")
    private String majorRemark;

    /** 创建时间 */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
