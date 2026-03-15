package com.cl.dao;

import com.cl.entity.TongzhijiluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;

/**
 * 通知记录
 * 
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluDao extends BaseMapper<TongzhijiluEntity> {
	
	/**
	 * 查询待发送的通知列表
	 * @param currentTime 当前时间
	 * @return 待发送通知列表
	 */
	List<TongzhijiluEntity> selectPendingNotifications(@Param("currentTime") String currentTime);
	
	/**
	 * 查询需要重试的失败通知
	 * @param currentTime 当前时间
	 * @param maxRetryCount 最大重试次数
	 * @return 需要重试的通知列表
	 */
	List<TongzhijiluEntity> selectRetryNotifications(@Param("currentTime") String currentTime, 
	                                                  @Param("maxRetryCount") Integer maxRetryCount);
	
	/**
	 * 统计各状态的通知数量
	 * @return 状态统计列表
	 */
	List<Map<String, Object>> selectStatusStatistics();
	
	/**
	 * 查询发送失败的通知列表
	 * @param wrapper 查询条件
	 * @return 失败通知列表
	 */
	List<TongzhijiluEntity> selectFailedNotifications(@Param("ew") Wrapper<TongzhijiluEntity> wrapper);
	
	/**
	 * 批量更新发送状态
	 * @param ids 记录ID列表
	 * @param status 目标状态
	 * @return 更新记录数
	 */
	int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
	
	/**
	 * 分页查询通知记录
	 */
	List<TongzhijiluEntity> selectListView(Pagination page, @Param("ew") Wrapper<TongzhijiluEntity> wrapper);
	
	/**
	 * 查询通知记录列表
	 */
	List<TongzhijiluEntity> selectListView(@Param("ew") Wrapper<TongzhijiluEntity> wrapper);
}
