package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.ProductSpecification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductSpecificationMapper {
    
    /**
     * 根据商品ID查询所有规格
     * @param productId 商品ID
     * @return 规格列表
     */
    List<ProductSpecification> findByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据规格ID查询规格
     * @param id 规格ID
     * @return 规格信息
     */
    ProductSpecification findById(@Param("id") Integer id);
    
    /**
     * 根据商品ID和规格查询
     * @param productId 商品ID
     * @param specifications 规格条件，如{color=红色, size=S}
     * @return 规格信息
     */
    ProductSpecification findByProductIdAndSpecifications(@Param("productId") Integer productId, 
                                                         @Param("specifications") Map<String, String> specifications);
    
    /**
     * 插入规格信息
     * @param specification 规格信息
     * @return 影响行数
     */
    int insert(ProductSpecification specification);
    
    /**
     * 更新规格信息
     * @param specification 规格信息
     * @return 影响行数
     */
    int update(ProductSpecification specification);
    
    /**
     * 删除规格
     * @param id 规格ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据商品ID删除所有规格
     * @param productId 商品ID
     * @return 影响行数
     */
    int deleteByProductId(@Param("productId") Integer productId);
    
    /**
     * 更新规格库存
     * @param id 规格ID
     * @param stock 新库存
     * @return 影响行数
     */
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
    
    /**
     * 增加规格销量
     * @param id 规格ID
     * @param count 增加数量
     * @return 影响行数
     */
    int increaseSales(@Param("id") Integer id, @Param("count") Integer count);
} 