package com.wuli.badminton.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 场地数据传输对象
 */
@Data
public class VenueDto {
    
    /**
     * 场地名称
     */
    @NotBlank(message = "场地名称不能为空")
    private String name;
    
    /**
     * 场地描述
     */
    private String description;
    
    /**
     * 场地位置
     */
    @NotBlank(message = "场地位置不能为空")
    private String location;
    
    /**
     * 每小时价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal pricePerHour;
    
    /**
     * 场地类型（1-羽毛球场）
     */
    @NotNull(message = "场地类型不能为空")
    private Integer type = 1;
    
    /**
     * 场地基础状态：0-未启用，1-启用
     */
    private Integer status = 1;
} 