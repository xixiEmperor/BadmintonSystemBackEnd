-- 测试数据脚本：为协同过滤算法添加测试数据
USE test;

-- 1. 检查当前数据
SELECT '=== 当前用户数据 ===' as info;
SELECT id, username, email FROM user WHERE id IN (1, 6);

SELECT '=== 当前商品数据 ===' as info;
SELECT id, name, sales, stock FROM mall_product WHERE status = 1 LIMIT 5;

-- 2. 创建第二个测试用户（如果不存在user2）
INSERT IGNORE INTO user (id, username, password, email, role, create_time) 
VALUES (7, 'user2', '$2a$10$example.hash.here', 'user2@example.com', 'ROLE_USER', NOW());

-- 3. 添加测试商品（如果商品不够）
INSERT IGNORE INTO mall_product (id, category_id, category_name, name, subtitle, main_image, price, stock, sales, status, has_specification, create_time, update_time)
VALUES 
(1, 1, '球拍类', '胜利羽毛球拍 TK-F隼', '专业进攻型球拍', '/images/product1.jpg', 299.00, 50, 15, 1, 0, NOW(), NOW()),
(2, 1, '球拍类', 'YONEX尤尼克斯弓箭11', '进攻防守兼备', '/images/product2.jpg', 899.00, 30, 8, 1, 0, NOW(), NOW()),
(3, 2, '球鞋类', '李宁云四代羽毛球鞋', '减震透气专业球鞋', '/images/product3.jpg', 459.00, 40, 12, 1, 0, NOW(), NOW()),
(4, 2, '球鞋类', 'YONEX CFZ2羽毛球鞋', '轻量化设计', '/images/product4.jpg', 678.00, 25, 6, 1, 0, NOW(), NOW()),
(5, 3, '配件类', '胜利羽毛球 金黄1号', '比赛级羽毛球', '/images/product5.jpg', 89.00, 100, 25, 1, 0, NOW(), NOW());

-- 4. 创建协同过滤测试订单数据
-- 用户1(id=6)的购买记录
INSERT IGNORE INTO mall_order (order_no, user_id, total_price, payment_type, status, payment_time, create_time, update_time)
VALUES 
(1001, 6, 388.00, 1, 20, NOW(), NOW(), NOW()),  -- 已付款
(1002, 6, 548.00, 1, 40, NOW(), NOW(), NOW());  -- 已完成

-- 用户2(id=7)的购买记录 - 与用户1有交集
INSERT IGNORE INTO mall_order (order_no, user_id, total_price, payment_type, status, payment_time, create_time, update_time)
VALUES 
(1003, 7, 958.00, 1, 20, NOW(), NOW(), NOW()),  -- 已付款
(1004, 7, 767.00, 1, 40, NOW(), NOW(), NOW());  -- 已完成

-- 管理员用户(id=1)的购买记录 - 作为另一个相似用户
INSERT IGNORE INTO mall_order (order_no, user_id, total_price, payment_type, status, payment_time, create_time, update_time)
VALUES 
(1005, 1, 1357.00, 1, 20, NOW(), NOW(), NOW()); -- 已付款

-- 5. 创建订单项数据 - 关键：要有交集商品
-- 用户1购买：商品1, 商品3
INSERT IGNORE INTO mall_order_item (order_no, product_id, product_name, product_image, current_unit_price, quantity, total_price, create_time, update_time)
VALUES 
(1001, 1, '胜利羽毛球拍 TK-F隼', '/images/product1.jpg', 299.00, 1, 299.00, NOW(), NOW()),
(1001, 5, '胜利羽毛球 金黄1号', '/images/product5.jpg', 89.00, 1, 89.00, NOW(), NOW()),
(1002, 3, '李宁云四代羽毛球鞋', '/images/product3.jpg', 459.00, 1, 459.00, NOW(), NOW()),
(1002, 5, '胜利羽毛球 金黄1号', '/images/product5.jpg', 89.00, 1, 89.00, NOW(), NOW());

-- 用户2购买：商品1, 商品2, 商品4 (与用户1有交集：商品1)
INSERT IGNORE INTO mall_order_item (order_no, product_id, product_name, product_image, current_unit_price, quantity, total_price, create_time, update_time)
VALUES 
(1003, 1, '胜利羽毛球拍 TK-F隼', '/images/product1.jpg', 299.00, 1, 299.00, NOW(), NOW()),
(1003, 2, 'YONEX尤尼克斯弓箭11', '/images/product2.jpg', 899.00, 1, 899.00, NOW(), NOW()),
(1003, 5, '胜利羽毛球 金黄1号', '/images/product5.jpg', 89.00, 1, 89.00, NOW(), NOW()),
(1004, 4, 'YONEX CFZ2羽毛球鞋', '/images/product4.jpg', 678.00, 1, 678.00, NOW(), NOW()),
(1004, 5, '胜利羽毛球 金黄1号', '/images/product5.jpg', 89.00, 1, 89.00, NOW(), NOW());

-- 管理员购买：商品1, 商品3, 商品2 (与用户1有交集：商品1, 商品3)
INSERT IGNORE INTO mall_order_item (order_no, product_id, product_name, product_image, current_unit_price, quantity, total_price, create_time, update_time)
VALUES 
(1005, 1, '胜利羽毛球拍 TK-F隼', '/images/product1.jpg', 299.00, 1, 299.00, NOW(), NOW()),
(1005, 2, 'YONEX尤尼克斯弓箭11', '/images/product2.jpg', 899.00, 1, 899.00, NOW(), NOW()),
(1005, 3, '李宁云四代羽毛球鞋', '/images/product3.jpg', 459.00, 1, 459.00, NOW(), NOW());

-- 6. 验证测试数据
SELECT '=== 验证订单数据 ===' as info;
SELECT o.order_no, o.user_id, u.username, o.status, o.total_price 
FROM mall_order o 
JOIN user u ON o.user_id = u.id 
ORDER BY o.order_no;

SELECT '=== 验证订单项数据 ===' as info;
SELECT oi.order_no, oi.product_id, oi.product_name, oi.quantity, o.user_id, u.username
FROM mall_order_item oi
JOIN mall_order o ON oi.order_no = o.order_no
JOIN user u ON o.user_id = u.id
ORDER BY oi.order_no, oi.product_id;

SELECT '=== 用户购买交集分析 ===' as info;
SELECT '用户1购买的商品:' as analysis, GROUP_CONCAT(DISTINCT oi.product_id) as products
FROM mall_order_item oi
JOIN mall_order o ON oi.order_no = o.order_no
WHERE o.user_id = 6 AND o.status IN (20, 40)
UNION ALL
SELECT '用户2购买的商品:', GROUP_CONCAT(DISTINCT oi.product_id)
FROM mall_order_item oi
JOIN mall_order o ON oi.order_no = o.order_no
WHERE o.user_id = 7 AND o.status IN (20, 40)
UNION ALL
SELECT '管理员购买的商品:', GROUP_CONCAT(DISTINCT oi.product_id)
FROM mall_order_item oi
JOIN mall_order o ON oi.order_no = o.order_no
WHERE o.user_id = 1 AND o.status IN (20, 40); 