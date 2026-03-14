package com.cl.config;

import com.cl.service.JiuzhentongzhiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Scheduled(fixedRate = 60000)
    public void processPendingNotifications() {
        try {
            logger.info("定时任务：开始处理待发送通知");
            jiuzhentongzhiService.processPendingNotifications();
        } catch (Exception e) {
            logger.error("定时任务：处理待发送通知时发生异常", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void retryFailedNotifications() {
        try {
            logger.info("定时任务：开始重试失败通知");
            jiuzhentongzhiService.retryFailedNotifications();
        } catch (Exception e) {
            logger.error("定时任务：重试失败通知时发生异常", e);
        }
    }
}
