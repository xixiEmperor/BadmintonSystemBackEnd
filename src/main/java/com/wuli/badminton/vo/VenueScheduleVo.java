package com.wuli.badminton.vo;

import lombok.Data;

/**
 * 场地时间表VO类
 */
@Data
public class VenueScheduleVo {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 场地ID
     */
    private Integer venueId;
    
    /**
     * 场地名称
     */
    private String venueName;
    
    /**
     * 日期 (YYYY-MM-DD)
     */
    private String scheduleDate;
    
    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;
    
    /**
     * 时间段描述 (例如: 08:00-09:00)
     */
    private String timeSlot;
    
    /**
     * 状态：1-空闲中，2-使用中，3-已预约，4-维护中
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 是否可预约：1-可预约，0-不可预约
     */
    private Integer bookable;
    
    /**
     * 可预约描述
     */
    private String bookableDesc;
    
    /**
     * 关联的预约订单ID
     */
    private Long reservationId;
    
    /**
     * 备注
     */
    private String remark;
} 