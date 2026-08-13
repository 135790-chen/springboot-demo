package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教室实体
 */
@Data
@NoArgsConstructor
@TableName("edu_classroom")
public class Classroom {

    @TableId(value = "classroom_id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("classroom_name")
    private String classroomName;

    @TableField("classroom_code")
    private String classroomCode;

    @TableField("capacity")
    private Integer capacity;

    /** NORMAL / MULTIMEDIA / LAB / LECTURE_HALL */
    @TableField("classroom_type")
    private String classroomType;

    @TableField("location")
    private String location;

    @TableField("building")
    private String building;

    @TableField("floor")
    private Integer floor;

    @TableField("classroom_status")
    private Integer classroomStatus;

    @TableField("classroom_deleted")
    private Integer classroomDeleted;

    @TableField("classroom_remark")
    private String classroomRemark;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
