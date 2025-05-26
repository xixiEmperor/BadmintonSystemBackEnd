package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 公告通知实体类
 */
@Data
public class Notice {
    
    /**
     * 通知ID
     */
    private Integer id;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知类型：1-普通通知，2-重要通知
     */
    private Integer type;
    
    /**
     * 发布状态：0-草稿，1-已发布
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 发布时间
     */
    private Date publishTime;
    
    // 通知类型常量
    public static final Integer TYPE_NORMAL = 1;      // 普通通知
    public static final Integer TYPE_IMPORTANT = 2;   // 重要通知
    
    // 发布状态常量
    public static final Integer STATUS_DRAFT = 0;     // 草稿
    public static final Integer STATUS_PUBLISHED = 1; // 已发布
    
    /**
     * 获取通知类型描述
     */
    public String getTypeDesc() {
        switch (type) {
            case 1:
                return "普通通知";
            case 2:
                return "重要通知";
            default:
                return "未知类型";
        }
    }
    
    /**
     * 获取发布状态描述
     */
    public String getStatusDesc() {
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            default:
                return "未知状态";
        }
    }
} 