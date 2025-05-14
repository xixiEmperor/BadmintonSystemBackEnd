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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.avatar.url}")
    private String avatarBaseUrl;

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
        
        // 生成唯一文件名
        String filename = FileUtil.generateUniqueFilename(file.getOriginalFilename());
        logger.info("生成的唯一文件名: {}", filename);
        
        try {
            // 确保上传目录存在
            logger.info("当前配置的上传路径: {}", uploadPath);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                logger.info("上传目录 {} 不存在，尝试创建", uploadPath);
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    logger.error("创建上传目录失败: {}", uploadPath);
                    throw new IOException("无法创建上传目录: " + uploadPath);
                }
                logger.info("成功创建上传目录: {}", uploadPath);
            } else if (!uploadDir.isDirectory()) {
                logger.error("上传路径 {} 存在但不是一个目录", uploadPath);
                throw new IOException("上传路径不是一个有效的目录: " + uploadPath);
            } else if (!uploadDir.canWrite()) {
                logger.error("上传目录 {} 不可写入", uploadPath);
                throw new IOException("上传目录没有写入权限: " + uploadPath);
            }
            
            // 打印实际使用的文件路径
            File targetFile = new File(uploadDir, filename);
            logger.info("即将保存文件到: {}", targetFile.getAbsolutePath());
            
            // 保存文件
            String savedFilename = FileUtil.saveFile(file, uploadPath, filename);
            logger.info("文件保存成功，返回文件名: {}", savedFilename);
            
            // 确认文件是否真的保存成功
            File savedFile = new File(uploadDir, savedFilename);
            if (!savedFile.exists()) {
                logger.error("文件应该已保存，但在目录中未找到: {}", savedFile.getAbsolutePath());
                throw new IOException("文件保存后无法确认: " + savedFile.getAbsolutePath());
            }
            logger.info("确认文件已保存到磁盘: {}", savedFile.getAbsolutePath());
            
            // 构建完整的URL路径
            String avatarUrl = avatarBaseUrl + savedFilename;
            logger.info("构建的头像URL: {}", avatarUrl);
            
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
} 