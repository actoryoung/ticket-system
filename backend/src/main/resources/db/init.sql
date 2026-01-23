-- ==========================================
-- 工单系统数据库初始化脚本
-- 数据库: ticket_system
-- ==========================================

CREATE DATABASE IF NOT EXISTS `ticket_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ticket_system`;

-- ==========================================
-- 1. 用户表
-- ==========================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, HANDLER-处理人员, ADMIN-管理员',
    `department` VARCHAR(100) COMMENT '部门',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==========================================
-- 2. 工单表
-- ==========================================
DROP TABLE IF EXISTS `tickets`;
CREATE TABLE `tickets` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `ticket_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
    `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
    `description` TEXT COMMENT '工单描述',
    `category` VARCHAR(50) NOT NULL COMMENT '工单分类: BUG-缺陷, FEATURE-功能需求, SUPPORT-技术支持, OTHER-其他',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级: LOW-低, MEDIUM-中, HIGH-高, URGENT-紧急',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已完成, CLOSED-已关闭',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `handler_id` BIGINT COMMENT '处理人ID',
    `attachment_urls` TEXT COMMENT '附件URL列表（JSON数组）',
    `resolution` TEXT COMMENT '解决方案',
    `resolved_time` DATETIME COMMENT '解决时间',
    `closed_time` DATETIME COMMENT '关闭时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_no` (`ticket_no`),
    KEY `idx_creator` (`creator_id`),
    KEY `idx_handler` (`handler_id`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`),
    KEY `idx_category` (`category`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_ticket_creator` FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`),
    CONSTRAINT `fk_ticket_handler` FOREIGN KEY (`handler_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- ==========================================
-- 3. 工单状态历史表
-- ==========================================
DROP TABLE IF EXISTS `ticket_status_history`;
CREATE TABLE `ticket_status_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `ticket_id` BIGINT NOT NULL COMMENT '工单ID',
    `old_status` VARCHAR(20) COMMENT '原状态',
    `new_status` VARCHAR(20) NOT NULL COMMENT '新状态',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) COMMENT '操作人姓名',
    `remark` VARCHAR(500) COMMENT '备注说明',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_history_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单状态历史表';

-- ==========================================
-- 4. 工单评论表
-- ==========================================
DROP TABLE IF EXISTS `ticket_comments`;
CREATE TABLE `ticket_comments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `ticket_id` BIGINT NOT NULL COMMENT '工单ID',
    `user_id` BIGINT NOT NULL COMMENT '评论人ID',
    `user_name` VARCHAR(50) COMMENT '评论人姓名',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `attachment_urls` TEXT COMMENT '附件URL列表（JSON数组）',
    `is_internal` TINYINT NOT NULL DEFAULT 0 COMMENT '是否内部评论: 0-否, 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_comment_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单评论表';

-- ==========================================
-- 初始化数据
-- ==========================================

-- 插入测试用户
INSERT INTO `users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `department`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', 'ADMIN', '技术部'),
('handler1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张处理', 'handler1@example.com', '13800138001', 'HANDLER', '技术部'),
('handler2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李工程师', 'handler2@example.com', '13800138002', 'HANDLER', '技术部'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王用户', 'user1@example.com', '13800138003', 'USER', '市场部'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵客户', 'user2@example.com', '13800138004', 'USER', '销售部');
-- 密码都是: 123456 (BCrypt加密)

-- 插入测试工单
INSERT INTO `tickets` (`ticket_no`, `title`, `description`, `category`, `priority`, `status`, `creator_id`, `handler_id`) VALUES
('T202301010001', '登录页面无法正常显示', '使用Chrome浏览器访问登录页面时，页面布局错乱，验证码图片无法加载', 'BUG', 'HIGH', 'PROCESSING', 4, 2),
('T202301010002', '新增数据导出功能', '希望能够将工单列表导出为Excel文件，便于数据分析和存档', 'FEATURE', 'MEDIUM', 'PENDING', 4, NULL),
('T202301010003', '系统响应速度慢', '在工单列表页面加载时，响应时间超过5秒，影响使用体验', 'SUPPORT', 'URGENT', 'RESOLVED', 5, 2),
('T202301010004', '邮件通知配置问题', '工单状态变更后，没有收到邮件通知', 'BUG', 'LOW', 'PENDING', 5, NULL);

-- 插入状态历史记录
INSERT INTO `ticket_status_history` (`ticket_id`, `old_status`, `new_status`, `operator_id`, `operator_name`, `remark`) VALUES
(3, 'PENDING', 'PROCESSING', 2, '张处理', '接单并开始处理'),
(3, 'PROCESSING', 'RESOLVED', 2, '张处理', '已优化查询性能，响应时间降至1秒内'),
(1, 'PENDING', 'PROCESSING', 2, '张处理', '接单处理');

-- 插入评论记录
INSERT INTO `ticket_comments` (`ticket_id`, `user_id`, `user_name`, `content`, `is_internal`) VALUES
(1, 4, '王用户', '问题比较紧急，请尽快处理，谢谢！', 0),
(1, 2, '张处理', '已定位问题，是CSS兼容性问题，正在修复中', 0),
(3, 5, '赵客户', '这个问题严重影响工作效率，希望能够优先处理', 0),
(3, 2, '张处理', '内部备注：已添加索引优化SQL查询', 1);

-- ==========================================
-- 查询视图
-- ==========================================

-- 工单统计视图
CREATE OR REPLACE VIEW `v_ticket_statistics` AS
SELECT
    DATE(create_time) AS stat_date,
    category,
    priority,
    status,
    COUNT(*) AS ticket_count,
    AVG(TIMESTAMPDIFF(HOUR, create_time, COALESCE(resolved_time, NOW()))) AS avg_process_hours
FROM tickets
WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(create_time), category, priority, status;

-- 用户工单统计视图
CREATE OR REPLACE VIEW `v_user_ticket_statistics` AS
SELECT
    u.id AS user_id,
    u.username,
    u.real_name,
    u.role,
    COUNT(DISTINCT t.id) AS total_tickets,
    SUM(CASE WHEN t.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_tickets,
    SUM(CASE WHEN t.status = 'PROCESSING' THEN 1 ELSE 0 END) AS processing_tickets,
    SUM(CASE WHEN t.status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved_tickets,
    SUM(CASE WHEN t.status = 'CLOSED' THEN 1 ELSE 0 END) AS closed_tickets
FROM users u
LEFT JOIN tickets t ON (u.id = t.creator_id OR u.id = t.handler_id)
GROUP BY u.id, u.username, u.real_name, u.role;
