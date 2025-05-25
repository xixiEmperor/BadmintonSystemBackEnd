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
     * 立即购买 - 基于购物车中的特定商品创建订单
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param specs 商品规格（可为空）
     * @return 订单号
     */
    Long createOrderByProduct(Integer productId, Integer quantity, java.util.Map<String, String> specs);
    
    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    OrderVo getOrderDetail(Long orderNo);
    
    /**
     * 获取订单列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 订单状态，null表示查询所有状态
     * @return 订单列表
     */
    PageInfo<OrderVo> getOrderList(Integer pageNum, Integer pageSize, Integer status);
    
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
    void reduceProductStock(Long orderNo);
    
    // ==================== 管理员功能 ====================
    
    /**
     * 管理员查看所有订单列表（支持搜索）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param username 用户名搜索（可选）
     * @param orderNo 订单号搜索（可选）
     * @return 订单列表
     */
    PageInfo<OrderVo> getAdminOrderList(Integer pageNum, Integer pageSize, String username, Long orderNo);
    
    /**
     * 管理员关闭订单
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean adminCloseOrder(Long orderNo);
    
    /**
     * 管理员验证提货码并完成订单
     * @param orderNo 订单号
     * @param pickupCode 提货码
     * @return 是否成功
     */
    boolean adminCompleteOrder(Long orderNo, String pickupCode);
} 