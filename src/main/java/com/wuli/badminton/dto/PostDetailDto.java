package com.wuli.badminton.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 帖子详情数据传输对象
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
     * 作者ID
     */
    private Long userId;
    
    /**
     * 作者昵称
     */
    private String authorNickname;
    
    /**
     * 作者用户名（备用）
     */
    private String authorUsername;
    
    /**
     * 作者头像
     */
    private String authorAvatar;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
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
    private Integer replies;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
    
    /**
     * 最后回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReplyTime;
} 