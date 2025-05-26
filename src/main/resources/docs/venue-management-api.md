# 场地管理API接口文档

## 1. 场地查询接口

### 1.1 获取所有场地列表
- **接口地址**: `GET /api/venue/list`
- **访问权限**: 公开接口
- **请求参数**: 无
- **响应数据**:
```json
{
  "status": 0,
  "message": "SUCCESS",
  "data": [
    {
      "id": 1,
      "name": "羽毛球场1号",
      "description": "标准羽毛球场地，配备专业羽毛球网和照明设备",
      "location": "体育馆1号场地",
      "pricePerHour": 30.00,
      "type": 1,
      "typeDesc": "羽毛球场",
      "status": 1,
      "statusDesc": "空闲中",
      "createTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:00:00"
    }
  ]
}
```

### 1.2 根据状态获取场地列表
- **接口地址**: `GET /api/venue/list/status/{status}`
- **访问权限**: 公开接口
- **请求参数**:
  - status: 场地状态 (1-空闲中 2-使用中 3-已预约 4-维护中)
- **响应数据**: 同1.1

### 1.3 获取场地详情
- **接口地址**: `GET /api/venue/{id}`
- **访问权限**: 公开接口
- **请求参数**:
  - id: 场地ID
- **响应数据**: 单个场地对象

## 2. 场地管理接口（需要管理员权限）

### 2.1 新增场地
- **接口地址**: `POST /api/venue/add`
- **访问权限**: 管理员权限
- **请求头**: 需要携带JWT token
- **请求参数**:
```json
{
  "name": "羽毛球场10号",
  "description": "新增的羽毛球场地",
  "location": "体育馆10号场地",
  "pricePerHour": 35.00,
  "type": 1,
  "status": 1
}
```
- **参数说明**:
  - name: 场地名称（必填）
  - description: 场地描述（可选）
  - location: 场地位置（必填）
  - pricePerHour: 每小时价格（必填，大于0）
  - type: 场地类型（必填，1-羽毛球场）
  - status: 场地状态（可选，默认为1-空闲中）

- **响应数据**:
```json
{
  "status": 0,
  "message": "SUCCESS",
  "data": "场地新增成功"
}
```

### 2.2 更新场地信息
- **接口地址**: `PUT /api/venue/update/{id}`
- **访问权限**: 管理员权限
- **请求头**: 需要携带JWT token
- **请求参数**: 同2.1
- **响应数据**:
```json
{
  "status": 0,
  "message": "SUCCESS",
  "data": "场地信息更新成功"
}
```

### 2.3 更新场地状态
- **接口地址**: `PUT /api/venue/status/{id}?status={status}`
- **访问权限**: 管理员权限
- **请求头**: 需要携带JWT token
- **请求参数**:
  - id: 场地ID（路径参数）
  - status: 新状态（查询参数，1-空闲中 2-使用中 3-已预约 4-维护中）
- **响应数据**:
```json
{
  "status": 0,
  "message": "SUCCESS",
  "data": "场地状态更新成功"
}
```

### 2.4 删除场地
- **接口地址**: `DELETE /api/venue/delete/{id}`
- **访问权限**: 管理员权限
- **请求头**: 需要携带JWT token
- **请求参数**:
  - id: 场地ID
- **响应数据**:
```json
{
  "status": 0,
  "message": "SUCCESS",
  "data": "场地删除成功"
}
```

## 3. 场地状态说明

| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 1 | 空闲中 | 场地可用，可以预约 |
| 2 | 使用中 | 场地正在使用，不可预约 |
| 3 | 已预约 | 场地已被预约，等待使用 |
| 4 | 维护中 | 场地维护，暂停使用 |

## 4. 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 20001 | 场地不存在 | 指定的场地ID不存在 |
| 20002 | 场地状态无效 | 传入的状态值不在有效范围内 |
| 10001 | 参数错误 | 请求参数格式不正确 |
| 10002 | 未授权 | 需要登录或权限不足 |
| 10004 | 服务器错误 | 系统内部错误 |

## 5. 使用示例

### 5.1 新增场地示例
```bash
curl -X POST http://localhost:8080/api/venue/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_jwt_token" \
  -d '{
    "name": "羽毛球场10号", 
    "description": "扩建的新场地",
    "location": "体育馆10号场地",
    "pricePerHour": 35.00,
    "type": 1
  }'
```

### 5.2 更新场地状态示例
```bash
curl -X PUT "http://localhost:8080/api/venue/status/10?status=4" \
  -H "Authorization: Bearer your_jwt_token"
```

### 5.3 删除场地示例
```bash
curl -X DELETE http://localhost:8080/api/venue/delete/10 \
  -H "Authorization: Bearer your_jwt_token"
```

## 6. 注意事项

1. **权限控制**: 所有管理接口都需要管理员权限，需要在请求头中携带有效的JWT token
2. **参数验证**: 新增和更新接口会进行参数验证，确保数据的完整性和有效性
3. **事务控制**: 所有修改操作都有事务控制，确保数据一致性
4. **删除限制**: 删除场地时会检查是否有未完成的预约（待实现）
5. **状态管理**: 场地状态会影响预约功能，维护中的场地不能被预约
6. **扩容支持**: 支持动态新增场地，适应体育馆扩建需求 