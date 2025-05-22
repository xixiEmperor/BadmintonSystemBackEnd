package com.wuli.badminton.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dao.MallOrderItemMapper;
import com.wuli.badminton.dao.MallOrderMapper;
import com.wuli.badminton.pojo.CartItem;
import com.wuli.badminton.pojo.MallOrder;
import com.wuli.badminton.pojo.MallOrderItem;
import com.wuli.badminton.pojo.MallProduct;
import com.wuli.badminton.pojo.ProductSpecification;
import com.wuli.badminton.service.CartService;
import com.wuli.badminton.service.MallOrderService;
import com.wuli.badminton.service.MallProductService;
import com.wuli.badminton.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.wuli.badminton.service.UserService;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商城订单服务实现类
 */
@Service
@Slf4j
public class MallOrderServiceImpl implements MallOrderService {
    
    @Autowired
    private MallOrderMapper mallOrderMapper;
    
    @Autowired
    private MallOrderItemMapper mallOrderItemMapper;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private MallProductService productService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建订单
     */
    @Override
    @Transactional
    public Long createOrder() {
        // 获取当前用户ID
    Long userId = userService.getCurrentUser().getId();
        // 1. 获取购物车中选中的商品
        List<CartItem> cartItems = cartService.listSelectedItems(userId);
        if (CollectionUtils.isEmpty(cartItems)) {
            log.error("【创建订单】购物车为空, userId={}", userId);
            return null;
        }
        
        // 2. 生成订单号
        Long orderNo = generateOrderNo();
        
        // 3. 计算订单总价
        BigDecimal totalPrice = new BigDecimal("0");
        
        // 4. 构建订单项列表
        List<MallOrderItem> orderItems = new ArrayList<>();
        Date now = new Date();
        
        for (CartItem cartItem : cartItems) {
            // 4.1 查询商品（实际使用中应该用mapper批量查询，这里简化处理）
            MallProduct product = productService.getProductById(cartItem.getProductId());
            if (product == null) {
                log.error("【创建订单】商品不存在, productId={}", cartItem.getProductId());
                throw new RuntimeException("商品不存在");
            }
            
            // 4.2 计算商品总价
            BigDecimal itemTotalPrice = product.getPrice()
                    .add(cartItem.getPriceAdjustment())
                    .multiply(new BigDecimal(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotalPrice);
            
            // 4.3 构建订单项
            MallOrderItem orderItem = new MallOrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(itemTotalPrice);
            orderItem.setSpecificationId(cartItem.getSpecificationId());
            orderItem.setSpecs(cartItem.getSpecs() != null ? String.valueOf(cartItem.getSpecs()) : null);
            orderItem.setPriceAdjustment(cartItem.getPriceAdjustment());
            orderItem.setCreateTime(now);
            orderItem.setUpdateTime(now);
            
            orderItems.add(orderItem);
        }
        
        // 5. 批量插入订单项
        mallOrderItemMapper.batchInsert(orderItems);
        
        // 6. 创建订单
        MallOrder order = new MallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setPaymentType(MallOrder.PAYMENT_TYPE_ONLINE);
        order.setStatus(MallOrder.STATUS_UNPAID);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        
        mallOrderMapper.insert(order);
        
        // 7. 清空购物车中已下单的商品
        cartService.deleteSelectedItems(userId);
        
        return orderNo;
    }
    
    /**
     * 获取订单详情
     */
    @Override
    public OrderVo getOrderDetail(Long orderNo) {
        MallOrder order = mallOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        
        // 获取订单项
        List<MallOrderItem> orderItems = mallOrderItemMapper.selectByOrderNo(orderNo);
        
        return buildOrderVo(order, orderItems);
    }
    
    /**
     * 获取订单列表
     */
    @Override
    public PageInfo<OrderVo> getOrderList(Integer pageNum, Integer pageSize) {
        Long userId = userService.getCurrentUser().getId();
        PageHelper.startPage(pageNum, pageSize);
        List<MallOrder> orders = mallOrderMapper.selectByUserId(userId);
        
        List<OrderVo> orderVoList = new ArrayList<>();
        for (MallOrder order : orders) {
            List<MallOrderItem> orderItems = mallOrderItemMapper.selectByOrderNo(order.getOrderNo());
            orderVoList.add(buildOrderVo(order, orderItems));
        }
        
        PageInfo<MallOrder> pageInfo = new PageInfo<>(orders);
        PageInfo<OrderVo> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result, "list");
        result.setList(orderVoList);
        
        return result;
    }
    
    /**
     * 取消订单
     */
    @Override
    @Transactional
    public boolean cancelOrder(Long orderNo) {
        Long userId = userService.getCurrentUser().getId();
        MallOrder order = mallOrderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有未付款的订单才能取消
        if (!Objects.equals(order.getStatus(), MallOrder.STATUS_UNPAID)) {
            return false;
        }
        
        order.setStatus(MallOrder.STATUS_CANCELED);
        order.setUpdateTime(new Date());
        
        return mallOrderMapper.updateStatusByOrderNo(order.getOrderNo(), 
                order.getStatus(), order.getUpdateTime()) > 0;
    }
    
    /**
     * 支付成功回调
     */
    @Override
    @Transactional
    public void paySuccess(Long orderNo) {
        MallOrder order = mallOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.error("【支付成功回调】订单不存在, orderNo={}", orderNo);
            return;
        }
        
        // 更新订单状态为已支付
        if (Objects.equals(order.getStatus(), MallOrder.STATUS_UNPAID)) {
            Date now = new Date();
            mallOrderMapper.updatePaymentInfo(orderNo, now, now);
            
            // 生成取货码
            String pickupCode = generatePickupCode(orderNo);
            mallOrderMapper.updatePickupCode(orderNo, pickupCode, now);
            
            // 扣减库存
            reduceProductStock(orderNo);
        }
    }
    
    /**
     * 生成取货码
     */
    @Override
    public String generatePickupCode(Long orderNo) {
        // 生成6位随机数字
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 通过订单号查询订单
     */
    @Override
    public MallOrder selectByOrderNo(Long orderNo) {
        return mallOrderMapper.selectByOrderNo(orderNo);
    }
    
    /**
     * 获取订单状态
     */
    @Override
    public Integer getOrderStatus(Long orderNo) {
        MallOrder order = mallOrderMapper.selectByOrderNo(orderNo);
        return order != null ? order.getStatus() : null;
    }
    
    /**
     * 生成订单号
     * 使用时间戳+随机数的方式
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + (long) (Math.random() * 10000);
    }
    
    /**
     * 构建OrderVo对象
     */
    private OrderVo buildOrderVo(MallOrder order, List<MallOrderItem> orderItems) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        
        List<OrderVo.OrderItemVo> orderItemVoList = orderItems.stream().map(item -> {
            OrderVo.OrderItemVo itemVo = new OrderVo.OrderItemVo();
            BeanUtils.copyProperties(item, itemVo);
            return itemVo;
        }).collect(Collectors.toList());
        
        orderVo.setOrderItemList(orderItemVoList);
        orderVo.setStatusDesc(order.getStatusDesc());
        
        return orderVo;
    }

    /**
     * 支付成功后扣减库存
     * @param orderNo 订单号
     */
    @Override
    @Transactional
    public void reduceProductStock(Long orderNo) {
        log.info("【扣减库存】开始处理: orderNo={}", orderNo);
        
        // 查询订单项
        List<MallOrderItem> orderItems = mallOrderItemMapper.selectByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(orderItems)) {
            log.warn("【扣减库存】订单项为空: orderNo={}", orderNo);
            return;
        }
        
        for (MallOrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();
            Integer specificationId = orderItem.getSpecificationId();
            
            try {
                if (specificationId != null) {
                    // 有规格商品，扣减规格库存
                    log.info("【扣减库存】扣减规格库存: productId={}, specificationId={}, quantity={}", 
                             productId, specificationId, quantity);
                    
                    // 获取当前规格库存
                    ProductSpecification specification = productService.getProductSpecificationMapper()
                            .findById(specificationId);
                    
                    if (specification != null) {
                        int newStock = specification.getStock() - quantity;
                        if (newStock < 0) newStock = 0;
                        
                        // 更新规格库存
                        productService.updateSpecificationStock(specificationId, newStock);
                        
                        // updateSpecificationStock方法内部会自动更新商品总库存，无需额外处理
                        log.info("【扣减库存】规格库存扣减成功: specificationId={}, 原库存={}, 新库存={}", 
                                 specificationId, specification.getStock(), newStock);
                    } else {
                        log.error("【扣减库存】商品规格不存在: productId={}, specificationId={}", 
                                  productId, specificationId);
                    }
                } else {
                    // 无规格商品，直接扣减商品库存
                    log.info("【扣减库存】扣减商品库存: productId={}, quantity={}", productId, quantity);
                    
                    // 获取当前商品库存
                    MallProduct product = productService.getProductById(productId);
                    
                    if (product != null) {
                        int newStock = product.getStock() - quantity;
                        if (newStock < 0) newStock = 0;
                        
                        // 更新商品库存
                        productService.updateStock(productId, newStock);
                        
                        log.info("【扣减库存】商品库存扣减成功: productId={}, 原库存={}, 新库存={}", 
                                 productId, product.getStock(), newStock);
                    } else {
                        log.error("【扣减库存】商品不存在: productId={}", productId);
                    }
                }
            } catch (Exception e) {
                log.error("【扣减库存】扣减失败: productId={}, specificationId={}, error={}", 
                          productId, specificationId, e.getMessage(), e);
                // 可以选择继续处理其他商品库存，或者抛出异常终止事务
                // throw new RuntimeException("扣减库存失败", e);
            }
        }
        
        log.info("【扣减库存】处理完成: orderNo={}", orderNo);
    }
} 