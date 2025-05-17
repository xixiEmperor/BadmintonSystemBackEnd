package com.wuli.badminton.pojo;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 商品规格选项实体类
 */
@Data
public class SpecificationOption {
    private Integer id;        // 选项ID
    private Integer productId; // 商品ID
    private String specKey;    // 规格类型，如"color"、"size"
    private List<String> specValues; // 可选值列表，如["红色","蓝色","黑色"]
    private Date createTime;   // 创建时间
    private Date updateTime;   // 更新时间
} 