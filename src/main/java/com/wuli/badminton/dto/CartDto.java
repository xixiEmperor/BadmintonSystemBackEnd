package com.wuli.badminton.dto;

import com.wuli.badminton.pojo.CartItem;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车数据传输对象
 */
@Data
public class CartDto {
    private List<CartItem> cartItems;      // 购物车商品列表
    private Integer totalQuantity;         // 总数量
    private BigDecimal totalPrice;         // 总价格
    private Boolean allSelected;           // 是否全选
} 