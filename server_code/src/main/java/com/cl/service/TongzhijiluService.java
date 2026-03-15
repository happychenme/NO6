package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.TongzhijiluEntity;
import com.cl.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 通知记录服务接口
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluService extends IService<TongzhijiluEntity> {

    /**
     * 分页查询通知记录
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询通知记录（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

    /**
     * 创建预约通知记录
     * @param yuyueId 预约ID
     * @param yuyuebianhao 预约编号
     * @param yishengzhanghao 医生账号
     * @param zhanghao 用户账号
     * @param shouji 手机号
     * @param yuyueshijian 预约时间
     */
    void createAppointmentNotifications(Long yuyueId, String yuyuebianhao, String yishengzhanghao,
                                        String zhanghao, String shouji, java.util.Date yuyueshijian);

    /**
     * 发送通知（带重试机制）
     * @param tongzhiId 通知记录ID
     * @return 是否发送成功
     */
    boolean sendNotificationWithRetry(Long tongzhiId);

    /**
     * 批量发送通知
     * @param tongzhiIds 通知记录ID列表
     * @return 发送结果统计
     */
    Map<String, Object> batchSendNotifications(List<Long> tongzhiIds);

    /**
     * 手动重试发送失败的通知
     * @param tongzhiId 通知记录ID
     * @return 是否重试成功
     */
    boolean manualRetry(Long tongzhiId);

    /**
     * 批量手动重试
     * @param tongzhiIds 通知记录ID列表
     * @return 重试结果统计
     */
    Map<String, Object> batchManualRetry(List<Long> tongzhiIds);

    /**
     * 标记通知为已处理
     * @param tongzhiId 通知记录ID
     * @param chuliren 处理人
     * @param chulibeizhu 处理备注
     * @return 是否标记成功
     */
    boolean markAsProcessed(Long tongzhiId, String chuliren, String chulibeizhu);

    /**
     * 获取发送状态统计
     * @return 状态统计信息
     */
    List<Map<String, Object>> getStatusStatistics();

    /**
     * 导出失败记录
     * @param params 查询参数
     * @return 失败记录列表
     */
    List<TongzhijiluEntity> exportFailedRecords(Map<String, Object> params);

    /**
     * 处理待发送的通知（定时任务调用）
     */
    void processPendingNotifications();

    /**
     * 处理需要重试的失败通知（定时任务调用）
     */
    void processRetryNotifications();
}
