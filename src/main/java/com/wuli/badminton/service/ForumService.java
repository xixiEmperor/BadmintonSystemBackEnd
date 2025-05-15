package com.wuli.badminton.service;

import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.PostDetailDto;
import com.wuli.badminton.dto.PostListDto;
import com.wuli.badminton.dto.PostReplyDto;
import com.wuli.badminton.pojo.Post;
import com.wuli.badminton.pojo.PostCategory;
import com.wuli.badminton.pojo.PostReply;

import java.util.List;

/**
 * 论坛服务接口
 */
public interface ForumService {
    /**
     * 查询帖子列表
     * 
     * @param categoryCode 分类代码
     * @param keyword 搜索关键词
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PostListDto> getPostList(String categoryCode, String keyword, int pageNum, int pageSize);
    
    /**
     * 查询热门帖子
     * 
     * @param limit 限制条数
     * @return 热门帖子列表
     */
    List<PostListDto> getHotPosts(int limit);
    
    /**
     * 查询所有分类
     * 
     * @return 分类列表
     */
    List<PostCategory> getAllCategories();
    
    /**
     * 根据ID获取帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子对象
     */
    Post getPostById(Long postId);
    
    /**
     * 获取带有用户信息的帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情DTO对象
     */
    PostDetailDto getPostDetailWithUserInfo(Long postId);
    
    /**
     * 增加帖子浏览次数
     * 
     * @param postId 帖子ID
     */
    void incrementPostViews(Long postId);
    
    /**
     * 创建新帖子
     * 
     * @param post 帖子对象
     * @return 创建后的帖子ID
     */
    Long createPost(Post post);
    
    /**
     * 更新帖子
     * 
     * @param post 帖子对象
     * @return 是否更新成功
     */
    boolean updatePost(Post post);
    
    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 是否删除成功
     */
    boolean deletePost(Long postId);
    
    /**
     * 获取帖子回复列表
     * 
     * @param postId 帖子ID
     * @return 回复列表
     */
    List<PostReply> getPostReplies(Long postId);
    
    /**
     * 获取带有用户信息的帖子回复列表
     * 
     * @param postId 帖子ID
     * @return 回复列表DTO
     */
    List<PostReplyDto> getPostRepliesWithUserInfo(Long postId);
    
    /**
     * 添加回复
     * 
     * @param reply 回复对象
     * @return 回复ID
     */
    Long addReply(PostReply reply);
    
    /**
     * 删除回复
     * 
     * @param replyId 回复ID
     * @return 是否删除成功
     */
    boolean deleteReply(Long replyId);
    
    /**
     * 更新帖子的回复数和最后回复时间
     * 
     * @param postId 帖子ID
     */
    void updatePostRepliesInfo(Long postId);
} 