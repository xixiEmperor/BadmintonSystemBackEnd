# 羽毛球系统商城模块API文档

## 1. 商品分类接口

### 1.1 获取商品分类列表

**接口描述**：获取所有有效的商品分类

**请求URL**：`/api/mall/categories`

**请求方式**：`GET`

**请求参数**：无

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Array | 分类列表 |
| &emsp;id | Integer | 分类ID |
| &emsp;name | String | 分类名称 |
| &emsp;status | Integer | 分类状态：1-正常，2-已废弃 |
| &emsp;sortOrder | Integer | 排序编号 |
| &emsp;createTime | Date | 创建时间 |
| &emsp;updateTime | Date | 更新时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": [
    {
      "id": 100001,
      "name": "球拍",
      "status": 1,
      "sortOrder": 1,
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    },
    {
      "id": 100002,
      "name": "羽毛球",
      "status": 1,
      "sortOrder": 2,
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    }
  ]
}
```

## 2. 商品接口

### 2.1 获取商品列表

**接口描述**：获取商品列表，支持分类筛选、关键词搜索、排序和分页

**请求URL**：`/api/mall/products`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| categoryId | 否 | String | 分类ID，默认值为"all"，表示所有分类 |
| keyword | 否 | String | 搜索关键词，匹配商品名称或副标题 |
| pageNum | 否 | Integer | 页码，默认为1 |
| pageSize | 否 | Integer | 每页数量，默认为10 |
| orderBy | 否 | String | 排序方式：price_asc（价格升序）、price_desc（价格降序）、sales_desc（销量降序） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 商品列表数据 |
| &emsp;pageNum | Integer | 当前页码 |
| &emsp;pageSize | Integer | 每页数量 |
| &emsp;size | Integer | 当前页实际数量 |
| &emsp;total | Long | 总数量 |
| &emsp;list | Array | 商品列表 |
| &emsp;&emsp;id | Integer | 商品ID |
| &emsp;&emsp;categoryId | Integer | 分类ID |
| &emsp;&emsp;categoryName | String | 分类名称 |
| &emsp;&emsp;name | String | 商品名称 |
| &emsp;&emsp;subtitle | String | 商品副标题 |
| &emsp;&emsp;mainImage | String | 主图URL |
| &emsp;&emsp;price | Decimal | 价格 |
| &emsp;&emsp;sales | Integer | 销量 |
| &emsp;&emsp;status | Integer | 状态：1-在售，2-下架 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "size": 2,
    "total": 2,
    "list": [
      {
        "id": 1,
        "categoryId": 100001,
        "categoryName": "球拍",
        "name": "YONEX弓箭11",
        "subtitle": "专业比赛羽毛球拍",
        "mainImage": "http://example.com/images/1.jpg",
        "price": 850.00,
        "sales": 128,
        "status": 1
      },
      {
        "id": 2,
        "categoryId": 100002,
        "categoryName": "羽毛球",
        "name": "YONEX AS-50",
        "subtitle": "耐打比赛用球",
        "mainImage": "http://example.com/images/2.jpg",
        "price": 120.00,
        "sales": 256,
        "status": 1
      }
    ]
  }
}
```

### 2.2 获取商品详情

**接口描述**：获取商品详细信息

**请求URL**：`/api/mall/products/{productId}`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 商品详情 |
| &emsp;id | Integer | 商品ID |
| &emsp;categoryId | Integer | 分类ID |
| &emsp;categoryName | String | 分类名称 |
| &emsp;name | String | 商品名称 |
| &emsp;subtitle | String | 商品副标题 |
| &emsp;mainImage | String | 主图URL |
| &emsp;subImages | String | 子图URL，逗号分隔 |
| &emsp;detail | String | 商品详情，支持HTML |
| &emsp;price | Decimal | 价格 |
| &emsp;stock | Integer | 库存 |
| &emsp;sales | Integer | 销量 |
| &emsp;status | Integer | 状态：1-在售，2-下架 |
| &emsp;hasSpecification | Integer | 是否有规格：0-无规格，1-有规格 |
| &emsp;specifications | Array | 商品规格列表，hasSpecification=1时返回 |
| &emsp;&emsp;id | Integer | 规格ID |
| &emsp;&emsp;specifications | Object | 规格信息，如{"color":"红色","size":"S"} |
| &emsp;&emsp;priceAdjustment | Decimal | 价格调整 |
| &emsp;&emsp;stock | Integer | 该规格库存 |
| &emsp;specOptions | Object | 规格选项，如{"color":["红色","蓝色"],"size":["S","M","L"]} |
| &emsp;createTime | Date | 创建时间 |
| &emsp;updateTime | Date | 更新时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "categoryId": 100001,
    "categoryName": "球拍",
    "name": "YONEX弓箭11",
    "subtitle": "专业比赛羽毛球拍",
    "mainImage": "http://example.com/images/1.jpg",
    "subImages": "http://example.com/images/1_1.jpg,http://example.com/images/1_2.jpg",
    "detail": "<p>商品详情，支持HTML</p>",
    "price": 850.00,
    "stock": 100,
    "sales": 128,
    "status": 1,
    "hasSpecification": 1,
    "specifications": [
      {
        "id": 1,
        "specifications": {"color": "红色", "size": "S"},
        "priceAdjustment": 0.00,
        "stock": 30
      },
      {
        "id": 2,
        "specifications": {"color": "蓝色", "size": "M"},
        "priceAdjustment": 10.00,
        "stock": 20
      }
    ],
    "specOptions": {
      "color": ["红色", "蓝色", "黑色"],
      "size": ["S", "M", "L"]
    },
    "createTime": "2023-05-10 12:00:00",
    "updateTime": "2023-05-10 12:00:00"
  }
}
```

### 2.3 上传商品图片

**接口描述**：上传商品图片（需要管理员权限）

**请求URL**：`/api/mall/products/upload`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| mainImage | 是 | File | 商品主图 |
| subImages | 否 | File[] | 商品子图，可多个 |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | String | 上传成功的图片JSON字符串，包含图片URL |

**响应示例**：

```json
{
  "status": 0,
  "msg": "上传成功",
  "data": "{\"mainImage\":\"http://example.com/images/1.jpg\",\"subImages\":[\"http://example.com/images/1_1.jpg\",\"http://example.com/images/1_2.jpg\"]}"
}
```

### 2.4 添加商品

**接口描述**：添加新商品（需要管理员权限）

**请求URL**：`/api/mall/products`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| categoryId | 是 | Integer | 分类ID |
| name | 是 | String | 商品名称 |
| subtitle | 否 | String | 商品副标题 |
| mainImage | 是 | String | 主图URL，通过上传图片接口获取 |
| subImages | 否 | String | 子图URL，通过上传图片接口获取，多张图片用逗号分隔 |
| detail | 否 | String | 商品详情，支持HTML |
| price | 是 | Decimal | 价格，必须大于0 |
| stock | 是 | Integer | 库存，必须大于等于0 |
| status | 否 | Integer | 状态：1-在售，2-下架，默认为1 |

**请求示例**：

```json
{
  "categoryId": 100001,
  "name": "YONEX弓箭11",
  "subtitle": "专业比赛羽毛球拍",
  "mainImage": "http://example.com/images/1.jpg",
  "subImages": "http://example.com/images/1_1.jpg,http://example.com/images/1_2.jpg",
  "detail": "<p>商品详情，支持HTML</p>",
  "price": 850.00,
  "stock": 100,
  "status": 1
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 返回数据 |
| &emsp;productId | Integer | 新添加的商品ID |

**响应示例**：

```json
{
  "status": 0,
  "msg": "添加商品成功",
  "data": {
    "productId": 1
  }
}
```

### 2.5 更新商品

**接口描述**：更新商品信息（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |
| categoryId | 否 | Integer | 分类ID |
| name | 否 | String | 商品名称 |
| subtitle | 否 | String | 商品副标题 |
| mainImage | 否 | String | 主图URL |
| subImages | 否 | String | 子图URL，多张图片用逗号分隔 |
| detail | 否 | String | 商品详情，支持HTML |
| price | 否 | Decimal | 价格，必须大于0 |
| stock | 否 | Integer | 库存，必须大于等于0 |
| status | 否 | Integer | 状态：1-在售，2-下架 |

**请求示例**：

```json
{
  "name": "YONEX弓箭11 Pro",
  "subtitle": "专业比赛羽毛球拍升级版",
  "price": 899.00
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "更新商品成功",
  "data": null
}
```

### 2.6 商品上架

**接口描述**：将商品状态改为在售（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}/on_sale`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "上架商品成功",
  "data": null
}
```

### 2.7 商品下架

**接口描述**：将商品状态改为下架（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}/off_sale`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "下架商品成功",
  "data": null
}
```

### 2.8 删除商品

**接口描述**：删除商品（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}`

**请求方式**：`DELETE`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "删除商品成功",
  "data": null
}
```

### 2.9 更新商品库存

**接口描述**：更新商品库存（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}/stock`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |
| stock | 是 | Integer | 新的库存数量，必须大于等于0 |

**请求示例**：

```json
{
  "stock": 50
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "更新库存成功",
  "data": null
}
```

## 3. 商品规格接口

### 3.1 获取商品规格列表

**接口描述**：获取商品的所有规格信息

**请求URL**：`/api/mall/products/{productId}/specifications`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Array | 规格列表 |
| &emsp;id | Integer | 规格ID |
| &emsp;productId | Integer | 商品ID |
| &emsp;specifications | Object | 规格信息，如{"color":"红色","size":"S"} |
| &emsp;priceAdjustment | Decimal | 价格调整，可以为负数表示折扣 |
| &emsp;stock | Integer | 该规格库存 |
| &emsp;sales | Integer | 该规格销量 |
| &emsp;status | Integer | 状态：1-正常，0-禁用 |
| &emsp;createTime | Date | 创建时间 |
| &emsp;updateTime | Date | 更新时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "specifications": {"color": "红色", "size": "S"},
      "priceAdjustment": 0.00,
      "stock": 30,
      "sales": 10,
      "status": 1,
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    },
    {
      "id": 2,
      "productId": 1,
      "specifications": {"color": "蓝色", "size": "M"},
      "priceAdjustment": 10.00,
      "stock": 20,
      "sales": 5,
      "status": 1,
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    }
  ]
}
```

### 3.2 根据规格条件获取特定商品规格

**接口描述**：根据规格条件（如颜色、尺寸等）获取匹配的商品规格信息

**请求URL**：`/api/mall/products/{productId}/specification`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |
| specifications | 是 | Object | 规格条件，如{"color":"红色","size":"S"} |

**请求示例**：

```json
{
  "color": "红色",
  "size": "S"
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 规格信息 |
| &emsp;id | Integer | 规格ID |
| &emsp;productId | Integer | 商品ID |
| &emsp;specifications | Object | 规格信息，如{"color":"红色","size":"S"} |
| &emsp;priceAdjustment | Decimal | 价格调整 |
| &emsp;stock | Integer | 该规格库存 |
| &emsp;sales | Integer | 该规格销量 |
| &emsp;status | Integer | 状态 |
| &emsp;createTime | Date | 创建时间 |
| &emsp;updateTime | Date | 更新时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "productId": 1,
    "specifications": {"color": "红色", "size": "S"},
    "priceAdjustment": 0.00,
    "stock": 30,
    "sales": 10,
    "status": 1,
    "createTime": "2023-05-10 12:00:00",
    "updateTime": "2023-05-10 12:00:00"
  }
}
```

### 3.3 获取商品规格选项

**接口描述**：获取商品的规格选项，用于前端展示可选规格

**请求URL**：`/api/mall/products/{productId}/spec_options`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Array | 规格选项列表 |
| &emsp;id | Integer | 选项ID |
| &emsp;productId | Integer | 商品ID |
| &emsp;specKey | String | 规格类型，如"color"、"size" |
| &emsp;specValues | Array | 可选值列表，如["红色","蓝色","黑色"] |
| &emsp;createTime | Date | 创建时间 |
| &emsp;updateTime | Date | 更新时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "specKey": "color",
      "specValues": ["红色", "蓝色", "黑色"],
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    },
    {
      "id": 2,
      "productId": 1,
      "specKey": "size",
      "specValues": ["S", "M", "L"],
      "createTime": "2023-05-10 12:00:00",
      "updateTime": "2023-05-10 12:00:00"
    }
  ]
}
```

### 3.4 添加商品规格

**接口描述**：为商品添加规格（需要管理员权限）

**请求URL**：`/api/mall/products/{productId}/specifications`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| productId | 是 | Integer | 商品ID（路径参数） |
| specifications | 是 | Object | 规格信息，如{"color":"红色","size":"S"} |
| priceAdjustment | 是 | Decimal | 价格调整，可以为负数表示折扣 |
| stock | 是 | Integer | 该规格库存，必须大于等于0 |
| status | 否 | Integer | 状态：1-正常，0-禁用，默认为1 |

**请求示例**：

```json
{
  "specifications": {"color": "黑色", "size": "L"},
  "priceAdjustment": 20.00,
  "stock": 15
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 返回数据 |
| &emsp;specificationId | Integer | 新添加的规格ID |

**响应示例**：

```json
{
  "status": 0,
  "msg": "添加商品规格成功",
  "data": {
    "specificationId": 3
  }
}
```

### 3.5 更新商品规格

**接口描述**：更新商品规格信息（需要管理员权限）

**请求URL**：`/api/mall/specifications/{specificationId}`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| specificationId | 是 | Integer | 规格ID（路径参数） |
| specifications | 否 | Object | 规格信息，如{"color":"红色","size":"S"} |
| priceAdjustment | 否 | Decimal | 价格调整 |
| stock | 否 | Integer | 库存，必须大于等于0 |
| status | 否 | Integer | 状态：1-正常，0-禁用 |

**请求示例**：

```json
{
  "priceAdjustment": 15.00,
  "stock": 25
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "更新商品规格成功",
  "data": null
}
```

### 3.6 删除商品规格

**接口描述**：删除商品规格（需要管理员权限）

**请求URL**：`/api/mall/specifications/{specificationId}`

**请求方式**：`DELETE`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| specificationId | 是 | Integer | 规格ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "删除商品规格成功",
  "data": null
}
```

### 3.7 更新商品规格库存

**接口描述**：更新商品规格库存（需要管理员权限）

**请求URL**：`/api/mall/specifications/{specificationId}/stock`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| specificationId | 是 | Integer | 规格ID（路径参数） |
| stock | 是 | Integer | 新的库存数量，必须大于等于0 |

**请求示例**：

```json
{
  "stock": 50
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | null | 无返回数据 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "更新规格库存成功",
  "data": null
}
```

## 3. 错误码说明

| 错误码 | 说明 |
| ------ | ------ |
| 0 | 成功 |
| 401 | 未登录 |
| 4001 | 参数错误，分类不存在 |
| 4002 | 参数错误，排序方式无效 |
| 4003 | 没有权限进行此操作/商品不存在或已下架 |
| 4004 | 参数验证错误（如：主图不能为空，商品名称不能为空等） |
| 5000 | 操作失败 |
| 999 | 服务器错误 |


## 前端实现指南

### 1. 商品展示功能

#### 商品分类展示
1. 使用`GET /api/mall/categories`获取所有有效商品分类
2. 将分类列表渲染为导航菜单或筛选选项
3. 实现示例:
```javascript
// 获取分类列表
axios.get('/api/mall/categories')
  .then(response => {
    if(response.data.status === 0) {
      this.categories = response.data.data;
    }
  })
  .catch(error => {
    console.error('获取分类失败:', error);
  });
```

#### 商品列表展示
1. 使用`GET /api/mall/products`获取商品列表，支持分页、搜索和排序
2. 实现筛选面板，包含分类选择、关键词搜索和排序选项
3. 实现分页控件，显示当前页码和跳转功能
4. 实现示例:
```javascript
// 获取商品列表
function getProductList(params = {}) {
  const defaultParams = {
    categoryId: 'all',
    pageNum: 1,
    pageSize: 10
  };
  const queryParams = {...defaultParams, ...params};
  
  axios.get('/api/mall/products', {params: queryParams})
    .then(response => {
      if(response.data.status === 0) {
        this.products = response.data.data.list;
        this.pagination = {
          pageNum: response.data.data.pageNum,
          pageSize: response.data.data.pageSize,
          total: response.data.data.total
        };
      }
    })
    .catch(error => {
      console.error('获取商品列表失败:', error);
    });
}
```

#### 商品详情展示
1. 使用`GET /api/mall/products/{productId}`获取商品详情
2. 展示商品详细信息，包括图片、价格、库存等
3. 显示商品详情HTML内容（注意安全处理）
4. 实现示例:
```javascript
// 获取商品详情
function getProductDetail(productId) {
  axios.get(`/api/mall/products/${productId}`)
    .then(response => {
      if(response.data.status === 0) {
        this.productDetail = response.data.data;
        // 解析子图列表
        if(this.productDetail.subImages) {
          this.subImageList = this.productDetail.subImages.split(',');
        }
      }
    })
    .catch(error => {
      console.error('获取商品详情失败:', error);
    });
}
```

### 2. 商品管理功能（管理员）

#### 上传商品图片
1. 创建表单上传组件，支持选择主图和多个子图
2. 使用`POST /api/mall/products/upload`上传图片
3. 将返回的图片URL保存用于商品添加/编辑
4. 实现示例:
```javascript
// 上传商品图片
function uploadProductImages(formData) {
  axios.post('/api/mall/products/upload', formData, {
    headers: {'Content-Type': 'multipart/form-data'}
  })
    .then(response => {
      if(response.data.status === 0) {
        // 解析JSON字符串获取图片URL
        const imageData = JSON.parse(response.data.data);
        this.mainImageUrl = imageData.mainImage;
        this.subImageUrls = imageData.subImages.join(',');
      }
    })
    .catch(error => {
      console.error('上传图片失败:', error);
    });
}
```

#### 添加商品
1. 创建商品表单，包含分类选择、名称、价格等字段
2. 集成图片上传功能
3. 使用`POST /api/mall/products`添加商品
4. 实现示例:
```javascript
// 添加商品
function addProduct(productData) {
  axios.post('/api/mall/products', productData)
    .then(response => {
      if(response.data.status === 0) {
        this.$message.success('添加商品成功');
        this.productId = response.data.data.productId;
        // 跳转到商品列表或详情页
      }
    })
    .catch(error => {
      console.error('添加商品失败:', error);
    });
}
```

#### 编辑商品
1. 创建与添加商品相似的表单，但预填充现有商品数据
2. 使用`PUT /api/mall/products/{productId}`更新商品
3. 实现示例:
```javascript
// 更新商品
function updateProduct(productId, productData) {
  axios.put(`/api/mall/products/${productId}`, productData)
    .then(response => {
      if(response.data.status === 0) {
        this.$message.success('更新商品成功');
        // 刷新商品数据或返回列表
      }
    })
    .catch(error => {
      console.error('更新商品失败:', error);
    });
}
```

#### 商品状态管理
1. 实现上架/下架按钮
2. 使用`PUT /api/mall/products/{productId}/on_sale`和`PUT /api/mall/products/{productId}/off_sale`切换状态
3. 实现示例:
```javascript
// 上架商品
function onSaleProduct(productId) {
  axios.put(`/api/mall/products/${productId}/on_sale`)
    .then(response => {
      if(response.data.status === 0) {
        this.$message.success('商品上架成功');
        // 刷新商品状态
      }
    })
    .catch(error => {
      console.error('商品上架失败:', error);
    });
}

// 下架商品
function offSaleProduct(productId) {
  axios.put(`/api/mall/products/${productId}/off_sale`)
    .then(response => {
      if(response.data.status === 0) {
        this.$message.success('商品下架成功');
        // 刷新商品状态
      }
    })
    .catch(error => {
      console.error('商品下架失败:', error);
    });
}
```

### 3. 实现建议

1. **使用组件化开发**：
   - 将商品卡片、分页控件、筛选面板等抽象为可复用组件
   - 使用Vue、React等前端框架实现状态管理

2. **响应式布局**：
   - 使用Grid或Flex布局实现商品列表的响应式显示
   - 针对移动设备优化界面

3. **图片处理**：
   - 实现图片预览放大功能
   - 商品详情页实现图片轮播

4. **表单验证**：
   - 对添加/编辑商品表单进行前端验证
   - 价格必须大于0，库存不能为负数等

5. **权限控制**：
   - 根据用户角色显示/隐藏管理功能
   - 对管理操作进行权限验证

6. **错误处理**：
   - 统一处理API错误响应
   - 友好展示错误信息

7. **缓存优化**：
   - 缓存分类列表等不常变化的数据
   - 使用LocalStorage存储用户偏好（如排序方式、每页显示数量）
