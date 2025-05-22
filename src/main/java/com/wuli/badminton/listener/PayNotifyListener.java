package com.wuli.badminton.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuli.badminton.config.RabbitMQConfig;
import com.wuli.badminton.dto.PayNotifyMessage;
import com.wuli.badminton.service.MallOrderService;
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
    
    /**
     * 处理支付通知消息
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAY_NOTIFY)
    public void processPayNotify(String message) {
        try {
            log.info("接收到支付通知: {}", message);
            PayNotifyMessage payNotifyMessage = objectMapper.readValue(message, PayNotifyMessage.class);
            
            // 根据业务类型分发处理
            String businessType = payNotifyMessage.getBusinessType();
            Long orderNo = payNotifyMessage.getOrderNo();
            
            if ("MALL".equals(businessType)) {
                // 处理商城订单支付
                mallOrderService.paySuccess(orderNo);
                log.info("商城订单支付成功处理完成: orderNo={}", orderNo);
            } else {
                log.warn("未知的业务类型: {}, orderNo={}", businessType, orderNo);
            }
        } catch (Exception e) {
            log.error("处理支付通知失败: {}", e.getMessage(), e);
        }
    }
} 