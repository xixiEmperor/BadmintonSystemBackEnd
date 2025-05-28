package com.wuli.badminton.service;

import com.wuli.badminton.dto.ChartDataDto;
import com.wuli.badminton.dto.DashboardStatsDto;

/**
 * 数据分析服务接口
 */
public interface AnalyticsService {
    
    /**
     * 获取仪表板统计数据
     * @return 仪表板统计数据
     */
    DashboardStatsDto getDashboardStats();
    
    /**
     * 获取用户注册趋势图表数据
     * @return 图表数据
     */
    ChartDataDto getUserRegistrationTrend();
    
    /**
     * 获取用户角色分布图表数据
     * @return 图表数据
     */
    ChartDataDto getUserRoleDistribution();
    
    /**
     * 获取预约趋势图表数据
     * @return 图表数据
     */
    ChartDataDto getReservationTrend();
    
    /**
     * 获取场地使用率排行图表数据
     * @return 图表数据
     */
    ChartDataDto getVenueUsageRanking();
    
    /**
     * 获取预约状态分布图表数据
     * @return 图表数据
     */
    ChartDataDto getReservationStatusDistribution();
    
    /**
     * 获取收入趋势图表数据
     * @return 图表数据
     */
    ChartDataDto getRevenueTrend();
    
    /**
     * 获取商城订单趋势图表数据
     * @return 图表数据
     */
    ChartDataDto getMallOrderTrend();
    
    /**
     * 获取热门商品排行图表数据
     * @return 图表数据
     */
    ChartDataDto getPopularProducts();
    
    /**
     * 获取商城订单状态分布图表数据
     * @return 图表数据
     */
    ChartDataDto getMallOrderStatusDistribution();
    
    /**
     * 获取发帖趋势图表数据
     * @return 图表数据
     */
    ChartDataDto getPostTrend();
    
    /**
     * 获取帖子分类分布图表数据
     * @return 图表数据
     */
    ChartDataDto getPostCategoryDistribution();
    
    /**
     * 获取最活跃用户排行图表数据
     * @return 图表数据
     */
    ChartDataDto getMostActiveUsers();
    
    /**
     * 获取每小时预约分布图表数据
     * @return 图表数据
     */
    ChartDataDto getHourlyReservationDistribution();
} 