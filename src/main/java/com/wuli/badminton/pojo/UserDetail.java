package com.wuli.badminton.pojo;

import lombok.Data;

@Data
public class UserDetail {
    private Long id;
    private Long userId;        // 关联到User表的id
    private String nickname;    // 昵称
    private String phone;       // 手机号
    private String bio;         // 个人简介
    private String gender;      // 性别
    private String birthday;    // 生日
    private String location;    // 位置
    private String lastLoginAt; // 最后登录时间
    private String createTime;  // 创建时间
    private String updateTime;  // 更新时间
} 