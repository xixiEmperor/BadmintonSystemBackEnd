package com.wuli.badminton.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 推荐商品DTO
 */
@Data
public class RecommendProductDto {
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 副标题
     */
    private String subtitle;
    
    /**
     * 主图URL
     */
    private String mainImage;
    
    /**
     * 推荐分数（用于排序）
     */
    @JsonIgnore
    private Double score;
    
    /**
     * 推荐原因（内部使用，不返回给前端）
     */
    @JsonIgnore
    private String reason;
    
    public RecommendProductDto() {}
    
    public RecommendProductDto(Integer productId, String productName, String subtitle, String mainImage) {
        this.productId = productId;
        this.productName = productName;
        this.subtitle = subtitle;
        this.mainImage = mainImage;
    }
    
    public RecommendProductDto(Integer productId, String productName, String subtitle, String mainImage, Double score) {
        this.productId = productId;
        this.productName = productName;
        this.subtitle = subtitle;
        this.mainImage = mainImage;
        this.score = score;
    }
} 