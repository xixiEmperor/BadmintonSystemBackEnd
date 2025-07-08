-- ========================================
-- 羽毛球系统 - 公告通知表结构
-- ========================================

-- 创建公告通知表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '通知类型：1-普通通知，2-重要通知',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布状态：0-草稿，1-已发布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告通知表';

-- ========================================
-- 插入初始测试数据
-- ========================================

-- 插入已发布的重要通知
INSERT INTO `notice` (`title`, `content`, `type`, `status`, `create_time`, `update_time`, `publish_time`) VALUES
('场地维护通知', '尊敬的用户：\n\n为了给大家提供更好的运动环境，本羽毛球馆将于本周六（2024年1月20日）上午8:00-12:00进行场地维护工作。\n\n维护期间暂停对外开放，给您带来的不便敬请谅解。\n\n维护完成后将第一时间恢复正常营业。\n\n感谢您的理解与支持！', 2, 1, '2024-01-15 10:30:00', '2024-01-15 10:30:00', '2024-01-15 10:30:00'),

('春节营业时间调整', '亲爱的会员朋友们：\n\n春节期间（2024年2月10日-2024年2月17日）营业时间调整如下：\n\n• 2月10日-2月12日：正常营业（9:00-22:00）\n• 2月13日-2月15日：休息\n• 2月16日-2月17日：正常营业（9:00-22:00）\n\n2月18日起恢复正常营业时间。\n\n祝大家春节快乐！', 2, 1, '2024-01-18 14:20:00', '2024-01-18 14:20:00', '2024-01-18 14:20:00'),

('新会员优惠活动', '新会员福利来啦！\n\n即日起至2024年2月29日，新注册会员可享受：\n\n1. 首次充值满200元送50元\n2. 免费体验课程一次\n3. 专业教练指导\n\n活动详情请咨询前台工作人员。\n\n名额有限，先到先得！', 1, 1, '2024-01-16 09:15:00', '2024-01-16 09:15:00', '2024-01-16 09:15:00'),

('羽毛球比赛报名通知', '【羽毛球友谊赛】报名开始！\n\n比赛时间：2024年3月15日（周五）19:00-21:00\n比赛地点：本馆1-4号场地\n报名截止：2024年3月10日\n\n比赛分组：\n• 男子单打\n• 女子单打\n• 混合双打\n\n报名费：50元/人（含奖品费用）\n\n请到前台报名或联系客服微信：badminton2024', 1, 1, '2024-01-19 16:45:00', '2024-01-19 16:45:00', '2024-01-19 16:45:00');

-- 插入草稿状态的通知
INSERT INTO `notice` (`title`, `content`, `type`, `status`, `create_time`, `update_time`, `publish_time`) VALUES
('夏季活动预告', '夏季即将到来，我们正在筹备一系列精彩活动...\n\n（此通知尚未完成，暂存为草稿）', 1, 0, '2024-01-20 11:30:00', '2024-01-20 11:30:00', NULL),

('设备升级通知', '为提升服务质量，我们计划对场馆设备进行升级...\n\n（具体时间待定，草稿状态）', 2, 0, '2024-01-21 08:45:00', '2024-01-21 08:45:00', NULL);

-- ========================================
-- 查询语句示例
-- ========================================

-- 查询所有已发布的通知（用户端）
-- SELECT * FROM notice WHERE status = 1 ORDER BY type DESC, publish_time DESC;

-- 查询重要通知（用户端）
-- SELECT * FROM notice WHERE status = 1 AND type = 2 ORDER BY publish_time DESC;

-- 查询所有通知（管理员端）
-- SELECT * FROM notice ORDER BY create_time DESC;

-- 查询草稿状态的通知（管理员端）
-- SELECT * FROM notice WHERE status = 0 ORDER BY create_time DESC;

-- ========================================
-- 索引说明
-- ========================================

-- idx_type: 按通知类型查询的索引
-- idx_status: 按发布状态查询的索引
-- idx_create_time: 按创建时间排序的索引
-- idx_publish_time: 按发布时间排序的索引

-- ========================================
-- 字段说明
-- ========================================

-- id: 主键，自增
-- title: 通知标题，最大200字符
-- content: 通知内容，使用TEXT类型支持长文本
-- type: 通知类型，1-普通通知，2-重要通知
-- status: 发布状态，0-草稿，1-已发布
-- create_time: 创建时间，自动设置为当前时间
-- update_time: 更新时间，自动更新为当前时间
-- publish_time: 发布时间，发布时设置，草稿状态为NULL

-- ========================================
-- 业务逻辑说明
-- ========================================

-- 1. 创建通知时默认status=0（草稿状态）
-- 2. 发布通知时将status更新为1，同时设置publish_time
-- 3. 用户端只能看到status=1的通知
-- 4. 管理员端可以看到所有状态的通知
-- 5. 重要通知(type=2)在列表中优先显示 