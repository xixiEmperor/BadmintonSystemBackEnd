package com.wuli.badminton.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {
    
    /**
     * 支付通知队列名称
     */
    public static final String QUEUE_PAY_NOTIFY = "payNotify";
    
    /**
     * 订单延迟队列名称
     */
    public static final String QUEUE_ORDER_DELAY = "orderDelay";
    
    /**
     * 订单取消队列名称
     */
    public static final String QUEUE_ORDER_CANCEL = "orderCancel";
    
    /**
     * 订单交换机名称
     */
    public static final String EXCHANGE_ORDER = "orderExchange";
    
    /**
     * 订单延迟路由键
     */
    public static final String ROUTING_KEY_ORDER_DELAY = "order.delay";
    
    /**
     * 订单取消路由键
     */
    public static final String ROUTING_KEY_ORDER_CANCEL = "order.cancel";
    
    /**
     * 声明支付通知队列
     */
    @Bean
    public Queue payNotifyQueue() {
        return new Queue(QUEUE_PAY_NOTIFY, true);
    }
    
    /**
     * 声明订单延迟队列
     * 设置消息TTL为10分钟，过期后转发到取消队列
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_DELAY)
                .withArgument("x-message-ttl", 600000) // 10分钟 = 600000毫秒
                .withArgument("x-dead-letter-exchange", EXCHANGE_ORDER)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_ORDER_CANCEL)
                .build();
    }
    
    /**
     * 声明订单取消队列
     */
    @Bean
    public Queue orderCancelQueue() {
        return new Queue(QUEUE_ORDER_CANCEL, true);
    }
    
    /**
     * 声明订单交换机
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_ORDER, true, false);
    }
    
    /**
     * 绑定延迟队列到交换机
     */
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderExchange()).with(ROUTING_KEY_ORDER_DELAY);
    }
    
    /**
     * 绑定取消队列到交换机
     */
    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue()).to(orderExchange()).with(ROUTING_KEY_ORDER_CANCEL);
    }
    
    /**
     * 配置消息转换器，使用JSON格式
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // 设置字符编码
        converter.setDefaultCharset("UTF-8");
        rabbitTemplate.setMessageConverter(converter);
        
        // 设置强制确认模式（可选）
        rabbitTemplate.setMandatory(true);
        
        return rabbitTemplate;
    }
    
    /**
     * 配置Jackson消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setDefaultCharset("UTF-8");
        return converter;
    }
} 