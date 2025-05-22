package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单创建响应
 */
@Data
public class OrderCreateVo {
    private Long orderNo;                    // 订单号
    private BigDecimal payment;              // 支付金额
    private String payUrl;                   // 支付链接
} 