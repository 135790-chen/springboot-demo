package com.example.demo.common;

import com.example.demo.common.exception.DuplicateStudentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 拦截所有 Controller 抛出的异常，统一转成 {@link Result} 格式返回。
 * 加了它之后，各个 Controller 不再需要写 try-catch。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── ① 参数校验失败 ──
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(Result.error(400, msg));
    }

    // ── ② 业务逻辑错误 ──
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleBizError(IllegalArgumentException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Result.error(400, e.getMessage()));
    }

    // ── ③ 数据重复冲突 ──
    @ExceptionHandler(DuplicateStudentException.class)
    public ResponseEntity<Result<?>> handleDuplicate(DuplicateStudentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Result.error(409, e.getMessage()));
    }

    // ── ④ 未知异常（兜底） ──
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleUnknown(Exception e) {
        log.error("未捕获异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }
}
