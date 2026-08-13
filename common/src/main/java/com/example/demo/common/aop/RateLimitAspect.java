package com.example.demo.common.aop;

import com.example.demo.common.Result;
import com.example.demo.common.annotation.RateLimit;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 方法级限流切面 — Redis 分布式限流，本地 ConcurrentHashMap 兜底
 */
@Aspect
@Component
@ConditionalOnClass(StringRedisTemplate.class)
public class RateLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitAspect.class);

    private static final String REDIS_KEY_PREFIX = "rate_limit:";

    /** Redis 不可用时本地兜底 */
    private final Map<String, Long> lastCallTime = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    void init() {
        if (redisTemplate == null) {
            log.warn("Redis 未配置，限流降级为进程内模式（多实例不共享）");
        }
    }

    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint p, RateLimit limit) throws Throwable {
        String key = REDIS_KEY_PREFIX + p.getSignature().toLongString();
        long seconds = limit.seconds();

        if (redisTemplate != null) {
            // Redis 分布式限流 — SET NX EX 原子操作，多实例共享
            Boolean ok = redisTemplate.opsForValue()
                    .setIfAbsent(key, String.valueOf(System.currentTimeMillis()),
                            seconds, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(ok)) {
                return Result.error(429, "请求太频繁，请" + seconds + "秒后再试");
            }
        } else {
            // 本地限流兜底（Redis 不可用时）
            long now = System.currentTimeMillis();
            Long last = lastCallTime.get(key);
            if (last != null && (now - last) < seconds * 1000L) {
                return Result.error(429, "请求太频繁，请" + seconds + "秒后再试");
            }
            lastCallTime.put(key, now);
        }

        return p.proceed();
    }
}