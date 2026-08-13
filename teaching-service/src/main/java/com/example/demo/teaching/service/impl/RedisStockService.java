package com.example.demo.teaching.service.impl;

import com.example.demo.teaching.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class RedisStockService implements StockService {

    private static final Logger log = LoggerFactory.getLogger(RedisStockService.class);

    private static final String STOCK_KEY_PREFIX = "course:stock:";
    private static final String DEDUP_KEY_PREFIX = "enrollment:dedup:";
    private static final int DEDUP_TTL_SECONDS = 300;

    private DefaultRedisScript<Long> seckillScript;

    @PostConstruct
    void initScript() {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setResultType(Long.class);
        seckillScript.setScriptText("""
            local stock_key = KEYS[1]
            local dedup_key = KEYS[2]
            local request_id = ARGV[1]

            if redis.call('EXISTS', dedup_key) == 1 then
                return -1
            end

            local stock = tonumber(redis.call('GET', stock_key) or '0')
            if stock <= 0 then
                return 0
            end

            redis.call('DECR', stock_key)
            redis.call('SETEX', dedup_key, %d, request_id)
            return 1
            """.formatted(DEDUP_TTL_SECONDS));
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    public int tryDeduct(Long courseId, Long studentId, String requestId) {
        String stockKey = STOCK_KEY_PREFIX + courseId;
        String dedupKey = DEDUP_KEY_PREFIX + studentId + ":" + courseId;

        List<String> keys = Arrays.asList(stockKey, dedupKey);
        Long result = redisTemplate.execute(seckillScript, keys, requestId);

        int code = result != null ? result.intValue() : 0;
        log.info("[Redis-Lua] courseId={} studentId={} result={}", courseId, studentId,
                code == 1 ? "预扣成功" : code == -1 ? "重复选课" : "库存不足");
        return code;
    }

    public void initStock(Long courseId, int maxStudents) {
        String key = STOCK_KEY_PREFIX + courseId;
        redisTemplate.opsForValue().set(key, String.valueOf(maxStudents));
        log.info("[Redis] 初始化课程库存 courseId={} stock={}", courseId, maxStudents);
    }

    public void rollbackStock(Long courseId, Long studentId) {
        String stockKey = STOCK_KEY_PREFIX + courseId;
        String dedupKey = DEDUP_KEY_PREFIX + studentId + ":" + courseId;
        redisTemplate.opsForValue().increment(stockKey);
        redisTemplate.delete(dedupKey);
        log.info("[Redis] 回滚库存 courseId={} studentId={}", courseId, studentId);
    }

    public void confirmDedup(Long courseId, Long studentId) {
        String dedupKey = DEDUP_KEY_PREFIX + studentId + ":" + courseId;
        redisTemplate.persist(dedupKey);
    }

    @Override
    public int initStockIfAbsent(Long courseId, java.util.function.Supplier<Integer> supplier) {
        String key = STOCK_KEY_PREFIX + courseId;
        // 先检查 key 是否存在，避免不必要的 supplier 调用
        String existing = redisTemplate.opsForValue().get(key);
        if (existing != null) {
            return Integer.parseInt(existing);
        }
        // 原子初始化：SETNX 直接写入真实库存值，消除竞态窗口
        int stock = supplier.get();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(stock));
        if (Boolean.TRUE.equals(success)) {
            log.info("[Redis] SETNX 初始化库存 courseId={} stock={}", courseId, stock);
            return stock;
        }
        // 并发场景：另一个线程抢先初始化，读取它的值
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Integer.parseInt(val) : stock;
    }

    public int getStock(Long courseId) {
        String key = STOCK_KEY_PREFIX + courseId;
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Integer.parseInt(val) : 0;
    }
}
