package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.MallOrderItemMapper;
import com.wuli.badminton.dao.MallProductMapper;
import com.wuli.badminton.dto.RecommendProductDto;
import com.wuli.badminton.pojo.CartItem;
import com.wuli.badminton.pojo.MallProduct;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.CartService;
import com.wuli.badminton.service.ProductRecommendService;
import com.wuli.badminton.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品推荐服务实现类
 */
@Service
public class ProductRecommendServiceImpl implements ProductRecommendService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductRecommendServiceImpl.class);
    
    @Autowired
    private MallOrderItemMapper orderItemMapper;
    
    @Autowired
    private MallProductMapper productMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CartService cartService;
    
    @Override
    public List<RecommendProductDto> getRecommendProducts() {
        logger.info("获取推荐商品列表");
        
        try {
            // 获取当前用户
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                logger.info("用户未登录，返回热门商品");
                return getHotProducts();
            }
            
            // 尝试获取协同过滤推荐
            List<RecommendProductDto> recommendations = getCollaborativeFilteringRecommendations(currentUser.getId());
            
            if (recommendations.size() >= 4) {
                logger.info("协同过滤推荐成功，返回前4个商品");
                return recommendations.subList(0, 4);
            } else {
                logger.info("协同过滤推荐数量不足，使用热门商品补充");
                // 协同过滤推荐不足，用热门商品补充
                List<RecommendProductDto> hotProducts = getHotProducts();
                
                // 合并推荐结果，去重
                Set<Integer> recommendedProductIds = recommendations.stream()
                    .map(RecommendProductDto::getProductId)
                    .collect(Collectors.toSet());
                
                for (RecommendProductDto hotProduct : hotProducts) {
                    if (!recommendedProductIds.contains(hotProduct.getProductId()) && recommendations.size() < 4) {
                        recommendations.add(hotProduct);
                    }
                }
                
                return recommendations.size() > 4 ? recommendations.subList(0, 4) : recommendations;
            }
        } catch (Exception e) {
            logger.error("获取推荐商品失败，返回热门商品: {}", e.getMessage(), e);
            return getHotProducts();
        }
    }
    
    @Override
    public List<RecommendProductDto> getCollaborativeFilteringRecommendations(Long userId) {
        logger.info("基于协同过滤算法获取推荐商品: userId={}", userId);
        
        try {
            // 1. 获取用户购买过的商品ID列表（从订单数据）
            List<Integer> userPurchasedProducts = orderItemMapper.getUserPurchasedProductIds(userId);
            logger.info("用户购买过的商品数量: {}", userPurchasedProducts.size());
            
            // 2. 获取用户购物车中的商品ID列表（从Redis）
            List<Integer> cartProductIds = getCartProductIds(userId);
            logger.info("用户购物车商品数量: {}", cartProductIds.size());
            
            // 3. 合并用户行为数据（购买 + 购物车）
            Set<Integer> allUserProducts = new HashSet<>();
            allUserProducts.addAll(userPurchasedProducts);
            allUserProducts.addAll(cartProductIds);
            
            if (allUserProducts.isEmpty()) {
                logger.info("用户无购买和购物车行为数据，返回热门商品");
                return new ArrayList<>();
            }
            
            logger.info("用户行为商品总数: {}", allUserProducts.size());
            
            // 4. 寻找购买了相同商品的其他用户（协同用户）
            List<Long> similarUsers = orderItemMapper.getUsersWhoAlsoBoughtProducts(
                new ArrayList<>(allUserProducts), userId);
            logger.info("相似用户数量: {}", similarUsers.size());
            
            if (similarUsers.isEmpty()) {
                logger.info("未找到相似用户，返回空推荐");
                return new ArrayList<>();
            }
            
            // 5. 获取相似用户购买的商品统计
            List<Map<String, Object>> purchaseStats = orderItemMapper.getSimilarUsersPurchaseStats(
                similarUsers, new ArrayList<>(allUserProducts));
            logger.info("相似用户购买商品统计数量: {}", purchaseStats.size());
            
            // 6. 提取推荐商品ID并计算推荐分数
            List<RecommendProductDto> recommendations = new ArrayList<>();
            for (Map<String, Object> stat : purchaseStats) {
                Integer productId = (Integer) stat.get("productId");
                Integer purchaseCount = ((Number) stat.get("purchaseCount")).intValue();
                Integer totalQuantity = ((Number) stat.get("totalQuantity")).intValue();
                
                // 计算推荐分数：购买次数 * 0.6 + 总购买数量 * 0.4
                double score = purchaseCount * 0.6 + totalQuantity * 0.4;
                
                // 获取商品详情
                MallProduct product = productMapper.findById(productId);
                if (product != null && product.getStatus() == 1 && product.getStock() > 0) {
                    RecommendProductDto dto = new RecommendProductDto(
                        product.getId(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        score
                    );
                    dto.setReason("协同过滤推荐 - 购买次数:" + purchaseCount + ", 总量:" + totalQuantity);
                    recommendations.add(dto);
                }
            }
            
            // 7. 按推荐分数排序
            recommendations.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            
            logger.info("协同过滤推荐商品数量: {}", recommendations.size());
            return recommendations;
            
        } catch (Exception e) {
            logger.error("协同过滤推荐失败: userId={}, error={}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RecommendProductDto> getHotProducts() {
        logger.info("获取热门商品");
        
        try {
            // 获取销量最高的商品
            List<MallProduct> hotProducts = productMapper.getHotProducts(8); // 获取8个，确保有足够的选择
            
            List<RecommendProductDto> recommendations = hotProducts.stream()
                .map(product -> {
                    RecommendProductDto dto = new RecommendProductDto(
                        product.getId(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getSales().doubleValue()
                    );
                    dto.setReason("热门商品 - 销量:" + product.getSales());
                    return dto;
                })
                .collect(Collectors.toList());
            
            logger.info("热门商品数量: {}", recommendations.size());
            return recommendations.size() > 4 ? recommendations.subList(0, 4) : recommendations;
            
        } catch (Exception e) {
            logger.error("获取热门商品失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取用户购物车中的商品ID列表
     * @param userId 用户ID
     * @return 商品ID列表
     */
    private List<Integer> getCartProductIds(Long userId) {
        try {
            List<CartItem> cartItems = cartService.listAllItems(userId);
            return cartItems.stream()
                .map(CartItem::getProductId)
                .distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("获取用户购物车商品失败: userId={}, error={}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }
} 