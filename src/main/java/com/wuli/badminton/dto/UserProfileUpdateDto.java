package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 用户个人资料更新请求 DTO
 */
@Data
public class UserProfileUpdateDto {
    private String nickname;
    private String phone;
    private String bio;
    private String gender;
    private String birthday;
    private String location;
} 