-- 创建帖子分类表
CREATE TABLE IF NOT EXISTS `post_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(20) NOT NULL COMMENT '分类代码',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子分类表';

-- 初始化帖子分类数据
INSERT INTO `post_category` (`name`, `code`, `description`, `sort_order`) VALUES 
('打球组队', 'team', '寻找羽毛球伙伴，组织比赛等', 1),
('公告通知', 'notice', '系统公告和重要通知', 2),
('求助问答', 'help', '技术问题咨询和解答', 3),
('经验交流', 'exp', '分享羽毛球心得和经验', 4);

-- 创建帖子表
CREATE TABLE IF NOT EXISTS `post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(200) NOT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `user_id` bigint(20) NOT NULL COMMENT '作者ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `views` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `likes` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `reply_count` int(11) NOT NULL DEFAULT '0' COMMENT '回复数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `post_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 创建帖子回复表
CREATE TABLE IF NOT EXISTS `post_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '回复用户ID',
  `content` text NOT NULL COMMENT '回复内容',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父回复ID，用于嵌套回复',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `fk_reply_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_reply_parent` FOREIGN KEY (`parent_id`) REFERENCES `post_reply` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子回复表';
