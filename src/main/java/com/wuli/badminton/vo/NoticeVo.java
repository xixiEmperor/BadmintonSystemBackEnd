package com.wuli.badminton.vo;

import lombok.Data;
import java.util.Date;

/**
 * 公告通知VO类
 */
@Data
public class NoticeVo {
    
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
     * 通知类型描述
     */
    private String typeDesc;
    
    /**
     * 发布状态：0-草稿，1-已发布
     */
    private Integer status;
    
    /**
     * 发布状态描述
     */
    private String statusDesc;
    
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
} 