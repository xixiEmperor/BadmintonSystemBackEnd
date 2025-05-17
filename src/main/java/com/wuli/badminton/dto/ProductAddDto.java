package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 添加商品DTO
 */
@Data
public class ProductAddDto {
    private Integer categoryId;  // 分类ID
    private String name;         // 商品名称
    private String subtitle;     // 商品副标题
    private String mainImage;    // 主图URL
    private String subImages;    // 子图URL，逗号分隔
    private String detail;       // 商品详情
    private BigDecimal price;    // 价格
    private Integer stock;       // 库存
    private Integer status;      // 状态：1-在售，2-下架
    private Integer hasSpecification; // 是否有规格：0-无规格，1-有规格
    private List<Map<String, Object>> specifications; // 商品规格列表，每个元素包含规格信息、价格调整和库存
    // 示例：[{"specs":{"color":"红色","size":"S"},"priceAdjustment":10.0,"stock":20}, ...]
    private Map<String, List<String>> specOptions;   // 规格选项，如{"color":["红色","蓝色"],"size":["S","M","L"]}
} 