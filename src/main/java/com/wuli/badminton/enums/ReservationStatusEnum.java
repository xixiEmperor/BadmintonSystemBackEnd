package com.wuli.badminton.enums;

import lombok.Getter;

/**
 * 预约订单状态枚举
 */
@Getter
public enum ReservationStatusEnum {
    
    /**
     * 待支付
     */
    PENDING_PAYMENT(1, "待支付"),
    
    /**
     * 已支付
     */
    PAID(2, "已支付"),
    
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    
    /**
     * 已取消（只有未支付可取消）
     */
    CANCELLED(4, "已取消"),
    
    /**
     * 已关闭（退款完成）
     */
    CLOSED(5, "已关闭"),
    
    /**
     * 退款中
     */
    REFUNDING(6, "退款中");
    
    private final Integer code;
    private final String desc;
    
    ReservationStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据状态码获取枚举
     */
    public static ReservationStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ReservationStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 