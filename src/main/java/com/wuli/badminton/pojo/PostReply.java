package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 帖子回复实体类
 */
@Data
public class PostReply {
    /**
     * 回复ID
     */
    private Long id;
    
    /**
     * 所属帖子ID
     */
    private Long postId;
    
    /**
     * 回复用户ID
     */
    private Long userId;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 父回复ID（用于回复嵌套）
     */
    private Long parentId;
    
    /**
     * 回复时间
     */
    private Date replyTime;
    
    /**
     * 点赞数量
     */
    private Integer likes;
    
    /**
     * 状态：0-删除，1-正常
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
     * 回复的目标回复ID（即实际回复的是哪条回复）
     */
    private Long replyToId;
    
    /**
     * 回复的目标用户ID
     */
    private Long replyToUserId;
} 