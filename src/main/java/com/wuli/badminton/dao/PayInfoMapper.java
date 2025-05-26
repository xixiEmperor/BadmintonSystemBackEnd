package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.PayInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付信息数据访问接口
 */
@Mapper
public interface PayInfoMapper {
    
    /**
     * 插入支付信息
     */
    int insert(PayInfo payInfo);
    
    /**
     * 根据订单号查询支付信息
     */
    PayInfo selectByOrderNo(Long orderNo);
    
    /**
     * 根据支付平台流水号查询支付信息
     */
    PayInfo selectByPlatformNumber(String platformNumber);
    
    /**
     * 更新支付状态
     */
    int updateByOrderNo(PayInfo payInfo);
    
    /**
     * 更新支付状态（按字段更新）
     */
    int updateStatusByOrderNo(@Param("orderNo") Long orderNo, 
                             @Param("status") Integer status,
                             @Param("platformNumber") String platformNumber,
                             @Param("platformStatus") String platformStatus);
} 