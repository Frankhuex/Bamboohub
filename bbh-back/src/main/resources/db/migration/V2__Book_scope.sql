-- 添加新列
ALTER TABLE book
    ADD COLUMN scope ENUM('ALLEDIT', 'ALLREAD', 'PRIVATE') NOT NULL DEFAULT 'PRIVATE';

-- 迁移数据
UPDATE book
SET scope =
    CASE
                WHEN is_public = 1 THEN 'ALLREAD'
                ELSE 'PRIVATE'
    END;

-- 删除旧列
ALTER TABLE book DROP COLUMN is_public;

ALTER TABLE `user`
    MODIFY COLUMN `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE `role`
    MODIFY COLUMN `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;



CREATE TABLE `para_role` (
     `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
     `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
     `para_id` int NOT NULL,
     `user_id` int NOT NULL,
     `role_type` enum(
        'CREATOR',
        'CONTRIBUTOR'
     ) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Role Type',
        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 添加外键（自动创建索引）
ALTER TABLE para_role
    ADD CONSTRAINT `para_role_ibfk_1`
        FOREIGN KEY (`para_id`) REFERENCES `paragraph` (`id`) ON DELETE CASCADE;

ALTER TABLE para_role
    ADD CONSTRAINT `para_role_ibfk_2`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;



ALTER TABLE book
    ADD COLUMN `end_para_id` int DEFAULT null;

ALTER TABLE book
    ADD CONSTRAINT `book_ibfk_2`
        FOREIGN KEY (`end_para_id`) REFERENCES `paragraph` (`id`) ON DELETE SET NULL;

ALTER TABLE `book`
    MODIFY COLUMN `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE `paragraph`
    MODIFY COLUMN `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;







CREATE TABLE `follow` (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
                             `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
                             `source_id` int NOT NULL,
                             `target_id` int NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 添加外键（自动创建索引）
ALTER TABLE follow
    ADD CONSTRAINT `follow_ibfk_1`
        FOREIGN KEY (`source_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

ALTER TABLE follow
    ADD CONSTRAINT `follow_ibfk_2`
        FOREIGN KEY (`target_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;



ALTER TABLE book
    MODIFY COLUMN scope ENUM('ALLEDIT', 'ALLREAD', 'ALLSEARCH','PRIVATE') NOT NULL DEFAULT 'ALLSEARCH';
