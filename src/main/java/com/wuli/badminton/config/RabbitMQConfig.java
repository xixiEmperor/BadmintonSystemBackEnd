package com.wuli.badminton.config;

import org.springframework.amqp.core.Queue;
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
     * 声明支付通知队列
     */
    @Bean
    public Queue payNotifyQueue() {
        return new Queue(QUEUE_PAY_NOTIFY, true);
    }
    
    /**
     * 配置消息转换器，使用JSON格式
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
} 