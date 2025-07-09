-- bbh-back/src/main/resources/db/migration/init.sql

CREATE DATABASE IF NOT EXISTS bamboohub;
CREATE USER IF NOT EXISTS 'bamboohub'@'%' IDENTIFIED BY '7777';
GRANT ALL PRIVILEGES ON bamboohub.* TO 'bamboohub'@'%';
FLUSH PRIVILEGES;