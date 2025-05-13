package com.wuli.badminton.enums;

/**
 * 响应状态枚举
 * 
 * 用于统一管理系统中所有的响应状态码和对应的描述信息
 * 与ResponseVo配合使用，实现统一的API响应格式
 * 
 * 状态码规则：
 * - 0: 成功
 * - 1-10: 通用错误码
 * - 其他: 特定业务错误码
 */
public enum ResponseEnum {
    

    SUCCESS(0, "成功"),

    ERROR(1, "错误"),
    
    PARAM_ERROR(3, "参数错误"),
    
    /**
     * 用户注册时用户名已存在
     */
    USERNAME_EXIST(1, "用户名已存在"),
    
    /**
     * 用户注册时邮箱已被注册
     */
    EMAIL_EXIST(2, "邮箱已被注册"),
    
    /**
     * 用户登录失败，用户名或密码错误
     */
    LOGIN_ERROR(1, "用户名或密码错误"),
    
    /**
     * 用户未登录，需要登录才能访问
     */
    NEED_LOGIN(10, "用户未登录"),
    
    /**
     * 服务器内部错误
     */
    SERVER_ERROR(999, "服务器异常"),
    
    /**
     * 未提供Token，未授权访问
     */
    UNAUTHORIZED(4010, "未授权访问"),
    
    /**
     * Token已过期或无效
     */
    TOKEN_EXPIRED(4011, "Token已过期"),
    
    /**
     * 退出登录成功
     */
    LOGOUT_SUCCESS(0, "退出成功"),
    
    /**
     * 邮箱不存在
     */
    EMAIL_NOT_EXIST(1, "邮箱不存在"),
    
    /**
     * 邮件发送失败
     */
    EMAIL_SEND_ERROR(2, "邮件发送失败，请稍后重试"),
    
    /**
     * 重置密码邮件发送成功
     */
    RESET_EMAIL_SENT(0, "重置密码链接已发送到您的邮箱"),
    
    /**
     * 重置令牌无效或已过期
     */
    RESET_TOKEN_INVALID(1, "重置令牌无效或已过期"),
    
    /**
     * 新密码格式不正确
     */
    PASSWORD_FORMAT_ERROR(2, "新密码格式不正确"),
    
    /**
     * 密码重置成功
     */
    PASSWORD_RESET_SUCCESS(0, "密码重置成功");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态描述
     */
    private final String desc;
    
    /**
     * 构造函数
     * @param code 状态码
     * @param desc 状态描述
     */
    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 获取状态码
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }
    
    /**
     * 获取状态描述
     * @return 状态描述
     */
    public String getDesc() {
        return desc;
    }
} 