package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 特殊日期配置实体类
 * 用于管理节假日、维护日等特殊日期的场地配置
 */
@Data
public class SpecialDateConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 配置名称
     */
    private String configName;
    
    /**
     * 特殊日期
     */
    private Date specialDate;
    
    /**
     * 配置类型：1-节假日，2-维护日，3-特殊开放日
     */
    private Integer configType;
    
    /**
     * 影响的场地ID（多个用逗号分隔，null表示全部场地）
     */
    private String affectedVenueIds;
    
    /**
     * 影响的时间段开始时间
     */
    private String startTime;
    
    /**
     * 影响的时间段结束时间
     */
    private String endTime;
    
    /**
     * 特殊日期的场地状态：1-空闲中，2-使用中，4-维护中
     */
    private Integer venueStatus;
    
    /**
     * 是否可预约：1-可预约，0-不可预约
     */
    private Integer bookable;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer enabled;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 