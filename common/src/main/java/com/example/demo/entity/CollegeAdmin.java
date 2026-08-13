package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_college_admin")
public class CollegeAdmin {

    @TableId(value = "rel_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long collegeId;
    private LocalDateTime gmtCreate;
}
