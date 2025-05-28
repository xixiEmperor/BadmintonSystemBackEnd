package com.wuli.badminton.dao;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据分析Mapper
 */
@Mapper
public interface AnalyticsMapper {
    
    // ==================== 用户统计 ====================
    /**
     * 获取用户总数
     */
    Long getTotalUsers();
    
    /**
     * 获取今日新增用户数
     */
    Long getNewUsersToday();
    
    /**
     * 获取本月新增用户数
     */
    Long getNewUsersThisMonth();
    
    /**
     * 获取今日活跃用户数（今日有登录的用户）
     */
    Long getActiveUsersToday();
    
    /**
     * 获取用户注册趋势（最近30天）
     */
    List<Map<String, Object>> getUserRegistrationTrend();
    
    /**
     * 获取用户角色分布
     */
    List<Map<String, Object>> getUserRoleDistribution();
    
    // ==================== 预约统计 ====================
    /**
     * 获取预约总数
     */
    Long getTotalReservations();
    
    /**
     * 获取今日预约数
     */
    Long getReservationsToday();
    
    /**
     * 获取本月预约数
     */
    Long getReservationsThisMonth();
    
    /**
     * 获取预约总收入
     */
    BigDecimal getReservationRevenue();
    
    /**
     * 获取今日预约收入
     */
    BigDecimal getRevenueToday();
    
    /**
     * 获取本月预约收入
     */
    BigDecimal getRevenueThisMonth();
    
    /**
     * 获取预约趋势（最近30天）
     */
    List<Map<String, Object>> getReservationTrend();
    
    /**
     * 获取场地使用率排行
     */
    List<Map<String, Object>> getVenueUsageRanking();
    
    /**
     * 获取预约状态分布
     */
    List<Map<String, Object>> getReservationStatusDistribution();
    
    /**
     * 获取收入趋势（最近30天）
     */
    List<Map<String, Object>> getRevenueTrend();
    
    // ==================== 商城统计 ====================
    /**
     * 获取商城订单总数
     */
    Long getTotalMallOrders();
    
    /**
     * 获取今日商城订单数
     */
    Long getMallOrdersToday();
    
    /**
     * 获取本月商城订单数
     */
    Long getMallOrdersThisMonth();
    
    /**
     * 获取商城总收入
     */
    BigDecimal getMallRevenue();
    
    /**
     * 获取商城今日收入
     */
    BigDecimal getMallRevenueToday();
    
    /**
     * 获取商城本月收入
     */
    BigDecimal getMallRevenueThisMonth();
    
    /**
     * 获取商城订单趋势（最近30天）
     */
    List<Map<String, Object>> getMallOrderTrend();
    
    /**
     * 获取热门商品排行
     */
    List<Map<String, Object>> getPopularProducts();
    
    /**
     * 获取商城订单状态分布
     */
    List<Map<String, Object>> getMallOrderStatusDistribution();
    
    // ==================== 论坛统计 ====================
    /**
     * 获取帖子总数
     */
    Long getTotalPosts();
    
    /**
     * 获取今日发帖数
     */
    Long getPostsToday();
    
    /**
     * 获取回复总数
     */
    Long getTotalReplies();
    
    /**
     * 获取今日回复数
     */
    Long getRepliesToday();
    
    /**
     * 获取发帖趋势（最近30天）
     */
    List<Map<String, Object>> getPostTrend();
    
    /**
     * 获取帖子分类分布
     */
    List<Map<String, Object>> getPostCategoryDistribution();
    
    /**
     * 获取最活跃用户排行
     */
    List<Map<String, Object>> getMostActiveUsers();
    
    // ==================== 场地统计 ====================
    /**
     * 获取场地总数
     */
    Long getTotalVenues();
    
    /**
     * 获取可用场地数
     */
    Long getAvailableVenues();
    
    /**
     * 获取场地利用率
     */
    Double getVenueUtilizationRate();
    
    /**
     * 获取每小时预约分布
     */
    List<Map<String, Object>> getHourlyReservationDistribution();
} 