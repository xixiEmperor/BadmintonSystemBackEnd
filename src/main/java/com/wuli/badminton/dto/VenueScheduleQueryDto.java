package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 场地时间表查询DTO
 */
@Data
public class VenueScheduleQueryDto {
    
    /**
     * 场地ID（可选）
     */
    private Integer venueId;
    
    /**
     * 查询日期 (YYYY-MM-DD)
     */
    @NotNull(message = "查询日期不能为空")
    private String scheduleDate;
    
    /**
     * 开始时间 (HH:mm)，可选
     */
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)，可选
     */
    private String endTime;
    
    /**
     * 状态筛选（可选）
     */
    private Integer status;
    
    /**
     * 是否只查询可预约的时段：1-是，0-否，null-全部
     */
    private Integer bookable;
} 