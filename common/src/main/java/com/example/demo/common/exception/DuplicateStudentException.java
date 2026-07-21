package com.example.demo.common.exception;

/**
 * 学生数据重复异常 — 当尝试插入已存在的唯一字段值时抛出
 */
public class DuplicateStudentException extends RuntimeException {

    public DuplicateStudentException(String message) {
        super(message);
    }
}
