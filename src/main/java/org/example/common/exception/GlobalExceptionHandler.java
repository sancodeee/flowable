package org.example.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.common.Result;
import org.example.common.ResultCodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理程序
 *
 * @author wangsen
 * @date 2024/01/14
 */
@Slf4j
@RestControllerAdvice(basePackages = "org.example.controller")
public class GlobalExceptionHandler {

    /**
     * 统一异常处理@ExceptionHandler,主要用于Exception
     *
     * @param e e
     * @return {@link Result}<{@link ?}>
     */
    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        log.error("全局异常信息：", e);
        return Result.error("500", "服务器发生不明异常");
    }

    /**
     * 自定义异常
     *
     * @param e e
     * @return {@link Result}<{@link ?}>
     */
    @ExceptionHandler(CustomException.class)
    public Result<?> customError(CustomException e) {
        log.error("自定义异常信息：", e);
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * jsr303校验异常
     *
     * @param e e
     * @return {@link Result}<{@link ?}>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> jsr303Error(ConstraintViolationException e) {
        log.error("数据校验异常信息：", e);
        return Result.error(ResultCodeEnum.PARAM_ERROR.code, e.getMessage());
    }
}
