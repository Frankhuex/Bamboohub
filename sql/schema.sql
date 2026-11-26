-- ============================================================
-- Bamboohub 数据库初始化脚本
-- 用法（首次在新机器部署时执行）：
--   mysql -u root -p < sql/schema.sql
-- ============================================================

-- 1. 创建数据库与默认访问账号（如需自定义密码请同步修改 application.properties 或启动参数）
CREATE DATABASE IF NOT EXISTS `bamboohub`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

CREATE USER IF NOT EXISTS 'bamboohub'@'%' IDENTIFIED BY '7777';
CREATE USER IF NOT EXISTS 'bamboohub'@'localhost' IDENTIFIED BY '7777';
GRANT ALL PRIVILEGES ON `bamboohub`.* TO 'bamboohub'@'%';
GRANT ALL PRIVILEGES ON `bamboohub`.* TO 'bamboohub'@'localhost';
FLUSH PRIVILEGES;

USE `bamboohub`;

SET FOREIGN_KEY_CHECKS = 0;

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `username` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
    `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
    `nickname` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 3. 书籍表
CREATE TABLE IF NOT EXISTS `book` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `start_para_id` int DEFAULT NULL,
    `end_para_id` int DEFAULT NULL,
    `scope` enum('ALLEDIT', 'ALLREAD', 'ALLSEARCH', 'PRIVATE') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ALLSEARCH',
    PRIMARY KEY (`id`),
    KEY `idx_book_start_para_id` (`start_para_id`),
    KEY `idx_book_end_para_id` (`end_para_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 4. 段落表
CREATE TABLE IF NOT EXISTS `paragraph` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `book_id` int NOT NULL,
    `author` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `content` text COLLATE utf8mb4_general_ci,
    `prev_para_id` int DEFAULT NULL,
    `next_para_id` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_paragraph_book_id` (`book_id`),
    CONSTRAINT `paragraph_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 补齐 book -> paragraph 的双向外键约束
ALTER TABLE `book`
    ADD CONSTRAINT `book_ibfk_1` FOREIGN KEY (`start_para_id`) REFERENCES `paragraph` (`id`) ON DELETE SET NULL,
    ADD CONSTRAINT `book_ibfk_2` FOREIGN KEY (`end_para_id`) REFERENCES `paragraph` (`id`) ON DELETE SET NULL;

-- 5. 书籍成员角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `book_id` int NOT NULL,
    `user_id` int NOT NULL,
    `role_type` enum('VIEWER', 'EDITOR', 'ADMIN', 'OWNER') COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Role Type',
    PRIMARY KEY (`id`),
    KEY `idx_role_book_id` (`book_id`),
    KEY `idx_role_user_id` (`user_id`),
    CONSTRAINT `role_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE,
    CONSTRAINT `role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 6. 段落贡献者角色表
CREATE TABLE IF NOT EXISTS `para_role` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `para_id` int NOT NULL,
    `user_id` int NOT NULL,
    `role_type` enum('CREATOR', 'CONTRIBUTOR') COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Role Type',
    PRIMARY KEY (`id`),
    KEY `idx_para_role_para_id` (`para_id`),
    KEY `idx_para_role_user_id` (`user_id`),
    CONSTRAINT `para_role_ibfk_1` FOREIGN KEY (`para_id`) REFERENCES `paragraph` (`id`) ON DELETE CASCADE,
    CONSTRAINT `para_role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- 7. 用户关注关系表
CREATE TABLE IF NOT EXISTS `follow` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `source_id` int NOT NULL,
    `target_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_follow_source_id` (`source_id`),
    KEY `idx_follow_target_id` (`target_id`),
    CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`source_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`target_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
