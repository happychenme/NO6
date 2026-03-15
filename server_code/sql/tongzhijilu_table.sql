-- 通知记录表
-- 用于存储和管理就诊通知的发送状态

CREATE TABLE IF NOT EXISTS `tongzhijilu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `yuyuebianhao` varchar(200) DEFAULT NULL COMMENT '预约编号',
  `yishengzhanghao` varchar(200) DEFAULT NULL COMMENT '医生账号',
  `zhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `yonghuleixing` int(11) DEFAULT '1' COMMENT '用户类型(1-用户, 2-医生)',
  `tongzhileixing` int(11) DEFAULT NULL COMMENT '通知类型(1-预约成功通知, 2-就诊前1天提醒, 3-就诊前2小时提醒, 4-就诊前15分钟提醒)',
  `tongzhineirong` text COMMENT '通知内容',
  `fasongzhuangtai` int(11) DEFAULT '0' COMMENT '发送状态(0-待发送, 1-发送成功, 2-发送失败, 3-发送中)',
  `jihuafasongshijian` datetime DEFAULT NULL COMMENT '计划发送时间',
  `shijifasongshijian` datetime DEFAULT NULL COMMENT '实际发送时间',
  `shibaiyuanyin` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `chongshicishu` int(11) DEFAULT '0' COMMENT '重试次数',
  `xiacichongshishijian` datetime DEFAULT NULL COMMENT '下次重试时间',
  `jieshourenshouji` varchar(200) DEFAULT NULL COMMENT '接收人手机号',
  `chulizhuangtai` int(11) DEFAULT '0' COMMENT '处理状态(0-未处理, 1-已处理, 2-已忽略)',
  `chuliren` varchar(200) DEFAULT NULL COMMENT '处理人',
  `chulishijian` datetime DEFAULT NULL COMMENT '处理时间',
  `chulibeizhu` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `jiuzhenshijian` datetime DEFAULT NULL COMMENT '就诊时间',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_yuyuebianhao` (`yuyuebianhao`),
  KEY `idx_fasongzhuangtai` (`fasongzhuangtai`),
  KEY `idx_jihuafasongshijian` (`jihuafasongshijian`),
  KEY `idx_chulizhuangtai` (`chulizhuangtai`),
  KEY `idx_zhanghao` (`zhanghao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- 添加表字段说明
ALTER TABLE `tongzhijilu` 
MODIFY COLUMN `yonghuleixing` int(11) DEFAULT '1' COMMENT '用户类型: 1-用户, 2-医生',
MODIFY COLUMN `tongzhileixing` int(11) DEFAULT NULL COMMENT '通知类型: 1-预约成功通知, 2-就诊前1天提醒, 3-就诊前2小时提醒, 4-就诊前15分钟提醒',
MODIFY COLUMN `fasongzhuangtai` int(11) DEFAULT '0' COMMENT '发送状态: 0-待发送, 1-发送成功, 2-发送失败, 3-发送中',
MODIFY COLUMN `chulizhuangtai` int(11) DEFAULT '0' COMMENT '处理状态: 0-未处理, 1-已处理, 2-已忽略';
