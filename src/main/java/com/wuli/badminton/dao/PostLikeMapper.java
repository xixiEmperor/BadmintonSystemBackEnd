package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子点赞数据访问接口
 */
@Mapper
public interface PostLikeMapper {
    
    /**
     * 保存点赞记录
     * 
     * @param postLike 点赞对象
     * @return 影响的行数
     */
    int insert(PostLike postLike);
    
    /**
     * 删除点赞记录
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * 查询用户是否已点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录，不存在则返回null
     */
    PostLike findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * 统计帖子点赞数
     * 
     * @param postId 帖子ID
     * @return 点赞数
     */
    int countByPostId(Long postId);
} 