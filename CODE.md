🎨 前端需要做的修改思路
1. 注册页面修改
添加验证码输入框：在邮箱输入框下方添加验证码输入框
添加发送验证码按钮：点击后调用 /api/auth/send-code 接口
倒计时功能：发送后显示60秒倒计时，防止频繁发送
修改注册请求：原来的注册请求需要增加 verificationCode 字段
2. 重置密码页面
创建忘记密码页面：包含邮箱输入、验证码输入、新密码输入
发送验证码逻辑：type设置为"reset"
重置密码提交：调用 /api/auth/reset-password 接口
3. 前端交互流程
注册流程：
1. 用户输入邮箱 → 点击"发送验证码" → 调用send-code接口
2. 用户收到邮件 → 输入验证码 → 填写用户名密码
3. 点击注册 → 调用register接口（包含验证码）

重置密码流程：
1. 用户输入邮箱 → 点击"发送验证码" → 调用send-code接口(type=reset)
2. 用户收到邮件 → 输入验证码和新密码
3. 点击重置 → 调用reset-password接口
4. 错误处理
验证码错误：提示"验证码错误或已过期"
发送频率限制：提示"发送太频繁，请稍后再试"
邮箱已存在：注册时提示"邮箱已被注册"
邮箱不存在：重置密码时提示"邮箱不存在"
5. UI优化建议
验证码输入框可以做成6位数字分离式输入
发送验证码按钮要有loading状态和倒计时显示
邮箱验证要实时检查格式
密码强度提示
这样的设计既保证了安全性，又提供了良好的用户体验。前端只需要按照这个API接口进行对接即可！

## 📧 邮箱验证码系统详细说明

### 🔧 存储机制
- **存储方式**: Redis
- **验证码格式**: 6位随机数字 (000000-999999)
- **有效期**: 5分钟
- **发送限制**: 同一邮箱同一类型60秒内只能发送一次

### 🔑 Redis Key结构
```
验证码存储：email_code:{邮箱}:{类型}    值：验证码    TTL：5分钟
发送限制：email_send_limit:{邮箱}:{类型}  值：1        TTL：60秒
```

### 📋 API接口详细文档

#### 1️⃣ 发送验证码接口
```http
POST /api/auth/send-code
Content-Type: application/json

请求体：
{
    "email": "user@example.com",
    "type": "register"  // register-注册 或 reset-重置密码
}

成功响应：
{
    "status": 0,
    "msg": "验证码发送成功，请查收邮件",
    "data": null
}

错误响应：
{
    "status": 1,
    "msg": "邮箱格式不正确",
    "data": null
}
```

#### 2️⃣ 用户注册接口
```http
POST /api/auth/register
Content-Type: application/json

请求体：
{
    "username": "testuser",
    "password": "123456",
    "email": "user@example.com",
    "verificationCode": "123456"
}

成功响应：
{
    "status": 0,
    "msg": "注册成功",
    "data": null
}

参数验证错误响应：
{
    "status": 1,
    "msg": "参数错误",
    "data": {
        "errors": [
            "用户名长度必须在5-10位之间",
            "密码长度必须在6-15位之间"
        ]
    }
}
```

#### 3️⃣ 重置密码接口
```http
POST /api/auth/reset-password
Content-Type: application/json

请求体：
{
    "email": "user@example.com",
    "newPassword": "newpassword123",
    "verificationCode": "123456"
}

成功响应：
{
    "status": 0,
    "msg": "密码重置成功",
    "data": null
}
```

### ⚠️ 错误码说明
- **PARAM_ERROR**: 参数错误
- **EMAIL_EXIST**: 邮箱已被注册
- **EMAIL_NOT_EXIST**: 邮箱不存在
- **VERIFICATION_CODE_ERROR**: 验证码错误或已过期
- **USERNAME_EXIST**: 用户名已存在
- **SERVER_ERROR**: 服务器内部错误

### 🔐 安全机制
1. **频率限制**: 60秒内同一邮箱同一类型只能发送一次验证码
2. **时效性**: 验证码5分钟后自动过期
3. **一次性**: 验证码使用后自动清除
4. **类型隔离**: 注册和重置密码的验证码相互独立
5. **参数验证**: 严格的邮箱格式、用户名长度、密码强度验证

### 📨 邮件模板
```
主题：【羽毛球系统】注册验证码 / 【羽毛球系统】密码重置验证码

内容：
您好！

您正在进行注册账号操作，验证码为：123456

验证码有效期为5分钟，请及时使用。
如果这不是您的操作，请忽略此邮件。

-----
武汉理工大学羽毛球系统
```

### 🧪 测试命令
```bash
# 发送注册验证码
curl -X POST http://localhost:8080/api/auth/send-code \
-H "Content-Type: application/json" \
-d '{"email":"test@example.com","type":"register"}'

# 注册用户
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"username":"testuser","password":"123456","email":"test@example.com","verificationCode":"123456"}'

# 发送重置密码验证码
curl -X POST http://localhost:8080/api/auth/send-code \
-H "Content-Type: application/json" \
-d '{"email":"test@example.com","type":"reset"}'

# 重置密码
curl -X POST http://localhost:8080/api/auth/reset-password \
-H "Content-Type: application/json" \
-d '{"email":"test@example.com","newPassword":"newpass123","verificationCode":"123456"}'
```