package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 预约订单查询DTO
 */
@Data
public class ReservationOrderQueryDto {
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 场地ID
     */
    private Integer venueId;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 预约日期开始
     */
    private String startDate;
    
    /**
     * 预约日期结束
     */
    private String endDate;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户名（模糊查询）
     */
    private String username;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
} 