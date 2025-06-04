package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.MallProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface MallProductMapper {
    
    /**
     * 查询商品列表
     * @param params 查询参数
     * @return 商品列表
     */
    List<MallProduct> findList(Map<String, Object> params);
    
    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品
     */
    MallProduct findById(@Param("id") Integer id);
    
    /**
     * 添加商品
     * @param product 商品信息
     * @return 影响行数
     */
    int insert(MallProduct product);
    
    /**
     * 更新商品
     * @param product 商品信息
     * @return 影响行数
     */
    int update(MallProduct product);
    
    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 更新库存
     * @param id 商品ID
     * @param stock 库存
     * @return 影响行数
     */
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
    
    /**
     * 增加销量
     * @param id 商品ID
     * @param count 销量增加数量
     * @return 影响行数
     */
    int increaseSales(@Param("id") Integer id, @Param("count") Integer count);
    
    /**
     * 获取热门商品（按销量排序）
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<MallProduct> getHotProducts(@Param("limit") Integer limit);
    
    /**
     * 根据商品ID列表批量查询商品基本信息
     * @param productIds 商品ID列表
     * @return 商品列表
     */
    List<MallProduct> getProductsByIds(@Param("productIds") List<Integer> productIds);
} 