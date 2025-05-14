package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.UserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDetailMapper {
    UserDetail findByUserId(@Param("userId") Long userId);
    void insert(UserDetail userDetail);
    void update(UserDetail userDetail);
} 