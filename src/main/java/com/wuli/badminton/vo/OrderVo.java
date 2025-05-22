package com.wuli.badminton.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单视图对象
 */
@Data
public class OrderVo {
    
    /**
     * 订单ID
     */
    private Integer id;
    
    /**
     * 订单号
     */
    private Long orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单总价
     */
    private BigDecimal totalPrice;
    
    /**
     * 支付类型：1-在线支付
     */
    private Integer paymentType;
    
    /**
     * 订单状态：10-未付款，20-已付款，30-已取消，40-已完成，50-已关闭
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;
    
    /**
     * 取货码
     */
    private String pickupCode;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 订单项列表
     */
    private List<OrderItemVo> orderItemList;
    
    /**
     * 订单项视图对象
     */
    @Data
    public static class OrderItemVo {
        
        /**
         * 订单项ID
         */
        private Integer id;
        
        /**
         * 订单号
         */
        private Long orderNo;
        
        /**
         * 商品ID
         */
        private Integer productId;
        
        /**
         * 商品名称
         */
        private String productName;
        
        /**
         * 商品图片
         */
        private String productImage;
        
        /**
         * 商品单价
         */
        private BigDecimal currentUnitPrice;
        
        /**
         * 商品数量
         */
        private Integer quantity;
        
        /**
         * 商品总价
         */
        private BigDecimal totalPrice;
        
        /**
         * 规格ID
         */
        private Integer specificationId;
        
        /**
         * 规格值，如颜色、尺寸等
         */
        private String specs;
        
        /**
         * 价格调整
         */
        private BigDecimal priceAdjustment;
    }
} 