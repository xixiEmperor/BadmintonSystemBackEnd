package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.SpecialDateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 特殊日期配置Mapper接口
 */
@Mapper
public interface SpecialDateConfigMapper {
    
    /**
     * 插入特殊日期配置
     */
    int insert(SpecialDateConfig config);
    
    /**
     * 根据ID查询
     */
    SpecialDateConfig selectById(@Param("id") Long id);
    
    /**
     * 查询所有特殊日期配置
     */
    List<SpecialDateConfig> selectAll();
    
    /**
     * 根据日期查询特殊配置
     */
    List<SpecialDateConfig> selectByDate(@Param("specialDate") Date specialDate);
    
    /**
     * 根据日期范围查询特殊配置
     */
    List<SpecialDateConfig> selectByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
    
    /**
     * 查询启用的特殊日期配置
     */
    List<SpecialDateConfig> selectEnabledConfigs();
    
    /**
     * 更新特殊日期配置
     */
    int updateById(SpecialDateConfig config);
    
    /**
     * 删除特殊日期配置
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 启用/禁用特殊日期配置
     */
    int updateEnabledStatus(@Param("id") Long id, @Param("enabled") Integer enabled);
} 