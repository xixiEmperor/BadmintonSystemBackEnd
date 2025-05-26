package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公告通知请求DTO
 */
@Data
public class NoticeDto {
    
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;
    
    /**
     * 通知类型：1-普通通知，2-重要通知
     */
    @NotNull(message = "通知类型不能为空")
    private Integer type;
} 