package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.PostCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子分类数据访问接口
 */
@Mapper
public interface PostCategoryMapper {
    /**
     * 插入分类
     * 
     * @param category 分类对象
     * @return 影响的行数
     */
    int insert(PostCategory category);
    
    /**
     * 根据ID查询分类
     * 
     * @param id 分类ID
     * @return 分类对象
     */
    PostCategory findById(Long id);
    
    /**
     * 根据代码查询分类
     * 
     * @param code 分类代码
     * @return 分类对象
     */
    PostCategory findByCode(String code);
    
    /**
     * 查询所有分类，按排序字段升序
     * 
     * @return 分类列表
     */
    List<PostCategory> findAll();
    
    /**
     * 更新分类
     * 
     * @param category 分类对象
     * @return 影响的行数
     */
    int update(PostCategory category);
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 影响的行数
     */
    int deleteById(Long id);
} 