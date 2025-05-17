package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.PostCategoryMapper;
import com.wuli.badminton.dao.PostMapper;
import com.wuli.badminton.dao.PostReplyMapper;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.dao.PostLikeMapper;
import com.wuli.badminton.dao.ReplyLikeMapper;
import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.PostListDto;
import com.wuli.badminton.dto.PostDetailDto;
import com.wuli.badminton.dto.PostReplyDto;
import com.wuli.badminton.pojo.Post;
import com.wuli.badminton.pojo.PostCategory;
import com.wuli.badminton.pojo.PostReply;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.pojo.PostLike;
import com.wuli.badminton.pojo.UserDetail;
import com.wuli.badminton.pojo.ReplyLike;
import com.wuli.badminton.service.ForumService;
import com.wuli.badminton.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 论坛服务实现类
 */
@Service
public class ForumServiceImpl implements ForumService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForumServiceImpl.class);
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostCategoryMapper categoryMapper;
    
    @Autowired
    private PostReplyMapper replyMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserDetailService userDetailService;
    
    @Autowired
    private PostLikeMapper postLikeMapper;
    
    @Autowired
    private ReplyLikeMapper replyLikeMapper;
    
    @Override
    public PageResult<PostListDto> getPostList(String categoryCode, String keyword, int pageNum, int pageSize) {
        logger.info("获取帖子列表: 分类={}, 关键词={}, 页码={}, 每页大小={}", categoryCode, keyword, pageNum, pageSize);
        
        // 参数校验
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        
        // 计算分页偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 根据分类代码查找分类ID
        Long categoryId = null;
        if (categoryCode != null && !categoryCode.equals("all") && !categoryCode.equals("hot")) {
            PostCategory category = categoryMapper.findByCode(categoryCode);
            if (category != null) {
                categoryId = category.getId();
                logger.info("找到分类: code={}, id={}, name={}", categoryCode, categoryId, category.getName());
            } else {
                logger.warn("未找到指定分类: {}", categoryCode);
            }
        }
        
        // 查询帖子列表
        List<Post> posts = postMapper.findByFilter(categoryId, keyword, offset, pageSize);
        // 查询总记录数
        long total = postMapper.countByFilter(categoryId, keyword);
        
        // 转换为DTO
        List<PostListDto> result = convertToPostListDto(posts);
        
        logger.info("查询帖子列表成功，共{}条记录", total);
        return PageResult.build(pageNum, pageSize, total, result);
    }
    

    
    @Override
    public List<PostCategory> getAllCategories() {
        logger.info("获取所有帖子分类");
        
        List<PostCategory> categories = categoryMapper.findAll();
        logger.info("查询分类成功，共{}条", categories.size());
        
        return categories;
    }
    
    @Override
    public Post getPostById(Long postId) {
        logger.info("根据ID获取帖子: id={}", postId);
        
        Post post = postMapper.findById(postId);
        if (post != null) {
            logger.info("查询帖子成功: id={}, title={}", postId, post.getTitle());
        } else {
            logger.warn("未找到指定帖子: id={}", postId);
        }
        
        return post;
    }
    
    @Override
    public void incrementPostViews(Long postId) {
        logger.info("增加帖子浏览次数: id={}", postId);
        
        int rows = postMapper.incrementViews(postId);
        if (rows > 0) {
            logger.info("帖子浏览次数增加成功: id={}", postId);
        } else {
            logger.warn("帖子浏览次数增加失败: id={}", postId);
        }
    }
    
    @Override
    @Transactional
    public Long createPost(Post post) {
        logger.info("创建新帖子: title={}, userId={}", post.getTitle(), post.getUserId());
        
        // 设置初始值
        post.setViews(0);
        post.setReplyCount(0);
        post.setStatus(1); // 1-正常
        post.setLikes(0);
        
        postMapper.insert(post);
        logger.info("帖子创建成功: id={}", post.getId());
        
        return post.getId();
    }
    
    @Override
    @Transactional
    public boolean updatePost(Post post) {
        logger.info("更新帖子: id={}, title={}", post.getId(), post.getTitle());
        
        int rows = postMapper.update(post);
        if (rows > 0) {
            logger.info("帖子更新成功: id={}", post.getId());
            return true;
        } else {
            logger.warn("帖子更新失败: id={}", post.getId());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deletePost(Long postId) {
        logger.info("删除帖子: id={}", postId);
        
        int rows = postMapper.deleteById(postId);
        if (rows > 0) {
            logger.info("帖子删除成功: id={}", postId);
            return true;
        } else {
            logger.warn("帖子删除失败: id={}", postId);
            return false;
        }
    }
    
    @Override
    public List<PostReply> getPostReplies(Long postId) {
        logger.info("获取帖子回复列表: postId={}", postId);
        
        // 默认按点赞数排序
        List<PostReply> replies = replyMapper.findByPostId(postId, "likes");
        logger.info("查询帖子回复成功，共{}条", replies.size());
        
        return replies;
    }
    
    @Override
    @Transactional
    public Long addReply(PostReply reply) {
        logger.info("添加帖子回复: postId={}, userId={}", reply.getPostId(), reply.getUserId());
        
        // 设置回复时间
        reply.setReplyTime(new Date());
        
        replyMapper.insert(reply);
        logger.info("回复添加成功: id={}", reply.getId());
        
        // 更新帖子回复数和最后回复时间
        updatePostRepliesInfo(reply.getPostId());
        
        return reply.getId();
    }
    
    @Override
    @Transactional
    public boolean deleteReply(Long replyId) {
        logger.info("删除回复: id={}", replyId);
        
        // 先获取回复信息
        PostReply reply = replyMapper.findById(replyId);
        if (reply == null) {
            logger.warn("未找到指定回复: id={}", replyId);
            return false;
        }
        
        // 删除回复
        int rows = replyMapper.deleteById(replyId);
        if (rows > 0) {
            logger.info("回复删除成功: id={}", replyId);
            
            // 更新帖子回复数和最后回复时间
            updatePostRepliesInfo(reply.getPostId());
            
            return true;
        } else {
            logger.warn("回复删除失败: id={}", replyId);
            return false;
        }
    }
    
    @Override
    @Transactional
    public void updatePostRepliesInfo(Long postId) {
        logger.info("更新帖子回复信息: postId={}", postId);
        
        int rows = postMapper.updateRepliesAndLastReplyTime(postId);
        if (rows > 0) {
            logger.info("帖子回复信息更新成功: postId={}", postId);
        } else {
            logger.warn("帖子回复信息更新失败: postId={}", postId);
        }
    }
    
    @Override
    public PostDetailDto getPostDetailWithUserInfo(Long postId) {
        logger.info("获取带用户信息的帖子详情: id={}", postId);
        
        Post post = postMapper.findById(postId);
        if (post == null) {
            logger.warn("未找到指定帖子: id={}", postId);
            return null;
        }
        
        PostDetailDto detailDto = new PostDetailDto();
        // 复制帖子基本信息
        detailDto.setId(post.getId());
        detailDto.setTitle(post.getTitle());
        detailDto.setContent(post.getContent());
        detailDto.setUserId(post.getUserId());
        detailDto.setViews(post.getViews());
        detailDto.setReplies(post.getReplyCount());
        detailDto.setLikes(post.getLikes());
        detailDto.setPublishTime(post.getPublishTime());
        detailDto.setLastReply(post.getLastReplyTime());
        detailDto.setCategoryId(post.getCategoryId());
        
        // 获取作者信息
        User author = userMapper.findById(post.getUserId());
        if (author != null) {
            detailDto.setAuthorUsername(author.getUsername());
            detailDto.setAuthorAvatar(author.getAvatar());
            
            // 获取用户详情信息
            UserDetail userDetail = userDetailService.findByUserId(post.getUserId());
            if (userDetail != null && userDetail.getNickname() != null && !userDetail.getNickname().isEmpty()) {
                detailDto.setAuthor(userDetail.getNickname());
            } else {
                // 如果昵称为空，则使用用户名
                detailDto.setAuthor(author.getUsername());
            }
        }
        
        // 获取分类信息
        PostCategory category = categoryMapper.findById(post.getCategoryId());
        if (category != null) {
            detailDto.setCategory(category.getName());
            detailDto.setCategoryCode(category.getCode());
        }
        
        logger.info("带用户信息的帖子详情获取成功: id={}", postId);
        return detailDto;
    }
    
    @Override
    public List<PostReplyDto> getPostRepliesWithUserInfo(Long postId, String orderBy) {
        logger.info("获取带用户信息的帖子回复列表: postId={}, orderBy={}", postId, orderBy);
        
        // 获取一级回复
        List<PostReply> rootReplies = replyMapper.findByPostId(postId, orderBy);
        List<PostReplyDto> replyDtos = new ArrayList<>();
        
        // 尝试获取当前登录用户
        User currentUser = null;
        try {
            currentUser = userMapper.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            logger.info("获取当前用户失败，将以未登录状态处理: {}", e.getMessage());
        }
        
        // 转换一级回复
        for (PostReply reply : rootReplies) {
            PostReplyDto dto = convertToReplyDto(reply, currentUser);
            
            // 获取子回复
            dto.setChildren(getChildReplies(reply.getId(), orderBy, currentUser));
            
            replyDtos.add(dto);
        }
        
        logger.info("带用户信息的帖子回复列表获取成功，共{}条一级回复", replyDtos.size());
        return replyDtos;
    }
    
    /**
     * 获取子回复列表
     * 
     * @param parentId 父回复ID
     * @param orderBy 排序方式
     * @param currentUser 当前用户
     * @return 子回复列表
     */
    private List<PostReplyDto> getChildReplies(Long parentId, String orderBy, User currentUser) {
        List<PostReply> childReplies = replyMapper.findByParentId(parentId, orderBy);
        List<PostReplyDto> childDtos = new ArrayList<>();
        
        for (PostReply childReply : childReplies) {
            PostReplyDto childDto = convertToReplyDto(childReply, currentUser);
            childDtos.add(childDto);
        }
        
        return childDtos;
    }
    
    /**
     * 将 PostReply 实体转换为 PostReplyDto
     * 
     * @param reply 回复实体
     * @param currentUser 当前用户
     * @return 回复DTO
     */
    private PostReplyDto convertToReplyDto(PostReply reply, User currentUser) {
        PostReplyDto dto = new PostReplyDto();
        
        // 复制回复基本信息
        dto.setId(reply.getId());
        dto.setPostId(reply.getPostId());
        dto.setUserId(reply.getUserId());
        dto.setContent(reply.getContent());
        dto.setParentId(reply.getParentId());
        dto.setReplyTime(reply.getReplyTime());
        dto.setLikes(reply.getLikes());
        
        // 获取用户信息
        User user = userMapper.findById(reply.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());
            
            // 获取用户详情信息
            UserDetail userDetail = userDetailService.findByUserId(reply.getUserId());
            if (userDetail != null && userDetail.getNickname() != null && !userDetail.getNickname().isEmpty()) {
                dto.setNickname(userDetail.getNickname());
            } else {
                // 如果昵称为空，则使用用户名
                dto.setNickname(user.getUsername());
            }
        }
        
        // 设置点赞状态
        if (currentUser != null) {
            boolean isLiked = isReplyLiked(reply.getId(), currentUser.getId());
            dto.setIsLiked(isLiked);
        } else {
            dto.setIsLiked(false);
        }
        
        return dto;
    }
    
    /**
     * 将Post实体转换为PostListDto
     * 
     * @param posts Post实体列表
     * @return PostListDto列表
     */
    private List<PostListDto> convertToPostListDto(List<Post> posts) {
        List<PostListDto> result = new ArrayList<>();
        
        for (Post post : posts) {
            PostListDto dto = new PostListDto();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setViews(post.getViews());
            dto.setReplies(post.getReplyCount());
            dto.setLikes(post.getLikes());
            dto.setPublishTime(post.getPublishTime());
            dto.setLastReply(post.getLastReplyTime());
            
            // 获取作者信息
            User author = userMapper.findById(post.getUserId());
            if (author != null) {
                // 获取用户详情信息
                UserDetail userDetail = userDetailService.findByUserId(post.getUserId());
                if (userDetail != null && userDetail.getNickname() != null && !userDetail.getNickname().isEmpty()) {
                    // 优先使用昵称
                    dto.setAuthor(userDetail.getNickname());
                } else {
                    // 如果昵称为空，则使用用户名
                    dto.setAuthor(author.getUsername());
                }
                dto.setAvatar(author.getAvatar());
            }
            
            // 获取分类信息
            PostCategory category = categoryMapper.findById(post.getCategoryId());
            if (category != null) {
                dto.setCategory(category.getName());
                dto.setCategoryCode(category.getCode());
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        logger.info("用户点赞帖子: postId={}, userId={}", postId, userId);
        
        // 判断帖子是否存在
        Post post = postMapper.findById(postId);
        if (post == null) {
            logger.warn("点赞失败，帖子不存在: postId={}", postId);
            return false;
        }
        
        // 判断是否已点赞
        if (isPostLiked(postId, userId)) {
            logger.info("用户已点赞该帖子: postId={}, userId={}", postId, userId);
            return true;
        }
        
        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        
        int rows = postLikeMapper.insert(postLike);
        
        if (rows > 0) {
            // 更新帖子点赞数
            post.setLikes(postLikeMapper.countByPostId(postId));
            postMapper.update(post);
            
            logger.info("点赞成功: postId={}, userId={}, 当前点赞数={}", postId, userId, post.getLikes());
            return true;
        } else {
            logger.warn("点赞失败: postId={}, userId={}", postId, userId);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        logger.info("用户取消点赞帖子: postId={}, userId={}", postId, userId);
        
        // 判断帖子是否存在
        Post post = postMapper.findById(postId);
        if (post == null) {
            logger.warn("取消点赞失败，帖子不存在: postId={}", postId);
            return false;
        }
        
        // 判断是否已点赞
        if (!isPostLiked(postId, userId)) {
            logger.info("用户未点赞该帖子，无需取消: postId={}, userId={}", postId, userId);
            return true;
        }
        
        int rows = postLikeMapper.delete(postId, userId);
        
        if (rows > 0) {
            // 更新帖子点赞数
            post.setLikes(postLikeMapper.countByPostId(postId));
            postMapper.update(post);
            
            logger.info("取消点赞成功: postId={}, userId={}, 当前点赞数={}", postId, userId, post.getLikes());
            return true;
        } else {
            logger.warn("取消点赞失败: postId={}, userId={}", postId, userId);
            return false;
        }
    }
    
    @Override
    public boolean isPostLiked(Long postId, Long userId) {
        PostLike postLike = postLikeMapper.findByPostIdAndUserId(postId, userId);
        return postLike != null;
    }
    
    @Override
    public PostDetailDto getPostDetail(Long postId) {
        logger.info("获取帖子详情(接口文档格式): postId={}", postId);
        
        // 获取带用户信息的帖子详情
        PostDetailDto detailDto = getPostDetailWithUserInfo(postId);
        if (detailDto == null) {
            logger.warn("未找到指定帖子: id={}", postId);
            return null;
        }
        
        // 尝试获取当前登录用户
        User currentUser = null;
        try {
            currentUser = userMapper.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            logger.info("获取当前用户失败，将以未登录状态处理: {}", e.getMessage());
        }
        
        // 设置是否已点赞
        if (currentUser != null) {
            boolean isLiked = isPostLiked(postId, currentUser.getId());
            detailDto.setIsLiked(isLiked);
            logger.info("当前用户 {} 是否已点赞该帖子: {}", currentUser.getUsername(), isLiked);
        } else {
            detailDto.setIsLiked(false);
            logger.info("未登录用户，点赞状态设为 false");
        }
        
        return detailDto;
    }
    
    @Override
    @Transactional
    public boolean likeReply(Long replyId, Long userId) {
        logger.info("用户点赞回复: replyId={}, userId={}", replyId, userId);
        
        // 判断回复是否存在
        PostReply reply = replyMapper.findById(replyId);
        if (reply == null) {
            logger.warn("点赞失败，回复不存在: replyId={}", replyId);
            return false;
        }
        
        // 判断是否已点赞
        if (isReplyLiked(replyId, userId)) {
            logger.info("用户已点赞该回复: replyId={}, userId={}", replyId, userId);
            return true;
        }
        
        // 创建点赞记录
        ReplyLike replyLike = new ReplyLike();
        replyLike.setReplyId(replyId);
        replyLike.setUserId(userId);
        
        int rows = replyLikeMapper.insert(replyLike);
        
        if (rows > 0) {
            // 更新回复点赞数
            int likeCount = replyLikeMapper.countByReplyId(replyId);
            replyMapper.updateLikes(replyId, likeCount);
            
            logger.info("点赞成功: replyId={}, userId={}, 当前点赞数={}", replyId, userId, likeCount);
            return true;
        } else {
            logger.warn("点赞失败: replyId={}, userId={}", replyId, userId);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean unlikeReply(Long replyId, Long userId) {
        logger.info("用户取消点赞回复: replyId={}, userId={}", replyId, userId);
        
        // 判断回复是否存在
        PostReply reply = replyMapper.findById(replyId);
        if (reply == null) {
            logger.warn("取消点赞失败，回复不存在: replyId={}", replyId);
            return false;
        }
        
        // 判断是否已点赞
        if (!isReplyLiked(replyId, userId)) {
            logger.info("用户未点赞该回复，无需取消: replyId={}, userId={}", replyId, userId);
            return true;
        }
        
        int rows = replyLikeMapper.delete(replyId, userId);
        
        if (rows > 0) {
            // 更新回复点赞数
            int likeCount = replyLikeMapper.countByReplyId(replyId);
            replyMapper.updateLikes(replyId, likeCount);
            
            logger.info("取消点赞成功: replyId={}, userId={}, 当前点赞数={}", replyId, userId, likeCount);
            return true;
        } else {
            logger.warn("取消点赞失败: replyId={}, userId={}", replyId, userId);
            return false;
        }
    }
    
    @Override
    public boolean isReplyLiked(Long replyId, Long userId) {
        ReplyLike replyLike = replyLikeMapper.findByReplyIdAndUserId(replyId, userId);
        return replyLike != null;
    }
} 