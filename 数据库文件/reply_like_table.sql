-- 创建回复点赞表
CREATE TABLE IF NOT EXISTS `reply_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `reply_id` bigint(20) NOT NULL COMMENT '回复ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_reply_user` (`reply_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_reply_id` (`reply_id`),
  CONSTRAINT `fk_like_reply` FOREIGN KEY (`reply_id`) REFERENCES `post_reply` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reply_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复点赞表'; 