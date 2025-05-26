package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 场地Mapper接口
 */
@Mapper
public interface VenueMapper {
    
    /**
     * 插入场地
     */
    int insert(Venue venue);
    
    /**
     * 批量插入场地
     */
    int insertBatch(@Param("list") List<Venue> venues);
    
    /**
     * 根据ID查询场地
     */
    Venue selectById(@Param("id") Integer id);
    
    /**
     * 查询所有场地
     */
    List<Venue> selectAll();
    
    /**
     * 根据状态查询场地
     */
    List<Venue> selectByStatus(@Param("status") Integer status);
    
    /**
     * 更新场地信息
     */
    int updateById(Venue venue);
    
    /**
     * 更新场地状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 删除场地
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 批量插入场地
     */
    int batchInsert(@Param("venues") List<Venue> venues);
    
    /**
     * 统计场地数量
     */
    int countAll();
    
    /**
     * 根据状态统计场地数量
     */
    int countByStatus(@Param("status") Integer status);
} 