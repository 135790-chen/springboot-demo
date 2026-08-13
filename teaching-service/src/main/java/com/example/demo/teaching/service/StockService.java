package com.example.demo.teaching.service;

public interface StockService {

    int tryDeduct(Long courseId, Long studentId, String requestId);

    void initStock(Long courseId, int maxStudents);

    int initStockIfAbsent(Long courseId, java.util.function.Supplier<Integer> supplier);

    void rollbackStock(Long courseId, Long studentId);

    void confirmDedup(Long courseId, Long studentId);

    int getStock(Long courseId);
}
