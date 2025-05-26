package com.wuli.badminton.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 场地可用性查询响应VO
 */
@Data
public class VenueAvailabilityVo {
    
    /**
     * 查询日期
     */
    private String date;
    
    /**
     * 查询时间段
     */
    private String timeSlot;
    
    /**
     * 可用场地总数
     */
    private Integer totalAvailable;
    
    /**
     * 查询的场地总数
     */
    private Integer totalVenues;
    
    /**
     * 可用场地列表
     */
    private List<AvailableVenue> availableVenues;
    
    /**
     * 不可用场地列表（可选，用于调试）
     */
    private List<UnavailableVenue> unavailableVenues;
    
    /**
     * 可用场地信息
     */
    @Data
    public static class AvailableVenue {
        private Integer id;
        private String name;
        private String description;
        private String location;
        private BigDecimal pricePerHour;
        private Integer type;
        private String typeDesc;
        private Boolean isRecommended; // 是否推荐（如价格合适、设施好等）
        private String recommendReason; // 推荐理由
    }
    
    /**
     * 不可用场地信息
     */
    @Data
    public static class UnavailableVenue {
        private Integer id;
        private String name;
        private String location;
        private BigDecimal pricePerHour;
        private String unavailableReason; // 不可用原因
        private Integer status; // 状态：2-使用中，3-已预约，4-维护中
        private String statusDesc; // 状态描述
    }
} 