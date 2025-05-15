package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 回复点赞实体类
 */
@Data
public class ReplyLike {
    
    /**
     * 点赞ID
     */
    private Long id;
    
    /**
     * 回复ID
     */
    private Long replyId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 