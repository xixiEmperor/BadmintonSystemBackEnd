package com.wuli.badminton.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuli.badminton.config.RabbitMQConfig;
import com.wuli.badminton.dao.PayInfoMapper;
import com.wuli.badminton.dto.PayNotifyMessage;
import com.wuli.badminton.pojo.PayInfo;
import com.wuli.badminton.service.MallOrderService;
import com.wuli.badminton.service.ReservationOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付通知消息监听器
 */
@Component
@Slf4j
public class PayNotifyListener {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MallOrderService mallOrderService;
    
    @Autowired
    private ReservationOrderService reservationOrderService;
    
    @Autowired
    private PayInfoMapper payInfoMapper;
    
    /**
     * 处理支付通知消息
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAY_NOTIFY)
    public void processPayNotify(String message) {
        try {
            log.info("接收到支付通知: {}", message);
            
            // 解码消息（处理ASCII编码格式）
            String decodedMessage = decodeMessage(message);
            log.info("解码后的消息: {}", decodedMessage);
            
            PayNotifyMessage payNotifyMessage = objectMapper.readValue(decodedMessage, PayNotifyMessage.class);
            
            // 根据业务类型分发处理
            String businessType = payNotifyMessage.getBusinessType();
            Long orderNo = payNotifyMessage.getOrderNo();
            
            if ("MALL".equals(businessType)) {
                // 处理商城订单支付
                mallOrderService.paySuccess(orderNo);
                log.info("商城订单支付成功处理完成: orderNo={}", orderNo);
            } else if ("RESERVATION".equals(businessType)) {
                // 处理预约订单支付
                // 恢复原始的预约订单号格式（加上RO前缀）
                String reservationOrderNo = "RO" + orderNo;
                
                // 查询真实的PayInfo记录（使用原始订单号格式）
                PayInfo payInfo = payInfoMapper.selectByOrderNo(reservationOrderNo);
                if (payInfo == null) {
                    log.error("预约订单支付处理失败，PayInfo记录不存在: orderNo={}", reservationOrderNo);
                    return;
                }
                
                reservationOrderService.paymentCallback(reservationOrderNo, payInfo.getId().longValue());
                log.info("预约订单支付成功处理完成: orderNo={}, payInfoId={}", reservationOrderNo, payInfo.getId());
            } else {
                log.warn("未知的业务类型: {}, orderNo={}", businessType, orderNo);
            }
        } catch (Exception e) {
            log.error("处理支付通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 解码消息，处理ASCII编码格式
     * @param message 原始消息
     * @return 解码后的消息
     */
    private String decodeMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return message;
        }
        
        message = message.trim();
        
        // 如果消息已经是JSON格式，直接返回
        if (message.startsWith("{") && message.endsWith("}")) {
            return message;
        }
        
        // 处理ASCII编码格式（逗号分隔的数字）
        if (message.contains(",") && message.matches("[0-9,\\s]+")) {
            try {
                String[] asciiCodes = message.split(",");
                StringBuilder sb = new StringBuilder();
                for (String code : asciiCodes) {
                    int ascii = Integer.parseInt(code.trim());
                    if (ascii >= 32 && ascii <= 126) { // 可打印ASCII字符范围
                        sb.append((char) ascii);
                    }
                }
                String decodedResult = sb.toString();
                log.info("ASCII解码: {} -> {}", message.substring(0, Math.min(50, message.length())) + "...", decodedResult);
                
                // 处理双重转义的JSON字符串
                if (decodedResult.startsWith("\"") && decodedResult.endsWith("\"")) {
                    // 去掉外层的双引号，并反转义内部的JSON
                    decodedResult = decodedResult.substring(1, decodedResult.length() - 1);
                    decodedResult = decodedResult.replace("\\\"", "\"");
                    log.info("去除双重转义后: {}", decodedResult);
                }
                
                return decodedResult;
            } catch (Exception e) {
                log.error("ASCII解码失败: {}, error: {}", message.substring(0, Math.min(50, message.length())), e.getMessage());
                return message;
            }
        }
        
        // 其他格式直接返回
        return message;
    }
} 