package com.wuli.badminton.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 仪表板统计数据DTO
 */
@Data
public class DashboardStatsDto {
    // 用户统计
    private Long totalUsers;              // 总用户数
    private Long newUsersToday;           // 今日新增用户
    private Long newUsersThisMonth;       // 本月新增用户
    private Long activeUsersToday;        // 今日活跃用户
    
    // 预约统计
    private Long totalReservations;       // 总预约数
    private Long reservationsToday;       // 今日预约数
    private Long reservationsThisMonth;   // 本月预约数
    private BigDecimal reservationRevenue;// 预约总收入
    private BigDecimal revenueToday;      // 今日收入
    private BigDecimal revenueThisMonth;  // 本月收入
    
    // 商城统计
    private Long totalOrders;             // 总订单数
    private Long ordersToday;             // 今日订单数
    private Long ordersThisMonth;         // 本月订单数
    private BigDecimal mallRevenue;       // 商城总收入
    private BigDecimal mallRevenueToday;  // 商城今日收入
    private BigDecimal mallRevenueThisMonth; // 商城本月收入
    
    // 论坛统计
    private Long totalPosts;              // 总帖子数
    private Long postsToday;              // 今日发帖数
    private Long totalReplies;            // 总回复数
    private Long repliesToday;            // 今日回复数
    
    // 场地统计
    private Long totalVenues;             // 总场地数
    private Long availableVenues;         // 可用场地数
    private Double venueUtilizationRate;  // 场地利用率
} 