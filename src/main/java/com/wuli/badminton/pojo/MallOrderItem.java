package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城订单项实体类
 */
@Data
public class MallOrderItem {
    
    /**
     * 主键id
     */
    private Integer id;
    
    /**
     * 订单号
     */
    private Long orderNo;
    
    /**
     * 商品id
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
     * 生成订单时的商品单价
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
     * 规格id
     */
    private Integer specificationId;
    
    /**
     * 规格值，例如颜色、尺寸等
     */
    private String specs;
    
    /**
     * 价格调整，规格引起的价格变动
     */
    private BigDecimal priceAdjustment;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 