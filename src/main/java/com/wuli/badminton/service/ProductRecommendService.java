package com.wuli.badminton.service;

import com.wuli.badminton.dto.RecommendProductDto;

import java.util.List;

/**
 * 商品推荐服务接口
 */
public interface ProductRecommendService {
    
    /**
     * 获取推荐商品列表
     * @return 推荐商品列表，长度为4
     */
    List<RecommendProductDto> getRecommendProducts();
    
    /**
     * 基于协同过滤算法获取推荐商品
     * @param userId 用户ID
     * @return 推荐商品列表
     */
    List<RecommendProductDto> getCollaborativeFilteringRecommendations(Long userId);
    
    /**
     * 获取热门商品（兜底策略）
     * @return 热门商品列表
     */
    List<RecommendProductDto> getHotProducts();
} 