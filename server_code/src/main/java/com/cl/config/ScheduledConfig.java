package com.cl.config;

import com.cl.service.TongzhijiluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置类
 * 用于定时处理通知发送和重试
 */
@Configuration
@EnableScheduling
public class ScheduledConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledConfig.class);

    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 处理待发送通知
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processPendingNotifications() {
        logger.debug("开始执行待发送通知处理任务");
        try {
            tongzhijiluService.processPendingNotifications();
        } catch (Exception e) {
            logger.error("待发送通知处理任务执行失败", e);
        }
    }

    /**
     * 处理需要重试的通知
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void processRetryNotifications() {
        logger.debug("开始执行通知重试任务");
        try {
            tongzhijiluService.processRetryNotifications();
        } catch (Exception e) {
            logger.error("通知重试任务执行失败", e);
        }
    }

    /**
     * 清理过期通知记录（可选）
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredNotifications() {
        logger.info("开始清理过期通知记录");
        // TODO: 实现清理逻辑，如删除3个月前的已发送成功记录
    }
}
