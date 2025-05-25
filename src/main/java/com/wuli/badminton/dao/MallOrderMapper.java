package com.wuli.badminton.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wuli.badminton.pojo.MallOrder;

import java.util.Date;
import java.util.List;

/**
 * 商城订单Mapper接口
 */
@Mapper
public interface MallOrderMapper {
    
    /**
     * 插入订单
     * @param record 订单记录
     * @return 影响行数
     */
    int insert(MallOrder record);
    
    /**
     * 通过订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    MallOrder selectByOrderNo(Long orderNo);
    
    /**
     * 通过取货码查询订单
     * @param pickupCode 取货码
     * @return 订单信息
     */
    MallOrder selectByPickupCode(String pickupCode);
    
    /**
     * 查询用户的订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<MallOrder> selectByUserId(Long userId);
    
    /**
     * 查询用户的订单列表（带状态筛选）
     * @param userId 用户ID
     * @param status 订单状态，null表示查询所有状态
     * @return 订单列表
     */
    List<MallOrder> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 管理员查询所有订单列表（支持搜索）
     * @param username 用户名搜索，null表示不按用户名搜索
     * @param orderNo 订单号搜索，null表示不按订单号搜索
     * @return 订单列表
     */
    List<MallOrder> selectAllOrdersForAdmin(@Param("username") String username, @Param("orderNo") Long orderNo);
    
    /**
     * 更新订单状态
     * @param orderNo 订单号
     * @param status 状态
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateStatusByOrderNo(@Param("orderNo") Long orderNo, 
                             @Param("status") Integer status,
                             @Param("updateTime") Date updateTime);
    
    /**
     * 更新支付信息
     * @param orderNo 订单号
     * @param paymentTime 支付时间
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updatePaymentInfo(@Param("orderNo") Long orderNo,
                         @Param("paymentTime") Date paymentTime,
                         @Param("updateTime") Date updateTime);
    
    /**
     * 更新取货码
     * @param orderNo 订单号
     * @param pickupCode 取货码
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updatePickupCode(@Param("orderNo") Long orderNo,
                        @Param("pickupCode") String pickupCode,
                        @Param("updateTime") Date updateTime);
} 