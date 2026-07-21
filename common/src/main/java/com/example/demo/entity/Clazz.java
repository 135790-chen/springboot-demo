package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("edu_class")
public class Clazz {

    @TableId(value = "class_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("class_name")
    @NotBlank(message = "班级名称不能为空")
    private String className;

    @TableField("class_code")
    @NotBlank(message = "班级编码不能为空")
    private String classCode;

    /** 年级（如：2026级） */
    private String grade;

    /** 专业 */
    private String major;

    /** 显示顺序 */
    @TableField("class_sort")
    private Integer classSort;

    /** 状态：1-正常 0-禁用 */
    @TableField("class_status")
    private Integer classStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("class_deleted")
    private Integer classDeleted;

    /** 备注 */
    @TableField("class_remark")
    private String classRemark;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;
}
