package com.example.demo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 毕业审核结果视图对象
 */
@Data
public class GraduationResultVO {

    /** 学生ID */
    private Long studentId;

    /** 学生学号 */
    private String studentNo;

    /** 学生姓名 */
    private String studentName;

    /** 年级 */
    private String grade;

    /** 专业名称 */
    private String majorName;

    /** 学院名称 */
    private String collegeName;

    /** 培养方案名称 */
    private String planName;

    /** 是否通过 */
    private boolean passed;

    /** 各类学分明细 */
    private List<CreditSummaryVO> creditDetails;

    /** 缺失项描述列表 */
    private List<String> missingItems;

    /** 已获总学分 */
    private BigDecimal totalEarnedCredits;

    /** 培养方案全部课程总学分（三维对比：已修/要求/最大） */
    private BigDecimal totalMaxCredits;

    /** 审核时间 */
    private LocalDateTime reviewTime;
}
