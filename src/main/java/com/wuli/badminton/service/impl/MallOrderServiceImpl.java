package com.wuli.badminton.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuli.badminton.config.RabbitMQConfig;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.wuli.badminton.service.UserService;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 商城订单服务实现类
 */
@Service
@Slf4j
public class MallOrderServiceImpl implements MallOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ForumServiceImpl.class);
    
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
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
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
            logger.error("【创建订单】购物车为空, userId={}", userId);
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
                logger.error("【创建订单】商品不存在, productId={}", cartItem.getProductId());
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
        
        // 8. 发送延迟消息，10分钟后自动取消订单
        sendOrderDelayMessage(orderNo);
        
        return orderNo;
    }
    
    /**
     * 立即购买 - 基于特定商品创建订单
     */
    @Override
    @Transactional
    public Long createOrderByProduct(Integer productId, Integer quantity, Map<String, String> specs) {
        // 获取当前用户ID
        Long userId = userService.getCurrentUser().getId();
        
        // 1. 查询商品信息
        MallProduct product = productService.getProductById(productId);
        if (product == null) {
            logger.error("【立即购买】商品不存在, productId={}", productId);
            throw new RuntimeException("商品不存在");
        }
        
        // 2. 检查商品状态
        if (product.getStatus() != 1) {
            logger.error("【立即购买】商品已下架, productId={}", productId);
            throw new RuntimeException("商品已下架");
        }
        
        // 3. 处理规格信息
        Integer specificationId = null;
        BigDecimal priceAdjustment = BigDecimal.ZERO;
        int availableStock = product.getStock();
        
        if (specs != null && !specs.isEmpty()) {
            // 有规格的商品，查询规格信息
            ProductSpecification specification = productService.getProductSpecification(productId, specs);
            if (specification == null) {
                logger.error("【立即购买】商品规格不存在, productId={}, specs={}", productId, specs);
                throw new RuntimeException("商品规格不存在");
            }
            
            specificationId = specification.getId();
            priceAdjustment = specification.getPriceAdjustment();
            availableStock = specification.getStock();
        }
        
        // 4. 检查库存
        if (availableStock < quantity) {
            logger.error("【立即购买】库存不足, productId={}, 需要数量={}, 可用库存={}", 
                     productId, quantity, availableStock);
            throw new RuntimeException("库存不足");
        }
        
        // 5. 生成订单号
        Long orderNo = generateOrderNo();
        Date now = new Date();
        
        // 6. 计算商品总价
        BigDecimal itemTotalPrice = product.getPrice()
                .add(priceAdjustment)
                .multiply(new BigDecimal(quantity));
        
        // 7. 创建订单项
        MallOrderItem orderItem = new MallOrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(itemTotalPrice);
        orderItem.setSpecificationId(specificationId);
        orderItem.setSpecs(specs != null && !specs.isEmpty() ? specs.toString() : null);
        orderItem.setPriceAdjustment(priceAdjustment);
        orderItem.setCreateTime(now);
        orderItem.setUpdateTime(now);
        
        // 8. 插入订单项（使用批量插入）
        List<MallOrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        mallOrderItemMapper.batchInsert(orderItems);
        
        // 9. 创建订单
        MallOrder order = new MallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(itemTotalPrice);
        order.setPaymentType(MallOrder.PAYMENT_TYPE_ONLINE);
        order.setStatus(MallOrder.STATUS_UNPAID);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        
        mallOrderMapper.insert(order);
        
        // 10. 发送延迟消息，10分钟后自动取消订单
        sendOrderDelayMessage(orderNo);
        
        logger.info("【立即购买】订单创建成功, orderNo={}, productId={}, quantity={}", 
                 orderNo, productId, quantity);
        
        return orderNo;
    }
    
    /**
     * 发送订单延迟消息，用于超时自动取消
     */
    private void sendOrderDelayMessage(Long orderNo) {
        try {
            logger.info("【订单超时关单】发送延迟消息: orderNo={}", orderNo);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_ORDER,
                    RabbitMQConfig.ROUTING_KEY_ORDER_DELAY,
                    orderNo.toString()
            );
        } catch (Exception e) {
            logger.error("【订单超时关单】发送延迟消息失败: orderNo={}, error={}", orderNo, e.getMessage(), e);
        }
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
    public PageInfo<OrderVo> getOrderList(Integer pageNum, Integer pageSize, Integer status) {
        Long userId = userService.getCurrentUser().getId();
        PageHelper.startPage(pageNum, pageSize);
        
        // 根据是否有状态筛选使用不同的查询方法
        List<MallOrder> orders;
        if (status != null) {
            orders = mallOrderMapper.selectByUserIdAndStatus(userId, status);
        } else {
            orders = mallOrderMapper.selectByUserId(userId);
        }
        
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
            logger.error("【支付成功回调】订单不存在, orderNo={}", orderNo);
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
            
            // 转换specs格式：从字符串转换为Map
            if (item.getSpecs() != null && !item.getSpecs().isEmpty()) {
                try {
                    TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
                    Map<String, String> specsMap = objectMapper.readValue(item.getSpecs(), typeRef);
                    itemVo.setSpecs(specsMap);
                } catch (Exception e) {
                    logger.warn("【订单详情】specs格式转换失败: orderNo={}, specs={}, error={}", 
                             order.getOrderNo(), item.getSpecs(), e.getMessage());
                    // 如果转换失败，设置为空Map
                    itemVo.setSpecs(new HashMap<>());
                }
            } else {
                itemVo.setSpecs(new HashMap<>());
            }
            
            return itemVo;
        }).collect(Collectors.toList());
        
        orderVo.setOrderItemList(orderItemVoList);
        
        return orderVo;
    }

    /**
     * 支付成功后扣减库存
     * @param orderNo 订单号
     */
    @Override
    @Transactional
    public void reduceProductStock(Long orderNo) {
        logger.info("【扣减库存】开始处理: orderNo={}", orderNo);
        
        // 查询订单项
        List<MallOrderItem> orderItems = mallOrderItemMapper.selectByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(orderItems)) {
            logger.warn("【扣减库存】订单项为空: orderNo={}", orderNo);
            return;
        }
        
        for (MallOrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();
            Integer specificationId = orderItem.getSpecificationId();
            
            try {
                if (specificationId != null) {
                    // 有规格商品，扣减规格库存
                    logger.info("【扣减库存】扣减规格库存: productId={}, specificationId={}, quantity={}", 
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
                        logger.info("【扣减库存】规格库存扣减成功: specificationId={}, 原库存={}, 新库存={}", 
                                 specificationId, specification.getStock(), newStock);
                    } else {
                        logger.error("【扣减库存】商品规格不存在: productId={}, specificationId={}", 
                                  productId, specificationId);
                    }
                } else {
                    // 无规格商品，直接扣减商品库存
                    logger.info("【扣减库存】扣减商品库存: productId={}, quantity={}", productId, quantity);
                    
                    // 获取当前商品库存
                    MallProduct product = productService.getProductById(productId);
                    
                    if (product != null) {
                        int newStock = product.getStock() - quantity;
                        if (newStock < 0) newStock = 0;
                        
                        // 更新商品库存
                        productService.updateStock(productId, newStock);
                        
                        logger.info("【扣减库存】商品库存扣减成功: productId={}, 原库存={}, 新库存={}", 
                                 productId, product.getStock(), newStock);
                    } else {
                        logger.error("【扣减库存】商品不存在: productId={}", productId);
                    }
                }
            } catch (Exception e) {
                logger.error("【扣减库存】扣减失败: productId={}, specificationId={}, error={}", 
                          productId, specificationId, e.getMessage(), e);
                // 可以选择继续处理其他商品库存，或者抛出异常终止事务
                // throw new RuntimeException("扣减库存失败", e);
            }
        }
        
        logger.info("【扣减库存】处理完成: orderNo={}", orderNo);
    }
} 