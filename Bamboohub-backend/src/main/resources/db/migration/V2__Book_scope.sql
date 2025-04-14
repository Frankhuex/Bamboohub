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