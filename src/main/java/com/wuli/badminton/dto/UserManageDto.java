package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 用户管理DTO
 * 用于后台管理系统显示用户信息
 */
@Data
public class UserManageDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatar;
    private String nickname;
    private String phone;
    private String gender;
    private String location;
    private String createTime;
    private String lastLoginAt;
    
    /**
     * 从User和UserDetail构建UserManageDto
     */
    public static UserManageDto build(com.wuli.badminton.pojo.User user, com.wuli.badminton.pojo.UserDetail userDetail) {
        UserManageDto dto = new UserManageDto();
        
        // 设置User中的字段
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatar(user.getAvatar());
        dto.setCreateTime(user.getCreateTime());
        
        // 如果userDetail不为空，设置UserDetail中的字段
        if (userDetail != null) {
            dto.setNickname(userDetail.getNickname());
            dto.setPhone(userDetail.getPhone());
            dto.setGender(userDetail.getGender());
            dto.setLocation(userDetail.getLocation());
            dto.setLastLoginAt(userDetail.getLastLoginAt());
        }
        
        return dto;
    }
} 