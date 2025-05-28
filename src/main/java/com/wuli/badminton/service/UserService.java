package com.wuli.badminton.service;

import com.wuli.badminton.dto.AvatarResponseDto;
import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.UserManageDto;
import com.wuli.badminton.dto.UserProfileDto;
import com.wuli.badminton.dto.UserProfileUpdateDto;
import com.wuli.badminton.dto.UserSearchDto;
import com.wuli.badminton.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    User findByEmail(String email);
    void save(User user);
    User getCurrentUser();
    
    UserProfileDto getUserProfile(Long userId);
    UserProfileDto getCurrentUserProfile();
    
    /**
     * 更新当前登录用户的个人资料
     * @param updateDto 更新数据
     * @return 更新后的用户资料
     */
    UserProfileDto updateCurrentUserProfile(UserProfileUpdateDto updateDto);
    
    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     * @throws IOException 文件处理异常
     */
    AvatarResponseDto updateAvatar(Long userId, MultipartFile file) throws IOException;
    
    /**
     * 更新当前登录用户的头像
     * @param file 头像文件
     * @return 头像URL
     * @throws IOException 文件处理异常
     */
    AvatarResponseDto updateCurrentUserAvatar(MultipartFile file) throws IOException;
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User getUserById(Long userId);
    
    /**
     * 重置用户密码为默认密码123456
     * @param userId 用户ID
     * @return 是否重置成功
     */
    boolean resetUserPassword(Long userId);
    
    /**
     * 分页查询用户列表（管理员功能）
     * @param searchDto 搜索条件
     * @return 分页结果
     */
    PageResult<UserManageDto> getUsersWithPagination(UserSearchDto searchDto);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
    
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
} 