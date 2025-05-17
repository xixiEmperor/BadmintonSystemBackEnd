package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.SpecificationOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpecificationOptionMapper {
    
    /**
     * 根据商品ID查询所有规格选项
     * @param productId 商品ID
     * @return 规格选项列表
     */
    List<SpecificationOption> findByProductId(@Param("productId") Integer productId);
    
    /**
     * 根据商品ID和规格类型查询
     * @param productId 商品ID
     * @param specKey 规格类型
     * @return 规格选项
     */
    SpecificationOption findByProductIdAndSpecKey(@Param("productId") Integer productId, 
                                                @Param("specKey") String specKey);
    
    /**
     * 插入规格选项
     * @param option 规格选项
     * @return 影响行数
     */
    int insert(SpecificationOption option);
    
    /**
     * 更新规格选项
     * @param option 规格选项
     * @return 影响行数
     */
    int update(SpecificationOption option);
    
    /**
     * 删除规格选项
     * @param id 选项ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据商品ID删除所有规格选项
     * @param productId 商品ID
     * @return 影响行数
     */
    int deleteByProductId(@Param("productId") Integer productId);
} 