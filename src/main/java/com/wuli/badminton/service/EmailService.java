package com.wuli.badminton.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * @param email 邮箱地址
     * @param type 验证码类型：register-注册，reset-重置密码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String type);
    
    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code, String type);
    
    /**
     * 清除验证码
     * @param email 邮箱地址
     * @param type 验证码类型
     */
    void clearCode(String email, String type);
} 