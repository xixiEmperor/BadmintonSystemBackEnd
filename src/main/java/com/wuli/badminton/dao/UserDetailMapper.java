package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.UserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDetailMapper {
    UserDetail findByUserId(@Param("userId") Long userId);
    void insert(UserDetail userDetail);
    void update(UserDetail userDetail);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(@Param("userId") Long userId);
} 