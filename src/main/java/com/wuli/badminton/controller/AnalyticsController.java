package com.wuli.badminton.controller;

import com.wuli.badminton.dto.ChartDataDto;
import com.wuli.badminton.dto.DashboardStatsDto;
import com.wuli.badminton.service.AnalyticsService;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 数据分析控制器
 * 提供管理员后台数据分析功能
 */
@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    /**
     * 获取仪表板统计数据
     * @return 统计数据
     */
    @GetMapping("/dashboard")
    public ResponseVo<DashboardStatsDto> getDashboardStats() {
        logger.info("管理员获取仪表板统计数据");
        try {
            DashboardStatsDto stats = analyticsService.getDashboardStats();
            return ResponseVo.success("获取成功", stats);
        } catch (Exception e) {
            logger.error("获取仪表板统计数据失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取统计数据失败");
        }
    }
    
    // ==================== 用户相关图表 ====================
    
    /**
     * 获取用户注册趋势图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/user-registration-trend")
    public ResponseVo<ChartDataDto> getUserRegistrationTrend() {
        logger.info("获取用户注册趋势图表数据");
        try {
            ChartDataDto chartData = analyticsService.getUserRegistrationTrend();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取用户注册趋势失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取用户角色分布图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/user-role-distribution")
    public ResponseVo<ChartDataDto> getUserRoleDistribution() {
        logger.info("获取用户角色分布图表数据");
        try {
            ChartDataDto chartData = analyticsService.getUserRoleDistribution();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取用户角色分布失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    // ==================== 预约相关图表 ====================
    
    /**
     * 获取预约趋势图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/reservation-trend")
    public ResponseVo<ChartDataDto> getReservationTrend() {
        logger.info("获取预约趋势图表数据");
        try {
            ChartDataDto chartData = analyticsService.getReservationTrend();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取预约趋势失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取场地使用率排行图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/venue-usage-ranking")
    public ResponseVo<ChartDataDto> getVenueUsageRanking() {
        logger.info("获取场地使用率排行图表数据");
        try {
            ChartDataDto chartData = analyticsService.getVenueUsageRanking();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取场地使用率排行失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取预约状态分布图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/reservation-status-distribution")
    public ResponseVo<ChartDataDto> getReservationStatusDistribution() {
        logger.info("获取预约状态分布图表数据");
        try {
            ChartDataDto chartData = analyticsService.getReservationStatusDistribution();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取预约状态分布失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取每小时预约分布图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/hourly-reservation-distribution")
    public ResponseVo<ChartDataDto> getHourlyReservationDistribution() {
        logger.info("获取每小时预约分布图表数据");
        try {
            ChartDataDto chartData = analyticsService.getHourlyReservationDistribution();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取每小时预约分布失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    // ==================== 收入相关图表 ====================
    
    /**
     * 获取收入趋势图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/revenue-trend")
    public ResponseVo<ChartDataDto> getRevenueTrend() {
        logger.info("获取收入趋势图表数据");
        try {
            ChartDataDto chartData = analyticsService.getRevenueTrend();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取收入趋势失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    // ==================== 商城相关图表 ====================
    
    /**
     * 获取商城订单趋势图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/mall-order-trend")
    public ResponseVo<ChartDataDto> getMallOrderTrend() {
        logger.info("获取商城订单趋势图表数据");
        try {
            ChartDataDto chartData = analyticsService.getMallOrderTrend();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取商城订单趋势失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取热门商品排行图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/popular-products")
    public ResponseVo<ChartDataDto> getPopularProducts() {
        logger.info("获取热门商品排行图表数据");
        try {
            ChartDataDto chartData = analyticsService.getPopularProducts();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取热门商品排行失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取商城订单状态分布图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/mall-order-status-distribution")
    public ResponseVo<ChartDataDto> getMallOrderStatusDistribution() {
        logger.info("获取商城订单状态分布图表数据");
        try {
            ChartDataDto chartData = analyticsService.getMallOrderStatusDistribution();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取商城订单状态分布失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    // ==================== 论坛相关图表 ====================
    
    /**
     * 获取发帖趋势图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/post-trend")
    public ResponseVo<ChartDataDto> getPostTrend() {
        logger.info("获取发帖趋势图表数据");
        try {
            ChartDataDto chartData = analyticsService.getPostTrend();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取发帖趋势失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取帖子分类分布图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/post-category-distribution")
    public ResponseVo<ChartDataDto> getPostCategoryDistribution() {
        logger.info("获取帖子分类分布图表数据");
        try {
            ChartDataDto chartData = analyticsService.getPostCategoryDistribution();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取帖子分类分布失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
    
    /**
     * 获取最活跃用户排行图表数据
     * @return 图表数据
     */
    @GetMapping("/charts/most-active-users")
    public ResponseVo<ChartDataDto> getMostActiveUsers() {
        logger.info("获取最活跃用户排行图表数据");
        try {
            ChartDataDto chartData = analyticsService.getMostActiveUsers();
            return ResponseVo.success("获取成功", chartData);
        } catch (Exception e) {
            logger.error("获取最活跃用户排行失败: {}", e.getMessage(), e);
            return ResponseVo.error(500, "获取图表数据失败");
        }
    }
} 