

CREATE TABLE `book` (
                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                        `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `start_para_id` int DEFAULT NULL,
                        `is_public` tinyint COMMENT '1代表公开任何人都能查看，0代表私有' DEFAULT 0,
                        PRIMARY KEY (`id`),
                        KEY `start_para_id` (`start_para_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 13 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `paragraph` (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                             `book_id` int NOT NULL,
                             `author` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `content` text COLLATE utf8mb4_general_ci,
                             `prev_para_id` int DEFAULT NULL,
                             `next_para_id` int DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `book_id` (`book_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 87 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
                        `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
                        `username` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                        `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                        `nickname` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
                        PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 9 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `role` (
                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
                        `create_time` datetime DEFAULT NULL COMMENT 'Create Time',
                        `book_id` int NOT NULL,
                        `user_id` int NOT NULL,
                        `role_type` enum(
        'VIEWER',
        'EDITOR',
        'ADMIN',
        'OWNER'
    ) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Role Type',
                        PRIMARY KEY (`id`),
                        KEY `book_id` (`book_id`),
                        KEY `user_id` (`user_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 9 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;


ALTER TABLE book ADD CONSTRAINT `book_ibfk_1` FOREIGN KEY (`start_para_id`) REFERENCES `paragraph` (`id`) ON DELETE SET NULL;

ALTER TABLE paragraph ADD CONSTRAINT `paragraph_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE;

ALTER TABLE role ADD CONSTRAINT `role_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE;

ALTER TABLE role ADD CONSTRAINT `role_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS=1;