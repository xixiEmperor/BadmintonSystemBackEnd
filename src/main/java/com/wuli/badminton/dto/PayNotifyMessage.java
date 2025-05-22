package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 支付成功通知消息
 */
@Data
public class PayNotifyMessage {
    
    /**
     * 订单号
     */
    private Long orderNo;
    
    /**
     * 支付平台类型: 1-支付宝，2-微信
     */
    private Integer payPlatform;
    
    /**
     * 支付平台流水号
     */
    private String platformNumber;
    
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 业务类型：MALL-商城订单，RESERVATION-场地预约
     */
    private String businessType;
} 