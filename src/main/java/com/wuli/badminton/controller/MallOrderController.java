package com.wuli.badminton.controller;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.service.MallOrderService;
import com.wuli.badminton.vo.BuyNowRequestDto;
import com.wuli.badminton.vo.OrderVo;
import com.wuli.badminton.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Logger;

/**
 * 商城订单控制器
 */
@RestController
@RequestMapping("/api/mall/orders")
public class MallOrderController {
    Logger logger = Logger.getLogger(MallOrderController.class.getName());
    @Autowired
    private MallOrderService mallOrderService;
    
    /**
     * 创建订单
     * @return 响应对象
     */
    @PostMapping
    public ResponseVo<Long> createOrder() {
        Long orderNo = mallOrderService.createOrder();
        if (orderNo == null) {
            return ResponseVo.error(ResponseEnum.ORDER_CREATE_ERROR);
        }
        return ResponseVo.success(orderNo);
    }
    
    /**
     * 获取订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 订单状态：10-未付款，20-已付款，30-已取消，40-已完成，50-已关闭，null-全部状态
     * @return 订单列表
     */
    @GetMapping
    public ResponseVo<PageInfo<OrderVo>> getOrderList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status) {
        PageInfo<OrderVo> orderList = mallOrderService.getOrderList(pageNum, pageSize, status);
        return ResponseVo.success(orderList);
    }
    
    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    @GetMapping("/{orderNo}")
    public ResponseVo<OrderVo> getOrderDetail(@PathVariable Long orderNo) {
        OrderVo orderVo = mallOrderService.getOrderDetail(orderNo);
        if (orderVo == null) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        return ResponseVo.success(orderVo);
    }
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @return 取消结果
     */
    @PostMapping("/{orderNo}/cancel")
    public ResponseVo<String> cancelOrder(
            @PathVariable Long orderNo) {
        boolean result = mallOrderService.cancelOrder(orderNo);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED, "取消订单失败，订单不存在或已支付");
        }
        return ResponseVo.success("取消订单成功");
    }
    
    /**
     * 查询订单状态
     * @param orderNo 订单号
     * @return 订单状态
     */
    @GetMapping("/{orderNo}/status")
    public ResponseVo<Integer> getOrderStatus(@PathVariable Long orderNo) {
        Integer status = mallOrderService.getOrderStatus(orderNo);
        if (status == null) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        return ResponseVo.success(status);
    }
    
    /**
     * 立即购买 - 基于特定商品创建订单
     */
    @PostMapping("/buy-now")
    public ResponseVo<Long> buyNow(@RequestBody @Valid BuyNowRequestDto request) {
        try {
            Long orderNo = mallOrderService.createOrderByProduct(
                    request.getProductId(), 
                    request.getQuantity(), 
                    request.getSpecs()
            );
            
            if (orderNo != null) {
                return ResponseVo.success(orderNo);
            } else {
                return ResponseVo.error(ResponseEnum.ORDER_CREATE_ERROR);
            }
        } catch (Exception e) {
            // 使用Logger的log方法记录错误信息
            logger.log(java.util.logging.Level.SEVERE, "【立即购买】创建订单失败", e);
            return ResponseVo.error(ResponseEnum.ORDER_CREATE_ERROR.getCode(), e.getMessage());
        }
    }
    
    // ==================== 管理员接口 ====================
    
    /**
     * 管理员查看所有订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param username 用户名搜索（可选）
     * @param orderNo 订单号搜索（可选）
     * @return 订单列表
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<PageInfo<OrderVo>> getAdminOrderList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "orderNo", required = false) Long orderNo) {
        
        logger.info("【管理员查询订单】请求参数: pageNum=" + pageNum + ", pageSize=" + pageSize + ", username=" + username + ", orderNo=" + orderNo);
        
        PageInfo<OrderVo> orderList = mallOrderService.getAdminOrderList(pageNum, pageSize, username, orderNo);
        return ResponseVo.success(orderList);
    }
    
    /**
     * 管理员关闭订单
     * @param orderNo 订单号
     * @return 操作结果
     */
    @PostMapping("/admin/{orderNo}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> adminCloseOrder(@PathVariable Long orderNo) {
        logger.info("【管理员关闭订单】订单号: " + orderNo);
        
        boolean result = mallOrderService.adminCloseOrder(orderNo);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED, "关闭订单失败，订单不存在或状态不允许关闭");
        }
        return ResponseVo.success("订单关闭成功");
    }
    
    /**
     * 管理员验证提货码并完成订单
     * @param orderNo 订单号
     * @param pickupCode 提货码
     * @return 操作结果
     */
    @PostMapping("/admin/{orderNo}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> adminCompleteOrder(
            @PathVariable Long orderNo,
            @RequestParam String pickupCode) {
        
        logger.info("【管理员完成订单】订单号: " + orderNo + ", 提货码: " + pickupCode);
        
        boolean result = mallOrderService.adminCompleteOrder(orderNo, pickupCode);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ORDER_CANCELED, "完成订单失败，订单不存在、状态不正确或提货码错误");
        }
        return ResponseVo.success("订单完成成功");
    }
} 