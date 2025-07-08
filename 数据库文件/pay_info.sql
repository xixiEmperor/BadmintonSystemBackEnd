-- 支付信息表
CREATE TABLE `pay_info` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `order_no` bigint(20) NOT NULL COMMENT '订单号',
    `pay_platform` int(4) NOT NULL COMMENT '支付平台:1-支付宝,2-微信',
    `platform_number` varchar(200) DEFAULT NULL COMMENT '支付平台的流水号',
    `platform_status` varchar(20) DEFAULT NULL COMMENT '支付平台的支付状态',
    `business_type` varchar(20) NOT NULL COMMENT '业务类型：MALL-商城订单支付 RESERVATION-预约订单支付',
    `pay_amount` decimal(20,2) NOT NULL COMMENT '支付金额',
    `status` int(4) NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uqe_order_no` (`order_no`),
    UNIQUE KEY `uqe_platform_number` (`platform_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付信息表'; 