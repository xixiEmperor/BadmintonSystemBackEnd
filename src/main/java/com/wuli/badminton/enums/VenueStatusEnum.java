package com.wuli.badminton.enums;

/**
 * 场地状态枚举
 */
public enum VenueStatusEnum {
    
    /**
     * 空闲中 - 可以预约
     */
    AVAILABLE(1, "空闲中"),
    
    /**
     * 使用中 - 不可预约（白天教学使用）
     */
    IN_USE(2, "使用中"),
    
    /**
     * 已预约 - 该时段已被预约
     */
    BOOKED(3, "已预约"),
    
    /**
     * 维护中 - 不可预约（设备维护、节假日等）
     */
    UNDER_MAINTENANCE(4, "维护中");
    
    private final Integer code;
    private final String desc;
    
    VenueStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    /**
     * 根据状态码获取枚举
     */
    public static VenueStatusEnum getByCode(Integer code) {
        for (VenueStatusEnum status : VenueStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 