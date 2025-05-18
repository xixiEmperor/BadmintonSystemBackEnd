package com.wuli.badminton.controller;

import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.PostDetailDto;
import com.wuli.badminton.dto.PostListDto;
import com.wuli.badminton.dto.PostReplyDto;
import com.wuli.badminton.pojo.Post;
import com.wuli.badminton.pojo.PostCategory;
import com.wuli.badminton.pojo.PostReply;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.ForumService;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    
    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);
    
    @Autowired
    private ForumService forumService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取帖子列表
     * 
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param category 分类代码
     * @param keyword 搜索关键词
     * @return 帖子分页列表
     */
    @GetMapping("/posts")
    public ResponseVo<PageResult<PostListDto>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        logger.info("获取帖子列表请求: page={}, pageSize={}, category={}, keyword={}", 
                page, pageSize, category, keyword);
        
        // 默认分类为"全部"
        if (category == null || category.isEmpty()) {
            category = "all";
        }
        
        PageResult<PostListDto> result = forumService.getPostList(category, keyword, page, pageSize);
        
        return ResponseVo.success(result);
    }
    
    /**
     * 获取所有帖子分类
     * 
     * @return 分类列表
     */
    @GetMapping("/categories")
    public ResponseVo<List<PostCategory>> getCategories() {
        logger.info("获取帖子分类列表请求");
        
        List<PostCategory> categories = forumService.getAllCategories();
        
        return ResponseVo.success(categories);
    }
    
    
    /**
     * 创建新帖子
     * 
     * @param requestBody 包含帖子信息的请求体
     * @return 创建结果，包含新帖子ID
     */
    @PostMapping("/posts/create")
    public ResponseVo<Map<String, Object>> createNewPost(@RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        String content = requestBody.get("content");
        String categoryCode = requestBody.get("category");
        
        logger.info("创建帖子请求: title={}, category={}", title, categoryCode);
        
        // 参数校验
        if (title == null || title.isEmpty()) {
            logger.warn("帖子标题不能为空");
            return ResponseVo.error(400, "帖子标题不能为空");
        }
        
        if (content == null || content.isEmpty()) {
            logger.warn("帖子内容不能为空");
            return ResponseVo.error(400, "帖子内容不能为空");
        }
        
        if (categoryCode == null || categoryCode.isEmpty()) {
            logger.warn("帖子分类不能为空");
            return ResponseVo.error(400, "帖子分类不能为空");
        }
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试创建帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 创建帖子对象
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(currentUser.getId());
            
        // 设置默认置顶状态为不置顶
        post.setIsTop(0);
        // 根据分类代码获取分类ID
        PostCategory category = forumService.getAllCategories().stream()
                .filter(c -> categoryCode.equals(c.getCode()))
                .findFirst()
                .orElse(null);
        
        if (category == null) {
            logger.warn("无效的分类代码: {}", categoryCode);
            return ResponseVo.error(400, "无效的分类代码");
        }
        
        post.setCategoryId(category.getId());
        
        // 保存帖子
        Long postId = forumService.createPost(post);
        
        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("postId", postId);
        
        return ResponseVo.success("帖子发布成功", responseData);
    }
    
    /**
     * 更新帖子
     * 
     * @param id 帖子ID
     * @param post 更新的帖子信息
     * @return 更新结果
     */
    @PutMapping("/posts/{id}")
    public ResponseVo<Boolean> updatePost(@PathVariable Long id, @RequestBody Post post) {
        logger.info("更新帖子请求: id={}", id);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试更新帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查帖子是否存在
        Post existingPost = forumService.getPostById(id);
        if (existingPost == null) {
            logger.warn("尝试更新不存在的帖子: id={}", id);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        // 检查是否是作者本人或管理员
        if (!existingPost.getUserId().equals(currentUser.getId()) && !"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("非作者用户尝试更新帖子: userId={}, postAuthorId={}", 
                    currentUser.getId(), existingPost.getUserId());
            return ResponseVo.error(403, "无权修改此帖子");
        }
        
        // 设置ID
        post.setId(id);
        
        boolean success = forumService.updatePost(post);
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "更新失败");
        }
    }
    
    /**
     * 删除帖子
     * 
     * @param id 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/posts/{id}")
    public ResponseVo<Boolean> deletePost(@PathVariable Long id) {
        logger.info("删除帖子请求: id={}", id);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试删除帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查帖子是否存在
        Post existingPost = forumService.getPostById(id);
        if (existingPost == null) {
            logger.warn("尝试删除不存在的帖子: id={}", id);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        // 检查是否是作者本人或管理员
        if (!existingPost.getUserId().equals(currentUser.getId()) && !"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("非作者用户尝试删除帖子: userId={}, postAuthorId={}", 
                    currentUser.getId(), existingPost.getUserId());
            return ResponseVo.error(403, "无权删除此帖子");
        }
        
        boolean success = forumService.deletePost(id);
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "删除失败");
        }
    }
    
    /**
     * 获取帖子回复列表
     * 
     * @param postId 帖子ID
     * @param orderBy 排序方式：likes-按点赞数排序，time-按时间排序，默认按点赞数
     * @return 带用户信息的回复列表
     */
    @GetMapping("/posts/{postId}/replies")
    public ResponseVo<List<PostReplyDto>> getPostReplies(
            @PathVariable Long postId,
            @RequestParam(required = false, defaultValue = "likes") String orderBy) {
        
        logger.info("获取帖子回复列表请求: postId={}, orderBy={}", postId, orderBy);
        
        // 检查帖子是否存在
        Post post = forumService.getPostById(postId);
        if (post == null) {
            logger.warn("尝试获取不存在帖子的回复: postId={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        // 获取带用户信息的回复列表
        List<PostReplyDto> replies = forumService.getPostRepliesWithUserInfo(postId, orderBy);
        
        return ResponseVo.success(replies);
    }
    
    /**
     * 添加帖子回复
     * 
     * @param postId 帖子ID
     * @param requestBody 回复信息
     * @return 添加结果
     */
    @PostMapping("/posts/{postId}/replies")
    public ResponseVo<Long> addReply(
            @PathVariable Long postId, 
            @RequestBody Map<String, Object> requestBody) {
        
        logger.info("添加帖子回复请求: postId={}", postId);
        
        // 获取回复内容
        String content = (String) requestBody.get("content");
        // 获取父回复ID
        Long parentId = requestBody.get("parentId") != null ? 
                Long.valueOf(requestBody.get("parentId").toString()) : null;
        // 获取回复目标ID
        Long replyToId = requestBody.get("replyToId") != null ? 
                Long.valueOf(requestBody.get("replyToId").toString()) : null;
        // 获取回复目标用户ID
        Long replyToUserId = requestBody.get("replyToUserId") != null ? 
                Long.valueOf(requestBody.get("replyToUserId").toString()) : null;
        
        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            return ResponseVo.error(400, "回复内容不能为空");
        }
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试添加回复");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查帖子是否存在
        Post post = forumService.getPostById(postId);
        if (post == null) {
            logger.warn("尝试回复不存在的帖子: postId={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        // 如果指定了父回复，但父回复不存在，则清空父回复ID
        Long updatedParentId = parentId;
        if (parentId != null) {
            // 直接通过ID查询回复，包含所有层级的回复
            PostReply parentReply = forumService.getReplyById(parentId);
            
            if (parentReply == null || !parentReply.getPostId().equals(postId)) {
                logger.warn("指定的父回复不存在或不属于该帖子: parentId={}, postId={}", parentId, postId);
                updatedParentId = null;
            }
        }
        
        // 如果指定了回复目标，但回复目标不存在，则清空回复目标ID
        Long updatedReplyToId = replyToId;
        Long updatedReplyToUserId = replyToUserId;
        if (replyToId != null) {
            // 直接通过ID查询回复，包含所有层级的回复
            PostReply replyTo = forumService.getReplyById(replyToId);
            
            if (replyTo == null || !replyTo.getPostId().equals(postId)) {
                logger.warn("指定的回复目标不存在或不属于该帖子: replyToId={}, postId={}", replyToId, postId);
                updatedReplyToId = null;
                updatedReplyToUserId = null;
            }
        }
        
        // 设置回复信息
        PostReply reply = new PostReply();
        reply.setPostId(postId);
        reply.setUserId(currentUser.getId());
        reply.setContent(content);
        reply.setParentId(updatedParentId);
        reply.setReplyToId(updatedReplyToId);
        reply.setReplyToUserId(updatedReplyToUserId);
        
        Long replyId = forumService.addReply(reply);
        
        return ResponseVo.success(replyId);
    }
    
    /**
     * 删除帖子回复
     * 
     * @param postId 帖子ID
     * @param replyId 回复ID
     * @return 删除结果
     */
    @DeleteMapping("/posts/{postId}/replies/{replyId}")
    public ResponseVo<Boolean> deleteReply(@PathVariable Long postId, @PathVariable Long replyId) {
        logger.info("删除帖子回复请求: postId={}, replyId={}", postId, replyId);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试删除回复");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查回复是否存在
        PostReply reply = forumService.getReplyById(replyId);
        if (reply == null) {
            logger.warn("尝试删除不存在的回复: replyId={}", replyId);
            return ResponseVo.error(404, "回复不存在");
        }
        
        // 检查回复是否属于指定帖子
        if (!reply.getPostId().equals(postId)) {
            logger.warn("回复不属于指定帖子: postId={}, replyId={}, actual postId={}", 
                    postId, replyId, reply.getPostId());
            return ResponseVo.error(400, "回复不属于指定帖子");
        }
        
        // 检查是否是回复作者本人或管理员
        if (!reply.getUserId().equals(currentUser.getId()) && !"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("非作者用户尝试删除回复: userId={}, replyAuthorId={}", 
                    currentUser.getId(), reply.getUserId());
            return ResponseVo.error(403, "无权删除此回复");
        }
        
        boolean success = forumService.deleteReply(replyId);
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "删除失败");
        }
    }
    

    
    /**
     * 获取符合接口文档的帖子详情
     * 
     * @param postId 帖子ID
     * @return 带有当前用户点赞状态的帖子详情
     */
    @GetMapping("/posts/detail")
    public ResponseVo<PostDetailDto> getPostDetailWithLiked(@RequestParam Long postId) {
        logger.info("获取帖子详情请求: postId={}", postId);
        
        // 增加浏览次数
        forumService.incrementPostViews(postId);
        
        // 获取带用户信息的帖子详情
        PostDetailDto postDetail = forumService.getPostDetail(postId);
        if (postDetail == null) {
            logger.warn("未找到指定帖子: id={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        return ResponseVo.success("请求成功", postDetail);
    }
    
    /**
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @return 点赞结果
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseVo<Boolean> likePost(@PathVariable Long postId) {
        logger.info("点赞帖子请求: postId={}", postId);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试点赞帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查帖子是否存在
        Post post = forumService.getPostById(postId);
        if (post == null) {
            logger.warn("尝试点赞不存在的帖子: postId={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        boolean success = forumService.likePost(postId, currentUser.getId());
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "点赞失败");
        }
    }
    
    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseVo<Boolean> unlikePost(@PathVariable Long postId) {
        logger.info("取消点赞帖子请求: postId={}", postId);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试取消点赞帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查帖子是否存在
        Post post = forumService.getPostById(postId);
        if (post == null) {
            logger.warn("尝试取消点赞不存在的帖子: postId={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        boolean success = forumService.unlikePost(postId, currentUser.getId());
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "取消点赞失败");
        }
    }
    
    /**
     * 点赞回复
     * 
     * @param postId 帖子ID
     * @param replyId 回复ID
     * @return 点赞结果
     */
    @PostMapping("/posts/{postId}/replies/{replyId}/like")
    public ResponseVo<Boolean> likeReply(@PathVariable Long postId, @PathVariable Long replyId) {
        logger.info("点赞回复请求: postId={}, replyId={}", postId, replyId);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试点赞回复");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 直接通过ID查询回复，包括所有层级的回复
        PostReply reply = forumService.getReplyById(replyId);
        
        if (reply == null) {
            logger.warn("尝试点赞不存在的回复: replyId={}", replyId);
            return ResponseVo.error(404, "回复不存在");
        }
        
        // 检查回复是否属于指定帖子
        if (!reply.getPostId().equals(postId)) {
            logger.warn("回复不属于指定帖子: postId={}, replyId={}, actual postId={}", 
                    postId, replyId, reply.getPostId());
            return ResponseVo.error(400, "回复不属于指定帖子");
        }
        
        boolean success = forumService.likeReply(replyId, currentUser.getId());
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "点赞失败");
        }
    }
    
    /**
     * 取消点赞回复
     * 
     * @param postId 帖子ID
     * @param replyId 回复ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/posts/{postId}/replies/{replyId}/like")
    public ResponseVo<Boolean> unlikeReply(@PathVariable Long postId, @PathVariable Long replyId) {
        logger.info("取消点赞回复请求: postId={}, replyId={}", postId, replyId);
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试取消点赞回复");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 直接通过ID查询回复，包括所有层级的回复
        PostReply reply = forumService.getReplyById(replyId);
        
        if (reply == null) {
            logger.warn("尝试取消点赞不存在的回复: replyId={}", replyId);
            return ResponseVo.error(404, "回复不存在");
        }
        
        // 检查回复是否属于指定帖子
        if (!reply.getPostId().equals(postId)) {
            logger.warn("回复不属于指定帖子: postId={}, replyId={}, actual postId={}", 
                    postId, replyId, reply.getPostId());
            return ResponseVo.error(400, "回复不属于指定帖子");
        }
        
        boolean success = forumService.unlikeReply(replyId, currentUser.getId());
        
        if (success) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.error(500, "取消点赞失败");
        }
    }
    /**
 * 设置帖子置顶状态（仅管理员可操作）
 * 
 * @param id 帖子ID
 * @param isTop 是否置顶
 * @return 设置结果
 */
@PutMapping("/posts/{id}/top")
public ResponseVo<Boolean> setPostTopStatus(
        @PathVariable Long id,
        @RequestParam Boolean isTop) {
    logger.info("设置帖子置顶状态请求: id={}, isTop={}", id, isTop);
    
    // 获取当前用户
    User currentUser = userService.getCurrentUser();
    if (currentUser == null) {
        logger.warn("未登录用户尝试设置帖子置顶状态");
        return ResponseVo.error(401, "请先登录");
    }
    
    // 检查是否为管理员
    if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
        logger.warn("非管理员用户尝试设置帖子置顶状态: userId={}", currentUser.getId());
        return ResponseVo.error(403, "无权操作，仅管理员可设置置顶状态");
    }
    
    // 检查帖子是否存在
    Post existingPost = forumService.getPostById(id);
    if (existingPost == null) {
        logger.warn("尝试设置不存在帖子的置顶状态: id={}", id);
        return ResponseVo.error(404, "帖子不存在");
    }
    
    boolean success = forumService.setPostTopStatus(id, isTop);
    
    if (success) {
        return ResponseVo.success(isTop ? "帖子已置顶" : "已取消置顶", true);
    } else {
        return ResponseVo.error(500, "操作失败");
    }
}

/**
 * 获取用户发帖列表
 * 
 * @param userId 用户ID
 * @param page 当前页码
 * @param pageSize 每页大小
 * @return 帖子分页列表
 */
@GetMapping("/posts/user")
public ResponseVo<PageResult<PostListDto>> getUserPosts(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize) {
    
    logger.info("获取用户发帖列表请求: userId={}, page={}, pageSize={}", 
            userId, page, pageSize);
    
    // 查询用户信息
    User user = userService.getUserById(userId);
    if (user == null) {
        logger.warn("查询不存在用户的发帖: userId={}", userId);
        return ResponseVo.error(404, "用户不存在");
    }
    
    PageResult<PostListDto> result = forumService.getUserPosts(userId, page, pageSize);
    
    return ResponseVo.success(result);
}
} 