package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 场地可用性查询DTO
 */
@Data
public class VenueAvailabilityQueryDto {
    
    /**
     * 查询日期 (YYYY-MM-DD)
     */
    @NotNull(message = "查询日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式错误，应为YYYY-MM-DD")
    private String date;
    
    /**
     * 开始时间 (HH:mm)
     */
    @NotNull(message = "开始时间不能为空")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "时间格式错误，应为HH:mm")
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    @NotNull(message = "结束时间不能为空")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "时间格式错误，应为HH:mm")
    private String endTime;
    
    /**
     * 场地类型（可选，1-羽毛球场）
     */
    private Integer venueType;
    
    /**
     * 最低价格（可选）
     */
    private Double minPrice;
    
    /**
     * 最高价格（可选）
     */
    private Double maxPrice;
} 