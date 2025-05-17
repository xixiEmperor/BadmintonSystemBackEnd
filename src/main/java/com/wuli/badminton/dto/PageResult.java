package com.wuli.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果DTO
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private int pageNum;    // 当前页码
    private int pageSize;   // 每页数量
    private int size;       // 当前页实际数量
    private long total;     // 总数量
    private List<T> list;   // 数据列表
    
    /**
     * 构建分页结果
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param total 总数量
     * @param list 数据列表
     * @return 分页结果
     */
    public static <T> PageResult<T> build(int pageNum, int pageSize, long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, list.size(), total, list);
    }
} 