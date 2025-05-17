package com.wuli.badminton.service;

import com.wuli.badminton.pojo.MallCategory;
import java.util.List;

public interface MallCategoryService {
    
    /**
     * 获取所有有效分类
     * @return 分类列表
     */
    List<MallCategory> getAllValidCategories();
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类信息
     */
    MallCategory getCategoryById(Integer id);
    
    /**
     * 添加分类
     * @param category 分类信息
     * @return 添加成功返回true，否则返回false
     */
    boolean addCategory(MallCategory category);
    
    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新成功返回true，否则返回false
     */
    boolean updateCategory(MallCategory category);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteCategory(Integer id);
} 