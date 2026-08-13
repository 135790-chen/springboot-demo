package com.example.demo.common.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import org.springframework.stereotype.Component;

/**
 * Sentinel URL 归一化 — 将含数字 ID 的路径聚合为通配模式
 * /students/123 → /students/*
 * /api/edu/course/456 → /api/edu/course/*
 */
@Component
public class SentinelUrlCleaner implements UrlCleaner {

    @Override
    public String clean(String originUrl) {
        // 把路径中的纯数字段替换为 *
        return originUrl.replaceAll("/\\d+(?=/|$)", "/*");
    }
}
