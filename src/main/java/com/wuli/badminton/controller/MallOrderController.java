package com.wuli.badminton.controller;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.service.MallOrderService;
import com.wuli.badminton.vo.BuyNowRequestDto;
import com.wuli.badminton.vo.OrderVo;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商城订单控制器
 */
@RestController
@RequestMapping("/orders")
@Slf4j
public class MallOrderController {
    
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
            log.error("【立即购买】创建订单失败", e);
            return ResponseVo.error(ResponseEnum.ORDER_CREATE_ERROR.getCode(), e.getMessage());
        }
    }
} 