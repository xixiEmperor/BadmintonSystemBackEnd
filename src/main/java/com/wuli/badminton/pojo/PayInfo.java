package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付信息实体类
 */
@Data
public class PayInfo {
    
    /**
     * 主键id
     */
    private Integer id;
    
    /**
     * 订单号（支持多种格式：数字/字符串）
     */
    private String orderNo;
    
    /**
     * 支付平台:1-支付宝,2-微信
     */
    private Integer payPlatform;
    
    /**
     * 支付平台的流水号
     */
    private String platformNumber;
    
    /**
     * 支付平台的支付状态
     */
    private String platformStatus;
    
    /**
     * 业务类型：MALL-商城订单支付 RESERVATION-场地预约支付
     */
    private String businessType;
    
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 支付状态：0-未支付，1-已支付
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 