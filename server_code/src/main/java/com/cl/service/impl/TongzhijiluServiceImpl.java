package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.TongzhijiluDao;
import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通知记录服务实现类
 *
 * @author
 * @email
 * @date 2025-03-27 15:44:15
 */
@Service("tongzhijiluService")
public class TongzhijiluServiceImpl extends ServiceImpl<TongzhijiluDao, TongzhijiluEntity> implements TongzhijiluService {

    private static final Logger logger = LoggerFactory.getLogger(TongzhijiluServiceImpl.class);

    @Value("${notification.retry.max-count:3}")
    private int maxRetryCount;

    @Value("${notification.retry.base-interval:5}")
    private int baseRetryInterval;

    @Autowired
    private TongzhijiluDao tongzhijiluDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhijiluEntity> page = this.selectPage(
                new Query<TongzhijiluEntity>(params).getPage(),
                new EntityWrapper<TongzhijiluEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper) {
        Page<TongzhijiluEntity> page = new Query<TongzhijiluEntity>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAppointmentNotifications(Long yuyueId, String yuyuebianhao, String yishengzhanghao,
                                                String zhanghao, String shouji, Date yuyueshijian) {
        logger.info("开始为预约[{}]创建通知记录", yuyuebianhao);

        Date now = new Date();

        // 1. 预约成功通知 - 立即发送
        TongzhijiluEntity successNotification = new TongzhijiluEntity();
        successNotification.setYuyuebianhao(yuyuebianhao);
        successNotification.setYishengzhanghao(yishengzhanghao);
        successNotification.setZhanghao(zhanghao);
        successNotification.setYonghuleixing(1);
        successNotification.setTongzhileixing(1);
        successNotification.setTongzhineirong(buildSuccessNotificationContent(yuyuebianhao, yuyueshijian));
        successNotification.setFasongzhuangtai(0);
        successNotification.setJihuafasongshijian(now);
        successNotification.setJieshourenshouji(shouji);
        successNotification.setChongshicishu(0);
        successNotification.setChulizhuangtai(0);
        successNotification.setJiuzhenshijian(yuyueshijian);
        successNotification.setAddtime(now);
        this.insert(successNotification);

        // 2. 就诊前1天提醒
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(yuyueshijian);
        cal1.add(Calendar.DAY_OF_MONTH, -1);
        if (cal1.getTime().after(now)) {
            TongzhijiluEntity dayBeforeNotification = new TongzhijiluEntity();
            dayBeforeNotification.setYuyuebianhao(yuyuebianhao);
            dayBeforeNotification.setYishengzhanghao(yishengzhanghao);
            dayBeforeNotification.setZhanghao(zhanghao);
            dayBeforeNotification.setYonghuleixing(1);
            dayBeforeNotification.setTongzhileixing(2);
            dayBeforeNotification.setTongzhineirong(buildDayBeforeNotificationContent(yuyuebianhao, yuyueshijian));
            dayBeforeNotification.setFasongzhuangtai(0);
            dayBeforeNotification.setJihuafasongshijian(cal1.getTime());
            dayBeforeNotification.setJieshourenshouji(shouji);
            dayBeforeNotification.setChongshicishu(0);
            dayBeforeNotification.setChulizhuangtai(0);
            dayBeforeNotification.setJiuzhenshijian(yuyueshijian);
            dayBeforeNotification.setAddtime(now);
            this.insert(dayBeforeNotification);
        }

        // 3. 就诊前2小时提醒
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(yuyueshijian);
        cal2.add(Calendar.HOUR_OF_DAY, -2);
        if (cal2.getTime().after(now)) {
            TongzhijiluEntity twoHourNotification = new TongzhijiluEntity();
            twoHourNotification.setYuyuebianhao(yuyuebianhao);
            twoHourNotification.setYishengzhanghao(yishengzhanghao);
            twoHourNotification.setZhanghao(zhanghao);
            twoHourNotification.setYonghuleixing(1);
            twoHourNotification.setTongzhileixing(3);
            twoHourNotification.setTongzhineirong(buildTwoHourNotificationContent(yuyuebianhao, yuyueshijian));
            twoHourNotification.setFasongzhuangtai(0);
            twoHourNotification.setJihuafasongshijian(cal2.getTime());
            twoHourNotification.setJieshourenshouji(shouji);
            twoHourNotification.setChongshicishu(0);
            twoHourNotification.setChulizhuangtai(0);
            twoHourNotification.setJiuzhenshijian(yuyueshijian);
            twoHourNotification.setAddtime(now);
            this.insert(twoHourNotification);
        }

        // 4. 就诊前15分钟提醒
        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(yuyueshijian);
        cal3.add(Calendar.MINUTE, -15);
        if (cal3.getTime().after(now)) {
            TongzhijiluEntity fifteenMinNotification = new TongzhijiluEntity();
            fifteenMinNotification.setYuyuebianhao(yuyuebianhao);
            fifteenMinNotification.setYishengzhanghao(yishengzhanghao);
            fifteenMinNotification.setZhanghao(zhanghao);
            fifteenMinNotification.setYonghuleixing(1);
            fifteenMinNotification.setTongzhileixing(4);
            fifteenMinNotification.setTongzhineirong(buildFifteenMinNotificationContent(yuyuebianhao, yuyueshijian));
            fifteenMinNotification.setFasongzhuangtai(0);
            fifteenMinNotification.setJihuafasongshijian(cal3.getTime());
            fifteenMinNotification.setJieshourenshouji(shouji);
            fifteenMinNotification.setChongshicishu(0);
            fifteenMinNotification.setChulizhuangtai(0);
            fifteenMinNotification.setJiuzhenshijian(yuyueshijian);
            fifteenMinNotification.setAddtime(now);
            this.insert(fifteenMinNotification);
        }

        logger.info("预约[{}]的通知记录创建完成", yuyuebianhao);
    }

    /**
     * 构建预约成功通知内容
     */
    private String buildSuccessNotificationContent(String yuyuebianhao, Date yuyueshijian) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("【预约成功】您的预约[%s]已成功，就诊时间为%s，请准时到达。", 
                yuyuebianhao, sdf.format(yuyueshijian));
    }

    /**
     * 构建就诊前1天提醒内容
     */
    private String buildDayBeforeNotificationContent(String yuyuebianhao, Date yuyueshijian) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("【就诊提醒】您明天(%s)有预约就诊[%s]，请做好准备。", 
                sdf.format(yuyueshijian), yuyuebianhao);
    }

    /**
     * 构建就诊前2小时提醒内容
     */
    private String buildTwoHourNotificationContent(String yuyuebianhao, Date yuyueshijian) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return String.format("【就诊提醒】您今天%s有预约就诊[%s]，请提前出发。", 
                sdf.format(yuyueshijian), yuyuebianhao);
    }

    /**
     * 构建就诊前15分钟提醒内容
     */
    private String buildFifteenMinNotificationContent(String yuyuebianhao, Date yuyueshijian) {
        return String.format("【就诊提醒】您的预约[%s]即将开始，请前往就诊地点。", yuyuebianhao);
    }

    @Override
    @Async("notificationExecutor")
    public boolean sendNotificationWithRetry(Long tongzhiId) {
        TongzhijiluEntity notification = this.selectById(tongzhiId);
        if (notification == null) {
            logger.error("通知记录不存在: {}", tongzhiId);
            return false;
        }

        // 更新为发送中状态
        notification.setFasongzhuangtai(3);
        this.updateById(notification);

        try {
            // 模拟发送通知（实际项目中调用短信/推送服务）
            boolean success = doSendNotification(notification);

            if (success) {
                notification.setFasongzhuangtai(1);
                notification.setShijifasongshijian(new Date());
                notification.setShibaiyuanyin(null);
                this.updateById(notification);
                logger.info("通知[{}]发送成功", tongzhiId);
                return true;
            } else {
                throw new RuntimeException("发送失败");
            }
        } catch (Exception e) {
            logger.error("通知[{}]发送失败: {}", tongzhiId, e.getMessage());
            handleSendFailure(notification, e.getMessage());
            return false;
        }
    }

    /**
     * 实际发送通知（模拟实现）
     */
    private boolean doSendNotification(TongzhijiluEntity notification) {
        // TODO: 实际项目中调用短信网关或推送服务
        // 这里模拟发送，随机成功或失败
        return Math.random() > 0.1; // 90%成功率
    }

    /**
     * 处理发送失败
     */
    private void handleSendFailure(TongzhijiluEntity notification, String errorMsg) {
        int retryCount = notification.getChongshicishu() + 1;
        notification.setChongshicishu(retryCount);
        notification.setShibaiyuanyin(errorMsg);

        if (retryCount >= maxRetryCount) {
            // 超过最大重试次数，标记为失败
            notification.setFasongzhuangtai(2);
            logger.warn("通知[{}]超过最大重试次数，标记为失败", notification.getId());
        } else {
            // 计算下次重试时间（指数退避算法）
            int delayMinutes = baseRetryInterval * (int) Math.pow(2, retryCount - 1);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, delayMinutes);
            notification.setXiacichongshishijian(cal.getTime());
            notification.setFasongzhuangtai(2);
            logger.info("通知[{}]将在{}分钟后重试", notification.getId(), delayMinutes);
        }

        this.updateById(notification);
    }

    @Override
    public Map<String, Object> batchSendNotifications(List<Long> tongzhiIds) {
        Map<String, Object> result = new HashMap<>();
        int success = 0;
        int failed = 0;

        for (Long id : tongzhiIds) {
            if (sendNotificationWithRetry(id)) {
                success++;
            } else {
                failed++;
            }
        }

        result.put("total", tongzhiIds.size());
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }

    @Override
    public boolean manualRetry(Long tongzhiId) {
        TongzhijiluEntity notification = this.selectById(tongzhiId);
        if (notification == null) {
            return false;
        }

        // 重置重试次数和状态
        notification.setChongshicishu(0);
        notification.setFasongzhuangtai(0);
        notification.setXiacichongshishijian(null);
        notification.setShibaiyuanyin(null);
        this.updateById(notification);

        // 立即发送
        return sendNotificationWithRetry(tongzhiId);
    }

    @Override
    public Map<String, Object> batchManualRetry(List<Long> tongzhiIds) {
        Map<String, Object> result = new HashMap<>();
        int success = 0;
        int failed = 0;

        for (Long id : tongzhiIds) {
            if (manualRetry(id)) {
                success++;
            } else {
                failed++;
            }
        }

        result.put("total", tongzhiIds.size());
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }

    @Override
    public boolean markAsProcessed(Long tongzhiId, String chuliren, String chulibeizhu) {
        TongzhijiluEntity notification = this.selectById(tongzhiId);
        if (notification == null) {
            return false;
        }

        notification.setChulizhuangtai(1);
        notification.setChuliren(chuliren);
        notification.setChulishijian(new Date());
        notification.setChulibeizhu(chulibeizhu);

        return this.updateById(notification);
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics() {
        return tongzhijiluDao.selectStatusStatistics();
    }

    @Override
    public List<TongzhijiluEntity> exportFailedRecords(Map<String, Object> params) {
        Wrapper<TongzhijiluEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("fasongzhuangtai", 2);

        if (params.get("startTime") != null) {
            wrapper.ge("addtime", params.get("startTime"));
        }
        if (params.get("endTime") != null) {
            wrapper.le("addtime", params.get("endTime"));
        }

        return this.selectList(wrapper);
    }

    @Override
    public void processPendingNotifications() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        List<TongzhijiluEntity> pendingList = tongzhijiluDao.selectPendingNotifications(currentTime);
        logger.info("发现{}条待发送通知", pendingList.size());

        for (TongzhijiluEntity notification : pendingList) {
            sendNotificationWithRetry(notification.getId());
        }
    }

    @Override
    public void processRetryNotifications() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        List<TongzhijiluEntity> retryList = tongzhijiluDao.selectRetryNotifications(currentTime, maxRetryCount);
        logger.info("发现{}条需要重试的通知", retryList.size());

        for (TongzhijiluEntity notification : retryList) {
            sendNotificationWithRetry(notification.getId());
        }
    }
}
