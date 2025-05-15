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
     * 获取热门帖子
     * 
     * @param limit 数量限制
     * @return 热门帖子列表
     */
    @GetMapping("/hot-posts")
    public ResponseVo<List<PostListDto>> getHotPosts(
            @RequestParam(defaultValue = "5") Integer limit) {
        
        logger.info("获取热门帖子请求: limit={}", limit);
        
        List<PostListDto> hotPosts = forumService.getHotPosts(limit);
        
        return ResponseVo.success(hotPosts);
    }
    
    /**
     * 获取帖子详情
     * 
     * @param id 帖子ID
     * @return 带用户信息的帖子详情
     */
    @GetMapping("/posts/{id}")
    public ResponseVo<PostDetailDto> getPostDetail(@PathVariable Long id) {
        logger.info("获取帖子详情请求: id={}", id);
        
        // 增加浏览次数
        forumService.incrementPostViews(id);
        
        // 获取带用户信息的帖子详情
        PostDetailDto postDetail = forumService.getPostDetailWithUserInfo(id);
        if (postDetail == null) {
            logger.warn("未找到指定帖子: id={}", id);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        return ResponseVo.success(postDetail);
    }
    
    /**
     * 创建新帖子（符合接口文档规范）
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
     * 创建帖子（原有方法）
     * 
     * @param post 帖子信息
     * @return 创建结果
     * @deprecated 请使用 {@link #createNewPost(Map)} 代替
     */
    @PostMapping("/posts")
    public ResponseVo<Long> createPost(@RequestBody Post post) {
        logger.info("创建帖子请求: title={}", post.getTitle());
        
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试创建帖子");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 设置作者ID
        post.setUserId(currentUser.getId());
        
        Long postId = forumService.createPost(post);
        
        return ResponseVo.success(postId);
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
     * @return 带用户信息的回复列表
     */
    @GetMapping("/posts/{postId}/replies")
    public ResponseVo<List<PostReplyDto>> getPostReplies(@PathVariable Long postId) {
        logger.info("获取帖子回复列表请求: postId={}", postId);
        
        // 检查帖子是否存在
        Post post = forumService.getPostById(postId);
        if (post == null) {
            logger.warn("尝试获取不存在帖子的回复: postId={}", postId);
            return ResponseVo.error(404, "帖子不存在");
        }
        
        // 获取带用户信息的回复列表
        List<PostReplyDto> replies = forumService.getPostRepliesWithUserInfo(postId);
        
        return ResponseVo.success(replies);
    }
    
    /**
     * 添加帖子回复
     * 
     * @param postId 帖子ID
     * @param reply 回复信息
     * @return 添加结果
     */
    @PostMapping("/posts/{postId}/replies")
    public ResponseVo<Long> addReply(@PathVariable Long postId, @RequestBody PostReply reply) {
        logger.info("添加帖子回复请求: postId={}", postId);
        
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
        
        // 设置回复信息
        reply.setPostId(postId);
        reply.setUserId(currentUser.getId());
        
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
        PostReply reply = forumService.getPostReplies(postId).stream()
                .filter(r -> r.getId().equals(replyId))
                .findFirst()
                .orElse(null);
        
        if (reply == null) {
            logger.warn("尝试删除不存在的回复: replyId={}", replyId);
            return ResponseVo.error(404, "回复不存在");
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
     * 获取帖子列表 (POST方式，用于接收JSON格式的请求)
     * 
     * @param requestBody 包含查询参数的请求体
     * @return 帖子分页列表
     */
    @PostMapping("/posts")
    public ResponseVo<PageResult<PostListDto>> getPostListByPost(@RequestBody Map<String, Object> requestBody) {
        
        // 提取请求参数
        Integer page = 1;
        Integer pageSize = 10;
        String category = null;
        String keyword = null;
        
        if (requestBody.containsKey("page")) {
            page = Integer.parseInt(requestBody.get("page").toString());
        }
        
        if (requestBody.containsKey("pageSize")) {
            pageSize = Integer.parseInt(requestBody.get("pageSize").toString());
        }
        
        if (requestBody.containsKey("category")) {
            category = requestBody.get("category").toString();
        }
        
        if (requestBody.containsKey("keyword")) {
            keyword = requestBody.get("keyword").toString();
        }
        
        logger.info("POST方式获取帖子列表请求: page={}, pageSize={}, category={}, keyword={}", 
                page, pageSize, category, keyword);
        
        // 默认分类为"全部"
        if (category == null || category.isEmpty()) {
            category = "all";
        }
        
        PageResult<PostListDto> result = forumService.getPostList(category, keyword, page, pageSize);
        
        return ResponseVo.success(result);
    }
} 