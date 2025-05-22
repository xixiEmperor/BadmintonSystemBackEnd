package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城订单实体类
 */
@Data
public class MallOrder {
    
    /**
     * 主键id
     */
    private Integer id;
    
    /**
     * 订单号
     */
    private Long orderNo;
    
    /**
     * 用户id
     */
    private Long userId;
    
    /**
     * 订单总价
     */
    private BigDecimal totalPrice;
    
    /**
     * 支付类型，1-在线支付
     */
    private Integer paymentType;
    
    /**
     * 订单状态：10-未付款，20-已付款，30-已取消，40-已完成，50-已关闭
     */
    private Integer status;
    
    /**
     * 支付时间
     */
    private Date paymentTime;
    
    /**
     * 取货码
     */
    private String pickupCode;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    // 订单状态常量
    public static final int STATUS_CANCELED = 0;     // 已取消
    public static final int STATUS_UNPAID = 10;      // 未付款
    public static final int STATUS_PAID = 20;        // 已付款
    public static final int STATUS_COMPLETED = 30;   // 已完成
    
    // 支付类型常量
    public static final int PAYMENT_TYPE_ONLINE = 1; // 在线支付
    
    /**
     * 获取订单状态描述
     */
    public String getStatusDesc() {
        switch (status) {
            case STATUS_CANCELED:
                return "已取消";
            case STATUS_UNPAID:
                return "待付款";
            case STATUS_PAID:
                return "已付款";
            case STATUS_COMPLETED:
                return "已完成";
            default:
                return "未知状态";
        }
    }
} 