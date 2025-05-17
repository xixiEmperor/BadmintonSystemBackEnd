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
    PostDetailDto  getPostDetailWithUserInfo(Long postId);
    
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
 * 设置帖子置顶状态
 * 
 * @param postId 帖子ID
 * @param isTop 是否置顶：true-置顶，false-取消置顶
 * @return 是否设置成功
 */

boolean setPostTopStatus(Long postId, boolean isTop);
    /**
     * 获取帖子回复列表
     * 
     * @param postId 帖子ID
     * @return 回复列表
     */
    List<PostReply> getPostReplies(Long postId);
    
    /**
     * 获取带有用户信息的帖子回复列表，支持排序
     * 
     * @param postId 帖子ID
     * @param orderBy 排序方式：likes-按点赞数排序，time-按时间排序，默认按点赞数
     * @return 回复列表DTO，包含嵌套结构
     */
    List<PostReplyDto> getPostRepliesWithUserInfo(Long postId, String orderBy);
    
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
    
    /**
     * 用户点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否点赞成功
     */
    boolean likeReply(Long replyId, Long userId);
    
    /**
     * 用户取消点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否取消成功
     */
    boolean unlikeReply(Long replyId, Long userId);
    
    /**
     * 判断用户是否已点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isReplyLiked(Long replyId, Long userId);
    
    /**
     * 用户点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞成功
     */
    boolean likePost(Long postId, Long userId);
    
    /**
     * 用户取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否取消成功
     */
    boolean unlikePost(Long postId, Long userId);
    
    /**
     * 判断用户是否已点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isPostLiked(Long postId, Long userId);
    
    /**
     * 获取符合接口文档的帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情
     */
    PostDetailDto getPostDetail(Long postId);
} 