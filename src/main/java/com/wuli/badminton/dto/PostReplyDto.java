package com.wuli.badminton.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 帖子回复数据传输对象
 */
@Data
public class PostReplyDto {
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
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户名（备用）
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 父回复ID（用于回复嵌套）
     */
    private Long parentId;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;
    
    /**
     * 子回复列表
     */
    private List<PostReplyDto> children;

    /**
     * 回复目标回复ID
     */
    private Long replyToId;

    /**
     * 回复目标的用户ID
     */
    private Long replyToUserId;

    /**
     * 回复目标的用户名
     */
    private String replyToUsername;

    /**
     * 回复目标的用户昵称
     */
    private String replyToNickname;
} 