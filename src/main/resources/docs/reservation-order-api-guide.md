# 预约订单API使用指南

## 概述

预约订单模块提供了完整的羽毛球场地预约功能，包括用户端的预约、支付、取消、退款，以及管理员端的订单管理。

## 核心特性

### 1. 动态预约管理
- ✅ 支持任意时间段预约（不限于整点）
- ✅ 实时冲突检测，避免重复预约
- ✅ 自动计算费用（支持按小时计费）
- ✅ 灵活的状态管理（待支付→已支付→已完成）

### 2. 完整的业务流程
- 🔄 创建订单 → 支付 → 使用 → 完成
- 🔄 取消订单（待支付状态）
- 🔄 申请退款（已支付状态，24小时前）

### 3. 权限控制
- 👤 用户端：创建、查看、取消自己的订单
- 👨‍💼 管理员：查看所有订单、处理退款、完成订单

## API接口文档

### 用户端接口

#### 1. 创建预约订单
```http
POST /api/reservations/create
Content-Type: application/json
Authorization: Bearer {token}

{
    "venueId": 1,
    "reservationDate": "2024-01-15",
    "startTime": "09:00",
    "endTime": "11:00",
    "payType": 1,
    "remark": "公司团建活动"
}
```

**响应示例：**
```json
{
    "status": 0,
    "msg": "成功",
    "data": {
        "id": 1,
        "orderNo": "RO1705123456789",
        "username": "张三",
        "venueName": "羽毛球场地1",
        "reservationDate": "2024-01-15",
        "timeSlot": "09:00-11:00",
        "duration": 120,
        "pricePerHour": 50.00,
        "totalAmount": 100.00,
        "status": 1,
        "statusDesc": "待支付",
        "payTypeDesc": "支付宝",
        "canCancel": true,
        "canRefund": false,
        "createTime": "2024-01-10 14:30:00"
    }
}
```

#### 2. 查询我的订单列表
```http
GET /api/reservations/my-orders?status=2
Authorization: Bearer {token}
```

#### 3. 查询订单详情
```http
GET /api/reservations/123
Authorization: Bearer {token}
```

#### 4. 取消订单
```http
POST /api/reservations/123/cancel?reason=临时有事无法参加
Authorization: Bearer {token}
```

#### 5. 申请退款
```http
POST /api/reservations/123/refund?reason=身体不适无法运动
Authorization: Bearer {token}
```

### 支付接口（统一支付系统）

#### 1. 发起支付（通用支付接口）
```http
POST /pay/create
Content-Type: application/json
Authorization: Bearer {token}

{
    "orderNo": "RO1705123456789",
    "amount": 100.00,
    "businessType": "RESERVATION"
}
```

**重要说明**：
- `orderNo`: 预约订单可以直接使用完整的订单号（包含"RO"前缀）
- `businessType`: 必须设置为 "RESERVATION" 标识预约订单支付
- `amount`: 订单总金额

**响应示例：**
```json
{
    "status": 0,
    "msg": "成功", 
    "data": {
        "codeUrl": "weixin://wxpay/bizpayurl?pr=xXKHR7Bzz",
        "orderId": "RO1705123456789",
        "orderAmount": 100.0,
        "outTradeNo": "4200001234202401151234567890"
    }
}
```

#### 2. 查询支付状态
```http
GET /pay/query?orderNo=RO1705123456789
Authorization: Bearer {token}
```

### 公开接口（无需认证）

#### 1. 查询场地可用性
```http
GET /api/reservations/availability?venueId=1&date=2024-01-15&startTime=09:00&endTime=11:00
```

**响应示例：**
```json
{
    "status": 0,
    "data": {
        "isAvailable": false,
        "reservations": [
            {
                "timeSlot": "09:00-10:00",
                "venueName": "羽毛球场地1",
                "statusDesc": "已支付"
            }
        ]
    }
}
```

#### 2. 查询场地预约记录
```http
GET /api/reservations/venue/1?date=2024-01-15
```

#### 3. 支付回调（供支付系统调用）
```http
POST /api/reservations/payment/callback
Content-Type: application/x-www-form-urlencoded

orderNo=RO1705123456789&payInfoId=12345
```

### 管理员接口

#### 1. 查询所有订单
```http
GET /api/reservations/admin/orders?page=1&size=10&status=2&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer {admin_token}
```

#### 2. 完成订单
```http
POST /api/reservations/admin/123/complete
Authorization: Bearer {admin_token}
```

#### 3. 管理员审批退款
```http
POST /api/reservations/admin/123/approve-refund?approved=true&adminRemark=用户有合理理由
Authorization: Bearer {admin_token}
```

**参数说明：**
- `approved`: true-审批通过，false-审批拒绝
- `adminRemark`: 管理员备注（可选）

**审批通过响应：**
```json
{
    "status": 0,
    "msg": "退款审批通过，已完成退款"
}
```

**审批拒绝响应：**
```json
{
    "status": 0,
    "msg": "退款申请已拒绝"
}
```

## 订单状态说明

| 状态码 | 状态名称 | 描述 | 可执行操作 | 操作者 |
|--------|----------|------|------------|--------|
| 1 | 待支付 | 订单已创建，等待支付 | 取消订单、支付 | 用户 |
| 2 | 已支付 | 支付完成，等待使用 | 申请退款（开始前30分钟） | 用户 |
| 3 | 已完成 | 预约时间已过，订单完成 | 无 | 前台确认 |
| 4 | 已取消 | 用户取消或系统取消 | 无 | 用户/系统 |
| 5 | 已关闭 | 退款完成，订单关闭 | 无 | 管理员审批 |
| 6 | 退款中 | 退款申请处理中 | 管理员审批 | 管理员 |

## 业务规则

### 1. 预约时间规则
- ✅ 只能预约当前时间之后的时段
- ✅ 预约时间精确到分钟（如09:30-10:45）
- ✅ 同一场地同一时间段不能重复预约
- ✅ 支持跨小时预约（如09:30-11:15，计费1.75小时）

### 2. 取消和退款规则
- ✅ 待支付状态的订单可以随时取消
- ✅ 已支付状态的订单可以申请退款
- ✅ **退款时间限制：距离预约开始时间30分钟内不能申请退款**
- ✅ 退款需要管理员审批，审批通过后订单状态变为"已关闭"
- ✅ 退款金额等于订单总金额

### 3. 订单完成规则
- ✅ 用户到场地后，联系前台工作人员
- ✅ 前台工作人员确认用户身份后，将订单标记为"已完成"
- ✅ 已完成的订单不可再进行任何操作

### 4. 权限规则
- ✅ 用户只能操作自己的订单
- ✅ 管理员可以查看和操作所有订单
- ✅ 前台工作人员可以完成订单（需要相应权限配置）
- ✅ 支付回调接口对外开放（供第三方支付系统调用）

## 测试数据

系统提供了丰富的测试数据，包括：
- 不同状态的订单（待支付、已支付、已完成、已取消、已退款）
- 不同时间的预约（今天、明天、后天）
- 不同场地的预约记录
- 不同支付方式的订单

可以运行 `reservation_order.sql` 文件来初始化测试数据。

## 错误码说明

| 错误码 | 错误信息 | 解决方案 |
|--------|----------|----------|
| 20001 | 场地不存在 | 检查场地ID是否正确 |
| 20004 | 预约时间无效 | 检查时间格式和逻辑 |
| 20009 | 预约时段冲突 | 选择其他时间段 |
| 20010 | 距离开场不足30分钟 | 提前申请退款 |
| 20008 | 预约订单不存在 | 检查订单ID |

## 注意事项

1. **时间格式**：所有时间都使用 `HH:mm` 格式（如 09:00, 14:30）
2. **日期格式**：所有日期都使用 `yyyy-MM-dd` 格式（如 2024-01-15）
3. **金额计算**：按实际时长计费，支持小数（如1.5小时 = 1小时30分钟）
4. **并发控制**：使用数据库级别的冲突检测，确保数据一致性
5. **性能优化**：建议对frequently查询的字段建立索引

## 集成示例

### 前端集成示例（JavaScript）
```javascript
// 1. 创建预约订单
async function createReservation(data) {
    const response = await fetch('/api/reservations/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(data)
    });
    return await response.json();
}

// 2. 发起支付（使用统一支付接口）
async function createPayment(orderNo, amount) {
    const response = await fetch('/pay/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            orderNo: orderNo, // 直接使用完整订单号（包含RO前缀）
            amount: amount,
            businessType: 'RESERVATION'
        })
    });
    return await response.json();
}

// 3. 完整的预约流程示例
async function reserveVenue() {
    try {
        // 步骤1：创建预约订单
        const reservationData = {
            venueId: 1,
            reservationDate: "2024-01-15",
            startTime: "09:00",
            endTime: "11:00",
            payType: 1,
            remark: "公司团建活动"
        };
        
        const orderResult = await createReservation(reservationData);
        if (orderResult.status !== 0) {
            throw new Error('创建订单失败: ' + orderResult.msg);
        }
        
        const order = orderResult.data;
        console.log('订单创建成功:', order.orderNo);
        
        // 步骤2：发起支付
        const payResult = await createPayment(order.orderNo, order.totalAmount);
        if (payResult.status !== 0) {
            throw new Error('创建支付失败: ' + payResult.msg);
        }
        
        // 步骤3：显示支付二维码
        const paymentInfo = payResult.data;
        showQRCode(paymentInfo.codeUrl);
        
        // 步骤4：轮询检查支付状态
        checkPaymentStatus(order.orderNo);
        
    } catch (error) {
        console.error('预约流程失败:', error.message);
    }
}

// 4. 查询场地可用性
async function checkAvailability(venueId, date, startTime, endTime) {
    const params = new URLSearchParams({
        venueId, date, startTime, endTime
    });
    const response = await fetch(`/api/reservations/availability?${params}`);
    return await response.json();
}

// 5. 检查支付状态
async function checkPaymentStatus(orderNo) {
    const response = await fetch(`/pay/query?orderNo=${orderNo}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return await response.json();
}
```