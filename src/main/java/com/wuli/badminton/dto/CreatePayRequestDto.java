package com.wuli.badminton.dto;

import java.math.BigDecimal;

/**
 * 支付请求 DTO
 */
public class CreatePayRequestDto {
    private String orderNo; // 支持数字和字符串格式的订单号
    private BigDecimal amount;
    private String businessType; // MALL-商城订单支付 RESERVATION-预约订单支付

    // Getters and Setters
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}