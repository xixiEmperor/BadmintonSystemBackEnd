package com.wuli.badminton.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MallProduct {
    private Integer id;           // 商品ID
    private Integer categoryId;   // 分类ID
    private String categoryName;  // 分类名称
    private String name;          // 商品名称
    private String subtitle;      // 商品副标题
    private String mainImage;     // 主图
    private String subImages;     // 子图，逗号分隔
    private String detail;        // 商品详情
    private BigDecimal price;     // 价格
    private Integer stock;        // 库存
    private Integer sales;        // 销量
    private Integer status;       // 状态：1-在售，2-下架，3-删除
    private Integer hasSpecification; // 是否有规格：0-无规格，1-有规格
    private Date createTime;      // 创建时间
    private Date updateTime;      // 更新时间
} 