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
     * @param userId 用户ID
     * @param dto 订单数据
     * @return 创建结果
     */
    ResponseVo<ReservationOrderVo> createOrder(Integer userId, ReservationOrderDto dto);
    
    /**
     * 根据订单ID查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    ResponseVo<ReservationOrderVo> getOrderById(Long id);
    
    /**
     * 根据订单号查询订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    ResponseVo<ReservationOrderVo> getOrderByNo(String orderNo);
    
    /**
     * 查询订单列表（分页）
     * @param queryDto 查询条件
     * @return 订单列表
     */
    ResponseVo<Map<String, Object>> getOrderList(ReservationOrderQueryDto queryDto);
    
    /**
     * 查询用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    ResponseVo<List<ReservationOrderVo>> getUserOrders(Integer userId, Integer status);
    
    /**
     * 取消订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param reason 取消原因
     * @return 取消结果
     */
    ResponseVo<String> cancelOrder(Integer userId, Long orderId, String reason);
    
    /**
     * 申请退款
     * @param orderId 订单ID
     * @param reason 退款原因
     * @return 申请结果
     */
    ResponseVo<String> refundOrder(Long orderId, String reason);
    
    /**
     * 查询场地可用性
     * @param dto 查询条件
     * @return 可用性信息
     */
    ResponseVo<Map<String, Object>> checkVenueAvailability(VenueAvailabilityDto dto);
    
    /**
     * 查询场地预约记录
     * @param venueId 场地ID
     * @param date 日期
     * @return 预约记录
     */
    ResponseVo<List<ReservationOrderVo>> getVenueReservations(Integer venueId, String date);
    
    /**
     * 支付回调接口
     * @param orderNo 订单号
     * @param payInfoId 支付信息ID
     * @return 处理结果
     */
    ResponseVo<String> paymentCallback(String orderNo, Long payInfoId);
    
    /**
     * 关联支付信息到订单（在创建支付二维码后调用）
     * @param orderNo 订单号
     * @param payInfoId 支付信息ID
     * @return 关联结果
     */
    ResponseVo<String> linkPayInfo(String orderNo, Long payInfoId);
    
    // ==================== 管理员接口 ====================
    
    /**
     * 管理员查询所有订单
     * @param queryDto 查询条件
     * @return 订单列表
     */
    ResponseVo<Map<String, Object>> getAdminOrderList(ReservationOrderQueryDto queryDto);
    
    /**
     * 管理员完成订单
     * @param orderId 订单ID
     * @return 操作结果
     */
    ResponseVo<String> completeOrder(Long orderId);
    
    /**
     * 管理员审批退款
     * @param orderId 订单ID
     * @param approved 是否通过
     * @param adminRemark 管理员备注
     * @return 审批结果
     */
    ResponseVo<String> approveRefund(Long orderId, Boolean approved, String adminRemark);
} 