package com.cl.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.cl.annotation.SysLog;
import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.mapper.EntityWrapper;

/**
 * 通知记录
 * 后端接口
 * @author
 * @email
 * @date 2025-03-27 15:44:15
 */
@RestController
@RequestMapping("/tongzhijilu")
public class TongzhijiluController {

    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                  HttpServletRequest request) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();

        // 预约编号模糊查询
        if (StringUtils.isNotBlank(tongzhijilu.getYuyuebianhao())) {
            ew.like("yuyuebianhao", tongzhijilu.getYuyuebianhao());
        }

        // 医生账号模糊查询
        if (StringUtils.isNotBlank(tongzhijilu.getYishengzhanghao())) {
            ew.like("yishengzhanghao", tongzhijilu.getYishengzhanghao());
        }

        // 用户账号模糊查询
        if (StringUtils.isNotBlank(tongzhijilu.getZhanghao())) {
            ew.like("zhanghao", tongzhijilu.getZhanghao());
        }

        // 通知类型筛选
        if (tongzhijilu.getTongzhileixing() != null) {
            ew.eq("tongzhileixing", tongzhijilu.getTongzhileixing());
        }

        // 发送状态筛选
        if (tongzhijilu.getFasongzhuangtai() != null) {
            ew.eq("fasongzhuangtai", tongzhijilu.getFasongzhuangtai());
        }

        // 用户类型筛选
        if (tongzhijilu.getYonghuleixing() != null) {
            ew.eq("yonghuleixing", tongzhijilu.getYonghuleixing());
        }

        // 处理状态筛选
        if (tongzhijilu.getChulizhuangtai() != null) {
            ew.eq("chulizhuangtai", tongzhijilu.getChulizhuangtai());
        }

        // 时间范围筛选
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        if (StringUtils.isNotBlank(startTime)) {
            ew.ge("jihuafasongshijian", startTime);
        }
        if (StringUtils.isNotBlank(endTime)) {
            ew.le("jihuafasongshijian", endTime);
        }

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(ew, params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(TongzhijiluEntity tongzhijilu) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhijilu, "tongzhijilu"));
        return R.ok().put("data", tongzhijiluService.selectList(ew));
    }

    /**
     * 详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @SysLog("新增通知记录")
    public R save(@RequestBody TongzhijiluEntity tongzhijilu) {
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知记录")
    public R update(@RequestBody TongzhijiluEntity tongzhijilu) {
        tongzhijiluService.updateById(tongzhijilu);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除通知记录")
    public R delete(@RequestBody Long[] ids) {
        tongzhijiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 手动重试发送通知
     */
    @RequestMapping("/manualRetry/{id}")
    @SysLog("手动重试发送通知")
    public R manualRetry(@PathVariable("id") Long id) {
        boolean success = tongzhijiluService.manualRetry(id);
        if (success) {
            return R.ok("重试成功");
        } else {
            return R.error("重试失败");
        }
    }

    /**
     * 批量手动重试
     */
    @RequestMapping("/batchRetry")
    @SysLog("批量手动重试通知")
    public R batchRetry(@RequestBody List<Long> ids) {
        Map<String, Object> result = tongzhijiluService.batchManualRetry(ids);
        return R.ok("批量重试完成").put("data", result);
    }

    /**
     * 标记通知为已处理
     */
    @RequestMapping("/markAsProcessed/{id}")
    @SysLog("标记通知为已处理")
    public R markAsProcessed(@PathVariable("id") Long id,
                             @RequestParam(required = false) String remark,
                             HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        boolean success = tongzhijiluService.markAsProcessed(id, username, remark);
        if (success) {
            return R.ok("标记成功");
        } else {
            return R.error("标记失败");
        }
    }

    /**
     * 获取发送状态统计
     */
    @RequestMapping("/statistics")
    public R getStatistics() {
        List<Map<String, Object>> statistics = tongzhijiluService.getStatusStatistics();
        return R.ok().put("data", statistics);
    }

    /**
     * 导出失败记录
     */
    @RequestMapping("/exportFailed")
    public R exportFailedRecords(@RequestParam Map<String, Object> params) {
        List<TongzhijiluEntity> list = tongzhijiluService.exportFailedRecords(params);

        // 转换为CSV格式数据
        List<Map<String, Object>> exportData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (TongzhijiluEntity entity : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("预约编号", entity.getYuyuebianhao());
            map.put("医生账号", entity.getYishengzhanghao());
            map.put("用户账号", entity.getZhanghao());
            map.put("通知类型", getNotificationTypeName(entity.getTongzhileixing()));
            map.put("通知内容", entity.getTongzhineirong());
            map.put("计划发送时间", entity.getJihuafasongshijian() != null ? sdf.format(entity.getJihuafasongshijian()) : "");
            map.put("失败原因", entity.getShibaiyuanyin());
            map.put("重试次数", entity.getChongshicishu());
            map.put("接收手机号", entity.getJieshourenshouji());
            map.put("创建时间", entity.getAddtime() != null ? sdf.format(entity.getAddtime()) : "");
            exportData.add(map);
        }

        return R.ok().put("data", exportData);
    }

    /**
     * 触发批量发送任务
     */
    @RequestMapping("/triggerBatchSend")
    @SysLog("触发批量发送任务")
    public R triggerBatchSend() {
        tongzhijiluService.processPendingNotifications();
        return R.ok("批量发送任务已触发");
    }

    /**
     * 触发批量重试任务
     */
    @RequestMapping("/triggerBatchRetry")
    @SysLog("触发批量重试任务")
    public R triggerBatchRetry() {
        tongzhijiluService.processRetryNotifications();
        return R.ok("批量重试任务已触发");
    }

    /**
     * 获取通知类型名称
     */
    private String getNotificationTypeName(Integer type) {
        switch (type) {
            case 1:
                return "预约成功通知";
            case 2:
                return "就诊前1天提醒";
            case 3:
                return "就诊前2小时提醒";
            case 4:
                return "就诊前15分钟提醒";
            default:
                return "未知";
        }
    }
}
