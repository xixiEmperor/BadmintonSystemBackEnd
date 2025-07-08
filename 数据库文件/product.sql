-- 商品类别表
CREATE TABLE IF NOT EXISTS `mall_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '类别Id',
  `name` varchar(50) DEFAULT NULL COMMENT '类别名称',
  `status` tinyint(1) DEFAULT '1' COMMENT '类别状态1-正常,2-已废弃',
  `sort_order` int(4) DEFAULT NULL COMMENT '排序编号,同类展示顺序,数值相等则自然排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类别表';

-- 商品表
CREATE TABLE IF NOT EXISTS `mall_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `category_id` int(11) NOT NULL COMMENT '分类id,对应mall_category表的id',
  `category_name` varchar(50) DEFAULT NULL COMMENT '分类名称：racket(球拍)、shuttle(羽毛球)、equipment(服装鞋帽)、accessories(配件)',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '商品副标题',
  `main_image` varchar(500) DEFAULT NULL COMMENT '产品主图,url相对地址',
  `sub_images` text COMMENT '图片地址,json格式,扩展用',
  `detail` text COMMENT '商品详情',
  `price` decimal(20,2) NOT NULL COMMENT '价格,单位-元保留两位小数',
  `stock` int(11) NOT NULL COMMENT '库存数量',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `status` int(6) DEFAULT '1' COMMENT '商品状态.1-在售 2-下架 3-删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品规格表
CREATE TABLE IF NOT EXISTS `mall_product_specification` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `specifications` json DEFAULT NULL COMMENT '规格信息，JSON格式，如{"color":"红色","size":"S"}',
  `price_adjustment` decimal(20,2) DEFAULT '0.00' COMMENT '价格调整，可以为负数表示折扣',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '该规格库存数量',
  `sales` int(11) DEFAULT '0' COMMENT '该规格销量',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_specification_product` FOREIGN KEY (`product_id`) REFERENCES `mall_product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 商品规格选项表（用于前端展示可选规格）
CREATE TABLE IF NOT EXISTS `mall_specification_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `spec_key` varchar(50) NOT NULL COMMENT '规格类型，如"color"、"size"',
  `spec_values` json NOT NULL COMMENT '可选值列表，JSON数组格式，如["红色","蓝色","黑色"]',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_spec_key` (`product_id`, `spec_key`),
  CONSTRAINT `fk_option_product` FOREIGN KEY (`product_id`) REFERENCES `mall_product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格选项表';

-- 修改商品表，添加has_specification字段
ALTER TABLE `mall_product` 
ADD COLUMN `has_specification` tinyint(1) DEFAULT '0' COMMENT '是否有规格：0-无规格，1-有规格' AFTER `status`; 