package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 帖子点赞实体类
 */
@Data
public class PostLike {
    
    /**
     * 点赞ID
     */
    private Long id;
    
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 