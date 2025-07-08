-- 预约订单测试数据
-- 使用固定的订单号和日期，便于测试和验证

-- 清空现有测试数据（可选）
DELETE FROM reservation_order WHERE username = 'testuser';

-- 插入测试数据
INSERT INTO `reservation_order` (
    `order_no`, `user_id`, `username`, `venue_id`, `venue_name`, 
    `reservation_date`, `start_time`, `end_time`, `duration`, 
    `price_per_hour`, `total_amount`, `status`, `pay_type`, `remark`
) VALUES 
-- 今天的订单
('RO1703520001001', 1, 'testuser', 1, '羽毛球场地1', CURDATE(), '09:00', '10:00', 60, 50.00, 50.00, 2, 1, '已支付，可申请退款'),
('RO1703520001002', 1, 'testuser', 1, '羽毛球场地1', CURDATE(), '14:00', '16:00', 120, 50.00, 100.00, 3, 2, '已完成订单'),
('RO1703520001003', 1, 'testuser', 2, '羽毛球场地2', CURDATE(), '10:00', '11:00', 60, 50.00, 50.00, 1, 1, '待支付订单'),

-- 明天的订单（可申请退款）
('RO1703520001004', 1, 'testuser', 1, '羽毛球场地1', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00', '09:00', 60, 50.00, 50.00, 2, 1, '明天预约，可退款'),
('RO1703520001005', 1, 'testuser', 3, '羽毛球场地3', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:00', '21:00', 120, 50.00, 100.00, 2, 2, '明天预约，可退款'),

-- 后天的订单
('RO1703520001006', 1, 'testuser', 2, '羽毛球场地2', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00', '17:00', 120, 50.00, 100.00, 6, 1, '退款申请中'),

-- 已取消的订单
('RO1703520001007', 1, 'testuser', 1, '羽毛球场地1', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00', '12:00', 120, 50.00, 100.00, 4, 1, '用户取消'),

-- 已关闭订单（退款完成）
('RO1703520001008', 1, 'testuser', 3, '羽毛球场地3', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '16:00', '18:00', 120, 50.00, 100.00, 5, 2, '退款完成'),

-- 特定日期的测试数据（用于API测试）
('RO2024122501', 1, 'testuser', 1, '羽毛球场地1', '2024-12-25', '19:00', '20:00', 60, 50.00, 50.00, 2, 1, '圣诞节预约'),
('RO2024122502', 2, 'user2', 2, '羽毛球场地2', '2024-12-25', '20:00', '21:00', 60, 50.00, 50.00, 3, 2, '圣诞节已完成'),
('RO2024122503', 3, 'user3', 3, '羽毛球场地3', '2024-12-25', '18:00', '19:00', 60, 50.00, 50.00, 1, 1, '圣诞节待支付');

-- 更新已支付订单的支付时间
UPDATE `reservation_order` 
SET `pay_time` = DATE_SUB(NOW(), INTERVAL 1 HOUR)
WHERE `status` IN (2, 3, 5, 6) AND `username` = 'testuser';

-- 更新退款相关信息
UPDATE `reservation_order` 
SET `refund_amount` = `total_amount`, 
    `refund_time` = DATE_SUB(NOW(), INTERVAL 30 MINUTE),
    `cancel_reason` = '用户申请退款，管理员审批通过'
WHERE `status` = 5 AND `username` = 'testuser';

UPDATE `reservation_order` 
SET `cancel_reason` = '用户申请退款中，等待管理员审批'
WHERE `status` = 6 AND `username` = 'testuser'; 