package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.MallOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 商城订单项Mapper接口
 */
@Mapper
public interface MallOrderItemMapper {
    
    /**
     * 批量插入订单项
     * @param orderItems 订单项列表
     * @return 影响行数
     */
    int batchInsert(@Param("orderItems") List<MallOrderItem> orderItems);
    
    /**
     * 根据订单号查询订单项
     * @param orderNo 订单号
     * @return 订单项列表
     */
    List<MallOrderItem> selectByOrderNo(Long orderNo);
    
    /**
     * 根据订单号删除订单项
     * @param orderNo 订单号
     * @return 影响行数
     */
    int deleteByOrderNo(Long orderNo);
} 