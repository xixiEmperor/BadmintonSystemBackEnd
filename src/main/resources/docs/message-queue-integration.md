# 消息队列集成说明

## 概述

系统使用RabbitMQ实现订单的异步处理，包括支付成功通知和订单超时取消功能。支持商城订单（MALL）和预约订单（RESERVATION）两种业务类型。

## 消息队列架构

### 队列设计
- **支付通知队列**: `QUEUE_PAY_NOTIFY` - 处理支付成功通知
- **订单取消队列**: `QUEUE_ORDER_CANCEL` - 处理订单超时取消
- **延迟队列**: 支持订单超时自动取消（通过TTL + DLQ实现）

### 业务类型标识
- `MALL`: 商城订单业务
- `RESERVATION`: 场地预约订单业务

## 支付成功处理流程

### 1. 完整支付流程
1. **创建订单**: 用户创建预约订单，状态为"待支付"
2. **发起支付**: 调用支付接口，**PayService自动创建PayInfo记录**
3. **支付平台回调**: `PayService.asyncNotify()` 处理支付平台的异步通知
4. **更新PayInfo**: 支付服务更新PayInfo记录的支付状态和流水号
5. **发送MQ消息**: 支付服务发送包含订单号和业务类型的消息
6. **业务处理**: `PayNotifyListener` 接收消息并调用对应的业务服务

### 2. 消息格式
```json
{
    "orderNo": 1234567890,
    "payPlatform": 1,
    "platformNumber": "alipay_20240115123456",
    "payAmount": 100.00,
    "businessType": "RESERVATION"
}
```

### 3. 处理逻辑
`PayNotifyListener.processPayNotify()` 根据 `businessType` 分发处理：

#### 商城订单 (MALL)
```java
mallOrderService.paySuccess(orderNo);
```

#### 预约订单 (RESERVATION)
```java
// 查询真实的PayInfo记录（重要：不再临时生成ID）
PayInfo payInfo = payInfoMapper.selectByOrderNo(orderNo);
if (payInfo == null) {
    log.error("预约订单支付处理失败，PayInfo记录不存在: orderNo={}", orderNo);
    return;
}

reservationOrderService.paymentCallback(orderNoStr, payInfo.getId().longValue());
```

**重要设计修正**: 
- ✅ **正确做法**: 从数据库查询真实的PayInfo记录ID
- ❌ **错误做法**: 临时生成PayInfo ID（会导致数据不一致）

### 4. PayInfo记录管理
PayInfo记录在支付流程中的作用：
- **创建时机**: 调用 `PayService.createPay()` 时自动创建
- **更新时机**: 支付平台异步通知时更新支付状态和流水号
- **关联关系**: 预约订单表的 `pay_info_id` 字段关联到PayInfo表的主键

### 5. 前端集成
前端在调用支付接口时需要携带 `businessType` 参数：

```javascript
// 创建支付订单
const payRequest = {
    orderNo: "RO1705123456789",
    amount: 100.00,
    businessType: "RESERVATION"  // 重要：标识业务类型
};
```

## 订单超时取消流程

### 1. 消息格式

#### 新格式（推荐）
包含业务类型信息的JSON格式：
```json
{
    "businessType": "RESERVATION",
    "orderNo": "RO1705123456789"
}
```

#### 兼容格式
纯订单号格式（默认按商城订单处理）：
```
1234567890
```

### 2. 处理逻辑
`OrderCancelListener.processOrderCancel()` 支持两种业务类型：

#### 商城订单超时取消
- 查询订单状态
- 检查是否为未支付状态
- 调用 `mallOrderService.cancelOrder()`

#### 预约订单超时取消
- 查询预约订单详情
- 检查是否为待支付状态（PENDING_PAYMENT）
- 调用 `reservationOrderService.cancelOrder()` 并标记为"系统超时自动取消"

### 3. 状态检查
- **商城订单**: 只有 `STATUS_UNPAID` 状态的订单会被取消
- **预约订单**: 只有 `PENDING_PAYMENT` 状态的订单会被取消

## 核心代码实现

### PayNotifyListener
```java
@RabbitListener(queues = RabbitMQConfig.QUEUE_PAY_NOTIFY)
public void processPayNotify(String message) {
    PayNotifyMessage payNotifyMessage = objectMapper.readValue(message, PayNotifyMessage.class);
    String businessType = payNotifyMessage.getBusinessType();
    
    if ("MALL".equals(businessType)) {
        mallOrderService.paySuccess(orderNo);
    } else if ("RESERVATION".equals(businessType)) {
        reservationOrderService.paymentCallback(orderNoStr, payInfoId);
    }
}
```

### OrderCancelListener
```java
@RabbitListener(queues = RabbitMQConfig.QUEUE_ORDER_CANCEL)
public void processOrderCancel(String message) {
    Map<String, Object> messageData = parseMessage(message);
    
    if (messageData != null) {
        String businessType = (String) messageData.get("businessType");
        
        if ("MALL".equals(businessType)) {
            processMallOrderCancel(orderNo);
        } else if ("RESERVATION".equals(businessType)) {
            processReservationOrderCancel(orderNo);
        }
    } else {
        // 兼容模式：默认按商城订单处理
        processMallOrderCancel(parseOrderNo(message));
    }
}
```

## 预约订单特有逻辑

### 1. 订单号格式
预约订单使用字符串格式的订单号（如："RO1705123456789"），而商城订单使用数字格式。

### 2. 状态管理
预约订单有自己的状态枚举 `ReservationStatusEnum`：
- `PENDING_PAYMENT(1)`: 待支付
- `PAID(2)`: 已支付
- `COMPLETED(3)`: 已完成
- `CANCELLED(4)`: 已取消

### 3. 超时取消逻辑
```java
private void processReservationOrderCancel(String orderNo) {
    // 查询订单
    ResponseVo<ReservationOrderVo> orderResponse = reservationOrderService.getOrderByNo(orderNo);
    
    // 检查状态
    if (ReservationStatusEnum.PENDING_PAYMENT.getCode().equals(currentStatus)) {
        // 系统自动取消
        reservationOrderService.cancelOrder(orderVo.getUserId(), orderVo.getId(), "系统超时自动取消");
    }
}
```

## 错误处理和日志

### 1. 日志规范
所有操作都有详细的日志记录，使用统一的日志前缀：
- `【订单超时关单】`: 超时取消相关日志
- `【商城订单超时关单】`: 商城订单特定日志
- `【预约订单超时关单】`: 预约订单特定日志

### 2. 异常处理
- 消息解析失败：记录错误日志，跳过处理
- 订单不存在：记录警告日志，跳过处理
- 状态不符合：记录信息日志，跳过处理
- 业务处理失败：记录错误日志，具体错误信息

## 测试验证

### 1. 支付成功测试
```bash
# 模拟预约订单支付成功
curl -X POST http://localhost:8080/api/reservations/payment/callback \
  -d "orderNo=RO1705123456789&payInfoId=12345"
```

### 2. 超时取消测试
```json
// 发送到订单取消队列的消息
{
    "businessType": "RESERVATION",
    "orderNo": "RO1705123456789"
}
```

## 注意事项

1. **消息格式兼容性**: 新的JSON格式向下兼容旧的纯订单号格式
2. **业务类型标识**: 前端调用支付接口时必须正确设置 `businessType`
3. **订单号类型**: 商城订单使用Long，预约订单使用String
4. **状态检查**: 不同业务类型有不同的状态枚举和检查逻辑
5. **错误恢复**: 消息处理失败不会影响其他消息的处理

## 未来扩展

系统设计支持轻松添加新的业务类型：
1. 在消息中添加新的 `businessType` 值
2. 在监听器中添加对应的处理分支
3. 注入相应的Service依赖
4. 实现特定的业务逻辑

这种设计确保了系统的可扩展性和业务隔离性。 