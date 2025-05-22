package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 购物车项实体
 */
@Data
public class CartItem {
    private Integer productId;               // 商品ID
    private String productName;              // 商品名称
    private String productImage;             // 商品图片
    private BigDecimal productPrice;         // 商品基础价格
    private BigDecimal priceAdjustment;      // 规格价格调整值
    private Map<String, String> specs;       // 规格信息，如：{"color":"红色", "size":"M"}
    private Integer quantity;                // 数量
    private Boolean selected;                // 是否选中
    private Integer stock;                   // 商品库存
    private BigDecimal totalPrice;           // 总价
    private Integer specificationId;         // 规格ID
}