package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("sys_teacher_evaluation")
public class TeacherEvaluation {

    @TableId(value = "eval_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long supervisorId;
    private Long teacherId;
    private Long courseId;
    private BigDecimal score;
    private String comment;
    private LocalDateTime evalTime;
    private LocalDateTime gmtCreate;
}
