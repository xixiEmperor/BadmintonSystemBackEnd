package com.wuli.badminton.enums;

/**
 * 预约订单状态枚举
 */
public enum ReservationStatusEnum {
    
    /**
     * 待支付
     */
    PENDING_PAYMENT(10, "待支付"),
    
    /**
     * 已支付
     */
    PAID(20, "已支付"),
    
    /**
     * 已完成
     */
    COMPLETED(30, "已完成"),
    
    /**
     * 已取消（只有未支付可取消）
     */
    CANCELLED(40, "已取消"),
    
    /**
     * 已关闭（退款完成）
     */
    CLOSED(50, "已关闭"),
    
    /**
     * 退款中
     */
    REFUNDING(60, "退款中");
    
    private final Integer code;
    private final String desc;
    
    ReservationStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    /**
     * 根据状态码获取枚举
     */
    public static ReservationStatusEnum getByCode(Integer code) {
        for (ReservationStatusEnum status : ReservationStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 