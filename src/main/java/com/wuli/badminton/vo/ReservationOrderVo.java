package com.wuli.badminton.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 预约订单VO类
 */
@Data
public class ReservationOrderVo {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 场地ID
     */
    private Integer venueId;
    
    /**
     * 场地名称
     */
    private String venueName;
    
    /**
     * 预约日期 (YYYY-MM-DD)
     */
    private String reservationDate;
    
    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;
    
    /**
     * 时间段描述 (例如: 09:00-11:00)
     */
    private String timeSlot;
    
    /**
     * 时长（小时）
     */
    private Integer duration;
    
    /**
     * 单价（每小时）
     */
    private BigDecimal pricePerHour;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 支付方式
     */
    private Integer payType;
    
    /**
     * 支付方式描述
     */
    private String payTypeDesc;
    
    /**
     * 支付时间
     */
    private String payTime;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款时间
     */
    private String refundTime;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 是否可以取消
     */
    private Boolean canCancel;
    
    /**
     * 是否可以退款
     */
    private Boolean canRefund;
} 