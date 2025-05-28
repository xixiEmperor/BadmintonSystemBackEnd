package com.wuli.badminton.service;

import com.wuli.badminton.dto.ReservationOrderDto;
import com.wuli.badminton.dto.ReservationOrderQueryDto;
import com.wuli.badminton.dto.VenueAvailabilityDto;
import com.wuli.badminton.vo.ReservationOrderVo;
import com.wuli.badminton.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * 预约订单Service接口
 */
public interface ReservationOrderService {
    
    /**
     * 创建预约订单
     */
    ResponseVo<ReservationOrderVo> createOrder(Integer userId, ReservationOrderDto dto);
    
    /**
     * 根据订单ID查询订单详情
     */
    ResponseVo<ReservationOrderVo> getOrderById(Long id);
    
    /**
     * 根据订单号查询订单详情
     */
    ResponseVo<ReservationOrderVo> getOrderByNo(String orderNo);
    
    /**
     * 查询订单列表（分页）
     */
    ResponseVo<Map<String, Object>> getOrderList(ReservationOrderQueryDto queryDto);
    
    /**
     * 查询用户的订单列表
     */
    ResponseVo<List<ReservationOrderVo>> getUserOrders(Integer userId, Integer status);
    
    /**
     * 取消订单
     */
    ResponseVo<String> cancelOrder(Integer userId, Long orderId, String reason);
    
    /**
     * 退款订单
     */
    ResponseVo<String> refundOrder(Long orderId, String reason);
    
    /**
     * 管理员审批退款
     */
    ResponseVo<String> approveRefund(Long orderId, Boolean approved, String adminRemark);
    
    /**
     * 支付完成回调
     */
    ResponseVo<String> paymentCallback(String orderNo, Long payInfoId);
    
    /**
     * 查询场地可用性
     */
    ResponseVo<Map<String, Object>> checkVenueAvailability(VenueAvailabilityDto dto);
    
    /**
     * 查询场地预约记录
     */
    ResponseVo<List<ReservationOrderVo>> getVenueReservations(Integer venueId, String date);
    
    /**
     * 完成订单（系统自动调用）
     */
    ResponseVo<String> completeOrder(Long orderId);
    
    /**
     * 管理员查询所有订单
     */
    ResponseVo<Map<String, Object>> getAdminOrderList(ReservationOrderQueryDto queryDto);
} 