package com.cl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.JiuzhentongzhiDao;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.entity.view.JiuzhentongzhiView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("jiuzhentongzhiService")
public class JiuzhentongzhiServiceImpl extends ServiceImpl<JiuzhentongzhiDao, JiuzhentongzhiEntity> implements JiuzhentongzhiService {
    
    private static final Logger logger = LoggerFactory.getLogger(JiuzhentongzhiServiceImpl.class);
    
    private static final String STATUS_PENDING = "待发送";
    private static final String STATUS_SENDING = "发送中";
    private static final String STATUS_SUCCESS = "发送成功";
    private static final String STATUS_FAILED = "发送失败";
    private static final String STATUS_HANDLED = "已处理";
    
    private static final String TYPE_BEFORE_APPOINTMENT = "就诊前提醒";
    private static final String TYPE_PREPARATION = "检查准备提醒";
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiuzhentongzhiEntity> page = this.selectPage(
                new Query<JiuzhentongzhiEntity>(params).getPage(),
                new EntityWrapper<JiuzhentongzhiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		  Page<JiuzhentongzhiView> page =new Query<JiuzhentongzhiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<JiuzhentongzhiView> selectListView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public JiuzhentongzhiView selectView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	public void createNotificationsForAppointment(YishengyuyueEntity appointment) {
	    logger.info("开始为预约 {} 创建通知", appointment.getYuyuebianhao());
	    
	    List<JiuzhentongzhiEntity> notifications = new ArrayList<>();
	    
	    Date appointmentTime = appointment.getYuyueshijian();
	    
	    JiuzhentongzhiEntity beforeNotification = createNotification(appointment, TYPE_BEFORE_APPOINTMENT, 
	            calculateNotificationTime(appointmentTime, -1, 0), "请提前15分钟到达医院，携带相关证件");
	    notifications.add(beforeNotification);
	    
	    JiuzhentongzhiEntity preparationNotification = createNotification(appointment, TYPE_PREPARATION, 
	            calculateNotificationTime(appointmentTime, 0, -2), "请做好检查前准备，如空腹等");
	    notifications.add(preparationNotification);
	    
	    this.insertBatch(notifications);
	    
	    logger.info("成功为预约 {} 创建了 {} 条通知", appointment.getYuyuebianhao(), notifications.size());
	}
	
	private JiuzhentongzhiEntity createNotification(YishengyuyueEntity appointment, String type, 
	        Date notifyTime, String remark) {
	    JiuzhentongzhiEntity notification = new JiuzhentongzhiEntity();
	    notification.setTongzhibianhao(UUID.randomUUID().toString().substring(0, 10));
	    notification.setYishengzhanghao(appointment.getYishengzhanghao());
	    notification.setDianhua(appointment.getDianhua());
	    notification.setJiuzhenshijian(appointment.getYuyueshijian());
	    notification.setTongzhishijian(notifyTime);
	    notification.setZhanghao(appointment.getZhanghao());
	    notification.setShouji(appointment.getShouji());
	    notification.setTongzhibeizhu(remark);
	    notification.setTongzhileixing(type);
	    notification.setZhuangtai(STATUS_PENDING);
	    notification.setChongcishu(0);
	    notification.setMaxChongcishu(3);
	    notification.setYuyueId(appointment.getId());
	    notification.setIsRead(0);
	    notification.setAddtime(new Date());
	    return notification;
	}
	
	private Date calculateNotificationTime(Date appointmentTime, int hoursOffset, int daysOffset) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(appointmentTime);
	    calendar.add(Calendar.HOUR, hoursOffset);
	    calendar.add(Calendar.DAY_OF_MONTH, daysOffset);
	    return calendar.getTime();
	}
	
	@Override
	public boolean sendNotification(JiuzhentongzhiEntity notification) {
	    logger.info("开始发送通知: {}", notification.getId());
	    
	    try {
	        notification.setZhuangtai(STATUS_SENDING);
	        notification.setLastFasongshijian(new Date());
	        this.updateById(notification);
	        
	        boolean sendSuccess = performSend(notification);
	        
	        if (sendSuccess) {
	            notification.setZhuangtai(STATUS_SUCCESS);
	            notification.setShibaiyuanyin(null);
	            this.updateById(notification);
	            logger.info("通知发送成功: {}", notification.getId());
	            return true;
	        } else {
	            handleSendFailure(notification, "发送失败");
	            return false;
	        }
	    } catch (Exception e) {
	        logger.error("发送通知时发生异常: {}", notification.getId(), e);
	        handleSendFailure(notification, e.getMessage());
	        return false;
	    }
	}
	
	private boolean performSend(JiuzhentongzhiEntity notification) {
	    logger.info("模拟发送通知到手机: {}, 内容: {}", notification.getShouji(), notification.getTongzhibeizhu());
	    return true;
	}
	
	private void handleSendFailure(JiuzhentongzhiEntity notification, String reason) {
	    notification.setZhuangtai(STATUS_FAILED);
	    notification.setShibaiyuanyin(reason);
	    notification.setChongcishu(notification.getChongcishu() + 1);
	    
	    if (notification.getChongcishu() < notification.getMaxChongcishu()) {
	        notification.setXiaciChongshishijian(calculateNextRetryTime(notification.getChongcishu()));
	    } else {
	        notification.setXiaciChongshishijian(null);
	    }
	    
	    this.updateById(notification);
	    logger.warn("通知发送失败: {}, 原因: {}, 重试次数: {}/{}", 
	            notification.getId(), reason, notification.getChongcishu(), notification.getMaxChongcishu());
	}
	
	private Date calculateNextRetryTime(int retryCount) {
	    Calendar calendar = Calendar.getInstance();
	    switch (retryCount) {
	        case 1:
	            calendar.add(Calendar.MINUTE, 30);
	            break;
	        case 2:
	            calendar.add(Calendar.HOUR, 2);
	            break;
	        default:
	            calendar.add(Calendar.HOUR, 4);
	            break;
	    }
	    return calendar.getTime();
	}
	
	@Override
	public void retryFailedNotifications() {
	    logger.info("开始重试失败的通知");
	    
	    EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
	    wrapper.eq("zhuangtai", STATUS_FAILED);
	    wrapper.isNotNull("xiaci_chongshishijian");
	    wrapper.le("xiaci_chongshishijian", new Date());
	    
	    List<JiuzhentongzhiEntity> failedNotifications = this.selectList(wrapper);
	    
	    for (JiuzhentongzhiEntity notification : failedNotifications) {
	        if (notification.getChongcishu() < notification.getMaxChongcishu()) {
	            sendNotification(notification);
	        }
	    }
	    
	    logger.info("完成重试失败的通知，共处理 {} 条", failedNotifications.size());
	}
	
	@Override
	public void processPendingNotifications() {
	    logger.info("开始处理待发送的通知");
	    
	    EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
	    wrapper.eq("zhuangtai", STATUS_PENDING);
	    wrapper.le("tongzhishijian", new Date());
	    
	    List<JiuzhentongzhiEntity> pendingNotifications = this.selectList(wrapper);
	    
	    for (JiuzhentongzhiEntity notification : pendingNotifications) {
	        sendNotification(notification);
	    }
	    
	    logger.info("完成处理待发送的通知，共处理 {} 条", pendingNotifications.size());
	}
	
	@Override
	public void manualRetry(Long notificationId) {
	    logger.info("手动重试通知: {}", notificationId);
	    
	    JiuzhentongzhiEntity notification = this.selectById(notificationId);
	    if (notification != null) {
	        notification.setChongcishu(0);
	        notification.setZhuangtai(STATUS_PENDING);
	        notification.setShibaiyuanyin(null);
	        this.updateById(notification);
	        sendNotification(notification);
	    }
	}
	
	@Override
	public void markAsHandled(Long notificationId) {
	    logger.info("标记通知为已处理: {}", notificationId);
	    
	    JiuzhentongzhiEntity notification = this.selectById(notificationId);
	    if (notification != null) {
	        notification.setZhuangtai(STATUS_HANDLED);
	        this.updateById(notification);
	    }
	}
	
	@Override
	public List<JiuzhentongzhiEntity> getNotificationsByAppointmentId(Long appointmentId) {
	    EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
	    wrapper.eq("yuyue_id", appointmentId);
	    return this.selectList(wrapper);
	}


}
