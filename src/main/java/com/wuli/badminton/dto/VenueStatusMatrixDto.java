package com.wuli.badminton.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 场地状态矩阵查询DTO
 */
@Data
public class VenueStatusMatrixDto {
    
    /**
     * 查询日期 (YYYY-MM-DD)
     */
    @NotNull(message = "查询日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式错误，应为YYYY-MM-DD")
    private String date;
    
    /**
     * 场地ID（可选，为空则查询所有场地）
     */
    private Integer venueId;
} 