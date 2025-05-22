package com.wuli.badminton.dto;

import lombok.Data;
import java.util.Map;

/**
 * 购物车添加商品表单
 */
@Data
public class CartItemForm {
    private Integer productId;               // 商品ID
    private Integer quantity;                // 数量
    private Map<String, String> specs;       // 规格信息，如：{"color":"红色", "size":"M"}
}