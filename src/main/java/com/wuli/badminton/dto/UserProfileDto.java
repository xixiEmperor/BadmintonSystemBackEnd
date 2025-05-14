package com.wuli.badminton.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String bio;
    private String role;
    private String gender;
    private String birthday;
    private String location;
    private String avatar;
    private String createdAt;
    private String lastLoginAt;
    
    // 可以添加一个工厂方法来从 User 和 UserDetail 对象创建 UserProfileDto
    public static UserProfileDto build(com.wuli.badminton.pojo.User user, com.wuli.badminton.pojo.UserDetail userDetail) {
        UserProfileDto dto = new UserProfileDto();
        
        // 设置 User 中的字段
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreateTime());
        
        // 如果 userDetail 不为空，设置 UserDetail 中的字段
        if (userDetail != null) {
            // 如果昵称为空，则使用用户名
            String nickname = userDetail.getNickname();
            dto.setNickname((nickname != null && !nickname.trim().isEmpty()) ? 
                           nickname : user.getUsername());
            dto.setPhone(userDetail.getPhone());
            dto.setBio(userDetail.getBio());
            dto.setGender(userDetail.getGender());
            dto.setBirthday(userDetail.getBirthday());
            dto.setLocation(userDetail.getLocation());
            dto.setLastLoginAt(userDetail.getLastLoginAt());
        } else {
            // 如果用户详情不存在，也设置昵称为用户名
            dto.setNickname(user.getUsername());
        }
        
        return dto;
    }
} 