-- 验证推荐算法逻辑的测试脚本
USE test;

-- 1. 用户1(id=6)购买过的商品
SELECT '=== 用户1(id=6)购买过的商品 ===' as info;
SELECT DISTINCT oi.product_id, p.name
FROM mall_order_item oi
INNER JOIN mall_order o ON oi.order_no = o.order_no
INNER JOIN mall_product p ON oi.product_id = p.id
WHERE o.user_id = 6 AND o.status IN (20, 40)
ORDER BY oi.product_id;

-- 2. 购买了用户1相同商品的其他用户
SELECT '=== 购买了相同商品的其他用户 ===' as info;
SELECT DISTINCT o.user_id, u.username, COUNT(DISTINCT oi.product_id) as common_products
FROM mall_order_item oi
INNER JOIN mall_order o ON oi.order_no = o.order_no
INNER JOIN user u ON o.user_id = u.id
WHERE oi.product_id IN (
    SELECT DISTINCT oi2.product_id
    FROM mall_order_item oi2
    INNER JOIN mall_order o2 ON oi2.order_no = o2.order_no
    WHERE o2.user_id = 6 AND o2.status IN (20, 40)
)
AND o.status IN (20, 40)
AND o.user_id != 6
GROUP BY o.user_id, u.username
ORDER BY common_products DESC;

-- 3. 相似用户购买的其他商品（推荐候选）
SELECT '=== 推荐候选商品 ===' as info;
SELECT 
    oi.product_id,
    p.name as product_name,
    COUNT(*) as purchase_count,
    SUM(oi.quantity) as total_quantity,
    (COUNT(*) * 0.6 + SUM(oi.quantity) * 0.4) as recommend_score
FROM mall_order_item oi
INNER JOIN mall_order o ON oi.order_no = o.order_no
INNER JOIN mall_product p ON oi.product_id = p.id
WHERE o.user_id IN (
    -- 相似用户列表
    SELECT DISTINCT o.user_id
    FROM mall_order_item oi
    INNER JOIN mall_order o ON oi.order_no = o.order_no
    WHERE oi.product_id IN (
        -- 用户1购买过的商品
        SELECT DISTINCT oi2.product_id
        FROM mall_order_item oi2
        INNER JOIN mall_order o2 ON oi2.order_no = o2.order_no
        WHERE o2.user_id = 6 AND o2.status IN (20, 40)
    )
    AND o.status IN (20, 40)
    AND o.user_id != 6
)
AND o.status IN (20, 40)
AND oi.product_id NOT IN (
    -- 排除用户1已购买的商品
    SELECT DISTINCT oi3.product_id
    FROM mall_order_item oi3
    INNER JOIN mall_order o3 ON oi3.order_no = o3.order_no
    WHERE o3.user_id = 6 AND o3.status IN (20, 40)
)
AND p.status = 1
AND p.stock > 0
GROUP BY oi.product_id, p.name
ORDER BY recommend_score DESC, purchase_count DESC
LIMIT 10; 