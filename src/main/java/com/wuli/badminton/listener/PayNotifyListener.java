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

import java.nio.charset.StandardCharsets;

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
            
            // 直接解析JSON消息
            PayNotifyMessage payNotifyMessage = objectMapper.readValue(message, PayNotifyMessage.class);
            log.info("成功解析支付通知消息: {}", payNotifyMessage);
            
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
            log.error("失败的消息内容: {}", message);
            
            // 如果是JSON解析失败，尝试ASCII转换
            if (e.getMessage().contains("Unexpected character")) {
                try {
                    log.warn("尝试ASCII码转换...");
                    String convertedMessage = convertAsciiToString(message);
                    log.info("ASCII转换后重新处理: {}", convertedMessage);
                    processPayNotify(convertedMessage);
                } catch (Exception ex) {
                    log.error("ASCII转换也失败了: {}", ex.getMessage(), ex);
                }
            }
        }
    }
    
    /**
     * 将ASCII码格式的字符串转换为正常字符串
     * @param asciiString ASCII码字符串，格式如 "34,123,92,34,111..."
     * @return 转换后的字符串
     */
    private String convertAsciiToString(String asciiString) {
        try {
            String[] asciiCodes = asciiString.split(",");
            StringBuilder sb = new StringBuilder();
            for (String code : asciiCodes) {
                int ascii = Integer.parseInt(code.trim());
                sb.append((char) ascii);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("ASCII码转换失败: {}", e.getMessage());
            throw new RuntimeException("ASCII码转换失败", e);
        }
    }
} 