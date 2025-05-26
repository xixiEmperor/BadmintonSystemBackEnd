package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 场地可用性查询DTO
 */
@Data
public class VenueAvailabilityDto {
    
    /**
     * 场地ID（可选，为空则查询所有场地）
     */
    private Integer venueId;
    
    /**
     * 查询日期 (YYYY-MM-DD)
     */
    @NotNull(message = "查询日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式错误，应为YYYY-MM-DD")
    private String date;
    
    /**
     * 开始时间（可选，用于检查特定时段）
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式错误，应为HH:mm")
    private String startTime;
    
    /**
     * 结束时间（可选，用于检查特定时段）
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式错误，应为HH:mm")
    private String endTime;
} 