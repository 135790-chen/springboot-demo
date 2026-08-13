package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学院实体
 */
@Data
@NoArgsConstructor
@TableName("edu_college")
public class College {

    @TableId(value = "college_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("college_name")
    @NotBlank(message = "学院名称不能为空")
    private String collegeName;

    @TableField("college_code")
    @NotBlank(message = "学院编码不能为空")
    private String collegeCode;

    /** 状态：1-正常 0-禁用 */
    @TableField("college_status")
    private Integer collegeStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("college_deleted")
    private Integer collegeDeleted;

    /** 备注 */
    @TableField("college_remark")
    private String collegeRemark;

    /** 创建时间 */
    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
