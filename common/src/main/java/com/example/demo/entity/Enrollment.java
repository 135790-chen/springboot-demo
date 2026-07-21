package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("edu_student_course")
public class Enrollment {

    @TableId(value = "rel_id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 课程ID */
    private Long courseId;

    /** 成绩 */
    private BigDecimal score;

    /** 状态：1-在读 2-已修完 3-退课 */
    @TableField("rel_status")
    private Integer relStatus;

    /** 创建时间 */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;
}
