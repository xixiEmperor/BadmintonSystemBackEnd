package com.wuli.badminton.controller;

import com.wuli.badminton.dto.RecommendProductDto;
import com.wuli.badminton.service.ProductRecommendService;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品推荐控制器
 */
@RestController
@RequestMapping("/api/mall/recommend")
public class RecommendController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendController.class);
    
    @Autowired
    private ProductRecommendService recommendService;
    
    /**
     * 获取推荐商品列表
     * @return 推荐商品列表，长度为4
     */
    @GetMapping("/products")
    public ResponseVo<List<RecommendProductDto>> getRecommendProducts() {
        logger.info("【商品推荐】获取推荐商品列表");
        
        try {
            List<RecommendProductDto> recommendations = recommendService.getRecommendProducts();
            
            // 确保返回的商品数量不超过4个
            if (recommendations.size() > 4) {
                recommendations = recommendations.subList(0, 4);
            }
            
            logger.info("【商品推荐】返回推荐商品数量: {}", recommendations.size());
            
            // 清除内部字段，不返回给前端
            recommendations.forEach(dto -> {
                dto.setScore(null);
                dto.setReason(null);
            });
            
            return ResponseVo.success("获取推荐商品成功", recommendations);
            
        } catch (Exception e) {
            logger.error("【商品推荐】获取推荐商品失败: {}", e.getMessage(), e);
            return ResponseVo.error(999, "获取推荐商品失败，请稍后重试");
        }
    }
} 