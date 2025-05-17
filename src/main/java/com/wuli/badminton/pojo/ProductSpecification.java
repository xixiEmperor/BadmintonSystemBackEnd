package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 商品规格实体类
 */
@Data
public class ProductSpecification {
    private Integer id;                 // 规格ID
    private Integer productId;          // 商品ID
    private Map<String, String> specifications; // 规格信息，如{"color":"红色","size":"S"}
    private BigDecimal priceAdjustment; // 价格调整
    private Integer stock;              // 该规格库存
    private Integer sales;              // 该规格销量
    private Integer status;             // 状态：1-正常，0-禁用
    private Date createTime;            // 创建时间
    private Date updateTime;            // 更新时间
} 