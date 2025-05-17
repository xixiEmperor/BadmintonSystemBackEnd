# 羽毛球系统论坛模块API文档

## 1. 帖子相关接口

### 1.1 获取帖子列表

**接口描述**：获取帖子列表，支持分类筛选、关键词搜索和分页

**请求URL**：`/api/forum/posts`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| page | 否 | Integer | 页码，默认为1 |
| pageSize | 否 | Integer | 每页数量，默认为10 |
| category | 否 | String | 分类代码，不传或"all"表示所有分类 |
| keyword | 否 | String | 搜索关键词，用于标题和内容模糊匹配 |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 帖子分页数据 |
| &emsp;pageNum | Integer | 当前页码 |
| &emsp;pageSize | Integer | 每页数量 |
| &emsp;total | Long | 总数量 |
| &emsp;list | Array | 帖子列表 |
| &emsp;&emsp;id | Long | 帖子ID |
| &emsp;&emsp;title | String | 帖子标题 |
| &emsp;&emsp;author | String | 作者昵称 |
| &emsp;&emsp;avatar | String | 作者头像URL |
| &emsp;&emsp;category | String | 分类名称 |
| &emsp;&emsp;categoryCode | String | 分类代码 |
| &emsp;&emsp;views | Integer | 浏览次数 |
| &emsp;&emsp;replies | Integer | 回复数量 |
| &emsp;&emsp;likes | Integer | 点赞数量 |
| &emsp;&emsp;publishTime | Date | 发布时间 |
| &emsp;&emsp;lastReply | Date | 最后回复时间 |
| &emsp;&emsp;isTop | Boolean | 是否置顶 |
**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "list": [
      {
        "id": 1,
        "title": "求组队打羽毛球",
        "author": "张三",
        "avatar": "/uploads/avatars/user1.jpg",
        "category": "打球组队",
        "categoryCode": "team",
        "views": 120,
        "replies": 5,
        "likes": 10,
        "publishTime": "2023-05-15 14:30:00",
        "lastReply": "2023-05-16 10:25:00"
      },
      {
        "id": 2,
        "title": "分享自己的羽毛球训练经验",
        "author": "李四",
        "avatar": "/uploads/avatars/user2.jpg",
        "category": "经验交流",
        "categoryCode": "exp",
        "views": 85,
        "replies": 3,
        "likes": 8,
        "publishTime": "2023-05-14 09:15:00",
        "lastReply": "2023-05-15 16:42:00"
      }
    ]
  }
}
```

### 1.2 获取帖子详情

**接口描述**：获取帖子详细信息，包括用户是否已点赞

**请求URL**：`/api/forum/posts/detail`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| postId | 是 | Long | 帖子ID |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 帖子详情 |
| &emsp;id | Long | 帖子ID |
| &emsp;title | String | 帖子标题 |
| &emsp;content | String | 帖子内容 |
| &emsp;author | String | 作者昵称 |
| &emsp;authorUsername | String | 作者用户名 |
| &emsp;authorAvatar | String | 作者头像URL |
| &emsp;userId | Long | 作者ID |
| &emsp;category | String | 分类名称 |
| &emsp;categoryCode | String | 分类代码 |
| &emsp;categoryId | Long | 分类ID |
| &emsp;views | Integer | 浏览次数 |
| &emsp;replies | Integer | 回复数量 |
| &emsp;likes | Integer | 点赞数量 |
| &emsp;isLiked | Boolean | 当前用户是否已点赞 |
| &emsp;publishTime | Date | 发布时间 |
| &emsp;lastReply | Date | 最后回复时间 |
| &emsp;&emsp;isTop | Boolean | 是否置顶 |
**响应示例**：

```json
{
  "status": 0,
  "msg": "请求成功",
  "data": {
    "id": 1,
    "title": "求组队打羽毛球",
    "content": "本周六下午两点，在XX体育馆打羽毛球，求3个人组队，有兴趣的请留言。",
    "author": "张三",
    "authorUsername": "zhangsan",
    "authorAvatar": "/uploads/avatars/user1.jpg",
    "userId": 10001,
    "category": "打球组队",
    "categoryCode": "team",
    "categoryId": 1,
    "views": 121,
    "replies": 5,
    "likes": 10,
    "isLiked": true,
    "publishTime": "2023-05-15 14:30:00",
    "lastReply": "2023-05-16 10:25:00"
  }
}
```

### 1.3 创建帖子

**接口描述**：创建新帖子

**请求URL**：`/api/forum/posts/create`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| title | 是 | String | 帖子标题 |
| content | 是 | String | 帖子内容 |
| category | 是 | String | 分类代码 |

**请求示例**：

```json
{
  "title": "求推荐入门级羽毛球拍",
  "content": "本人初学者，想请教各位大神有什么适合入门的羽毛球拍推荐？预算300元以内。",
  "category": "help"
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 返回数据 |
| &emsp;postId | Long | 新创建的帖子ID |

**响应示例**：

```json
{
  "status": 0,
  "msg": "帖子发布成功",
  "data": {
    "postId": 30
  }
}
```

### 1.4 更新帖子

**接口描述**：更新帖子内容（仅帖子作者或管理员可操作）

**请求URL**：`/api/forum/posts/{id}`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |
| title | 否 | String | 更新的标题 |
| content | 否 | String | 更新的内容 |

**请求示例**：

```json
{
  "title": "【已解决】求推荐入门级羽毛球拍",
  "content": "本人初学者，想请教各位大神有什么适合入门的羽毛球拍推荐？预算300元以内。\n\n更新：感谢各位推荐，已入手尤尼克斯NF170。"
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否更新成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": true
}
```

### 1.5 删除帖子

**接口描述**：删除帖子（仅帖子作者或管理员可操作）

**请求URL**：`/api/forum/posts/{id}`

**请求方式**：`DELETE`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否删除成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": true
}
```

### 1.6 帖子点赞

**接口描述**：对帖子进行点赞操作（需登录）

**请求URL**：`/api/forum/posts/{id}/like`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 点赞结果 |
| &emsp;liked | Boolean | 是否点赞成功 |
| &emsp;likes | Integer | 当前帖子点赞总数 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "点赞成功",
  "data": {
    "liked": true,
    "likes": 11
  }
}
```

### 1.7 取消帖子点赞

**接口描述**：取消对帖子的点赞（需登录）

**请求URL**：`/api/forum/posts/{id}/unlike`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 取消点赞结果 |
| &emsp;unliked | Boolean | 是否取消点赞成功 |
| &emsp;likes | Integer | 当前帖子点赞总数 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "已取消点赞",
  "data": {
    "unliked": true,
    "likes": 10
  }
}
```

### 1.8 获取热门帖子

**接口描述**：获取热门帖子列表（根据浏览量、回复数和点赞数综合计算）

**请求URL**：`/api/forum/posts/hot`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| limit | 否 | Integer | 返回数量，默认为5，最大为20 |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Array | 热门帖子列表 |
| &emsp;id | Long | 帖子ID |
| &emsp;title | String | 帖子标题 |
| &emsp;author | String | 作者昵称 |
| &emsp;views | Integer | 浏览数 |
| &emsp;replies | Integer | 回复数 |
| &emsp;likes | Integer | 点赞数 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": [
    {
      "id": 5,
      "title": "羽毛球技术教学：如何正确握拍",
      "author": "王教练",
      "views": 320,
      "replies": 18,
      "likes": 45
    },
    {
      "id": 8,
      "title": "2023年羽毛球世锦赛观赛指南",
      "author": "体育爱好者",
      "views": 278,
      "replies": 24,
      "likes": 36
    }
  ]
}
```
### 1.9 设置帖子置顶状态（管理员）

**接口描述**：设置帖子的置顶状态（仅管理员可操作）

**请求URL**：`/api/forum/posts/{id}/top`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |
| isTop | 是 | Boolean | 是否置顶：true-置顶，false-取消置顶 |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否设置成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "帖子已置顶",
  "data": true
}
```
## 2. 回复相关接口

### 2.1 获取帖子回复列表

**接口描述**：获取指定帖子的回复列表，支持分页和排序

**请求URL**：`/api/forum/posts/{id}/replies`

**请求方式**：`GET`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 帖子ID（路径参数） |
| page | 否 | Integer | 页码，默认为1 |
| pageSize | 否 | Integer | 每页数量，默认为20 |
| orderBy | 否 | String | 排序方式，可选值：`time_asc`（时间升序）、`time_desc`（时间降序，默认）、`likes`（点赞数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 回复分页数据 |
| &emsp;pageNum | Integer | 当前页码 |
| &emsp;pageSize | Integer | 每页数量 |
| &emsp;total | Long | 总数量 |
| &emsp;list | Array | 回复列表 |
| &emsp;&emsp;id | Long | 回复ID |
| &emsp;&emsp;postId | Long | 所属帖子ID |
| &emsp;&emsp;parentId | Long | 父回复ID（若为一级回复则为null） |
| &emsp;&emsp;content | String | 回复内容 |
| &emsp;&emsp;userId | Long | 回复者ID |
| &emsp;&emsp;author | String | 回复者昵称 |
| &emsp;&emsp;authorAvatar | String | 回复者头像URL |
| &emsp;&emsp;likes | Integer | 点赞数 |
| &emsp;&emsp;isLiked | Boolean | 当前用户是否已点赞 |
| &emsp;&emsp;childReplies | Array | 子回复列表（若有的话） |
| &emsp;&emsp;&emsp;id | Long | 子回复ID |
| &emsp;&emsp;&emsp;... | ... | 子回复其他字段（同父回复） |
| &emsp;&emsp;replyTime | Date | 回复时间 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": {
    "pageNum": 1,
    "pageSize": 20,
    "total": 5,
    "list": [
      {
        "id": 101,
        "postId": 1,
        "parentId": null,
        "content": "我可以一起，联系方式：123456",
        "userId": 10002,
        "author": "李四",
        "authorAvatar": "/uploads/avatars/user2.jpg",
        "likes": 3,
        "isLiked": false,
        "childReplies": [
          {
            "id": 105,
            "postId": 1,
            "parentId": 101,
            "content": "我也想一起，请带上我",
            "userId": 10003,
            "author": "王五",
            "authorAvatar": "/uploads/avatars/user3.jpg",
            "likes": 1,
            "isLiked": true,
            "replyTime": "2023-05-15 16:10:00"
          }
        ],
        "replyTime": "2023-05-15 15:30:00"
      },
      {
        "id": 102,
        "postId": 1,
        "parentId": null,
        "content": "我有兴趣，但是时间可能有点问题，可以改到周日吗？",
        "userId": 10004,
        "author": "赵六",
        "authorAvatar": "/uploads/avatars/user4.jpg",
        "likes": 0,
        "isLiked": false,
        "childReplies": [],
        "replyTime": "2023-05-15 16:45:00"
      }
    ]
  }
}
```

### 2.2 发表回复

**接口描述**：发表回复（需登录）

**请求URL**：`/api/forum/replies`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| postId | 是 | Long | 帖子ID |
| parentId | 否 | Long | 父回复ID（如果回复其他回复） |
| content | 是 | String | 回复内容 |

**请求示例**：

```json
{
  "postId": 1,
  "content": "这个周六我有空，可以一起打"
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 回复结果 |
| &emsp;replyId | Long | 新创建的回复ID |

**响应示例**：

```json
{
  "status": 0,
  "msg": "回复成功",
  "data": {
    "replyId": 106
  }
}
```

### 2.3 更新回复

**接口描述**：更新回复内容（仅回复作者或管理员可操作）

**请求URL**：`/api/forum/replies/{id}`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 回复ID（路径参数） |
| content | 是 | String | 更新的内容 |

**请求示例**：

```json
{
  "content": "这个周六我有空，可以一起打，联系方式：13812345678"
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否更新成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": true
}
```

### 2.4 删除回复

**接口描述**：删除回复（仅回复作者或管理员可操作）

**请求URL**：`/api/forum/replies/{id}`

**请求方式**：`DELETE`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 回复ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否删除成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": true
}
```

### 2.5 回复点赞

**接口描述**：对回复进行点赞操作（需登录）

**请求URL**：`/api/forum/replies/{id}/like`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 回复ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 点赞结果 |
| &emsp;liked | Boolean | 是否点赞成功 |
| &emsp;likes | Integer | 当前回复点赞总数 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "点赞成功",
  "data": {
    "liked": true,
    "likes": 4
  }
}
```

### 2.6 取消回复点赞

**接口描述**：取消对回复的点赞（需登录）

**请求URL**：`/api/forum/replies/{id}/unlike`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 回复ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 取消点赞结果 |
| &emsp;unliked | Boolean | 是否取消点赞成功 |
| &emsp;likes | Integer | 当前回复点赞总数 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "已取消点赞",
  "data": {
    "unliked": true,
    "likes": 3
  }
}
```

## 3. 分类相关接口   //暂不需实现

### 3.1 获取所有分类

**接口描述**：获取所有帖子分类列表

**请求URL**：`/api/forum/categories`

**请求方式**：`GET`

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Array | 分类列表 |
| &emsp;id | Long | 分类ID |
| &emsp;name | String | 分类名称 |
| &emsp;code | String | 分类代码 |
| &emsp;description | String | 分类描述 |
| &emsp;icon | String | 分类图标 |
| &emsp;sort | Integer | 排序值 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "打球组队",
      "code": "team",
      "description": "羽毛球活动组队、约球",
      "icon": "team-icon.png",
      "sort": 1
    },
    {
      "id": 2,
      "name": "经验交流",
      "code": "exp",
      "description": "分享羽毛球技术、心得体会",
      "icon": "exp-icon.png",
      "sort": 2
    },
    {
      "id": 3,
      "name": "装备交流",
      "code": "equip",
      "description": "羽毛球装备讨论、推荐",
      "icon": "equip-icon.png",
      "sort": 3
    },
    {
      "id": 4,
      "name": "求助问答",
      "code": "help",
      "description": "羽毛球相关问题求助",
      "icon": "help-icon.png",
      "sort": 4
    }
  ]
}
```

### 3.2 添加分类（管理员）

**接口描述**：添加新的帖子分类（仅管理员可操作）

**请求URL**：`/api/forum/categories`

**请求方式**：`POST`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| name | 是 | String | 分类名称 |
| code | 是 | String | 分类代码（唯一，字母和数字） |
| description | 否 | String | 分类描述 |
| icon | 否 | String | 分类图标 |
| sort | 否 | Integer | 排序值（越小越靠前） |

**请求示例**：

```json
{
  "name": "赛事资讯",
  "code": "news",
  "description": "羽毛球赛事信息、观赛指南",
  "icon": "news-icon.png",
  "sort": 5
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Object | 新创建的分类 |
| &emsp;id | Long | 分类ID |
| &emsp;name | String | 分类名称 |
| &emsp;code | String | 分类代码 |
| &emsp;description | String | 分类描述 |
| &emsp;icon | String | 分类图标 |
| &emsp;sort | Integer | 排序值 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "分类添加成功",
  "data": {
    "id": 5,
    "name": "赛事资讯",
    "code": "news",
    "description": "羽毛球赛事信息、观赛指南",
    "icon": "news-icon.png",
    "sort": 5
  }
}
```

### 3.3 更新分类（管理员）

**接口描述**：更新帖子分类信息（仅管理员可操作）

**请求URL**：`/api/forum/categories/{id}`

**请求方式**：`PUT`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 分类ID（路径参数） |
| name | 否 | String | 分类名称 |
| description | 否 | String | 分类描述 |
| icon | 否 | String | 分类图标 |
| sort | 否 | Integer | 排序值 |

**请求示例**：

```json
{
  "name": "赛事新闻",
  "description": "羽毛球赛事信息、比赛结果与观赛指南",
  "sort": 4
}
```

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否更新成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "分类更新成功",
  "data": true
}
```

### 3.4 删除分类（管理员）

**接口描述**：删除帖子分类（仅管理员可操作）

**请求URL**：`/api/forum/categories/{id}`

**请求方式**：`DELETE`

**请求参数**：

| 参数名 | 必选 | 类型 | 说明 |
| ------ | ------ | ------ | ------ |
| id | 是 | Long | 分类ID（路径参数） |

**响应参数**：

| 参数名 | 类型 | 说明 |
| ------ | ------ | ------ |
| status | Integer | 响应状态码，0表示成功 |
| msg | String | 响应消息 |
| data | Boolean | 是否删除成功 |

**响应示例**：

```json
{
  "status": 0,
  "msg": "分类删除成功",
  "data": true
}
```

## 4. 前端实现指南

### 4.1 论坛模块架构

论坛模块前端实现应采用组件化设计，主要包含以下页面/组件：

1. **论坛首页**：显示帖子列表、分类、热门帖子等
2. **帖子详情页**：展示帖子内容和回复列表
3. **发帖页面**：用于创建新帖子
4. **编辑帖子页面**：用于编辑已有帖子
5. **管理员分类管理页面**：用于管理帖子分类

#### 建议的项目结构：

```
/src
  /views
    /forum
      ForumHome.vue       # 论坛首页
      PostDetail.vue      # 帖子详情页
      PostCreate.vue      # 发帖页面
      PostEdit.vue        # 编辑帖子页面
      CategoryManage.vue  # 分类管理页面（管理员）
  /components
    /forum
      PostList.vue        # 帖子列表组件
      PostItem.vue        # 帖子项组件
      CategorySelector.vue # 分类选择器
      ReplyList.vue       # 回复列表组件
      ReplyItem.vue       # 回复项组件
      ReplyEditor.vue     # 回复编辑器组件
      HotPostList.vue     # 热门帖子列表组件
  /api
    forum.js              # 论坛相关API请求
  /store
    /modules
      forum.js            # 论坛相关状态管理
```

### 4.2 API封装

在 `/api/forum.js` 中封装后端API请求：

```javascript
import request from '@/utils/request'

// 帖子相关API
export function getPostList(params) {
  return request({
    url: '/api/forum/posts',
    method: 'get',
    params
  })
}

export function getPostDetail(postId) {
  return request({
    url: '/api/forum/posts/detail',
    method: 'get',
    params: { postId }
  })
}

export function createPost(data) {
  return request({
    url: '/api/forum/posts/create',
    method: 'post',
    data
  })
}

export function updatePost(id, data) {
  return request({
    url: `/api/forum/posts/${id}`,
    method: 'put',
    data
  })
}

export function deletePost(id) {
  return request({
    url: `/api/forum/posts/${id}`,
    method: 'delete'
  })
}

export function likePost(id) {
  return request({
    url: `/api/forum/posts/${id}/like`,
    method: 'post'
  })
}

export function unlikePost(id) {
  return request({
    url: `/api/forum/posts/${id}/unlike`,
    method: 'post'
  })
}

export function getHotPosts(limit) {
  return request({
    url: '/api/forum/posts/hot',
    method: 'get',
    params: { limit }
  })
}

// 回复相关API
export function getPostReplies(postId, params) {
  return request({
    url: `/api/forum/posts/${postId}/replies`,
    method: 'get',
    params
  })
}

export function createReply(data) {
  return request({
    url: '/api/forum/replies',
    method: 'post',
    data
  })
}

export function updateReply(id, data) {
  return request({
    url: `/api/forum/replies/${id}`,
    method: 'put',
    data
  })
}

export function deleteReply(id) {
  return request({
    url: `/api/forum/replies/${id}`,
    method: 'delete'
  })
}

export function likeReply(id) {
  return request({
    url: `/api/forum/replies/${id}/like`,
    method: 'post'
  })
}

export function unlikeReply(id) {
  return request({
    url: `/api/forum/replies/${id}/unlike`,
    method: 'post'
  })
}

// 分类相关API
export function getCategories() {
  return request({
    url: '/api/forum/categories',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/api/forum/categories',
    method: 'post',
    data
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/api/forum/categories/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/api/forum/categories/${id}`,
    method: 'delete'
  })
}
```

### 4.3 主要组件实现

#### 4.3.1 论坛首页 (ForumHome.vue)

```vue
<template>
  <div class="forum-home">
    <div class="forum-header">
      <h1>羽毛球论坛</h1>
      <el-button type="primary" @click="$router.push('/forum/post/create')" v-if="isLoggedIn">发布帖子</el-button>
    </div>
    
    <div class="forum-container">
      <div class="forum-main">
        <!-- 分类选择器 -->
        <div class="category-bar">
          <el-radio-group v-model="currentCategory" @change="handleCategoryChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button :label="item.code" v-for="item in categories" :key="item.id">
              {{ item.name }}
            </el-radio-button>
          </el-radio-group>
          
          <div class="search-box">
            <el-input placeholder="搜索帖子" v-model="keyword" clearable @keyup.enter="handleSearch">
              <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
            </el-input>
          </div>
        </div>
        
        <!-- 帖子列表 -->
        <post-list
          :posts="posts"
          :loading="loading"
          :total="total"
          :page.sync="page"
          :pageSize.sync="pageSize"
          @page-change="fetchPosts"
        />
      </div>
      
      <div class="forum-sidebar">
        <!-- 热门帖子 -->
        <hot-post-list />
      </div>
    </div>
  </div>
</template>

<script>
import { getPostList } from '@/api/forum'
import { getCategories } from '@/api/forum'
import { mapGetters } from 'vuex'
import PostList from '@/components/forum/PostList'
import HotPostList from '@/components/forum/HotPostList'

export default {
  name: 'ForumHome',
  components: {
    PostList,
    HotPostList
  },
  data() {
    return {
      posts: [],
      categories: [],
      loading: false,
      currentCategory: 'all',
      keyword: '',
      page: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.fetchCategories()
    this.fetchPosts()
  },
  methods: {
    async fetchCategories() {
      try {
        const res = await getCategories()
        if (res.status === 0) {
          this.categories = res.data
        }
      } catch (error) {
        console.error('获取分类失败', error)
        this.$message.error('获取分类失败')
      }
    },
    async fetchPosts() {
      this.loading = true
      try {
        const params = {
          page: this.page,
          pageSize: this.pageSize
        }
        
        if (this.currentCategory !== 'all') {
          params.category = this.currentCategory
        }
        
        if (this.keyword) {
          params.keyword = this.keyword
        }
        
        const res = await getPostList(params)
        if (res.status === 0) {
          this.posts = res.data.list
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取帖子列表失败', error)
        this.$message.error('获取帖子列表失败')
      } finally {
        this.loading = false
      }
    },
    handleCategoryChange() {
      this.page = 1
      this.fetchPosts()
    },
    handleSearch() {
      this.page = 1
      this.fetchPosts()
    }
  }
}
</script>

<style scoped>
.forum-home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forum-container {
  display: flex;
  gap: 20px;
}

.forum-main {
  flex: 1;
}

.forum-sidebar {
  width: 300px;
}

.category-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-box {
  width: 250px;
}
</style>
```

#### 4.3.2 帖子列表组件 (PostList.vue)

```vue
<template>
  <div class="post-list">
    <el-card v-if="loading">
      <div v-for="i in 5" :key="i" class="post-item-skeleton">
        <el-skeleton :rows="3" animated />
      </div>
    </el-card>
    
    <el-card v-else-if="posts.length === 0" class="empty-tip">
      暂无帖子
    </el-card>
    
    <el-card v-else class="post-list-card">
      <div v-for="post in posts" :key="post.id" class="post-item" @click="viewPost(post.id)">
        <div class="post-author">
          <el-avatar :src="post.avatar" :size="40">{{ post.author.substring(0, 1) }}</el-avatar>
          <span class="author-name">{{ post.author }}</span>
        </div>
        
        <div class="post-content">
          <h3 class="post-title">{{ post.title }}</h3>
          <div class="post-info">
            <el-tag size="mini" type="info">{{ post.category }}</el-tag>
            <span class="post-date">{{ formatDate(post.publishTime) }}</span>
            <div class="post-stats">
              <span><i class="el-icon-view"></i> {{ post.views }}</span>
              <span><i class="el-icon-chat-line-square"></i> {{ post.replies }}</span>
              <span><i class="el-icon-star-off"></i> {{ post.likes }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page.sync="currentPage"
          :page-size.sync="pageSize"
          :total="total"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'

export default {
  name: 'PostList',
  props: {
    posts: {
      type: Array,
      default: () => []
    },
    loading: Boolean,
    total: Number,
    page: Number,
    pageSize: Number
  },
  computed: {
    currentPage: {
      get() {
        return this.page
      },
      set(val) {
        this.$emit('update:page', val)
      }
    }
  },
  methods: {
    formatDate,
    viewPost(id) {
      this.$router.push(`/forum/post/${id}`)
    },
    handlePageChange() {
      this.$emit('page-change')
    }
  }
}
</script>

<style scoped>
.post-list-card {
  margin-bottom: 20px;
}

.post-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.post-item:hover {
  background-color: #f5f7fa;
}

.post-item:last-child {
  border-bottom: none;
}

.post-author {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80px;
  margin-right: 15px;
}

.author-name {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 80px;
}

.post-content {
  flex: 1;
}

.post-title {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 500;
}

.post-info {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.post-date {
  margin: 0 15px;
}

.post-stats {
  margin-left: auto;
  display: flex;
}

.post-stats span {
  margin-left: 15px;
}

.post-item-skeleton {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.post-item-skeleton:last-child {
  border-bottom: none;
}

.empty-tip {
  text-align: center;
  padding: 30px;
  color: #909399;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}
</style>
```

#### 4.3.3 热门帖子组件 (HotPostList.vue)

```vue
<template>
  <el-card class="hot-post-list">
    <div slot="header" class="header">
      <span>热门帖子</span>
    </div>
    
    <div v-if="loading">
      <div v-for="i in 5" :key="i" class="hot-post-skeleton">
        <el-skeleton :rows="1" animated />
      </div>
    </div>
    
    <div v-else-if="posts.length === 0" class="empty-tip">
      暂无热门帖子
    </div>
    
    <div v-else>
      <div v-for="post in posts" :key="post.id" class="hot-post-item" @click="viewPost(post.id)">
        <div class="hot-post-title">{{ post.title }}</div>
        <div class="hot-post-info">
          <span class="author">{{ post.author }}</span>
          <div class="stats">
            <span><i class="el-icon-view"></i> {{ post.views }}</span>
            <span><i class="el-icon-chat-line-square"></i> {{ post.replies }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { getHotPosts } from '@/api/forum'

export default {
  name: 'HotPostList',
  data() {
    return {
      posts: [],
      loading: false
    }
  },
  created() {
    this.fetchHotPosts()
  },
  methods: {
    async fetchHotPosts() {
      this.loading = true
      try {
        const res = await getHotPosts(5)
        if (res.status === 0) {
          this.posts = res.data
        }
      } catch (error) {
        console.error('获取热门帖子失败', error)
      } finally {
        this.loading = false
      }
    },
    viewPost(id) {
      this.$router.push(`/forum/post/${id}`)
    }
  }
}
</script>

<style scoped>
.hot-post-list {
  margin-bottom: 20px;
}

.header {
  font-weight: bold;
}

.hot-post-item {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
}

.hot-post-item:last-child {
  border-bottom: none;
}

.hot-post-item:hover .hot-post-title {
  color: #409EFF;
}

.hot-post-title {
  font-size: 14px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s;
}

.hot-post-info {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.stats {
  display: flex;
}

.stats span {
  margin-left: 10px;
}

.hot-post-skeleton {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.hot-post-skeleton:last-child {
  border-bottom: none;
}

.empty-tip {
  text-align: center;
  padding: 15px 0;
  color: #909399;
  font-size: 14px;
}
</style>
```

#### 4.3.4 帖子详情页 (PostDetail.vue)

```vue
<template>
  <div class="post-detail" v-loading="loading">
    <div class="back-link">
      <el-link icon="el-icon-arrow-left" @click="$router.push('/forum')">返回论坛</el-link>
    </div>
    
    <div v-if="post">
      <el-card class="post-detail-card">
        <div class="post-header">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-meta">
            <el-tag size="small">{{ post.category }}</el-tag>
            <span class="publish-time">发布于：{{ formatDate(post.publishTime) }}</span>
            <span class="views"><i class="el-icon-view"></i> {{ post.views }}</span>
          </div>
        </div>
        
        <div class="post-author-info">
          <el-avatar :src="post.authorAvatar" :size="50">{{ post.author.substring(0, 1) }}</el-avatar>
          <div class="author-detail">
            <div class="author-name">{{ post.author }}</div>
            <div class="author-id">@{{ post.authorUsername }}</div>
          </div>
        </div>
        
        <div class="post-content" v-html="formatContent(post.content)"></div>
        
        <div class="post-actions">
          <el-button 
            type="text" 
            :class="{ 'liked': post.isLiked }" 
            @click="toggleLike"
          >
            <i :class="post.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            {{ post.likes }} 点赞
          </el-button>
          
          <div class="action-buttons" v-if="canManagePost">
            <el-button type="text" @click="editPost">编辑</el-button>
            <el-button type="text" @click="deletePost">删除</el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 回复区域 -->
      <div class="reply-section">
        <h3 class="reply-title">
          <span>回复 ({{ post.replies }})</span>
          <div class="reply-sort">
            <el-radio-group v-model="replyOrderBy" size="mini" @change="fetchReplies">
              <el-radio-button label="time_desc">最新</el-radio-button>
              <el-radio-button label="time_asc">最早</el-radio-button>
              <el-radio-button label="likes">点赞数</el-radio-button>
            </el-radio-group>
          </div>
        </h3>
        
        <!-- 回复编辑器 -->
        <div class="reply-editor" v-if="isLoggedIn">
          <el-input
            type="textarea"
            :rows="4"
            placeholder="写下你的回复..."
            v-model="replyContent"
            maxlength="1000"
            show-word-limit
          ></el-input>
          <div class="reply-button">
            <el-button type="primary" @click="submitReply" :loading="submitting">发表回复</el-button>
          </div>
        </div>
        <div class="login-tip" v-else>
          <el-link type="primary" @click="$router.push('/login')">登录</el-link> 后才能回复
        </div>
        
        <!-- 回复列表 -->
        <reply-list
          :replies="replies"
          :loading="repliesLoading"
          :total="repliesTotal"
          :page.sync="repliesPage"
          :pageSize.sync="repliesPageSize"
          @page-change="fetchReplies"
          @reply-to-reply="replyToReply"
          @edit-reply="editReply"
          @delete-reply="deleteReply"
          @like-reply="handleLikeReply"
          @unlike-reply="handleUnlikeReply"
        />
      </div>
    </div>
    
    <el-dialog 
      title="删除确认" 
      :visible.sync="deleteDialogVisible" 
      width="30%"
      :close-on-click-modal="false"
    >
      <span>确定要删除此帖子吗？此操作不可恢复。</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete" :loading="deleting">确定删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getPostDetail, likePost, unlikePost, deletePost } from '@/api/forum'
import { getPostReplies, createReply } from '@/api/forum'
import { formatDate } from '@/utils/date'
import ReplyList from '@/components/forum/ReplyList'

export default {
  name: 'PostDetail',
  components: {
    ReplyList
  },
  data() {
    return {
      loading: false,
      post: null,
      replyContent: '',
      submitting: false,
      deleteDialogVisible: false,
      deleting: false,
      
      // 回复相关
      replies: [],
      repliesLoading: false,
      repliesPage: 1,
      repliesPageSize: 20,
      repliesTotal: 0,
      replyOrderBy: 'time_desc',
      replyToParentId: null,
      replyToUsername: ''
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userId', 'isAdmin']),
    postId() {
      return this.$route.params.id
    },
    canManagePost() {
      return this.isLoggedIn && (this.isAdmin || (this.post && this.post.userId === this.userId))
    },
    replyPlaceholder() {
      return this.replyToUsername 
        ? `回复 @${this.replyToUsername}：` 
        : '写下你的回复...'
    }
  },
  created() {
    this.fetchPostDetail()
    this.fetchReplies()
  },
  methods: {
    formatDate,
    formatContent(content) {
      // 可以添加内容格式化逻辑，如将换行符转换为<br>等
      return content.replace(/\n/g, '<br>')
    },
    async fetchPostDetail() {
      this.loading = true
      try {
        const res = await getPostDetail(this.postId)
        if (res.status === 0) {
          this.post = res.data
        } else {
          this.$message.error(res.msg || '获取帖子详情失败')
        }
      } catch (error) {
        console.error('获取帖子详情失败', error)
        this.$message.error('获取帖子详情失败')
      } finally {
        this.loading = false
      }
    },
    async fetchReplies() {
      this.repliesLoading = true
      try {
        const params = {
          page: this.repliesPage,
          pageSize: this.repliesPageSize,
          orderBy: this.replyOrderBy
        }
        
        const res = await getPostReplies(this.postId, params)
        if (res.status === 0) {
          this.replies = res.data.list
          this.repliesTotal = res.data.total
        } else {
          this.$message.error(res.msg || '获取回复列表失败')
        }
      } catch (error) {
        console.error('获取回复列表失败', error)
        this.$message.error('获取回复列表失败')
      } finally {
        this.repliesLoading = false
      }
    },
    async toggleLike() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        if (this.post.isLiked) {
          const res = await unlikePost(this.postId)
          if (res.status === 0) {
            this.post.isLiked = false
            this.post.likes = res.data.likes
            this.$message.success('已取消点赞')
          } else {
            this.$message.error(res.msg || '取消点赞失败')
          }
        } else {
          const res = await likePost(this.postId)
          if (res.status === 0) {
            this.post.isLiked = true
            this.post.likes = res.data.likes
            this.$message.success('点赞成功')
          } else {
            this.$message.error(res.msg || '点赞失败')
          }
        }
      } catch (error) {
        console.error('操作失败', error)
        this.$message.error('操作失败')
      }
    },
    async submitReply() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      if (!this.replyContent.trim()) {
        this.$message.warning('回复内容不能为空')
        return
      }
      
      this.submitting = true
      try {
        const data = {
          postId: this.postId,
          content: this.replyContent
        }
        
        if (this.replyToParentId) {
          data.parentId = this.replyToParentId
        }
        
        const res = await createReply(data)
        if (res.status === 0) {
          this.$message.success('回复成功')
          this.replyContent = ''
          this.replyToParentId = null
          this.replyToUsername = ''
          
          // 刷新回复列表
          await this.fetchReplies()
          // 刷新帖子详情以更新回复计数
          await this.fetchPostDetail()
        } else {
          this.$message.error(res.msg || '回复失败')
        }
      } catch (error) {
        console.error('回复失败', error)
        this.$message.error('回复失败')
      } finally {
        this.submitting = false
      }
    },
    editPost() {
      this.$router.push(`/forum/post/edit/${this.postId}`)
    },
    deletePost() {
      this.deleteDialogVisible = true
    },
    async confirmDelete() {
      this.deleting = true
      try {
        const res = await deletePost(this.postId)
        if (res.status === 0) {
          this.$message.success('帖子已删除')
          this.deleteDialogVisible = false
          this.$router.push('/forum')
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        console.error('删除帖子失败', error)
        this.$message.error('删除帖子失败')
      } finally {
        this.deleting = false
      }
    },
    replyToReply(replyId, username) {
      this.replyToParentId = replyId
      this.replyToUsername = username
      this.replyContent = `@${username} `
      // 滚动到回复框
      this.$nextTick(() => {
        document.querySelector('.reply-editor textarea').focus()
      })
    },
    editReply(replyId) {
      // 实现回复编辑功能
    },
    deleteReply(replyId) {
      // 实现回复删除功能
    },
    handleLikeReply(replyId) {
      // 实现回复点赞功能
    },
    handleUnlikeReply(replyId) {
      // 实现取消回复点赞功能
    }
  }
}
</script>

<style scoped>
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-link {
  margin-bottom: 15px;
}

.post-detail-card {
  margin-bottom: 20px;
}

.post-header {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.post-title {
  font-size: 22px;
  margin: 0 0 15px;
}

.post-meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.publish-time,
.views {
  margin-left: 15px;
}

.post-author-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.author-detail {
  margin-left: 15px;
}

.author-name {
  font-size: 16px;
  font-weight: 500;
}

.author-id {
  font-size: 13px;
  color: #909399;
}

.post-content {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 20px;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.liked {
  color: #409EFF;
}

.reply-section {
  margin-top: 30px;
}

.reply-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.reply-editor {
  margin-bottom: 30px;
}

.reply-button {
  margin-top: 10px;
  text-align: right;
}

.login-tip {
  text-align: center;
  margin-bottom: 30px;
  color: #909399;
}
</style>
```

#### 4.3.5 分类管理页面 (CategoryManage.vue)

```vue
<template>
  <div class="category-manage">
    <el-card>
      <div slot="header">
        <span>论坛分类管理</span>
        <el-button 
          type="primary" 
          size="small" 
          style="float: right; margin-top: -5px;"
          @click="showAddDialog"
        >
          添加分类
        </el-button>
      </div>
      
      <el-table
        :data="categories"
        border
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
        ></el-table-column>
        
        <el-table-column
          prop="name"
          label="分类名称"
          width="150"
        ></el-table-column>
        
        <el-table-column
          prop="code"
          label="分类代码"
          width="120"
        ></el-table-column>
        
        <el-table-column
          prop="description"
          label="描述"
        ></el-table-column>
        
        <el-table-column
          prop="sort"
          label="排序值"
          width="100"
        ></el-table-column>
        
        <el-table-column
          label="操作"
          width="150"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="showEditDialog(scope.row)"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="showDeleteDialog(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加分类' : '编辑分类'"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="categoryForm" :rules="rules" ref="categoryForm" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name"></el-input>
        </el-form-item>
        
        <el-form-item label="分类代码" prop="code" v-if="dialogType === 'add'">
          <el-input v-model="categoryForm.code" :disabled="dialogType === 'edit'"></el-input>
          <div class="form-tip">只能包含字母和数字，创建后不可修改</div>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="categoryForm.description" :rows="3"></el-input>
        </el-form-item>
        
        <el-form-item label="排序值" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="1" :max="99"></el-input-number>
          <div class="form-tip">数值越小越靠前</div>
        </el-form-item>
        
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="图标URL或图标代码"></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除确认"
      :visible.sync="deleteDialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <span>确定要删除分类"{{ categoryToDelete ? categoryToDelete.name : '' }}"吗？删除分类可能会影响已发布的帖子。</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete" :loading="deleting">确定删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/forum'

export default {
  name: 'CategoryManage',
  data() {
    return {
      categories: [],
      loading: false,
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      categoryForm: {
        id: null,
        name: '',
        code: '',
        description: '',
        icon: '',
        sort: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { max: 20, message: '长度不能超过20个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入分类代码', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' },
          { max: 20, message: '长度不能超过20个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过200个字符', trigger: 'blur' }
        ]
      },
      submitting: false,
      deleteDialogVisible: false,
      categoryToDelete: null,
      deleting: false
    }
  },
  created() {
    this.fetchCategories()
  },
  methods: {
    async fetchCategories() {
      this.loading = true
      try {
        const res = await getCategories()
        if (res.status === 0) {
          this.categories = res.data
        } else {
          this.$message.error(res.msg || '获取分类列表失败')
        }
      } catch (error) {
        console.error('获取分类列表失败', error)
        this.$message.error('获取分类列表失败')
      } finally {
        this.loading = false
      }
    },
    showAddDialog() {
      this.dialogType = 'add'
      this.categoryForm = {
        id: null,
        name: '',
        code: '',
        description: '',
        icon: '',
        sort: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm.clearValidate()
      })
    },
    showEditDialog(row) {
      this.dialogType = 'edit'
      this.categoryForm = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm.clearValidate()
      })
    },
    showDeleteDialog(row) {
      this.categoryToDelete = row
      this.deleteDialogVisible = true
    },
    async submitCategory() {
      this.$refs.categoryForm.validate(async (valid) => {
        if (!valid) {
          return
        }
        
        this.submitting = true
        try {
          let res
          
          if (this.dialogType === 'add') {
            res = await createCategory(this.categoryForm)
          } else {
            res = await updateCategory(this.categoryForm.id, this.categoryForm)
          }
          
          if (res.status === 0) {
            this.$message.success(this.dialogType === 'add' ? '分类添加成功' : '分类更新成功')
            this.dialogVisible = false
            this.fetchCategories()
          } else {
            this.$message.error(res.msg || (this.dialogType === 'add' ? '添加失败' : '更新失败'))
          }
        } catch (error) {
          console.error(this.dialogType === 'add' ? '添加分类失败' : '更新分类失败', error)
          this.$message.error(this.dialogType === 'add' ? '添加分类失败' : '更新分类失败')
        } finally {
          this.submitting = false
        }
      })
    },
    async confirmDelete() {
      if (!this.categoryToDelete) {
        return
      }
      
      this.deleting = true
      try {
        const res = await deleteCategory(this.categoryToDelete.id)
        if (res.status === 0) {
          this.$message.success('分类删除成功')
          this.deleteDialogVisible = false
          this.fetchCategories()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        console.error('删除分类失败', error)
        this.$message.error('删除分类失败')
      } finally {
        this.deleting = false
      }
    }
  }
}
</script>

<style scoped>
.category-manage {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
```

### 4.4 实现建议与最佳实践

#### 4.4.1 状态管理

使用Vuex管理用户状态和论坛相关的全局状态：

```javascript
// store/modules/forum.js
import { getCategories, getHotPosts } from '@/api/forum'

const state = {
  categories: [],
  hotPosts: [],
  categoriesLoaded: false
}

const mutations = {
  SET_CATEGORIES(state, categories) {
    state.categories = categories
    state.categoriesLoaded = true
  },
  SET_HOT_POSTS(state, posts) {
    state.hotPosts = posts
  }
}

const actions = {
  async fetchCategories({ commit, state }) {
    // 如果已加载过分类，则不重复加载
    if (state.categoriesLoaded) return
    
    try {
      const res = await getCategories()
      if (res.status === 0) {
        commit('SET_CATEGORIES', res.data)
      }
    } catch (error) {
      console.error('获取分类失败', error)
    }
  },
  
  async fetchHotPosts({ commit }, limit = 5) {
    try {
      const res = await getHotPosts(limit)
      if (res.status === 0) {
        commit('SET_HOT_POSTS', res.data)
      }
    } catch (error) {
      console.error('获取热门帖子失败', error)
    }
  }
}

const getters = {
  getCategoryByCode: (state) => (code) => {
    return state.categories.find(cat => cat.code === code)
  },
  getCategoryNameByCode: (state) => (code) => {
    const category = state.categories.find(cat => cat.code === code)
    return category ? category.name : '未分类'
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
```

#### 4.4.2 路由配置

```javascript
// router/index.js
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/forum',
    component: () => import('@/views/forum/ForumHome'),
    meta: { title: '论坛' }
  },
  {
    path: '/forum/post/:id',
    component: () => import('@/views/forum/PostDetail'),
    meta: { title: '帖子详情' }
  },
  {
    path: '/forum/post/create',
    component: () => import('@/views/forum/PostCreate'),
    meta: { title: '发布帖子', requireAuth: true }
  },
  {
    path: '/forum/post/edit/:id',
    component: () => import('@/views/forum/PostEdit'),
    meta: { title: '编辑帖子', requireAuth: true }
  },
  {
    path: '/forum/admin/categories',
    component: () => import('@/views/forum/CategoryManage'),
    meta: { title: '分类管理', requireAuth: true, requireAdmin: true }
  }
]

// 添加到主路由配置中
```

#### 4.4.3 权限控制

确保在路由导航守卫中实现权限控制：

```javascript
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 羽毛球系统` : '羽毛球系统'
  
  // 权限检查
  if (to.meta.requireAuth) {
    const isLoggedIn = store.getters.isLoggedIn
    if (!isLoggedIn) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 管理员权限检查
    if (to.meta.requireAdmin && !store.getters.isAdmin) {
      next('/forbidden')
      return
    }
  }
  
  next()
})
```

#### 4.4.4 性能优化

1. **按需加载组件**：使用Vue的异步组件和webpack的代码分割功能。
2. **分页加载数据**：帖子和回复列表都应实现分页加载。
3. **缓存频繁使用的数据**：如分类列表可以缓存在Vuex中。
4. **防抖处理**：对搜索输入框添加防抖处理。

```javascript
// utils/debounce.js
export function debounce(func, wait) {
  let timeout
  return function(...args) {
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      func.apply(this, args)
    }, wait)
  }
}

// 使用方式
import { debounce } from '@/utils/debounce'

export default {
  methods: {
    search: debounce(function() {
      this.fetchPosts()
    }, 300)
  }
}
```

#### 4.4.5 富文本编辑器集成

对于帖子内容和回复，可以考虑集成富文本编辑器，如wangEditor或Quill：

```javascript
// 安装依赖
// npm install quill

// 组件中使用
import Quill from 'quill'
import 'quill/dist/quill.snow.css'

export default {
  mounted() {
    const options = {
      theme: 'snow',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],
          ['blockquote', 'code-block'],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          ['link', 'image'],
          ['clean']
        ]
      },
      placeholder: '请输入内容...'
    }
    
    this.editor = new Quill('#editor', options)
  },
  methods: {
    getContent() {
      return this.editor.root.innerHTML
    }
  }
}
```

#### 4.4.6 用户体验增强

1. **加载状态提示**：使用骨架屏或加载动画。
2. **错误处理**：为所有API调用添加适当的错误处理和用户提示。
3. **确认操作**：对删除等危险操作增加确认步骤。
4. **提示信息**：对用户操作结果给予明确的反馈。
5. **移动端适配**：使用响应式设计，确保在移动设备上的良好体验。

### 4.5 小结

本前端实现指南提供了论坛模块的组件设计和关键实现代码，包括：

1. 论坛首页及帖子列表展示
2. 帖子详情页与回复功能
3. 发帖与编辑帖子功能
4. 帖子分类管理（管理员）
5. 点赞功能
6. 路由配置与权限控制

开发者可以根据实际需求进行调整和扩展，如添加帖子置顶、精华帖标记、用户权限管理等功能。同时，注意与后端API的对接，确保数据交互的一致性和安全性。
