package com.wuli.badminton.controller;

import com.wuli.badminton.dto.ReservationOrderDto;
import com.wuli.badminton.dto.ReservationOrderQueryDto;
import com.wuli.badminton.dto.VenueAvailabilityDto;
import com.wuli.badminton.service.ReservationOrderService;
import com.wuli.badminton.vo.ReservationOrderVo;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 预约订单控制器
 */
@RestController
@RequestMapping("/api/reservations")
@Slf4j
public class ReservationOrderController {
    
    @Autowired
    private ReservationOrderService reservationOrderService;
    
    /**
     * 创建预约订单
     */
    @PostMapping("/create")
    public ResponseVo<ReservationOrderVo> createOrder(@Valid @RequestBody ReservationOrderDto dto) {
        Integer userId = getCurrentUserId();
        log.info("用户{}创建预约订单，场地ID：{}，时间：{} {}~{}", 
                userId, dto.getVenueId(), dto.getReservationDate(), dto.getStartTime(), dto.getEndTime());
        return reservationOrderService.createOrder(userId, dto);
    }
    
    /**
     * 查询我的订单列表
     */
    @GetMapping("/my-orders")
    public ResponseVo<List<ReservationOrderVo>> getMyOrders(
            @RequestParam(required = false) Integer status) {
        Integer userId = getCurrentUserId();
        return reservationOrderService.getUserOrders(userId, status);
    }
    
    /**
     * 根据订单ID查询订单详情
     */
    @GetMapping("/{id}")
    public ResponseVo<ReservationOrderVo> getOrderById(@PathVariable Long id) {
        return reservationOrderService.getOrderById(id);
    }
    
    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("/order-no/{orderNo}")
    public ResponseVo<ReservationOrderVo> getOrderByNo(@PathVariable String orderNo) {
        return reservationOrderService.getOrderByNo(orderNo);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ResponseVo<String> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        Integer userId = getCurrentUserId();
        return reservationOrderService.cancelOrder(userId, id, reason);
    }
    
    /**
     * 申请退款
     */
    @PostMapping("/{id}/refund")
    public ResponseVo<String> refundOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return reservationOrderService.refundOrder(id, reason);
    }
    
    /**
     * 查询场地可用性
     */
    @GetMapping("/availability")
    public ResponseVo<Map<String, Object>> checkVenueAvailability(@Valid VenueAvailabilityDto dto) {
        return reservationOrderService.checkVenueAvailability(dto);
    }
    
    /**
     * 查询场地预约记录
     */
    @GetMapping("/venue/{venueId}")
    public ResponseVo<List<ReservationOrderVo>> getVenueReservations(
            @PathVariable Integer venueId,
            @RequestParam String date) {
        return reservationOrderService.getVenueReservations(venueId, date);
    }
    
    /**
     * 支付回调接口（供支付系统调用）
     */
    @PostMapping("/payment/callback")
    public ResponseVo<String> paymentCallback(
            @RequestParam String orderNo,
            @RequestParam Long payInfoId) {
        return reservationOrderService.paymentCallback(orderNo, payInfoId);
    }
    
    // ==================== 管理员接口 ====================
    
    /**
     * 管理员查询所有订单
     */
    @GetMapping("/admin/orders")
    public ResponseVo<Map<String, Object>> getAdminOrderList(@Valid ReservationOrderQueryDto queryDto) {
        // 设置默认分页参数
        if (queryDto.getPage() == null) {
            queryDto.setPage(1);
        }
        if (queryDto.getSize() == null) {
            queryDto.setSize(10);
        }
        return reservationOrderService.getAdminOrderList(queryDto);
    }
    
    /**
     * 管理员完成订单
     */
    @PostMapping("/admin/{id}/complete")
    public ResponseVo<String> completeOrder(@PathVariable Long id) {
        return reservationOrderService.completeOrder(id);
    }
    
    /**
     * 管理员审批退款
     */
    @PostMapping("/admin/{id}/approve-refund")
    public ResponseVo<String> approveRefund(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String adminRemark) {
        return reservationOrderService.approveRefund(id, approved, adminRemark);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 这里需要根据实际的用户认证实现来获取用户ID
            // 暂时返回一个测试用户ID
            return 1;
        }
        throw new RuntimeException("用户未登录");
    }
} 