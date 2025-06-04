package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 发送验证码请求DTO
 */
@Data
public class SendCodeRequestDto {
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 验证码类型：register-注册，reset-重置密码
     */
    private String type;
} 