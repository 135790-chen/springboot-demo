package com.example.demo.common.aop;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Long> lastCallTime = new ConcurrentHashMap<>();

    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint p, RateLimit limit) throws Throwable {
        String key = p.getSignature().toLongString();
        long now = System.currentTimeMillis();
        Long last = lastCallTime.get(key);

        if (last != null && (now - last) < limit.seconds() * 1000L) {
            return Result.error(429, "请求太频繁，请" + limit.seconds() + "秒后再试");
        }

        lastCallTime.put(key, now);
        return p.proceed();
    }
}