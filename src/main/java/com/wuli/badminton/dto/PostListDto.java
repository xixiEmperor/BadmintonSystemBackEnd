package com.wuli.badminton.dto;

import lombok.Data;
import java.util.Date;

/**
 * 帖子列表项数据传输对象
 */
@Data
public class PostListDto {
    /**
     * 帖子ID
     */
    private Long id;
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 作者名称
     */
    private String author;
    
    /**
     * 作者头像
     */
    private String avatar;
    
    /**
     * 分类名称
     */
    private String category;
    
    /**
     * 分类代码
     */
    private String categoryCode;
    
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
     * 发布时间
     */
    private Date publishTime;
    
    /**
     * 最后回复时间
     */
    private Date lastReply;

    /**
     * 是否置顶
     */
    private Boolean isTop = false;
} 