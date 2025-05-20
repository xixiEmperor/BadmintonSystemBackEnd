package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品列表展示DTO
 */
@Data
public class ProductListDto {
    private Integer id;          // 商品ID
    private Integer categoryId;  // 分类ID
    private String categoryName; // 分类名称
    private String name;         // 商品名称
    private String subtitle;     // 商品副标题
    private String mainImage;    // 主图
    private BigDecimal price;    // 价格
    private Integer stock;       // 库存
    private Integer sales;       // 销量
    private Integer status;      // 状态
} 