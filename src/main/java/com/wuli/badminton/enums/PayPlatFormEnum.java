package com.wuli.badminton.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;

/**
 * 支付平台枚举
 */
public enum PayPlatFormEnum {
    // 1-支付宝, 2-微信
    ALIPAY(1),
    WX(2);
    
    private Integer code;
    
    PayPlatFormEnum(int code) {
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }
    
    /**
     * 根据支付类型获取支付平台
     */
    public static PayPlatFormEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum) {
        for (PayPlatFormEnum payPlatFormEnum : PayPlatFormEnum.values()) {
            if (bestPayTypeEnum.getPlatform().name().equals(payPlatFormEnum.name())) {
                return payPlatFormEnum;
            }
        }
        throw new RuntimeException("错误的支付平台:" + bestPayTypeEnum.name());
    }
} 