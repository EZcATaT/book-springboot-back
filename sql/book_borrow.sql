-- ============================================================
-- 图书借阅管理系统 数据库初始化脚本
-- 数据库：MySQL 5.6+
-- 说明：脚本会重建 user、book、borrow 三张表并写入演示数据。
--       重复执行会清空原有数据，请谨慎在生产库中使用。
-- ============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `book_borrow`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `book_borrow`;

DROP TABLE IF EXISTS `borrow`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id`       INT          NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `username` VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `password` VARCHAR(100) NOT NULL COMMENT '登录密码',
    `name`     VARCHAR(50)  NOT NULL COMMENT '姓名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

CREATE TABLE `book` (
    `id`        INT          NOT NULL AUTO_INCREMENT COMMENT '图书id',
    `book_name` VARCHAR(100) NOT NULL COMMENT '书名',
    `author`    VARCHAR(50)  NOT NULL COMMENT '作者',
    `isbn`      VARCHAR(20)  NOT NULL COMMENT 'ISBN',
    `stock`     INT          NOT NULL DEFAULT 0 COMMENT '库存',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_isbn` (`isbn`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书表';

CREATE TABLE `borrow` (
    `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '借阅记录id',
    `user_id`     INT      NOT NULL COMMENT '用户id',
    `book_id`     INT      NOT NULL COMMENT '图书id',
    `borrow_time` DATETIME NOT NULL COMMENT '借阅时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '归还时间，NULL 表示未归还',
    PRIMARY KEY (`id`),
    KEY `idx_borrow_user_id` (`user_id`),
    KEY `idx_borrow_book_id` (`book_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '借阅记录表';

-- 用户：admin 为管理员，其余账号为普通用户
INSERT INTO `user` (`id`, `username`, `password`, `name`) VALUES
(1, 'admin',  '123456', '管理员'),
(2, 'reader', '123456', '读者小王'),
(3, 'lisi',   '123456', '李四');

-- 图书库存与未归还记录对应：
-- 三体当前借出1本，库存4；Spring Boot实战当前借出1本，库存3。
INSERT INTO `book` (`id`, `book_name`, `author`, `isbn`, `stock`) VALUES
(1, '三体',                 '刘慈欣',           '9787536692930', 4),
(2, '活着',                 '余华',             '9787506365437', 5),
(3, 'Spring Boot实战',      'Craig Walls',      '9787115441232', 3),
(4, '数据结构（C语言版）',   '严蔚敏',           '9787302147510', 2),
(5, '百年孤独',             '加西亚·马尔克斯',  '9787544253994', 4),
(6, '深入理解Java虚拟机',   '周志明',           '9787111641247', 3),
(7, '人类简史',             '尤瓦尔·赫拉利',    '9787508647357', 4),
(8, '代码整洁之道',         'Robert C. Martin', '9787115216878', 3);

-- 借阅记录：包含已归还和未归还两种状态
INSERT INTO `borrow` (`id`, `user_id`, `book_id`, `borrow_time`, `return_time`) VALUES
(1, 2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY),  NULL),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 3, 3, DATE_SUB(NOW(), INTERVAL 2 DAY),  NULL),
(4, 3, 5, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
(5, 2, 6, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY));
