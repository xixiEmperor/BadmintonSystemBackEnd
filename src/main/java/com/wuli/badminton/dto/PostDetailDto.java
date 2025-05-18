package com.wuli.badminton.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 帖子详情数据传输对象
 * 符合接口文档要求的格式
 */
@Data
public class PostDetailDto {
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
     * 作者ID（仅用于内部处理，不在API响应中返回）
     */
    private Long userId;
    
    /**
     * 作者昵称
     */
    private String author;
    
    /**
     * 作者用户名（备用，仅用于内部处理）
     */
    private String authorUsername;
    
    /**
     * 作者头像（仅用于内部处理，不在API响应中返回）
     */
    private String authorAvatar;
    
    /**
     * 分类ID（仅用于内部处理，不在API响应中返回）
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String category;
    
    /**
     * 分类代码（仅用于内部处理，不在API响应中返回）
     */
    private String categoryCode;
    
    /**
     * 浏览次数
     */
    private Integer views;
    
    /**
     * 回复数量
     */
    private Integer replyCount = 0;
    
    /**
     * 点赞数量
     */
    private Integer likes;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publishTime;
    
    /**
     * 最后回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastReply;

    /**
     * 是否置顶
     */
    private Boolean isTop = false;
} 