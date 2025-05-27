package com.wuli.badminton.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 场地VO类
 */
@Data
public class VenueVo {
    
    /**
     * 场地ID
     */
    private Integer id;
    
    /**
     * 场地名称
     */
    private String name;
    
    /**
     * 场地描述
     */
    private String description;
    
    /**
     * 场地位置
     */
    private String location;
    
    /**
     * 每小时价格
     */
    private BigDecimal pricePerHour;
    
    /**
     * 场地类型：1-羽毛球场
     */
    private Integer type;
    
    /**
     * 场地类型描述
     */
    private String typeDesc;
    
    /**
     * 场地基础状态：0-未启用，1-启用
     */
    private Integer status;
    
    /**
     * 场地状态描述
     */
    private String statusDesc;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
} 