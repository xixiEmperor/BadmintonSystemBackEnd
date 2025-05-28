package com.wuli.badminton.controller;

import com.wuli.badminton.dto.AvatarResponseDto;
import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.UserManageDto;
import com.wuli.badminton.dto.UserProfileDto;
import com.wuli.badminton.dto.UserProfileUpdateDto;
import com.wuli.badminton.dto.UserSearchDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.util.FileUtil;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    

    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUser() {
        User user = userService.getCurrentUser();
        user.setPassword(null); // 不返回密码
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminOnly() {
        return ResponseEntity.ok("Welcome to admin page!");
    }
    
    @GetMapping("/profile")
    public ResponseVo<?> getUserProfile() {
        logger.info("获取当前用户个人资料");
        try {
            UserProfileDto profile = userService.getCurrentUserProfile();
            if (profile == null) {
                logger.warn("获取用户资料失败：用户不存在");
                return ResponseVo.error(ResponseEnum.NEED_LOGIN);
            }
            logger.info("获取用户资料成功");
            return ResponseVo.success("操作成功", profile);
        } catch (Exception e) {
            logger.error("获取用户资料出错: {}", e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
    
    @PutMapping("/profile")
    public ResponseVo<?> updateUserProfile(@RequestBody UserProfileUpdateDto updateDto) {
        logger.info("更新当前用户个人资料");
        try {
            UserProfileDto updatedProfile = userService.updateCurrentUserProfile(updateDto);
            if (updatedProfile == null) {
                logger.warn("更新用户资料失败：用户不存在");
                return ResponseVo.error(ResponseEnum.NEED_LOGIN);
            }
            logger.info("更新用户资料成功");
            return ResponseVo.success("更新成功", updatedProfile);
        } catch (Exception e) {
            logger.error("更新用户资料出错: {}", e.getMessage());
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
    
    /**
     * 上传用户头像
     * @param file 头像文件
     * @return 响应结果
     */
    @PostMapping("/avatar")
    public ResponseVo<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        logger.info("上传用户头像，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            logger.warn("上传的文件为空");
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "请选择要上传的文件");
        }
        
        try {
            // 检查文件类型
            if (!FileUtil.isAllowedImageType(file.getContentType())) {
                logger.warn("文件类型不支持: {}", file.getContentType());
                return ResponseVo.error(ResponseEnum.FILE_TYPE_NOT_ALLOWED);
            }
            
            // 检查文件大小
            if (FileUtil.isFileSizeExceeded(file.getSize())) {
                logger.warn("文件大小超过限制: {} bytes", file.getSize());
                return ResponseVo.error(ResponseEnum.FILE_SIZE_EXCEEDED);
            }
            
            // 更新用户头像
            AvatarResponseDto responseDto = userService.updateCurrentUserAvatar(file);
            logger.info("头像上传成功: {}", responseDto.getAvatarUrl());
            
            return ResponseVo.success("上传成功", responseDto);
        } catch (Exception e) {
            logger.error("头像上传失败: {}", e.getMessage(), e);
            return ResponseVo.error(ResponseEnum.FILE_UPLOAD_ERROR);
        }
    }
    
    // ==================== 管理员功能 ====================
    
    /**
     * 管理员分页查询用户列表
     * @param searchDto 搜索条件
     * @return 用户列表
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<?> adminGetUserList(UserSearchDto searchDto) {
        logger.info("管理员查询用户列表，搜索条件: {}", searchDto);
        
        try {
            PageResult<UserManageDto> result = userService.getUsersWithPagination(searchDto);
            logger.info("查询用户列表成功，总数: {}", result.getTotal());
            return ResponseVo.success("查询成功", result);
        } catch (Exception e) {
            logger.error("查询用户列表失败: {}", e.getMessage(), e);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }

    /**
     * 管理员重置用户密码
     * @param userId 用户ID
     * @return 重置结果
     */
    @PutMapping("/admin/users/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<?> adminResetUserPassword(@PathVariable Long userId) {
        logger.info("管理员重置用户密码，用户ID: {}", userId);
        
        try {
            // 检查用户是否存在
            if (userService.getUserById(userId) == null) {
                logger.warn("用户不存在，ID: {}", userId);
                return ResponseVo.error(ResponseEnum.USER_NOT_EXIST);
            }
            
            // 重置密码
            boolean success = userService.resetUserPassword(userId);
            
            if (success) {
                logger.info("用户密码重置成功，用户ID: {}", userId);
                return ResponseVo.success("密码重置成功，新密码为：123456");
            } else {
                logger.error("用户密码重置失败，用户ID: {}", userId);
                return ResponseVo.error(ResponseEnum.PASSWORD_RESET_FAILED);
            }
        } catch (Exception e) {
            logger.error("重置用户密码异常，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }

    /**
     * 管理员获取用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<?> adminGetUserDetail(@PathVariable Long userId) {
        logger.info("管理员查询用户详情，用户ID: {}", userId);
        
        try {
            // 获取用户基本信息
            User user = userService.getUserById(userId);
            if (user == null) {
                logger.warn("用户不存在，ID: {}", userId);
                return ResponseVo.error(ResponseEnum.USER_NOT_EXIST);
            }
            
            // 获取用户详细资料
            UserProfileDto userProfile = userService.getUserProfile(userId);
            
            logger.info("查询用户详情成功，用户ID: {}, 用户名: {}", userId, user.getUsername());
            return ResponseVo.success("查询成功", userProfile);
        } catch (Exception e) {
            logger.error("查询用户详情失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
    }
} 