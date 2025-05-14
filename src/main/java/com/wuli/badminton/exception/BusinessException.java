package com.wuli.badminton.exception;

import com.wuli.badminton.enums.ResponseEnum;

/**
 * 业务异常类
 * 用于表示业务逻辑错误，比如参数校验失败，权限不足等
 */
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    private final String message;
    
    public BusinessException(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getDesc();
    }
    
    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
} 