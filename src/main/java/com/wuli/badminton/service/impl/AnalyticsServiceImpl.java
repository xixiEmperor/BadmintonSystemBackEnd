package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.AnalyticsMapper;
import com.wuli.badminton.dto.ChartDataDto;
import com.wuli.badminton.dto.DashboardStatsDto;
import com.wuli.badminton.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据分析服务实现类
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);
    
    @Autowired
    private AnalyticsMapper analyticsMapper;
    
    @Override
    public DashboardStatsDto getDashboardStats() {
        logger.info("获取仪表板统计数据");
        try {
            DashboardStatsDto stats = new DashboardStatsDto();
            
            // 用户统计
            stats.setTotalUsers(analyticsMapper.getTotalUsers());
            stats.setNewUsersToday(analyticsMapper.getNewUsersToday());
            stats.setNewUsersThisMonth(analyticsMapper.getNewUsersThisMonth());
            stats.setActiveUsersToday(analyticsMapper.getActiveUsersToday());
            
            // 预约统计
            stats.setTotalReservations(analyticsMapper.getTotalReservations());
            stats.setReservationsToday(analyticsMapper.getReservationsToday());
            stats.setReservationsThisMonth(analyticsMapper.getReservationsThisMonth());
            stats.setReservationRevenue(analyticsMapper.getReservationRevenue());
            stats.setRevenueToday(analyticsMapper.getRevenueToday());
            stats.setRevenueThisMonth(analyticsMapper.getRevenueThisMonth());
            
            // 商城统计
            stats.setTotalOrders(analyticsMapper.getTotalMallOrders());
            stats.setOrdersToday(analyticsMapper.getMallOrdersToday());
            stats.setOrdersThisMonth(analyticsMapper.getMallOrdersThisMonth());
            stats.setMallRevenue(analyticsMapper.getMallRevenue());
            stats.setMallRevenueToday(analyticsMapper.getMallRevenueToday());
            stats.setMallRevenueThisMonth(analyticsMapper.getMallRevenueThisMonth());
            
            // 论坛统计
            stats.setTotalPosts(analyticsMapper.getTotalPosts());
            stats.setPostsToday(analyticsMapper.getPostsToday());
            stats.setTotalReplies(analyticsMapper.getTotalReplies());
            stats.setRepliesToday(analyticsMapper.getRepliesToday());
            
            // 场地统计
            stats.setTotalVenues(analyticsMapper.getTotalVenues());
            stats.setAvailableVenues(analyticsMapper.getAvailableVenues());
            stats.setVenueUtilizationRate(analyticsMapper.getVenueUtilizationRate());
            
            logger.info("仪表板统计数据获取成功");
            return stats;
        } catch (Exception e) {
            logger.error("获取仪表板统计数据失败: {}", e.getMessage(), e);
            return new DashboardStatsDto();
        }
    }
    
    @Override
    public ChartDataDto getUserRegistrationTrend() {
        logger.info("获取用户注册趋势图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getUserRegistrationTrend();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("date").toString());
                values.add(item.get("count"));
            }
            
            return new ChartDataDto(labels, values, "用户注册趋势", "line");
        } catch (Exception e) {
            logger.error("获取用户注册趋势失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getUserRoleDistribution() {
        logger.info("获取用户角色分布图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getUserRoleDistribution();
            List<ChartDataDto.PieDataItem> pieData = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                pieData.add(new ChartDataDto.PieDataItem(
                    item.get("name").toString(),
                    item.get("value")
                ));
            }
            
            ChartDataDto result = new ChartDataDto();
            result.setData(new ArrayList<>(pieData));
            result.setTitle("用户角色分布");
            result.setType("pie");
            return result;
        } catch (Exception e) {
            logger.error("获取用户角色分布失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getReservationTrend() {
        logger.info("获取预约趋势图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getReservationTrend();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("date").toString());
                values.add(item.get("count"));
            }
            
            return new ChartDataDto(labels, values, "预约趋势", "line");
        } catch (Exception e) {
            logger.error("获取预约趋势失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getVenueUsageRanking() {
        logger.info("获取场地使用率排行图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getVenueUsageRanking();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("name").toString());
                values.add(item.get("value"));
            }
            
            return new ChartDataDto(labels, values, "场地使用率排行", "bar");
        } catch (Exception e) {
            logger.error("获取场地使用率排行失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getReservationStatusDistribution() {
        logger.info("获取预约状态分布图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getReservationStatusDistribution();
            List<ChartDataDto.PieDataItem> pieData = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                pieData.add(new ChartDataDto.PieDataItem(
                    item.get("name").toString(),
                    item.get("value")
                ));
            }
            
            ChartDataDto result = new ChartDataDto();
            result.setData(new ArrayList<>(pieData));
            result.setTitle("预约状态分布");
            result.setType("pie");
            return result;
        } catch (Exception e) {
            logger.error("获取预约状态分布失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getRevenueTrend() {
        logger.info("获取收入趋势图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getRevenueTrend();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("date").toString());
                values.add(item.get("amount"));
            }
            
            return new ChartDataDto(labels, values, "收入趋势", "line");
        } catch (Exception e) {
            logger.error("获取收入趋势失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getMallOrderTrend() {
        logger.info("获取商城订单趋势图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getMallOrderTrend();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("date").toString());
                values.add(item.get("count"));
            }
            
            return new ChartDataDto(labels, values, "商城订单趋势", "line");
        } catch (Exception e) {
            logger.error("获取商城订单趋势失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getPopularProducts() {
        logger.info("获取热门商品排行图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getPopularProducts();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("name").toString());
                values.add(item.get("value"));
            }
            
            return new ChartDataDto(labels, values, "热门商品排行", "bar");
        } catch (Exception e) {
            logger.error("获取热门商品排行失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getMallOrderStatusDistribution() {
        logger.info("获取商城订单状态分布图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getMallOrderStatusDistribution();
            List<ChartDataDto.PieDataItem> pieData = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                pieData.add(new ChartDataDto.PieDataItem(
                    item.get("name").toString(),
                    item.get("value")
                ));
            }
            
            ChartDataDto result = new ChartDataDto();
            result.setData(new ArrayList<>(pieData));
            result.setTitle("商城订单状态分布");
            result.setType("pie");
            return result;
        } catch (Exception e) {
            logger.error("获取商城订单状态分布失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getPostTrend() {
        logger.info("获取发帖趋势图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getPostTrend();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("date").toString());
                values.add(item.get("count"));
            }
            
            return new ChartDataDto(labels, values, "发帖趋势", "line");
        } catch (Exception e) {
            logger.error("获取发帖趋势失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getPostCategoryDistribution() {
        logger.info("获取帖子分类分布图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getPostCategoryDistribution();
            List<ChartDataDto.PieDataItem> pieData = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                pieData.add(new ChartDataDto.PieDataItem(
                    item.get("name").toString(),
                    item.get("value")
                ));
            }
            
            ChartDataDto result = new ChartDataDto();
            result.setData(new ArrayList<>(pieData));
            result.setTitle("帖子分类分布");
            result.setType("pie");
            return result;
        } catch (Exception e) {
            logger.error("获取帖子分类分布失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getMostActiveUsers() {
        logger.info("获取最活跃用户排行图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getMostActiveUsers();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("name").toString());
                values.add(item.get("value"));
            }
            
            return new ChartDataDto(labels, values, "最活跃用户排行", "bar");
        } catch (Exception e) {
            logger.error("获取最活跃用户排行失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    @Override
    public ChartDataDto getHourlyReservationDistribution() {
        logger.info("获取每小时预约分布图表数据");
        try {
            List<Map<String, Object>> data = analyticsMapper.getHourlyReservationDistribution();
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (Map<String, Object> item : data) {
                labels.add(item.get("hour").toString());
                values.add(item.get("count"));
            }
            
            return new ChartDataDto(labels, values, "每小时预约分布", "bar");
        } catch (Exception e) {
            logger.error("获取每小时预约分布失败: {}", e.getMessage(), e);
            return new ChartDataDto(new ArrayList<>(), new ArrayList<>());
        }
    }
} 