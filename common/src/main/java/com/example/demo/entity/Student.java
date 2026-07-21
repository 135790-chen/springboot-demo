package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("edu_student")
public class Student {

    @TableId(value = "student_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("student_no")
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @TableField("student_name")
    @NotBlank(message = "姓名不能为空")
    private String studentName;

    /** 性别：1-男 2-女 0-未知 */
    private Integer gender;

    private String phone;

    private String email;

    /** 出生日期 */
    private LocalDate birthday;

    /** 所属班级ID */
    private Long classId;

    /** 入学年份 */
    @TableField("enrollment_year")
    private String enrollmentYear;

    /** 状态：1-在读 2-休学 3-毕业 0-退学 */
    @TableField("student_status")
    private Integer studentStatus;

    /** 删除状态：1-删除 0-未删除 */
    @TableField("student_deleted")
    private Integer studentDeleted;

    /** 备注 */
    @TableField("student_remark")
    private String studentRemark;

    /** 年级（冗余字段，数据源为 edu_class.grade） */
    private String grade;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;
}
