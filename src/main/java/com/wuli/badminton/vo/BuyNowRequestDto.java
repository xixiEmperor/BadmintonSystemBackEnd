package com.wuli.badminton.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 立即购买请求DTO
 */
@Data
public class BuyNowRequestDto {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    
    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;
    
    /**
     * 商品规格
     */
    private Map<String, String> specs;
} 