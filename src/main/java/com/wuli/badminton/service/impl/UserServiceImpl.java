package com.wuli.badminton.service.impl;

import com.wuli.badminton.dto.AvatarResponseDto;
import com.wuli.badminton.dto.UserProfileDto;
import com.wuli.badminton.dto.UserProfileUpdateDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.exception.BusinessException;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.pojo.UserDetail;
import com.wuli.badminton.dao.UserMapper;
import com.wuli.badminton.service.UserDetailService;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.util.FileUtil;
import com.wuli.badminton.util.UploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDetailService userDetailService;
    
    @Autowired
    private UploadUtil uploadUtil;

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        logger.info("查找用户: {}, 结果: {}", username, user != null ? "找到" : "未找到");
        return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = userMapper.findByEmail(email);
        logger.info("根据邮箱查找用户: {}, 结果: {}", email, user != null ? "找到" : "未找到");
        return user;
    }

    @Override
    @Transactional
    public void save(User user) {
        logger.info("保存用户: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        
        // 创建对应的用户详情
        User savedUser = findByUsername(user.getUsername());
        if (savedUser != null) {
            userDetailService.createDefaultDetail(savedUser.getId());
        }
        
        logger.info("用户保存成功");
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }
    
    @Override
    public UserProfileDto getUserProfile(Long userId) {
        logger.info("获取用户 ID {} 的资料", userId);
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("用户 ID {} 不存在", userId);
            return null;
        }
        
        UserDetail userDetail = userDetailService.findByUserId(userId);
        if (userDetail == null) {
            logger.info("用户 ID {} 的详细资料不存在，创建默认资料", userId);
            userDetail = userDetailService.createDefaultDetail(userId);
        }
        
        return UserProfileDto.build(user, userDetail);
    }
    
    @Override
    public UserProfileDto getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("当前用户不存在");
            return null;
        }
        return getUserProfile(currentUser.getId());
    }
    
    @Override
    @Transactional
    public UserProfileDto updateCurrentUserProfile(UserProfileUpdateDto updateDto) {
        logger.info("更新当前用户的个人资料");
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("当前用户不存在");
            return null;
        }
        
        // 更新用户详情
        UserDetail updatedDetail = userDetailService.updateUserDetail(currentUser.getId(), updateDto);
        
        // 返回更新后的完整用户资料
        return UserProfileDto.build(currentUser, updatedDetail);
    }
    
    @Override
    @Transactional
    public AvatarResponseDto updateAvatar(Long userId, MultipartFile file) throws IOException {
        logger.info("更新用户 ID {} 的头像", userId);
        
        // 检查文件类型
        if (!FileUtil.isAllowedImageType(file.getContentType())) {
            logger.warn("文件类型不允许: {}", file.getContentType());
            throw new BusinessException(ResponseEnum.FILE_TYPE_NOT_ALLOWED);
        }
        
        // 检查文件大小
        if (FileUtil.isFileSizeExceeded(file.getSize())) {
            logger.warn("文件大小超过限制: {} bytes", file.getSize());
            throw new BusinessException(ResponseEnum.FILE_SIZE_EXCEEDED);
        }
        
        // 获取用户
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("用户 ID {} 不存在", userId);
            throw new BusinessException(ResponseEnum.NEED_LOGIN);
        }
        
        try {
            // 使用阿里云OSS上传头像
            logger.info("开始上传头像到阿里云OSS");
            String avatarUrl = uploadUtil.upload(file);
            logger.info("头像上传成功，OSS访问地址: {}", avatarUrl);
            
            // 更新用户头像URL
            user.setAvatar(avatarUrl);
            userMapper.updateAvatar(userId, avatarUrl);
            logger.info("数据库头像URL更新成功");
            
            return new AvatarResponseDto(avatarUrl);
        } catch (Exception e) {
            logger.error("头像上传失败: {}", e.getMessage(), e);
            throw new IOException("头像上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public AvatarResponseDto updateCurrentUserAvatar(MultipartFile file) throws IOException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("当前用户不存在");
            throw new BusinessException(ResponseEnum.NEED_LOGIN);
        }
        
        return updateAvatar(currentUser.getId(), file);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("加载用户详情: {}", username);
        User user = findByUsername(username);
        if (user == null) {
            logger.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        logger.info("用户详情加载成功: {}", username);
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    @Override
    public User getUserById(Long userId) {
        logger.info("根据ID查询用户: {}", userId);
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.warn("ID为{}的用户不存在", userId);
        }
        return user;
    }
} 