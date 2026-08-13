package com.example.demo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 培养方案视图对象 — 包含所属专业名称
 */
@Data
public class TrainingPlanVO {

    private Long id;
    private String planName;
    private Long majorId;

    /** 专业名称（JOIN edu_major） */
    private String majorName;

    private String grade;
    private Integer version;
    private BigDecimal totalRequiredCredits;
    private BigDecimal majorElectiveMinCredits;
    private BigDecimal generalElectiveMinCredits;
    private Integer planStatus;
    private String planRemark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
