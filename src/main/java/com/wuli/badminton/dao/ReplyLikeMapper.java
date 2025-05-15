package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.ReplyLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 回复点赞数据访问接口
 */
@Mapper
public interface ReplyLikeMapper {
    
    /**
     * 保存点赞记录
     * 
     * @param replyLike 点赞对象
     * @return 影响的行数
     */
    int insert(ReplyLike replyLike);
    
    /**
     * 删除点赞记录
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int delete(@Param("replyId") Long replyId, @Param("userId") Long userId);
    
    /**
     * 查询用户是否已点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 点赞记录，不存在则返回null
     */
    ReplyLike findByReplyIdAndUserId(@Param("replyId") Long replyId, @Param("userId") Long userId);
    
    /**
     * 统计回复点赞数
     * 
     * @param replyId 回复ID
     * @return 点赞数
     */
    int countByReplyId(Long replyId);
} 