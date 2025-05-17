package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.MallCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MallCategoryMapper {
    
    /**
     * 查询所有有效分类
     */
    List<MallCategory> findAllValid();
    
    /**
     * 根据ID查询分类
     */
    MallCategory findById(@Param("id") Integer id);
    
    /**
     * 添加分类
     */
    int insert(MallCategory category);
    
    /**
     * 更新分类
     */
    int update(MallCategory category);
    
    /**
     * 删除分类（逻辑删除，将status改为2）
     */
    int delete(@Param("id") Integer id);
} 