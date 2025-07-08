-- 预约订单表
-- 业务流程：创建订单→支付→使用→完成 或 已支付→申请退款→管理员审批→已关闭
-- 退款规则：用户在预约开始前30分钟可申请退款，需管理员审批
-- 完成规则：用户到场后由前台工作人员将订单标记为已完成
CREATE TABLE IF NOT EXISTS `reservation_order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `venue_id` INT NOT NULL COMMENT '场地ID',
    `venue_name` VARCHAR(100) NOT NULL COMMENT '场地名称',
    `reservation_date` DATE NOT NULL COMMENT '预约日期',
    `start_time` VARCHAR(8) NOT NULL COMMENT '开始时间(HH:mm)',
    `end_time` VARCHAR(8) NOT NULL COMMENT '结束时间(HH:mm)',
    `duration` INT NOT NULL COMMENT '时长(分钟)',
    `price_per_hour` DECIMAL(10,2) NOT NULL COMMENT '每小时价格',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `status` INT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已关闭(退款完成)，6-退款中',
    `pay_info_id` BIGINT NULL COMMENT '支付信息ID',
    `pay_type` INT NULL COMMENT '支付方式：1-支付宝，2-微信',
    `pay_time` DATETIME NULL COMMENT '支付时间',
    `refund_amount` DECIMAL(10,2) NULL COMMENT '退款金额',
    `refund_time` DATETIME NULL COMMENT '退款时间',
    `cancel_reason` VARCHAR(255) NULL COMMENT '取消/退款原因',
    `remark` VARCHAR(255) NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_venue_id` (`venue_id`),
    INDEX `idx_reservation_date` (`reservation_date`),
    INDEX `idx_status` (`status`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_pay_info_id` (`pay_info_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

-- 插入测试数据
INSERT INTO `reservation_order` (
    `order_no`, `user_id`, `username`, `venue_id`, `venue_name`, 
    `reservation_date`, `start_time`, `end_time`, `duration`, 
    `price_per_hour`, `total_amount`, `status`, `pay_type`, `remark`
) VALUES 
-- 今天的订单
('RO' + UNIX_TIMESTAMP() + '001', 1, 'testuser', 1, '羽毛球场地1', CURDATE(), '09:00', '10:00', 60, 50.00, 50.00, 2, 1, '已支付，可申请退款'),
('RO' + UNIX_TIMESTAMP() + '002', 1, 'testuser', 1, '羽毛球场地1', CURDATE(), '14:00', '16:00', 120, 50.00, 100.00, 3, 2, '已完成订单'),
('RO' + UNIX_TIMESTAMP() + '003', 1, 'testuser', 2, '羽毛球场地2', CURDATE(), '10:00', '11:00', 60, 50.00, 50.00, 1, 1, '待支付订单'),

-- 明天的订单（可申请退款）
('RO' + UNIX_TIMESTAMP() + '004', 1, 'testuser', 1, '羽毛球场地1', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00', '09:00', 60, 50.00, 50.00, 2, 1, '明天预约，可退款'),
('RO' + UNIX_TIMESTAMP() + '005', 1, 'testuser', 3, '羽毛球场地3', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:00', '21:00', 120, 50.00, 100.00, 2, 2, '明天预约，可退款'),

-- 后天的订单
('RO' + UNIX_TIMESTAMP() + '006', 1, 'testuser', 2, '羽毛球场地2', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00', '17:00', 120, 50.00, 100.00, 6, 1, '退款申请中'),

-- 已取消的订单
('RO' + UNIX_TIMESTAMP() + '007', 1, 'testuser', 1, '羽毛球场地1', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00', '12:00', 120, 50.00, 100.00, 4, 1, '用户取消'),

-- 已关闭订单（退款完成）
('RO' + UNIX_TIMESTAMP() + '008', 1, 'testuser', 3, '羽毛球场地3', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '16:00', '18:00', 120, 50.00, 100.00, 5, 2, '退款完成');

-- 更新已支付订单的支付时间
UPDATE `reservation_order` 
SET `pay_time` = DATE_SUB(NOW(), INTERVAL 1 HOUR)
WHERE `status` IN (2, 3, 5, 6);

-- 更新退款相关信息
UPDATE `reservation_order` 
SET `refund_amount` = `total_amount`, 
    `refund_time` = DATE_SUB(NOW(), INTERVAL 30 MINUTE),
    `cancel_reason` = '用户申请退款，管理员审批通过'
WHERE `status` = 5;

UPDATE `reservation_order` 
SET `cancel_reason` = '用户申请退款中，等待管理员审批'
WHERE `status` = 6; 