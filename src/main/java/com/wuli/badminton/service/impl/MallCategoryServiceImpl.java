package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.MallCategoryMapper;
import com.wuli.badminton.pojo.MallCategory;
import com.wuli.badminton.service.MallCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallCategoryServiceImpl implements MallCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(MallCategoryServiceImpl.class);

    @Autowired
    private MallCategoryMapper categoryMapper;

    @Override
    public List<MallCategory> getAllValidCategories() {
        logger.info("获取所有有效商品分类");
        return categoryMapper.findAllValid();
    }

    @Override
    public MallCategory getCategoryById(Integer id) {
        logger.info("根据ID获取商品分类: id={}", id);
        return categoryMapper.findById(id);
    }

    @Override
    public boolean addCategory(MallCategory category) {
        logger.info("添加商品分类: {}", category.getName());
        // 确保状态为正常
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        return categoryMapper.insert(category) > 0;
    }

    @Override
    public boolean updateCategory(MallCategory category) {
        logger.info("更新商品分类: id={}, name={}", category.getId(), category.getName());
        return categoryMapper.update(category) > 0;
    }

    @Override
    public boolean deleteCategory(Integer id) {
        logger.info("删除商品分类: id={}", id);
        return categoryMapper.delete(id) > 0;
    }
} 