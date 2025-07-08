-- ==================================================
-- 场地状态管理模块 - 数据库初始化脚本
-- ==================================================

-- 1. 创建场地表
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '场地ID',
  `name` varchar(50) NOT NULL COMMENT '场地名称',
  `description` varchar(500) DEFAULT NULL COMMENT '场地描述',
  `location` varchar(100) NOT NULL COMMENT '场地位置',
  `price_per_hour` decimal(10,2) NOT NULL COMMENT '每小时价格',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT '场地类型：1-羽毛球场',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '场地基础状态：1-可用，0-不可用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 3. 创建特殊日期配置表
DROP TABLE IF EXISTS `special_date_config`;
CREATE TABLE `special_date_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `special_date` date NOT NULL COMMENT '特殊日期',
  `config_type` int(11) NOT NULL COMMENT '配置类型：1-节假日，2-维护日，3-特殊开放日',
  `affected_venue_ids` varchar(500) DEFAULT NULL COMMENT '影响的场地ID，多个用逗号分隔，null表示全部场地',
  `start_time` varchar(5) DEFAULT NULL COMMENT '影响的时间段开始时间',
  `end_time` varchar(5) DEFAULT NULL COMMENT '影响的时间段结束时间',
  `venue_status` int(11) NOT NULL COMMENT '特殊日期的场地状态：1-空闲中，2-使用中，4-维护中',
  `bookable` int(11) NOT NULL COMMENT '是否可预约：1-可预约，0-不可预约',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `enabled` int(11) NOT NULL DEFAULT '1' COMMENT '是否启用：1-启用，0-禁用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_special_date` (`special_date`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特殊日期配置表';

-- ==================================================
-- 初始化基础数据
-- ==================================================

-- 1. 初始化场地数据（羽毛球场1-9号）
INSERT INTO `venue` (`name`, `description`, `location`, `price_per_hour`, `type`, `status`, `create_time`, `update_time`) VALUES
('羽毛球场1号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆1号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场2号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆2号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场3号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆3号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场4号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆4号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场5号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆5号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场6号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆6号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场7号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆7号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场8号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆8号场地', 30.00, 1, 1, NOW(), NOW()),
('羽毛球场9号', '标准羽毛球场地，配备专业羽毛球网和照明设备', '体育馆9号场地', 30.00, 1, 1, NOW(), NOW());

-- 2. 初始化特殊日期配置示例
INSERT INTO `special_date_config` (`config_name`, `special_date`, `config_type`, `affected_venue_ids`, `start_time`, `end_time`, `venue_status`, `bookable`, `description`, `enabled`, `create_time`, `update_time`) VALUES
('元旦节', '2024-01-01', 1, NULL, '08:00', '21:00', 4, 0, '元旦节全天停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-10', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-11', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-12', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-13', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-14', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-15', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-16', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('春节假期', '2024-02-17', 1, NULL, '08:00', '21:00', 4, 0, '春节假期停止预约', 1, NOW(), NOW()),
('劳动节', '2024-05-01', 1, NULL, '08:00', '21:00', 4, 0, '劳动节停止预约', 1, NOW(), NOW()),
('端午节', '2024-06-10', 1, NULL, '08:00', '21:00', 4, 0, '端午节停止预约', 1, NOW(), NOW()),
('中秋节', '2024-09-17', 1, NULL, '08:00', '21:00', 4, 0, '中秋节停止预约', 1, NOW(), NOW()),
('国庆节', '2024-10-01', 1, NULL, '08:00', '21:00', 4, 0, '国庆节停止预约', 1, NOW(), NOW()),
('国庆节', '2024-10-02', 1, NULL, '08:00', '21:00', 4, 0, '国庆节停止预约', 1, NOW(), NOW()),
('国庆节', '2024-10-03', 1, NULL, '08:00', '21:00', 4, 0, '国庆节停止预约', 1, NOW(), NOW()),
('1-4号场地维护', '2024-01-20', 2, '1,2,3,4', '13:00', '17:00', 4, 0, '下午1-4号场地设备维护', 1, NOW(), NOW());

-- ==================================================
-- 创建必要的索引（如果上面没有的话）
-- ==================================================

-- 为special_date_config表创建复合索引
CREATE INDEX `idx_special_config_query` ON `special_date_config` (`special_date`, `enabled`);

-- ==================================================
-- 说明
-- ==================================================

/*
执行此脚本后，系统将拥有：
1. 9个羽毛球场地（羽毛球场1号-9号）
2. 基础的节假日配置（2024年主要节假日）
*/ 