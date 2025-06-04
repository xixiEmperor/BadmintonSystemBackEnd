package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 预约订单DTO类
 */
@Data
public class ReservationOrderDto {
    
    /**
     * 场地ID
     */
    @NotNull(message = "场地ID不能为空")
    private Integer venueId;
    
    /**
     * 预约日期 (YYYY-MM-DD)
     */
    @NotNull(message = "预约日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式错误，应为YYYY-MM-DD")
    private String reservationDate;
    
    /**
     * 开始时间 (HH:mm)
     */
    @NotNull(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式错误，应为HH:mm")
    private String startTime;
    
    /**
     * 结束时间 (HH:mm)
     */
    @NotNull(message = "结束时间不能为空")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式错误，应为HH:mm")
    private String endTime;
    
    /**
     * 备注
     */
    private String remark;
} 