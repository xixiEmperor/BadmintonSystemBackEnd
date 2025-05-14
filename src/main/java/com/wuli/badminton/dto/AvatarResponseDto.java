package com.wuli.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 头像上传响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponseDto {
    private String avatarUrl;
} 