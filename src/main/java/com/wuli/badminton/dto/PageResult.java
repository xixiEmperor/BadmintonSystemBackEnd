package com.wuli.badminton.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页结果数据传输对象
 * @param <T> 分页数据项类型
 */
@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 当前页的数据列表
     */
    private List<T> list;
    
    /**
     * 构造函数
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param total 总记录数
     * @param list 当前页数据
     */
    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        
        // 计算总页数
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
} 