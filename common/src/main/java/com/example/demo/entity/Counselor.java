package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_counselor")
public class Counselor {

    @TableId(value = "counselor_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String counselorNo;
    private String counselorName;
    private String phone;
    private String email;
    private Integer counselorStatus;
    private Integer counselorDeleted;
    private String counselorRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
