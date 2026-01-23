package com.ticketsystem.exception;

import com.ticketsystem.common.Result;
import com.ticketsystem.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验异常: URI={}, Errors={}", request.getRequestURI(), errorMsg);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), errorMsg);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常: URI={}, Errors={}", request.getRequestURI(), errorMsg);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), errorMsg);
    }

    /**
     * 约束违反异常处理（@Validated）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String errorMsg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.error("约束违反异常: URI={}, Errors={}", request.getRequestURI(), errorMsg);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), errorMsg);
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数异常: URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return Result.error(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 运行时异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.ERROR.getCode(), "系统错误: " + e.getMessage());
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.ERROR.getCode(), "系统异常，请联系管理员");
    }
}
