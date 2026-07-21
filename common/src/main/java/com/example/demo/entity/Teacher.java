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
@TableName("edu_teacher")
public class Teacher {

    @TableId(value = "teacher_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("teacher_no")
    @NotBlank(message = "教师工号不能为空")
    private String teacherNo;

    @TableField("teacher_name")
    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;

    /** 性别：1-男 2-女 0-未知 */
    private Integer gender;

    private String phone;

    private String email;

    /** 职称（教授/副教授/讲师/助教） */
    private String title;

    /** 状态：1-在职 0-离职 */
    @TableField("teacher_status")
    private Integer teacherStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("teacher_deleted")
    private Integer teacherDeleted;

    /** 备注 */
    @TableField("teacher_remark")
    private String teacherRemark;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;
}
