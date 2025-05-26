package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 特殊日期配置DTO
 */
@Data
public class SpecialDateConfigDto {
    
    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;
    
    /**
     * 特殊日期 (YYYY-MM-DD)
     */
    @NotBlank(message = "特殊日期不能为空")
    private String specialDate;
    
    /**
     * 配置类型：1-节假日，2-维护日，3-特殊开放日
     */
    @NotNull(message = "配置类型不能为空")
    private Integer configType;
    
    /**
     * 影响的场地ID（多个用逗号分隔，null表示全部场地）
     */
    private String affectedVenueIds;
    
    /**
     * 影响的时间段开始时间 (HH:mm)
     */
    private String startTime;
    
    /**
     * 影响的时间段结束时间 (HH:mm)
     */
    private String endTime;
    
    /**
     * 特殊日期的场地状态：1-空闲中，2-使用中，4-维护中
     */
    @NotNull(message = "场地状态不能为空")
    private Integer venueStatus;
    
    /**
     * 是否可预约：1-可预约，0-不可预约
     */
    @NotNull(message = "可预约状态不能为空")
    private Integer bookable;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer enabled = 1;
} 