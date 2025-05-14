package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findByEmail(@Param("email") String email);
    User findById(@Param("id") Long id);
    void insert(User user);
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
} 