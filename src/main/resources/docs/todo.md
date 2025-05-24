# 商城模块
- [*] redis保存购物车 创建订单 消息队列转发
- [ ] redis 实现商城推荐缓存 协同过滤算法 双写购物车redis 然后分析购物行为和订单行为 实现推荐算法
- [ ] 支付成功之后扣减库存 进一步的优化建议
- [*] 订单超时关单 调用cancleOrder接口 同时补回库存
  并发控制：在高并发场景下，简单的库存扣减可能会出现超卖问题。建议使用Redis分布式锁或数据库行锁确保库存扣减的原子性。
  库存预占：考虑在下单时预占库存，支付成功后扣减预占的库存，超时未支付则释放预占库存。
  事务补偿：如果库存扣减失败，应有补偿机制确保数据一致性。
  日志完善：添加更详细的日志记录，便于问题追踪和分析。
- [ ] 管理员后台管理订单 增加接口/admin/orders 可以查看所有用户订单列表 并根据用户名username字段通过id和order表关联/订单号进行搜索 
- 管理员可以关闭订单（发生线下退款等行为）设置订单状态为50-已关闭 则不计入订单统计
- 管理员也可以根据提货码 验证提货码正确后修改订单状态为40-已完成
- [ ] 随机码生成算法 唯一 不重复 hash分散

# 场地预约模块
- [ ]后台场地管理：上架场地，默认为羽毛球场1号-8号，维护场地预约状况 
周一到周五的白天不能开放预约 状态默认为使用中(因为白天对校内上课使用)晚上18:00-21:00开放预约 最短预约时长为一个小时
周末全天开放预约 从早上8点到晚上9点开放预约
支持节假日等特殊日期选择部分日期及时段全部停用或启用方便快速管理。   
- [ ] 前台用户选择时段并完成预约后 需要复用已有的pay模块相关接口完成支付 类似于商城模块 
在PayInfo 表中也记录businessType字段，消息队列监听器通过该字段PayNotifyListener39-45line来区分是哪个业务
- [ ] 订单创建成功后 则场地该时段无法预约 一个场地一个时段只能预约一次 
- 一个账号一次只能有一个预约中订单 若取消或者超时未支付 则恢复场地状态为可预约

用户提交预约 → 检查时间段是否空闲  否 -> 显示错误提示（该时段不可用）
                     ↓ 是
     创建预约订单（状态：待支付）
                     ↓
     锁定场地状态为“已预约”
                     ↓
     调用 Pay 系统发起支付请求
                     ↓
       是否支付成功？
         ↓ 是             ↓ 否
   更新订单状态为“已支付”    判断是否超时 有消息监听器取消订单 或用户取消
                     ↓                   ↓ 是
     场地状态仍为“已预约”      场地状态恢复为“空闲中”
                     ↓                   ↓
     用户到场联系前台确认使用           订单状态更新为“已取消”
                     ↓
     支付完成后判断是否在“开场前10分钟”：
                     ↓ 是                         ↓ 否
     不允许退款，提示“距离开场不足10分钟”     可申请退款（需审核）
                     ↓                           ↓
                                         用户申请退款 → 进入“退款中”状态
                                                             ↓
                                           判断用户当日退款次数是否 ≤ 2 次
                                                             ↓ 是
                                       商家审核通过 → 场地状态恢复为“空闲中”
                                                             ↓
                                         订单状态更新为“已关闭”，并记录退款时间
- 场地状态 使用中 维护中(暂时不开放预约) 已预约 空闲中
- 订单状态 待支付 已支付 已完成 已关闭 已取消(只有未支付可取消) 退款中(这里比商品模块多一个退款中 因为时效性较强 退款后释放场地同时关闭订单)
后台对于以下信息需要有alert 信息通知 
- 用户提交退款申请 ✅ 是 提示“有新的退款申请，请尽快处理”
- 场地状态异常（场地处于预约中 到点了没有使用） ✅ 是 提示“场地长时间处于预约中，请人工核查”

# 电商系统设计文档

## 目录
1. [商品模块](#1-商品模块)
2. [购物车模块](#2-购物车模块)
3. [订单模块](#3-订单模块)
4. [支付集成](#4-支付集成)
5. [商品推荐系统](#5-商品推荐系统)

## 1. 商品模块
*(已完成，此处略)*

## 2. 购物车模块

### 2.1 概述

购物车模块采用Redis作为主要存储媒介，不使用关系型数据库持久化购物车数据。同时，在用户进行加购等行为时，通过异步消息队列记录行为数据用于商品推荐算法。

### 2.3 功能设计

#### 2.3.1 核心功能

1. **查询购物车**
    - 从Redis获取用户购物车数据
    - 计算购物车总价和数量
    - 结合商品实时信息（如库存、价格）返回前端

2. **添加商品到购物车**
    - 验证商品状态和库存
    - 添加/更新Redis中的购物车数据
    - 异步记录加购行为到用户行为表

3. **更新购物车数量**
    - 验证新数量合法性
    - 更新Redis中的购物车数据
    - 异步记录更新行为

4. **选择/取消选择商品**
    - 更新Redis中商品的选中状态
    - 支持单个选择和全选操作

5. **删除购物车商品**
    - 从Redis中删除指定商品
    - 异步记录删除行为

#### 2.3.2 API接口设计

| 接口                   | 方法   | 说明                      | 参数                          |
|------------------------|--------|---------------------------|-------------------------------|
| `/cart`                | GET    | 获取购物车列表            | 无                            |
| `/cart`                | POST   | 添加商品到购物车          | productId, quantity, specs    |
| `/cart/{productId}`    | PUT    | 更新购物车商品数量        | quantity                      |
| `/cart/{productId}`    | DELETE | 删除购物车商品            | 无                            |
| `/cart/selectAll`      | PUT    | 全选/取消全选             | selected                      |
| `/cart/select/{productId}` | PUT | 选择/取消选择单个商品    | selected                      |


```java
@Component
public class UserBehaviorListener {
    
    @Autowired
    private AmqpTemplate amqpTemplate;
    
    @Async
    @EventListener
    public void handleUserBehaviorEvent(UserBehaviorEvent event) {
        try {
            // 构建消息
            Map<String, Object> message = new HashMap<>();
            message.put("userId", event.getUserId());
            message.put("productId", event.getProductId());
            message.put("behaviorType", event.getBehaviorType());
            message.put("specs", event.getSpecs());
            message.put("timestamp", System.currentTimeMillis());
            
            // 发送到RabbitMQ
            amqpTemplate.convertAndSend("user-behaviors", message);
        } catch (Exception e) {
            log.error("发送用户行为事件失败", e);
        }
    }
}
```

### 2.5 技术选型

1. **存储**：Redis Hash存储购物车数据
2. **通信**：RabbitMQ消息队列用于异步处理行为数据
3. **权限**：sepringSecurity JWT认证
4. **API格式**：RESTful API

## 3. 订单模块

### 3.1 概述

订单模块负责购物车商品的结算、订单创建和管理。本系统中不需要收货地址管理，采用线下提货模式，支付成功后系统自动生成提货码。

### 3.2 数据模型

#### 3.2.1 订单表

```sql
CREATE TABLE `mall_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `order_no` bigint(20) NOT NULL COMMENT '订单号',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `payment` decimal(20,2) NOT NULL COMMENT '实际付款金额,单位是元,保留两位小数',
  `payment_type` int(4) NOT NULL COMMENT '支付类型,1-在线支付',
  `status` int(10) NOT NULL COMMENT '订单状态:0-已取消,10-未付款,20-已付款,30-已完成',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '交易关闭时间',
  `pickup_code` varchar(20) DEFAULT NULL COMMENT '提货码',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no_index` (`order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

#### 3.2.2 订单明细表

```sql
CREATE TABLE `mall_order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单子表id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `order_no` bigint(20) NOT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL COMMENT '商品id',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) DEFAULT NULL COMMENT '商品图片地址',
  `specifications` varchar(500) DEFAULT NULL COMMENT '商品规格JSON',
  `current_unit_price` decimal(20,2) NOT NULL COMMENT '生成订单时的商品单价',
  `quantity` int(10) NOT NULL COMMENT '商品数量',
  `total_price` decimal(20,2) NOT NULL COMMENT '商品总价,单位是元,保留两位小数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `order_no_index` (`order_no`) USING BTREE,
  KEY `order_no_user_id_index` (`user_id`,`order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

### 3.3 功能设计

#### 3.3.1 核心功能

1. **创建订单**
    - 获取购物车已选商品
    - 验证商品状态和库存
    - 生成订单和订单明细
    - 扣减商品库存
    - 清空购物车已选商品
    - 记录购买行为
    - 生成支付信息并发送到支付系统
    用户点击支付
        ↓
    Mall / Reservation 模块构造支付请求
        ↓
    发送请求至 Pay 系统（包含 orderNo, amount, businessType）
        ↓
    Pay 系统生成支付单，并记录 businessType 到 PayInfo 表
        ↓
    跳转至微信/支付宝支付页面
        ↓
    支付完成后回调 Pay 系统
        ↓
    Pay 系统更新 PayInfo.status = SUCCESS
        ↓
    向 RabbitMQ 发送消息（含 businessType）
        ↓
    Mall / Reservation 监听消息并更新订单状态
2. **支付状态同步**
    - 接收支付系统的支付结果通知
    - 更新订单状态
    - 生成提货码（支付成功时）

3. **订单查询**
    - 查询订单列表（分页）
    - 查询订单详情
    - 提货码验证

4. **订单管理**
    - 取消订单
    - 确认完成订单(验证完提货码)

#### 3.3.2 API接口设计

| 接口                        | 方法   | 说明             | 参数                      |
|-----------------------------|--------|------------------|---------------------------|
| `/orders`                   | POST   | 创建订单         | remark                    |
| `/orders`                   | GET    | 获取订单列表     | status, pageNum, pageSize |
| `/orders/{orderNo}`         | GET    | 获取订单详情     | 无                        |
| `/orders/{orderNo}/cancel`  | PUT    | 取消订单         | 无                        |
| `/orders/pickup/{code}`     | GET    | 验证提货码       | 无                        |
| `/orders/{orderNo}/complete`| PUT    | 完成订单（提货后）| 无                       |

### 3.4 实现细节

#### 3.4.1 创建订单

```java
@Transactional
public OrderDto create(Integer userId, String remark) {
    // 获取购物车
    String cartKey = String.format("cart:%d", userId);
    Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);
    
    List<CartProductDto> selectedCartItems = new ArrayList<>();
    for (Object obj : cartEntries.values()) {
        CartProductDto item = (CartProductDto) obj;
        if (item.getProductSelected()) {
            selectedCartItems.add(item);
        }
    }
    
    // 验证购物车非空
    if (selectedCartItems.isEmpty()) {
        throw new BusinessException("请选择商品后下单");
    }
    
    // 检查商品状态和库存
    for (CartProductDto item : selectedCartItems) {
        Product product = productMapper.selectByPrimaryKey(item.getProductId());
        if (product == null || product.getStatus() != ProductStatusEnum.ON_SALE.getCode()) {
            throw new BusinessException("商品" + item.getProductName() + "已下架");
        }
        if (product.getStock() < item.getQuantity()) {
            throw new BusinessException("商品" + item.getProductName() + "库存不足");
        }
    }
    
    // 计算总价
    BigDecimal totalAmount = selectedCartItems.stream()
            .map(CartProductDto::getProductTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // 生成订单号
    long orderNo = generateOrderNo();
    
    // 创建订单对象
    Order order = new Order();
    order.setOrderNo(orderNo);
    order.setUserId(userId);
    order.setPayment(totalAmount);
    order.setPaymentType(1); // 在线支付
    order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
    order.setRemark(remark);
    Date now = new Date();
    order.setCreateTime(now);
    order.setUpdateTime(now);
    
    // 保存订单
    int rows = orderMapper.insert(order);
    if (rows <= 0) {
        throw new BusinessException("创建订单失败");
    }
    
    // 创建订单明细
    List<OrderItem> orderItems = new ArrayList<>();
    for (CartProductDto item : selectedCartItems) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(item.getProductId());
        orderItem.setProductName(item.getProductName());
        orderItem.setProductImage(item.getProductMainImage());
        orderItem.setSpecifications(objectMapper.writeValueAsString(item.getSpecifications()));
        orderItem.setCurrentUnitPrice(item.getProductPrice());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setTotalPrice(item.getProductTotalPrice());
        orderItem.setCreateTime(now);
        orderItem.setUpdateTime(now);
        
        orderItems.add(orderItem);
        
        // 减库存
        productMapper.decreaseStock(item.getProductId(), item.getQuantity());
        
        // 增加销量
        productMapper.increaseSales(item.getProductId(), item.getQuantity());
        
        // 记录购买行为
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(item.getProductId());
        behavior.setBehaviorType(4); // 购买
        behavior.setSpecs(objectMapper.writeValueAsString(item.getSpecifications()));
        behavior.setCreateTime(now);
        behaviorMapper.insert(behavior);
        
        // 从购物车中删除
        redisTemplate.opsForHash().delete(cartKey, item.getProductId().toString());
    }
    
    // 批量保存订单明细
    orderItemMapper.batchInsert(orderItems);
    
    // 生成支付信息并发送到支付系统
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setOrderNo(orderNo);
    paymentRequest.setUserId(userId);
    paymentRequest.setAmount(totalAmount);
    paymentRequest.setPaymentType(1);
    
    kafkaTemplate.send("payment-requests", paymentRequest);
    
    // 返回订单DTO
    return findDetailByOrderNo(orderNo, userId);
}
```

#### 3.4.2 支付回调处理

```java
@Transactional
public void handlePaymentCallback(PaymentCallbackDto callback) {
    // 查询订单
    Order order = orderMapper.selectByOrderNo(callback.getOrderNo());
    if (order == null) {
        log.error("订单不存在，orderNo={}", callback.getOrderNo());
        return;
    }
    
    // 订单状态检查
    if (order.getStatus() != OrderStatusEnum.NOT_PAY.getCode()) {
        log.error("订单状态错误，orderNo={}, status={}", callback.getOrderNo(), order.getStatus());
        return;
    }
    
    if (callback.getStatus() == PaymentStatusEnum.SUCCESS.getCode()) {
        // 更新订单状态为已支付
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        
        // 生成提货码
        String pickupCode = generatePickupCode();
        order.setPickupCode(pickupCode);
        
        // 发送提货码通知（如短信、邮件等）
        // ...
    } else {
        // 支付失败，可选择是否更新订单状态
        log.info("支付失败，orderNo={}", callback.getOrderNo());
    }
    
    order.setUpdateTime(new Date());
    orderMapper.updateByPrimaryKeySelective(order);
}
```

#### 3.4.3 提货码生成与验证

```java
/**
 * 生成提货码
 */
private String generatePickupCode() {
    // 生成8位随机数字
    Random random = new Random();
    int code = 100000 + random.nextInt(900000);
    
    // 检查是否已存在
    Order existOrder = orderMapper.selectByPickupCode(String.valueOf(code));
    if (existOrder != null) {
        // 重新生成
        return generatePickupCode();
    }
    
    return String.valueOf(code);
}

/**
 * 验证提货码
 */
public OrderDto verifyPickupCode(String pickupCode) {
    Order order = orderMapper.selectByPickupCode(pickupCode);
    if (order == null) {
        throw new BusinessException("提货码无效");
    }
    
    if (order.getStatus() != OrderStatusEnum.PAID.getCode()) {
        throw new BusinessException("订单状态异常，无法提货");
    }
    
    // 返回订单详情
    return findDetailByOrderNo(order.getOrderNo(), order.getUserId());
}
```

### 3.5 技术选型

1. **存储**：MySQL数据库存储订单数据
2. **通信**：RabbitMQ消息队列用于支付系统集成
3. **事务**：Spring声明式事务
4. **提货码**：8位随机数生成，确保唯一性

## 4. 支付集成

### 4.1 概述
支付模块负责与外部支付系统集成，处理订单支付请求和回调通知。本系统使用RabbitMQ消息队列与支付系统交互。

### 4.2 支付流程

1. **创建订单后**：
    - 生成支付请求信息
    - 通过`PayService.create()`方法调用第三方支付接口
    - 返回前端支付链接或支付二维码

2. **支付完成后**：
    - 支付系统通过RabbitMQ消息队列发送支付结果
    - 订单系统接收并处理支付结果
    - 更新订单状态

### 4.3 支付请求消息格式

```json
{
  "orderNo": 12345678901,
  "userId": 1001,
  "amount": 199.00,
  "paymentType": 1,
  "callbackTopic": "payNotify",
  "timestamp": 1617893347000
}
```


### 4.4 支付回调消息格式

```json
{
  "orderNo": 12345678901,
  "transactionId": "pay123456789",
  "amount": 199.00,
  "status": 1,  
  "message": "支付成功",
  "payTime": 1617893407000
}
```


### 4.5 如何调用和使用Pay项目

#### 1. 添加Maven依赖
在您的项目pom.xml中添加对pay项目的依赖：
```xml
<dependency>
    <groupId>com.wuli</groupId>
    <artifactId>pay</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```


#### 2. 配置RabbitMQ
在application.yml中添加RabbitMQ配置：
```yaml
spring:
  rabbitmq:
    addresses: 127.0.0.1
    port: 5672
    username: guest
    password: guest
```


#### 3. 创建支付
使用IPayService接口发起支付：
```java
@Autowired
private IPayService payService;

public String createPayment(Long orderNo, BigDecimal amount) {
    // 根据支付类型选择BestPayTypeEnum.WX_NATIVE或BestPayTypeEnum.ALIPAY_PC
    PayResponse response = payService.create(orderNo.toString(), amount, BestPayTypeEnum.WX_NATIVE);
    return response.getPayUrl(); // 返回支付二维码URL
}
```


#### 4. 查询支付状态
使用IPayService接口查询支付状态：
```java
@Autowired
private IPayService payService;

public PayInfo checkPaymentStatus(String orderId) {
    return payService.queryByOrderId(orderId);
}
```


#### 5. 处理支付回调
创建监听器消费RabbitMQ消息：
```java
@Component
@RabbitListener(queues = "payNotify")
@Slf4j
public class PayMessageListener {

    @Autowired
    private IOrderService orderService;
    
    @RabbitHandler
    public void process(String message) {
        log.info("【receive message:{}】", message);
        PayInfo payInfo = new Gson().fromJson(message, PayInfo.class);
        if (payInfo.getPlatformStatus().equals("SUCCESS")) {
            // 修改订单状态为已支付
            orderService.paid(payInfo.getOrderNo());
        }
    }
}
```


#### 6. 配置支付参数
在application.yml中配置支付相关参数：
```yaml
wx:
  appId: your_wx_app_id
  mchId: your_wx_mch_id
  mchKey: your_wx_mch_key
  notifyUrl: http://your-domain.com/pay/notify
  returnUrl: http://your-domain.com/order/list

alipay:
  appId: your_alipay_app_id
  privateKey: your_alipay_private_key
  publicKey: your_alipay_public_key
  notifyUrl: http://your-domain.com/pay/notify
  returnUrl: http://your-domain.com/order/list
```


### 4.6 实现细节

1. **支付服务初始化**
```java
@Component
public class BestPayConfig {

    @Autowired
    private WxAccountConfig wxAccountConfig;

    @Autowired
    private AlipayAccountConfig alipayAccountConfig;

    @Bean
    public BestPayService bestPayService(WxPayConfig wxPayConfig) {
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(alipayAccountConfig.getAppId());
        aliPayConfig.setPrivateKey(alipayAccountConfig.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(alipayAccountConfig.getPublicKey());
        aliPayConfig.setNotifyUrl(alipayAccountConfig.getNotifyUrl());
        aliPayConfig.setReturnUrl(alipayAccountConfig.getReturnUrl());

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }
}
```


2. **支付回调处理**
```java
@RestController
@Slf4j
public class PayController {

    @Autowired
    private IPayService payService;

    /**
     * 第三方支付平台异步通知处理
     */
    @PostMapping("/notify")
    public String asyncNotify(@RequestBody String notifyData) {
        return payService.asyncNotify(notifyData);
    }
}
```


### 4.7 关键注意事项

1. **支付金额一致性校验**
   在接收到支付结果通知时，务必验证支付金额是否与数据库中的订单金额一致

2. **幂等性处理**
   支付结果处理需要实现幂等，避免重复处理同一笔支付

3. **超时处理**
   设置订单支付超时机制，超时未支付的订单自动关闭

4. **安全考虑**
   - 验证第三方支付平台的异步通知签名，防止伪造通知
   - 使用HTTPS保证通信安全

5. **跨域配置**
   如果mall系统和pay系统是分离部署的，需要配置CORS以允许跨域访问

通过以上方式，您可以方便地集成和使用这个pay项目来为您的系统添加支付功能。

## 5. 商品推荐系统

### 5.1 概述

商品推荐系统基于用户行为数据，使用协同过滤算法生成个性化推荐结果，并使用Redis缓存提高性能。系统包含离线计算和实时推荐两部分。

### 5.2 数据模型

#### 5.2.1 用户行为表

已在购物车模块中定义。

#### 5.2.2 商品相似度表

```sql
CREATE TABLE `mall_product_similarity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id_a` int(11) NOT NULL COMMENT '商品A',
  `product_id_b` int(11) NOT NULL COMMENT '商品B',
  `similarity` decimal(5,4) NOT NULL COMMENT '相似度',
  `algorithm_type` tinyint(4) NOT NULL COMMENT '算法类型：1-基于用户协同过滤，2-基于物品协同过滤',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_product_pair_algorithm` (`product_id_a`,`product_id_b`,`algorithm_type`),
  KEY `idx_product_a` (`product_id_a`,`similarity`),
  KEY `idx_product_b` (`product_id_b`,`similarity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品相似度表';
```

#### 5.2.3 用户推荐结果表

```sql
CREATE TABLE `mall_user_recommend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `product_id` int(11) NOT NULL COMMENT '推荐商品ID',
  `score` decimal(5,4) NOT NULL COMMENT '推荐分数',
  `reason_type` tinyint(4) NOT NULL COMMENT '推荐原因：1-历史购买，2-相似用户喜欢，3-购物车推荐',
  `reference_id` int(11) DEFAULT NULL COMMENT '参考商品ID，如基于哪个商品推荐的',
  `position` int(11) NOT NULL COMMENT '推荐位置顺序',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_product` (`user_id`,`product_id`),
  KEY `idx_user_position` (`user_id`,`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户推荐结果表';
```

#### 5.2.4 Redis缓存结构

```
KEY: recommend:user:{userId}
TYPE: List
VALUE: 推荐商品ID列表
EXPIRE: 24小时

KEY: recommend:popular:{category}
TYPE: List
VALUE: 热门商品ID列表
EXPIRE: 24小时

KEY: recommend:similar:{productId}
TYPE: List
VALUE: 相似商品ID列表
EXPIRE: 7天
```

### 5.3 功能设计

#### 5.3.1 核心功能

1. **离线计算相似度矩阵**
    - 定期计算商品相似度矩阵
    - 更新相似度数据库表

2. **用户个性化推荐**
    - 基于用户历史行为生成推荐
    - 缓存推荐结果到Redis

3. **相似商品推荐**
    - 为商品详情页提供相似商品推荐
    - 缓存相似商品列表

4. **热门商品推荐**
    - 基于全站行为统计热门商品
    - 按分类提供热门商品推荐

#### 5.3.2 API接口设计

| 接口                           | 方法   | 说明                  | 参数                     |
|--------------------------------|--------|----------------------|--------------------------|
| `/recommend/personal`          | GET    | 获取个性化推荐        | limit                    |
| `/recommend/similar/{productId}`| GET    | 获取相似商品推荐      | limit                    |
| `/recommend/popular`           | GET    | 获取热门商品推荐      | category, limit          |
| `/recommend/cart`              | GET    | 基于购物车的推荐      | limit                    |

### 5.4 实现细节

#### 5.4.1 基于物品的协同过滤算法（离线计算）

```java
@Scheduled(cron = "0 0 2 ? * MON") // 每周一凌晨2点执行
public void calculateItemSimilarity() {
    log.info("开始计算商品相似度");
    
    // 1. 获取用户行为数据
    List<UserBehavior> behaviors = behaviorMapper.selectRecentBehaviors(90); // 最近90天数据
    
    // 2. 构建商品-用户矩阵：商品ID -> (用户ID -> 权重)
    Map<Integer, Map<Integer, Double>> itemUserMatrix = new HashMap<>();
    
    for (UserBehavior behavior : behaviors) {
        Integer userId = behavior.getUser;
    }}
```
-[] redis实现排行榜 显示近期用户打卡排名