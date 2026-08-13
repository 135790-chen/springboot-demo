package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 选课本地表 — 保证 Redis 扣库存 → Kafka → MySQL 的最终一致性
 *
 * status: 0=PENDING 1=SUCCESS 2=FAILED
 */
@Data
@NoArgsConstructor
@TableName("edu_enrollment_outbox")
public class EnrollmentOutbox {

    @TableId(value = "outbox_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("request_id")
    private String requestId;

    @TableField("student_id")
    private Long studentId;

    @TableField("course_id")
    private Long courseId;

    /** 0=PENDING 1=SUCCESS 2=FAILED */
    @TableField("status")
    private Integer status;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("error_msg")
    private String errorMsg;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
