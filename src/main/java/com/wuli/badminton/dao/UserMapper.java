package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findByEmail(@Param("email") String email);
    User findById(@Param("id") Long id);
    void insert(User user);
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
    
    /**
     * 重置用户密码
     * @param id 用户ID
     * @param password 新密码（已加密）
     */
    void resetPassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 分页查询用户列表
     * @param keyword 搜索关键词（用户名）
     * @param role 角色过滤
     * @param offset 偏移量
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> findUsersWithPagination(@Param("keyword") String keyword, 
                                      @Param("role") String role,
                                      @Param("offset") Integer offset, 
                                      @Param("size") Integer size);
    
    /**
     * 统计用户总数
     * @param keyword 搜索关键词（用户名）
     * @param role 角色过滤
     * @return 用户总数
     */
    Long countUsers(@Param("keyword") String keyword, @Param("role") String role);
} 