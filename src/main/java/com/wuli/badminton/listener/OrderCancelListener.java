package com.wuli.badminton.listener;

import com.wuli.badminton.config.RabbitMQConfig;
import com.wuli.badminton.pojo.MallOrder;
import com.wuli.badminton.service.MallOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单取消监听器
 * 处理延迟队列中的超时订单
 */
@Component
public class OrderCancelListener {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderCancelListener.class);
    
    @Autowired
    private MallOrderService mallOrderService;
    
    /**
     * 处理订单超时取消
     * 监听订单取消队列，处理从延迟队列转发过来的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_CANCEL)
    public void processOrderCancel(String orderNoStr) {
        logger.info("【订单超时关单】收到取消消息: orderNo={}", orderNoStr);
        
        try {
            // 解析订单号
            Long orderNo = Long.parseLong(orderNoStr);
            
            // 查询订单状态
            MallOrder order = mallOrderService.selectByOrderNo(orderNo);
            if (order == null) {
                logger.warn("【订单超时关单】订单不存在: orderNo={}", orderNo);
                return;
            }
            
            // 检查订单状态，只有未支付的订单才需要取消
            if (order.getStatus().equals(MallOrder.STATUS_UNPAID)) {
                logger.info("【订单超时关单】开始取消订单: orderNo={}, 当前状态={}", orderNo, order.getStatus());
                
                // 取消订单
                boolean success = mallOrderService.cancelOrder(orderNo);
                if (success) {
                    logger.info("【订单超时关单】订单取消成功: orderNo={}", orderNo);
                } else {
                    logger.error("【订单超时关单】订单取消失败: orderNo={}", orderNo);
                }
            } else {
                logger.info("【订单超时关单】订单无需取消: orderNo={}, 当前状态={}", orderNo, order.getStatus());
            }
            
        } catch (NumberFormatException e) {
            logger.error("【订单超时关单】订单号格式错误: orderNoStr={}", orderNoStr, e);
        } catch (Exception e) {
            logger.error("【订单超时关单】处理异常: orderNoStr={}, error={}", orderNoStr, e.getMessage(), e);
        }
    }
} 