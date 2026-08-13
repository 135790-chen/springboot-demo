package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_school")
public class School {

    @TableId(value = "school_id", type = IdType.ASSIGN_ID)
    private Long id;
    private String schoolName;
    private String schoolCode;
    private Integer schoolStatus;
    private Integer schoolDeleted;
    private String schoolRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
