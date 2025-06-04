package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 重置密码请求DTO
 */
@Data
public class ResetPasswordDto {
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 新密码
     */
    private String newPassword;
    
    /**
     * 邮箱验证码
     */
    private String verificationCode;
} 