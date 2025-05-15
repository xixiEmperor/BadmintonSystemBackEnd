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
     * 根据帖子ID查询回复
     * 
     * @param postId 帖子ID
     * @return 回复列表
     */
    List<PostReply> findByPostId(Long postId);
    
    /**
     * 根据父回复ID查询子回复
     * 
     * @param parentId 父回复ID
     * @return 子回复列表
     */
    List<PostReply> findByParentId(Long parentId);
    
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
} 