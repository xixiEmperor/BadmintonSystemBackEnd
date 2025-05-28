package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约订单实体类
 */
@Data
public class ReservationOrder {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号（唯一）
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
     * 预约日期
     */
    private Date reservationDate;
    
    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;
    
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
     * 订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已关闭，6-退款中
     */
    private Integer status;
    
    /**
     * 支付信息ID（关联PayInfo表）
     */
    private Long payInfoId;
    
    /**
     * 支付时间
     */
    private Date payTime;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款时间
     */
    private Date refundTime;
    
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
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 