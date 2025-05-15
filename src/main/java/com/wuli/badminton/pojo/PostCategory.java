package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

/**
 * 帖子分类实体类
 */
@Data
public class PostCategory {
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类代码，用于前端匹配
     */
    private String code;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 