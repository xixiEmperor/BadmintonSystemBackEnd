package com.wuli.badminton.controller;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.service.MallOrderService;
import com.wuli.badminton.vo.OrderVo;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @return 订单列表
     */
    @GetMapping
    public ResponseVo<PageInfo<OrderVo>> getOrderList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<OrderVo> orderList = mallOrderService.getOrderList(pageNum, pageSize);
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
} 