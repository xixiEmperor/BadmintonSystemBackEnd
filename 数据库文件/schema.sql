CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    avatar VARCHAR(255),
    email VARCHAR(100),
    create_time DATETIME NOT NULL
);

-- 创建管理员账号，密码为admin123
-- 这里的加密值默认为test，您应该替换为自己生成的加密值
INSERT INTO user (username, password, role, avatar, email, create_time)
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyUor6LAm', 'ROLE_ADMIN', 'https://via.placeholder.com/150', 'admin@example.com', NOW()
WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin'); 