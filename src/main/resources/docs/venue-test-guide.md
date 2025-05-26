# 场地状态管理模块 - 使用说明与测试指南

## 1. 初始化步骤

### 1.1 数据库初始化

**首先执行SQL脚本初始化数据库：**

```bash
# 连接到MySQL数据库
mysql -u your_username -p your_database_name

# 执行初始化脚本
source src/main/resources/sql/venue_init.sql
```

**或者直接复制SQL内容在数据库管理工具中执行**

这个脚本会：
- 创建3张表：`venue`、`venue_schedule`、`special_date_config`
- 初始化9个羽毛球场地数据
- 添加2024年主要节假日配置
- 创建必要的索引

### 1.2 启动应用

```bash
# 启动Spring Boot应用
mvn spring-boot:run
```

启动后定时任务会在每天凌晨1点自动生成时间表。

### 1.3 手动生成时间表（可选）

如果需要立即测试，可以调用API生成时间表：

```bash
# 生成今天的时间表
curl -X POST "http://localhost:8080/api/venue/schedule/generate?date=2024-01-16" \
  -H "Authorization: Bearer your_admin_token"

# 批量生成7天的时间表
curl -X POST "http://localhost:8080/api/venue/schedule/generate/batch?days=7" \
  -H "Authorization: Bearer your_admin_token"
```

## 2. 接口测试

### 2.1 场地管理测试

**获取场地列表（无需权限）**
```bash
curl -X GET "http://localhost:8080/api/venue/list"
```

**获取场地详情**
```bash
curl -X GET "http://localhost:8080/api/venue/1"
```

### 2.2 场地时间表测试

**查看场地预约矩阵图（重要接口）**
```bash
curl -X GET "http://localhost:8080/api/venue/schedule/matrix?scheduleDate=2024-01-16"
```

**查询指定条件的时间表**
```bash
curl -X GET "http://localhost:8080/api/venue/schedule/list?scheduleDate=2024-01-16&venueId=1&bookable=1"
```

**检查时间段可用性**
```bash
curl -X GET "http://localhost:8080/api/venue/schedule/check?venueId=1&date=2024-01-16&startTime=18:00&endTime=19:00"
```

### 2.3 特殊日期配置测试（需要管理员权限）

**创建特殊配置**
```bash
curl -X POST "http://localhost:8080/api/venue/special-config" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_admin_token" \
  -d '{
    "configName": "测试维护日",
    "specialDate": "2024-01-20",
    "configType": 2,
    "affectedVenueIds": "1,2",
    "startTime": "14:00",
    "endTime": "16:00",
    "venueStatus": 4,
    "bookable": 0,
    "description": "1-2号场地下午维护",
    "enabled": 1
  }'
```

**查看特殊配置列表**
```bash
curl -X GET "http://localhost:8080/api/venue/special-config/list" \
  -H "Authorization: Bearer your_admin_token"
```

## 3. 本地测试工具推荐

### 3.1 使用Postman

1. 导入接口集合
2. 设置环境变量：
   - `base_url`: http://localhost:8080
   - `admin_token`: 管理员JWT Token

### 3.2 使用curl脚本

创建测试脚本 `test-venue-api.sh`：

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
ADMIN_TOKEN="your_admin_token_here"

echo "=== 场地模块API测试 ==="

echo "1. 获取场地列表"
curl -s "$BASE_URL/api/venue/list" | jq

echo -e "\n2. 生成今天的时间表"
curl -s -X POST "$BASE_URL/api/venue/schedule/generate?date=$(date +%Y-%m-%d)" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq

echo -e "\n3. 查看场地预约矩阵图"
curl -s "$BASE_URL/api/venue/schedule/matrix?scheduleDate=$(date +%Y-%m-%d)" | jq

echo -e "\n4. 检查时间段可用性"
curl -s "$BASE_URL/api/venue/schedule/check?venueId=1&date=$(date +%Y-%m-%d)&startTime=18:00&endTime=19:00" | jq

echo -e "\n测试完成！"
```

## 4. 前端集成指南

### 4.1 场地预约矩阵图组件

```javascript
// Vue.js 示例
<template>
  <div class="venue-matrix">
    <table>
      <thead>
        <tr>
          <th>场地</th>
          <th v-for="slot in timeSlots" :key="slot.startTime">
            {{slot.startTime}}-{{slot.endTime}}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="venue in venues" :key="venue.id">
          <td>{{venue.name}}</td>
          <td v-for="slot in timeSlots" :key="slot.startTime"
              :class="getSlotClass(venue.id, slot.startTime)"
              @click="selectSlot(venue.id, slot.startTime)">
            {{getSlotStatus(venue.id, slot.startTime)}}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      venues: [],
      timeSlots: [],
      scheduleMatrix: {}
    }
  },
  async mounted() {
    await this.loadMatrix()
  },
  methods: {
    async loadMatrix() {
      const response = await fetch('/api/venue/schedule/matrix?scheduleDate=' + this.selectedDate)
      const data = await response.json()
      this.venues = data.data.venues
      this.timeSlots = data.data.timeSlots
      this.scheduleMatrix = data.data.scheduleMatrix
    },
    getSlotClass(venueId, startTime) {
      const slot = this.scheduleMatrix[venueId] && this.scheduleMatrix[venueId][startTime]
      if (!slot) return 'venue-maintenance'
      
      if (slot.bookable === 1) return 'venue-available'
      if (slot.status === 2) return 'venue-in-use'
      if (slot.status === 3) return 'venue-booked'
      return 'venue-maintenance'
    },
    getSlotStatus(venueId, startTime) {
      const slot = this.scheduleMatrix[venueId] && this.scheduleMatrix[venueId][startTime]
      return slot ? slot.statusDesc : '维护中'
    },
    selectSlot(venueId, startTime) {
      // 处理时间段选择逻辑
      if (this.getSlotClass(venueId, startTime) === 'venue-available') {
        this.$emit('slot-selected', { venueId, startTime })
      }
    }
  }
}
</script>

<style scoped>
.venue-available { background-color: #52c41a; color: white; cursor: pointer; }
.venue-in-use { background-color: #d9d9d9; color: #666; }
.venue-booked { background-color: #ff4d4f; color: white; }
.venue-maintenance { background-color: #fa8c16; color: white; }
</style>
```

### 4.2 时间段检查工具函数

```javascript
// 检查时间段是否可预约
async function checkTimeSlotAvailable(venueId, date, startTime, endTime) {
  try {
    const response = await fetch(
      `/api/venue/schedule/check?venueId=${venueId}&date=${date}&startTime=${startTime}&endTime=${endTime}`
    )
    const result = await response.json()
    return result.code === 0 && result.data === true
  } catch (error) {
    console.error('检查时间段可用性失败:', error)
    return false
  }
}

// 使用示例
const isAvailable = await checkTimeSlotAvailable(1, '2024-01-16', '18:00', '19:00')
if (isAvailable) {
  // 可以预约
} else {
  // 不可预约
}
```

## 5. 业务规则验证

### 5.1 时间规则测试

**工作日规则验证：**
- 周一到周五 08:00-18:00 应该显示"使用中"，不可预约
- 周一到周五 18:00-21:00 应该显示"空闲中"，可预约

**周末规则验证：**
- 周六日 08:00-21:00 应该显示"空闲中"，可预约

### 5.2 特殊日期配置验证

创建一个节假日配置，检查是否覆盖了默认规则：

```bash
# 创建明天的特殊配置
curl -X POST "http://localhost:8080/api/venue/special-config" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"configName\": \"测试节假日\",
    \"specialDate\": \"$(date -d tomorrow +%Y-%m-%d)\",
    \"configType\": 1,
    \"venueStatus\": 4,
    \"bookable\": 0,
    \"description\": \"测试节假日配置\",
    \"enabled\": 1
  }"

# 生成明天的时间表
curl -X POST "http://localhost:8080/api/venue/schedule/generate?date=$(date -d tomorrow +%Y-%m-%d)" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 查看矩阵图验证配置生效
curl "http://localhost:8080/api/venue/schedule/matrix?scheduleDate=$(date -d tomorrow +%Y-%m-%d)"
```

## 6. 常见问题排查

### 6.1 权限问题

如果管理员接口返回403错误：
1. 检查用户是否已登录
2. 确认用户角色是否为ADMIN
3. 验证JWT Token是否有效

### 6.2 时间表生成问题

如果时间表生成失败：
1. 检查场地表是否有数据
2. 确认日期格式是否正确（yyyy-MM-dd）
3. 查看应用日志排查具体错误

### 6.3 矩阵图数据为空

如果矩阵图查询无数据：
1. 确认查询的日期是否已生成时间表
2. 检查数据库venue_schedule表是否有对应日期的数据
3. 验证场地状态是否正常

### 6.4 特殊配置不生效

如果特殊日期配置没有生效：
1. 确认配置的enabled字段是否为1
2. 检查日期范围是否正确
3. 重新生成对应日期的时间表

## 7. 性能优化建议

### 7.1 数据库优化

1. 定期清理历史时间表数据（超过7天的数据）
2. 为frequently查询字段添加索引
3. 使用Redis缓存热点数据

### 7.2 接口优化

1. 矩阵图接口可以添加缓存
2. 批量操作时使用事务
3. 大量数据查询时使用分页

## 8. 下一步开发

基于此模块，可以继续开发：

1. **预约下单模块**：使用venue_schedule表进行预约
2. **支付模块集成**：创建预约订单后调用支付接口
3. **用户预约管理**：用户查看自己的预约记录
4. **场地使用统计**：基于时间表数据生成统计报表

这个场地状态管理模块为整个预约系统提供了坚实的基础！ 