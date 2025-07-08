# WUT羽毛球管理系统 - 后端

## 项目简介

这是一个基于Spring Boot开发的羽毛球场馆管理系统后端服务，为武汉理工大学羽毛球场馆提供完整的管理解决方案。

## 主要功能模块

### 🏸 核心功能

- **用户管理系统** - 用户注册、登录、权限管理
- **场馆预订系统** - 羽毛球场地预订、时间管理
- **支付系统** - 支持微信支付和支付宝支付
- **商城系统** - 羽毛球用品销售、购物车、订单管理
- **论坛系统** - 用户交流、帖子发布、点赞评论
- **通知公告** - 系统公告发布管理
- **数据分析** - 场馆使用情况、收入统计分析

### 🔧 管理功能

- **场馆配置** - 场地信息管理、特殊日期配置
- **商品管理** - 商品上架、规格管理、库存管理
- **订单管理** - 预订订单、购物订单处理
- **用户管理** - 用户信息管理、权限分配

## 技术栈

### 后端框架

- **Spring Boot**: 2.1.7.RELEASE
- **Spring Security**: 用户认证和授权
- **MyBatis**: 2.1.0 数据持久化框架
- **PageHelper**: 1.2.13 分页插件

### 数据库

- **MySQL**: 主数据库
- **Redis**: 缓存和会话存储

### 消息队列

- **RabbitMQ**: 异步消息处理

### 第三方服务

- **阿里云OSS**: 文件存储服务
- **邮件服务**: 126邮箱SMTP
- **支付服务**: 微信支付、支付宝支付

### 开发工具

- **Java**: 1.8
- **Maven**: 项目构建工具
- **Lombok**: 简化Java代码
- **JWT**: 0.9.1 token认证

## 环境要求

### 基础环境

- **JDK**: 1.8+
- **Maven**: 3.6+
- **MySQL**: 5.7+ 或 8.0+
- **Redis**: 6.0+
- **RabbitMQ**: 3.8+

### 开发工具推荐

- **IDE**: IntelliJ IDEA / Eclipse
- **数据库工具**: Navicat / DataGrip
- **API测试**: Postman / Apifox

## 快速开始

### 1. 克隆项目

```bash
git clone [您的仓库地址]
cd 后端部署
```

### 2. 数据库配置

#### 2.1 创建数据库

```sql
CREATE DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.2 导入数据库脚本

按顺序执行以下SQL文件（位于 `数据库文件/` 目录）：

```bash
# 1. 基础表结构
mysql -u root -p test < 数据库文件/schema.sql

# 2. 场馆相关表
mysql -u root -p test < 数据库文件/venue_init.sql
mysql -u root -p test < 数据库文件/reservation_order.sql

# 3. 商城相关表
mysql -u root -p test < 数据库文件/product.sql
mysql -u root -p test < 数据库文件/mall_order.sql
mysql -u root -p test < 数据库文件/pay_info.sql

# 4. 论坛相关表
mysql -u root -p test < 数据库文件/forum_tables.sql
mysql -u root -p test < 数据库文件/post_like_table.sql
mysql -u root -p test < 数据库文件/reply_like_table.sql

# 5. 其他功能表
mysql -u root -p test < 数据库文件/notice.sql
mysql -u root -p test < 数据库文件/user_detail.sql

# 6. 测试数据（可选）
mysql -u root -p test < 数据库文件/test_data_reservation.sql
mysql -u root -p test < 数据库文件/forum_test_data.sql
mysql -u root -p test < 数据库文件/mall_test_data.sql
```

### 3. 配置application.yml

创建 `src/main/resources/application.yml` 文件，参考以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 您的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: 127.0.0.1
    port: 6379
    database: 0
    timeout: 5000
    password: 您的Redis密码（如果有）
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  
  mail:
    host: smtp.126.com
    port: 25
    username: 您的邮箱@126.com
    password: 您的邮箱授权码
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wuli.badminton.pojo

jwt:
  secret: "您的JWT密钥"
  expiration: 86400000 # 24小时

# 阿里云OSS配置（需要申请自己的账号）
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    accessKeyId: 您的AccessKeyId
    accessKeySecret: 您的AccessKeySecret
    bucketName: 您的存储桶名称
    domain: https://您的存储桶名称.oss-cn-hangzhou.aliyuncs.com/

# 支付配置（需要申请自己的商户号）
pay:
  return-url: http://localhost:8080/orders
  wx:
    app-id: 您的微信AppId
    mch-id: 您的微信商户号
    mch-key: 您的微信商户密钥
    notify-url: 您的回调地址/pay/notify
    return-url: http://localhost:8080/orders
  alipay:
    app-id: 您的支付宝AppId
    private-key: 您的应用私钥
    public-key: 支付宝公钥
    notify-url: 您的回调地址/pay/notify
    return-url: http://localhost:8080/orders
```

### 4. 启动依赖服务

#### 4.1 启动MySQL

```bash
# Windows
net start mysql

# Linux/Mac
sudo systemctl start mysql
```

#### 4.2 启动Redis

```bash
# Windows
redis-server

# Linux/Mac
sudo systemctl start redis
```

#### 4.3 启动RabbitMQ

```bash
# Windows
rabbitmq-server

# Linux/Mac
sudo systemctl start rabbitmq-server
```

### 5. 编译和运行项目

#### 5.1 Maven编译

```bash
mvn clean compile
```

#### 5.2 运行项目

```bash
# 方式1: 使用Maven
mvn spring-boot:run

# 方式2: 使用jar包
mvn clean package
java -jar target/badminton.jar

# 方式3: 在IDE中运行
# 直接运行 BadmintonApplication.java 主类
```

### 6. 验证启动

项目启动成功后，可以通过以下方式验证：

- **服务器地址**: http://localhost:8080
- **健康检查**: 查看控制台日志确认各模块启动成功
- **数据库连接**: 确认数据库连接正常
- **Redis连接**: 确认缓存服务正常

## 默认账号

系统默认创建了管理员账号：

- **用户名**: admin
- **密码**: admin123
- **角色**: 管理员

## 项目结构

```
src/main/java/com/wuli/badminton/
├── config/          # 配置类
├── controller/      # 控制器层
├── dao/            # 数据访问层
├── dto/            # 数据传输对象
├── enums/          # 枚举类
├── exception/      # 异常处理
├── handler/        # 处理器
├── listener/       # 监听器
├── pojo/           # 实体类
├── security/       # 安全配置
├── service/        # 服务层
├── util/           # 工具类
├── vo/             # 视图对象
└── BadmintonApplication.java  # 启动类
```

## 开发说明

### API接口

- 所有API接口都有统一的返回格式
- 使用JWT进行用户认证
- 支持跨域请求配置

### 数据库

- 使用MyBatis进行数据库操作
- 支持分页查询
- 配置了连接池优化

### 安全

- 使用Spring Security进行安全管理
- 密码使用BCrypt加密
- JWT token过期时间为24小时

## 注意事项

1. **配置文件安全**: `application.yml` 文件包含敏感信息，已被git忽略，需要手动创建
2. **第三方服务**: 阿里云OSS、支付接口需要申请自己的账号和密钥
3. **邮件服务**: 需要配置真实的邮箱账号和授权码
4. **端口占用**: 确保8080端口未被占用
5. **防火墙**: 如需外部访问，请开放相应端口

## 常见问题

### 1. 数据库连接失败

- 检查MySQL服务是否启动
- 确认数据库用户名密码正确
- 验证数据库名称是否存在

### 2. Redis连接失败

- 检查Redis服务是否启动
- 确认Redis密码配置正确

### 3. 项目启动端口冲突

- 修改application.yml中的server.port配置
- 或者停止占用8080端口的其他服务

### 4. 文件上传失败

- 检查阿里云OSS配置是否正确
- 确认存储桶权限设置

## 联系方式

如有问题，请通过以下方式联系：

- 项目Issue
- 邮箱: [您的邮箱]

## 许可证

[根据您的许可证类型填写]
