package com.wuli.badminton.service;

import com.wuli.badminton.dto.UserProfileUpdateDto;
import com.wuli.badminton.pojo.UserDetail;

public interface UserDetailService {
    UserDetail findByUserId(Long userId);
    void save(UserDetail userDetail);
    void update(UserDetail userDetail);
    UserDetail createDefaultDetail(Long userId);
    
    /**
     * 更新用户详情
     * @param userId 用户ID
     * @param updateDto 更新数据
     * @return 更新后的用户详情
     */
    UserDetail updateUserDetail(Long userId, UserProfileUpdateDto updateDto);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
} 