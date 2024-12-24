CREATE DATABASE IF NOT EXISTS bamboohub;
USE bamboohub;

SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `book` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `start_para_id` int DEFAULT NULL,
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

ALTER TABLE book ADD CONSTRAINT `book_ibfk_1` FOREIGN KEY (`start_para_id`) REFERENCES `paragraph` (`id`) ON DELETE SET NULL;

ALTER TABLE paragraph ADD CONSTRAINT `paragraph_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS=1;