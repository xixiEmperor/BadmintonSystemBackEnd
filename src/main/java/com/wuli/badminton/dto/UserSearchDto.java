package com.wuli.badminton.dto;

import lombok.Data;

/**
 * 用户搜索请求DTO
 */
@Data
public class UserSearchDto {
    /**
     * 搜索关键词（用户名）
     */
    private String keyword;
    
    /**
     * 页码，从1开始
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 角色过滤
     */
    private String role;
} 