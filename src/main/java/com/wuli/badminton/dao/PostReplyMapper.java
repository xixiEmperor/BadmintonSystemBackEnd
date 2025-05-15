package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.PostReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子回复数据访问接口
 */
@Mapper
public interface PostReplyMapper {
    /**
     * 插入回复
     * 
     * @param reply 回复对象
     * @return 影响的行数
     */
    int insert(PostReply reply);
    
    /**
     * 根据ID查询回复
     * 
     * @param id 回复ID
     * @return 回复对象
     */
    PostReply findById(Long id);
    
    /**
     * 根据帖子ID查询回复，支持排序
     * 
     * @param postId 帖子ID
     * @param orderBy 排序方式：likes-按点赞数排序，time-按时间排序，默认按点赞数
     * @return 回复列表
     */
    List<PostReply> findByPostId(@Param("postId") Long postId, @Param("orderBy") String orderBy);
    
    /**
     * 根据父回复ID查询子回复，支持排序
     * 
     * @param parentId 父回复ID
     * @param orderBy 排序方式：likes-按点赞数排序，time-按时间排序，默认按时间
     * @return 子回复列表
     */
    List<PostReply> findByParentId(@Param("parentId") Long parentId, @Param("orderBy") String orderBy);
    
    /**
     * 更新回复
     * 
     * @param reply 回复对象
     * @return 影响的行数
     */
    int update(PostReply reply);
    
    /**
     * 删除回复
     * 
     * @param id 回复ID
     * @return 影响的行数
     */
    int deleteById(Long id);
    
    /**
     * 统计帖子的回复数量
     * 
     * @param postId 帖子ID
     * @return 回复总数
     */
    long countByPostId(Long postId);
    
    /**
     * 查询帖子的最后回复时间
     * 
     * @param postId 帖子ID
     * @return 最后回复时间
     */
    PostReply findLastReplyByPostId(Long postId);
    
    /**
     * 更新回复的点赞数
     * 
     * @param replyId 回复ID
     * @param likes 点赞数
     * @return 影响的行数
     */
    int updateLikes(@Param("replyId") Long replyId, @Param("likes") int likes);
} 