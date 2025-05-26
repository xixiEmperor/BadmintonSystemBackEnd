package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 场地时间表实体类
 * 用于记录每个场地在特定日期和时间段的状态
 */
@Data
public class VenueSchedule {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 场地ID
     */
    private Integer venueId;
    
    /**
     * 场地名称（冗余字段，方便查询）
     */
    private String venueName;
    
    /**
     * 日期 (YYYY-MM-DD)
     */
    private Date scheduleDate;
    
    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;
    
    /**
     * 状态：1-空闲中，2-使用中，3-已预约，4-维护中
     * 对应 VenueStatusEnum
     */
    private Integer status;
    
    /**
     * 是否可预约：1-可预约，0-不可预约
     */
    private Integer bookable;
    
    /**
     * 关联的预约订单ID（如果状态为已预约）
     */
    private Long reservationId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 