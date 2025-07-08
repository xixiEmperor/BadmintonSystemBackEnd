-- 商城订单表
CREATE TABLE `mall_order` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `order_no` bigint(20) NOT NULL COMMENT '订单号',
    `user_id` bigint(20) NOT NULL COMMENT '用户id',
    `total_price` decimal(20,2) NOT NULL COMMENT '订单总价',
    `payment_type` int(4) NOT NULL DEFAULT '1' COMMENT '支付类型，1-在线支付',
    `status` int(4) NOT NULL DEFAULT '10' COMMENT '订单状态：10-未付款，20-已付款，30-已取消，40-已完成，50-已关闭',
    `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
    `pickup_code` varchar(16) DEFAULT NULL COMMENT '取货码',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uqe_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单表';

-- 商城订单项表
CREATE TABLE `mall_order_item` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `order_no` bigint(20) NOT NULL COMMENT '订单号',
    `product_id` int(11) NOT NULL COMMENT '商品id',
    `product_name` varchar(100) NOT NULL COMMENT '商品名称',
    `product_image` varchar(200) DEFAULT NULL COMMENT '商品图片',
    `current_unit_price` decimal(20,2) NOT NULL COMMENT '生成订单时的商品单价',
    `quantity` int(10) NOT NULL COMMENT '商品数量',
    `total_price` decimal(20,2) NOT NULL COMMENT '商品总价',
    `specification_id` int(11) DEFAULT NULL COMMENT '规格id',
    `specs` varchar(100) DEFAULT NULL COMMENT '规格值，例如颜色、尺寸等',
    `price_adjustment` decimal(20,2) DEFAULT '0.00' COMMENT '价格调整，规格引起的价格变动',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单项表'; 