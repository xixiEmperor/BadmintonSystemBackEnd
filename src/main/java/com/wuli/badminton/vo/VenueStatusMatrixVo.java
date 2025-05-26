package com.wuli.badminton.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 场地状态矩阵VO
 */
@Data
public class VenueStatusMatrixVo {
    
    /**
     * 查询日期
     */
    private String date;
    
    /**
     * 场地列表
     */
    private List<VenueInfo> venues;
    
    /**
     * 时间段列表
     */
    private List<String> timeSlots;
    
    /**
     * 状态矩阵 - key为场地ID，value为该场地各时间段的状态
     */
    private Map<String, Map<String, TimeSlotStatus>> statusMatrix;
    
    /**
     * 场地基本信息
     */
    @Data
    public static class VenueInfo {
        private Integer id;
        private String name;
        private String location;
        private BigDecimal pricePerHour;
        private Boolean isAvailable; // 场地基础状态是否可用
    }
    
    /**
     * 时间段状态信息
     */
    @Data
    public static class TimeSlotStatus {
        private Integer status; // 1-空闲中，2-使用中，3-已预约，4-维护中
        private String statusDesc; // 状态描述
        private Boolean bookable; // 是否可预约
        private String reason; // 状态原因（如"教学时间"、"用户张三预约"等）
        private Long reservationId; // 预约订单ID（已预约状态时）
        private String username; // 预约用户名（已预约状态时）
    }
} 