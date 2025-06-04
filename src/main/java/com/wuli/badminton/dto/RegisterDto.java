package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDto {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 邮箱验证码
     */
    private String verificationCode;
} 