package com.wuli.badminton.dao;

import com.wuli.badminton.dto.ReservationOrderQueryDto;
import com.wuli.badminton.pojo.ReservationOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 预约订单Mapper接口
 */
@Mapper
public interface ReservationOrderMapper {
    
    /**
     * 插入预约订单
     */
    int insert(ReservationOrder reservationOrder);
    
    /**
     * 根据ID查询
     */
    ReservationOrder selectById(@Param("id") Long id);
    
    /**
     * 根据订单号查询
     */
    ReservationOrder selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 根据条件查询订单列表
     */
    List<ReservationOrder> selectByCondition(ReservationOrderQueryDto queryDto);
    
    /**
     * 统计订单数量
     */
    int countByCondition(ReservationOrderQueryDto queryDto);
    
    /**
     * 更新订单
     */
    int updateById(ReservationOrder reservationOrder);
    
    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新支付信息
     */
    int updatePayInfo(@Param("id") Long id, @Param("payInfoId") Long payInfoId, 
                      @Param("payType") Integer payType, @Param("payTime") Date payTime);
    
    /**
     * 更新退款信息
     */
    int updateRefundInfo(@Param("id") Long id, @Param("refundAmount") java.math.BigDecimal refundAmount, 
                         @Param("refundTime") Date refundTime);
    
    /**
     * 查询场地在指定时间段的冲突订单
     * （状态为已支付、已完成的订单）
     */
    List<ReservationOrder> selectConflictOrders(@Param("venueId") Integer venueId,
                                               @Param("reservationDate") Date reservationDate,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);
    
    /**
     * 查询场地在指定时间段的冲突订单（带悲观锁）
     * （状态为待支付、已支付、已完成的订单）
     */
    List<ReservationOrder> selectConflictOrdersForUpdate(@Param("venueId") Integer venueId,
                                                        @Param("reservationDate") Date reservationDate,
                                                        @Param("startTime") String startTime,
                                                        @Param("endTime") String endTime);
    
    /**
     * 查询用户的订单列表
     */
    List<ReservationOrder> selectByUserId(@Param("userId") Integer userId, 
                                         @Param("status") Integer status);
    
    /**
     * 查询场地的预约记录
     */
    List<ReservationOrder> selectByVenueAndDate(@Param("venueId") Integer venueId,
                                               @Param("reservationDate") Date reservationDate);
    
    /**
     * 查询指定日期范围的订单
     */
    List<ReservationOrder> selectByDateRange(@Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate,
                                            @Param("status") Integer status);
    
    /**
     * 删除订单（逻辑删除，实际更新状态）
     */
    int deleteById(@Param("id") Long id);
} 