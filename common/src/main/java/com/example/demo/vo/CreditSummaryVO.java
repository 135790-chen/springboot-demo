package com.example.demo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 学分汇总视图对象（毕业审核中间结果）
 */
@Data
public class CreditSummaryVO {

    /** 课程类别：REQUIRED / MAJOR_ELECTIVE / GENERAL_ELECTIVE */
    private String category;

    /** 类别中文名 */
    private String categoryName;

    /** 培养方案要求的最低学分 */
    private BigDecimal requiredCredits;

    /** 学生已获得学分 */
    private BigDecimal earnedCredits;

    /** 培养方案该分类下全部课程的总学分（最大学分上限） */
    private BigDecimal maxCredits;

    /** 是否达标 */
    private boolean satisfied;

    /** 差值（负数表示不足） */
    private BigDecimal gap;
}
