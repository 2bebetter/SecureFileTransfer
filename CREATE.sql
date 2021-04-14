create schema filetransfer;
use filetransfer;
DROP TABLE IF EXISTS `user`;

-- user
CREATE TABLE `user`
(
	`id` VARCHAR(16) NOT NULL,
	`username` VARCHAR(20) CHARACTER SET UTF8 COLLATE UTF8_BIN NOT NULL,
	`password` VARCHAR(20) CHARACTER SET UTF8 COLLATE UTF8_BIN NOT NULL,
    `last_time` LONG NOT NULL,
	PRIMARY KEY(`id`)
);

SELECT COUNT(*) count, table_schema FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = 'filetransfer' GROUP BY table_schema;