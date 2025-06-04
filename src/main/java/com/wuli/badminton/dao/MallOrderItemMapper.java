package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.MallOrderItem;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 商城订单项Mapper接口
 */
@Mapper
public interface MallOrderItemMapper {
    
    /**
     * 批量插入订单项
     * @param orderItems 订单项列表
     * @return 影响行数
     */
    int batchInsert(@Param("orderItems") List<MallOrderItem> orderItems);
    
    /**
     * 根据订单号查询订单项
     * @param orderNo 订单号
     * @return 订单项列表
     */
    List<MallOrderItem> selectByOrderNo(Long orderNo);
    
    /**
     * 根据订单号删除订单项
     * @param orderNo 订单号
     * @return 影响行数
     */
    int deleteByOrderNo(Long orderNo);
    
    /**
     * 获取用户购买过的商品ID列表
     * @param userId 用户ID
     * @return 商品ID列表
     */
    List<Integer> getUserPurchasedProductIds(@Param("userId") Long userId);
    
    /**
     * 获取购买了指定商品的其他用户ID列表
     * @param productIds 商品ID列表
     * @param excludeUserId 排除的用户ID
     * @return 用户ID列表
     */
    List<Long> getUsersWhoAlsoBoughtProducts(@Param("productIds") List<Integer> productIds, @Param("excludeUserId") Long excludeUserId);
    
    /**
     * 获取相似用户购买的商品统计
     * @param userIds 相似用户ID列表
     * @param excludeProductIds 排除的商品ID列表（用户已购买的商品）
     * @return 商品购买统计Map，key为商品ID，value为购买次数
     */
    @MapKey("productId")
    List<Map<String, Object>> getSimilarUsersPurchaseStats(@Param("userIds") List<Long> userIds, @Param("excludeProductIds") List<Integer> excludeProductIds);
} 