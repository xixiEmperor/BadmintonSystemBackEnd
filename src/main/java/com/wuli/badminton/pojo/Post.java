package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 论坛帖子实体类
 */
@Data
public class Post {
    /**
     * 帖子ID
     */
    private Long id;
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 作者ID（关联用户表）
     */
    private Long userId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 浏览次数
     */
    private Integer views;
    
    /**
     * 回复数量
     */
    private Integer replyCount;
    
    /**
     * 点赞数量
     */
    private Integer likes;
    
    /**
     * 状态：0-删除，1-正常
     */
    private Integer status;
    
    /**
     * 发布时间
     */
    private Date publishTime;
    
    /**
     * 最后回复时间
     */
    private Date lastReplyTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;

        /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop = 0; // 默认值为0，表示不置顶
    
} 