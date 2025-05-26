package com.wuli.badminton.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.wuli.badminton.pojo.PayInfo;

import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PayService {
    String BUSINESS_TYPE_MALL = "MALL";
    String BUSINESS_TYPE_RESERVATION = "RESERVATION";
    
    /**
     * 查询支付状态
     * @param orderNo 订单号（支持数字和字符串格式）
     * @return 支付状态
     */
    boolean checkPayStatus(String orderNo);
    
    /**
     * 创建支付（底层方法）
     * @param orderId 订单ID
     * @param amount 金额
     * @param bestPayTypeEnum 支付类型
     * @param businessType 业务类型
     * @return 支付响应
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum, String businessType);
    
    /**
     * 异步通知处理
     * @param notifyData 通知数据
     * @return 处理结果
     */
    String asyncNotify(String notifyData);
    
    /**
     * 创建支付记录
     * @param orderNo 订单号（支持数字和字符串格式）
     * @param amount 金额
     * @param businessType 业务类型
     * @return 支付结果
     */
    PayResponse createPay(String orderNo, BigDecimal amount, String businessType);
    
    /**
     * 根据订单号查询支付信息
     * @param orderNo 订单号（支持数字和字符串格式）
     * @return 支付信息
     */
    PayInfo queryByOrderId(String orderNo);
    
    /**
     * 获取前端支付成功跳转地址
     * @param orderNo 订单号（支持数字和字符串格式）
     * @return 跳转地址
     */
    String getReturnUrl(String orderNo);
} 