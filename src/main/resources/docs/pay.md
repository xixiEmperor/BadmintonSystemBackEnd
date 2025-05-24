## 1. 订单列表查看功能

**功能特点：**
- 支持分页查询（默认第1页，每页10条）
- 只显示当前用户的订单
- 包含订单详情和订单项信息

## 2. 延迟支付功能分析

### **支付流程设计：**

```
创建订单 → 获取支付链接 → 用户支付 → 支付回调
    ↓           ↓            ↓         ↓
  订单号    支付界面URL    第三方处理   更新状态
```

### **关键实现机制：**

#### **1. 订单状态管理**
```java
public class MallOrderServiceImpl implements MallOrderService {
    // 示例方法
    public void createOrder() {
        // 订单创建时状态为"未支付"
        order.setStatus(MallOrder.STATUS_UNPAID);
// 10分钟内都可以支付
// 延迟队列会在10分钟后检查状态
        if (order.getStatus().equals(MallOrder.STATUS_UNPAID)) {
            // 仍未支付，执行取消
            mallOrderService.cancelOrder(orderNo);
        } else {
            // 已支付，跳过取消
            logger.info("订单无需取消，当前状态={}", order.getStatus());
        }
    }
}
```

#### **2. 支付接口设计**
```java
// PayController.java - 创建支付
@PostMapping("/create")
public ResponseVo<PayResponse> create(@RequestBody CreatePayRequestDto requestDto) {
    PayResponse response = payService.createPay(
        requestDto.getOrderNo(), 
        requestDto.getAmount(), 
        requestDto.getBusinessType()
    );
    return ResponseVo.success(response);
}

// 查询支付状态
@GetMapping("/query")
public ResponseVo<PayInfo> queryPayStatus(@RequestParam("orderNo") Long orderNo) {
    PayInfo payInfo = payService.queryByOrderId(orderNo);
    return ResponseVo.success(payInfo);
}
```

## 3. 延迟支付的完整流程

### **用户操作流程：**

```
1. 用户创建订单 → 获得订单号
2. 用户暂时离开，不立即支付
3. 用户稍后返回 → 查看订单列表
4. 选择未支付订单 → 点击支付
5. 调用支付接口 → 获取支付链接
6. 完成支付 → 订单状态更新
```

### **技术实现：**

#### **后端支持：**
```java
// PayService 支持根据订单号重新创建支付
public PayResponse createPay(Long orderNo, BigDecimal amount, String businessType) {
    // 1. 验证订单状态
    MallOrder order = mallOrderService.selectByOrderNo(orderNo);
    if (order.getStatus() != MallOrder.STATUS_UNPAID) {
        throw new RuntimeException("订单状态不允许支付");
    }
    
    // 2. 创建支付请求
    // 3. 返回支付链接
}
```

## 4. 时间窗口管理

### **10分钟支付窗口：**

```java
// 延迟队列配置 - 10分钟TTL
@Bean
public Queue orderDelayQueue() {
    return QueueBuilder.durable(QUEUE_ORDER_DELAY)
            .withArgument("x-message-ttl", 600000) // 10分钟
            .build();
}
```

### **支付时序图：**

```
时间轴: 0分钟    5分钟    10分钟    15分钟
       |        |        |         |
创建订单 ●        |        |         |
       |        |        |         |
用户支付         ●        |         |
       |        |        |         |
延迟消息                  ●         |
检查状态                  |         |
(已支付,跳过)              |         |
```

## 5. 订单状态查询

用户可以随时查询订单状态：

```java
// 获取订单详情
@GetMapping("/{orderNo}")
public ResponseVo<OrderVo> getOrderDetail(@PathVariable Long orderNo) {
    OrderVo orderVo = mallOrderService.getOrderDetail(orderNo);
    return ResponseVo.success(orderVo);
}

// 查询订单状态
@GetMapping("/{orderNo}/status")
public ResponseVo<Integer> getOrderStatus(@PathVariable Long orderNo) {
    Integer status = mallOrderService.getOrderStatus(orderNo);
    return ResponseVo.success(status);
}
```

## 总结

这个系统设计很好地支持了**延迟支付**功能：

1. **订单持久化**：订单创建后保存在数据库中
2. **状态管理**：通过订单状态控制支付流程
3. **时间窗口**：10分钟内允许支付
4. **重复支付**：支持多次获取支付链接
5. **自动关单**：超时未支付自动取消

用户可以在10分钟内的任何时候返回系统，查看订单列表，选择未支付订单进行支付，系统会重新生成支付链接供用户使用。
