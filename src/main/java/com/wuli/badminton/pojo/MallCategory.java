package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class MallCategory {
    private Integer id;        // 分类ID
    private String name;       // 分类名称
    private Integer status;    // 分类状态：1-正常，2-已废弃
    private Integer sortOrder; // 排序编号
    private Date createTime;   // 创建时间
    private Date updateTime;   // 更新时间
} 