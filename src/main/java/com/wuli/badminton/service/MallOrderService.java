package com.wuli.badminton.service;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.pojo.MallOrder;
import com.wuli.badminton.vo.OrderVo;

/**
 * 商城订单服务接口
 */
public interface MallOrderService {
    

    Long createOrder();
    
    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderVo getOrderDetail(Long orderNo);
    
    /**
     * 获取订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    PageInfo<OrderVo> getOrderList(Integer pageNum, Integer pageSize);
    
    /**
     * 取消订单
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean cancelOrder(Long orderNo);
    
    /**
     * 支付成功回调
     * @param orderNo 订单号
     */
    void paySuccess(Long orderNo);
    
    /**
     * 生成取货码
     * @param orderNo 订单号
     * @return 取货码
     */
    String generatePickupCode(Long orderNo);
    
    /**
     * 通过订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    MallOrder selectByOrderNo(Long orderNo);
    
    /**
     * 获取订单状态
     * @param orderNo 订单号
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderNo);

    /**
     * 支付成功后扣减库存
     * @param orderNo 订单号
     */
    public void reduceProductStock(Long orderNo);
} 