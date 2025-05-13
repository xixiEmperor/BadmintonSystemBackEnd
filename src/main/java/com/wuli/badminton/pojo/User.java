package com.wuli.badminton.pojo;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;     //角色
    private String avatar;  //头像
    private String email;
    private String createTime;
    private String updateTime;
} 