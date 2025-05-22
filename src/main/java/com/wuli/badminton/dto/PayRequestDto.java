package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 支付请求DTO
 */
@Data
public class PayRequestDto {
    private Long orderNo;            // 订单号
    private BigDecimal amount;       // 支付金额
    private String businessType;     // 业务类型
} 