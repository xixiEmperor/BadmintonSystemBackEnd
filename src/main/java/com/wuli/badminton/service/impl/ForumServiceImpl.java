package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.PostCategoryMapper;
import com.wuli.badminton.dao.PostMapper;
import com.wuli.badminton.dao.PostReplyMapper;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.PostListDto;
import com.wuli.badminton.dto.PostDetailDto;
import com.wuli.badminton.dto.PostReplyDto;
import com.wuli.badminton.pojo.Post;
import com.wuli.badminton.pojo.PostCategory;
import com.wuli.badminton.pojo.PostReply;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.pojo.UserDetail;
import com.wuli.badminton.service.ForumService;
import com.wuli.badminton.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        
        // 获取"热门"分类的特殊处理
        if ("hot".equals(categoryCode)) {
            List<Post> hotPosts = postMapper.findHotPosts(pageSize);
            long total = hotPosts.size();
            List<PostListDto> result = convertToPostListDto(hotPosts);
            logger.info("查询热门帖子成功，共{}条", total);
            return new PageResult<>(pageNum, pageSize, total, result);
        }
        
        // 查询帖子列表
        List<Post> posts = postMapper.findByFilter(categoryId, keyword, offset, pageSize);
        // 查询总记录数
        long total = postMapper.countByFilter(categoryId, keyword);
        
        // 转换为DTO
        List<PostListDto> result = convertToPostListDto(posts);
        
        logger.info("查询帖子列表成功，共{}条记录", total);
        return new PageResult<>(pageNum, pageSize, total, result);
    }
    
    @Override
    public List<PostListDto> getHotPosts(int limit) {
        logger.info("获取热门帖子, limit={}", limit);
        
        if (limit <= 0) {
            limit = 5;
        }
        
        List<Post> hotPosts = postMapper.findHotPosts(limit);
        List<PostListDto> result = convertToPostListDto(hotPosts);
        
        logger.info("查询热门帖子成功，共{}条", result.size());
        return result;
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
        
        List<PostReply> replies = replyMapper.findByPostId(postId);
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
        detailDto.setPublishTime(post.getPublishTime());
        detailDto.setLastReplyTime(post.getLastReplyTime());
        detailDto.setCategoryId(post.getCategoryId());
        
        // 获取作者信息
        User author = userMapper.findById(post.getUserId());
        if (author != null) {
            detailDto.setAuthorUsername(author.getUsername());
            detailDto.setAuthorAvatar(author.getAvatar());
            
            // 获取用户详情信息
            UserDetail userDetail = userDetailService.findByUserId(post.getUserId());
            if (userDetail != null && userDetail.getNickname() != null && !userDetail.getNickname().isEmpty()) {
                detailDto.setAuthorNickname(userDetail.getNickname());
            } else {
                // 如果昵称为空，则使用用户名
                detailDto.setAuthorNickname(author.getUsername());
            }
        }
        
        // 获取分类信息
        PostCategory category = categoryMapper.findById(post.getCategoryId());
        if (category != null) {
            detailDto.setCategoryName(category.getName());
            detailDto.setCategoryCode(category.getCode());
        }
        
        logger.info("带用户信息的帖子详情获取成功: id={}", postId);
        return detailDto;
    }
    
    @Override
    public List<PostReplyDto> getPostRepliesWithUserInfo(Long postId) {
        logger.info("获取带用户信息的帖子回复列表: postId={}", postId);
        
        List<PostReply> replies = replyMapper.findByPostId(postId);
        List<PostReplyDto> replyDtos = new ArrayList<>();
        
        for (PostReply reply : replies) {
            PostReplyDto dto = new PostReplyDto();
            // 复制回复基本信息
            dto.setId(reply.getId());
            dto.setPostId(reply.getPostId());
            dto.setUserId(reply.getUserId());
            dto.setContent(reply.getContent());
            dto.setParentId(reply.getParentId());
            dto.setReplyTime(reply.getReplyTime());
            
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
            
            replyDtos.add(dto);
        }
        
        logger.info("带用户信息的帖子回复列表获取成功，共{}条", replyDtos.size());
        return replyDtos;
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
} 