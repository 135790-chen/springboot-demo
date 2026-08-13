package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_supervisor")
public class Supervisor {

    @TableId(value = "supervisor_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String supervisorNo;
    private String supervisorName;
    private String phone;
    private String email;
    private Integer supervisorStatus;
    private Integer supervisorDeleted;
    private String supervisorRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
