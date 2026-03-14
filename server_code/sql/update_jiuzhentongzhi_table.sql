-- 就诊通知表结构更新
-- 为就诊通知表添加通知状态、重试次数、失败原因等字段

USE `cl515882190`;

-- 添加新字段到jiuzhentongzhi表
ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `tongzhileixing` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知类型' AFTER `tongzhibeizhu`,
ADD COLUMN `zhuangtai` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '待发送' COMMENT '通知状态' AFTER `tongzhileixing`,
ADD COLUMN `chongcishu` int(11) DEFAULT 0 COMMENT '重试次数' AFTER `zhuangtai`,
ADD COLUMN `max_chongcishu` int(11) DEFAULT 3 COMMENT '最大重试次数' AFTER `chongcishu`,
ADD COLUMN `shibaiyuanyin` text COLLATE utf8mb4_unicode_ci COMMENT '失败原因' AFTER `max_chongcishu`,
ADD COLUMN `xiaci_chongshishijian` datetime DEFAULT NULL COMMENT '下次重试时间' AFTER `shibaiyuanyin`,
ADD COLUMN `last_fasongshijian` datetime DEFAULT NULL COMMENT '最后发送时间' AFTER `xiaci_chongshishijian`,
ADD COLUMN `yuyue_id` bigint(20) DEFAULT NULL COMMENT '预约ID' AFTER `last_fasongshijian`,
ADD COLUMN `is_read` int(11) DEFAULT 0 COMMENT '是否已读' AFTER `yuyue_id`;

-- 更新现有数据的状态
UPDATE `jiuzhentongzhi` SET `zhuangtai` = '发送成功' WHERE `zhuangtai` IS NULL;
