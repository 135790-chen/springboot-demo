package com.example.demo.common.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.demo.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Sentinel 统一限流/熔断异常处理器 —— 返回 JSON 格式 Result 而非默认白页
 */
@Component
public class SentinelBlockHandler implements BlockExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SentinelBlockHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       BlockException ex) throws Exception {
        log.warn("Sentinel 触发限流/熔断: rule={}", ex.getRule());
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(Result.error(429, "系统繁忙，请稍后重试"))
        );
    }
}
