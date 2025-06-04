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
    PASSWORD_RESET_SUCCESS(0, "密码重置成功"),
    
    /**
     * 文件格式不支持
     */
    FILE_TYPE_NOT_ALLOWED(2, "文件格式不支持，请上传jpg、png或jpeg格式图片"),
    
    /**
     * 文件大小超过限制
     */
    FILE_SIZE_EXCEEDED(3, "文件大小超过限制，最大支持2MB"),
    
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(4, "文件上传失败"),
    
    /**
     * 头像更新成功
     */
    AVATAR_UPDATE_SUCCESS(0, "头像更新成功"),
    
    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(10010, "订单不存在"),
    
    /**
     * 支付创建失败
     */
    PAY_CREATE_ERROR(10020, "支付创建失败"),
    
    /**
     * 支付状态查询失败
     */
    PAY_QUERY_ERROR(10021, "支付状态查询失败"),
    
    /**
     * 支付异步通知处理失败
     */
    PAY_NOTIFY_ERROR(10022, "支付通知处理失败"),
    
    /**
     * 订单已支付
     */
    ORDER_PAID(10030, "订单已支付"),
    
    /**
     * 订单已取消
     */
    ORDER_CANCELED(10031, "订单已取消"),
    
    /**
     * 订单创建失败
     */
    ORDER_CREATE_ERROR(10040, "订单创建失败"),
    
    // 场地预约相关错误码 (20000-20999)
    /**
     * 场地不存在
     */
    VENUE_NOT_EXIST(20001, "场地不存在"),
    
    /**
     * 场地已被预约
     */
    VENUE_ALREADY_BOOKED(20002, "该时段已被预约"),
    
    /**
     * 场地维护中
     */
    VENUE_UNDER_MAINTENANCE(20003, "场地维护中，暂不可预约"),
    
    /**
     * 预约时间无效
     */
    INVALID_RESERVATION_TIME(20004, "预约时间无效"),
    
    /**
     * 预约时间已过
     */
    RESERVATION_TIME_PASSED(20005, "预约时间已过"),
    
    /**
     * 不在可预约时间范围内
     */
    NOT_IN_BOOKABLE_TIME(20006, "不在可预约时间范围内"),
    
    /**
     * 用户已有待支付订单
     */
    USER_HAS_PENDING_ORDER(20007, "您有待支付的预约订单，请先处理"),
    
    /**
     * 预约订单不存在
     */
    RESERVATION_NOT_EXIST(20008, "预约订单不存在"),
    
    /**
     * 预约时段冲突
     */
    RESERVATION_TIME_CONFLICT(20009, "预约时段冲突"),
    
    /**
     * 退款时间限制
     */
    REFUND_TIME_LIMIT(20010, "距离开场不足30分钟，无法退款"),
    
    /**
     * 退款次数限制
     */
    REFUND_COUNT_LIMIT(20011, "当日退款次数已达上限"),
    
    /**
     * 特殊日期配置不存在
     */
    SPECIAL_DATE_CONFIG_NOT_EXIST(20012, "特殊日期配置不存在"),
    
    /**
     * 场地状态更新成功
     */
    VENUE_STATUS_UPDATE_SUCCESS(0, "场地状态更新成功"),
    
    /**
     * 预约创建成功
     */
    RESERVATION_CREATE_SUCCESS(0, "预约创建成功"),
    
    /**
     * 预约支付成功
     */
    RESERVATION_PAY_SUCCESS(0, "预约支付成功"),
    
    /**
     * 退款申请成功
     */
    REFUND_REQUEST_SUCCESS(0, "退款申请提交成功"),
    
    // 用户管理相关错误码 (30000-30999)
    /**
     * 用户不存在
     */
    USER_NOT_EXIST(30001, "用户不存在"),
    
    /**
     * 密码重置失败
     */
    PASSWORD_RESET_FAILED(30002, "密码重置失败"),
    
    /**
     * 权限不足
     */
    PERMISSION_DENIED(30003, "权限不足，仅管理员可操作"),
    
    /**
     * 验证码错误或已过期
     */
    VERIFICATION_CODE_ERROR(30004, "验证码错误或已过期");
    
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