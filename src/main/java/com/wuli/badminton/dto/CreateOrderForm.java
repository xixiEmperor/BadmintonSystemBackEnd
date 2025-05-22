package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 创建订单表单
 */
@Data
public class CreateOrderForm {
    private String remark;                   // 订单备注
    private Integer paymentType = 1;         // 支付类型，默认为在线支付
} 