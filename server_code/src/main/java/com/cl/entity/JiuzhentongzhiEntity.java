package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 就诊通知
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("jiuzhentongzhi")
public class JiuzhentongzhiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public JiuzhentongzhiEntity() {
		
	}
	
	public JiuzhentongzhiEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 通知编号
	 */
					
	private String tongzhibianhao;
	
	/**
	 * 医生账号
	 */
					
	private String yishengzhanghao;
	
	/**
	 * 电话
	 */
					
	private String dianhua;
	
	/**
	 * 就诊时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date jiuzhenshijian;
	
	/**
	 * 通知时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date tongzhishijian;
	
	/**
	 * 账号
	 */
					
	private String zhanghao;
	
	/**
	 * 手机
	 */
					
	private String shouji;
	
	/**
	 * 通知备注
	 */
					
	private String tongzhibeizhu;
	
	/**
	 * 通知类型：就诊前提醒、检查准备提醒等
	 */
					
	private String tongzhileixing;
	
	/**
	 * 通知状态：待发送、发送中、发送成功、发送失败、已处理
	 */
					
	private String zhuangtai;
	
	/**
	 * 重试次数
	 */
					
	private Integer chongcishu;
	
	/**
	 * 最大重试次数
	 */
					
	private Integer maxChongcishu;
	
	/**
	 * 失败原因
	 */
					
	private String shibaiyuanyin;
	
	/**
	 * 下次重试时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date xiaciChongshishijian;
	
	/**
	 * 最后发送时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date lastFasongshijian;
	
	/**
	 * 预约ID，关联预约记录
	 */
					
	private Long yuyueId;
	
	/**
	 * 是否已读
	 */
					
	private Integer isRead;
	

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：通知编号
	 */
	public void setTongzhibianhao(String tongzhibianhao) {
		this.tongzhibianhao = tongzhibianhao;
	}
	/**
	 * 获取：通知编号
	 */
	public String getTongzhibianhao() {
		return tongzhibianhao;
	}
	/**
	 * 设置：医生账号
	 */
	public void setYishengzhanghao(String yishengzhanghao) {
		this.yishengzhanghao = yishengzhanghao;
	}
	/**
	 * 获取：医生账号
	 */
	public String getYishengzhanghao() {
		return yishengzhanghao;
	}
	/**
	 * 设置：电话
	 */
	public void setDianhua(String dianhua) {
		this.dianhua = dianhua;
	}
	/**
	 * 获取：电话
	 */
	public String getDianhua() {
		return dianhua;
	}
	/**
	 * 设置：就诊时间
	 */
	public void setJiuzhenshijian(Date jiuzhenshijian) {
		this.jiuzhenshijian = jiuzhenshijian;
	}
	/**
	 * 获取：就诊时间
	 */
	public Date getJiuzhenshijian() {
		return jiuzhenshijian;
	}
	/**
	 * 设置：通知时间
	 */
	public void setTongzhishijian(Date tongzhishijian) {
		this.tongzhishijian = tongzhishijian;
	}
	/**
	 * 获取：通知时间
	 */
	public Date getTongzhishijian() {
		return tongzhishijian;
	}
	/**
	 * 设置：账号
	 */
	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	/**
	 * 获取：账号
	 */
	public String getZhanghao() {
		return zhanghao;
	}
	/**
	 * 设置：手机
	 */
	public void setShouji(String shouji) {
		this.shouji = shouji;
	}
	/**
	 * 获取：手机
	 */
	public String getShouji() {
		return shouji;
	}
	/**
	 * 设置：通知备注
	 */
	public void setTongzhibeizhu(String tongzhibeizhu) {
		this.tongzhibeizhu = tongzhibeizhu;
	}
	/**
	 * 获取：通知备注
	 */
	public String getTongzhibeizhu() {
		return tongzhibeizhu;
	}
	/**
	 * 设置：通知类型
	 */
	public void setTongzhileixing(String tongzhileixing) {
		this.tongzhileixing = tongzhileixing;
	}
	/**
	 * 获取：通知类型
	 */
	public String getTongzhileixing() {
		return tongzhileixing;
	}
	/**
	 * 设置：通知状态
	 */
	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}
	/**
	 * 获取：通知状态
	 */
	public String getZhuangtai() {
		return zhuangtai;
	}
	/**
	 * 设置：重试次数
	 */
	public void setChongcishu(Integer chongcishu) {
		this.chongcishu = chongcishu;
	}
	/**
	 * 获取：重试次数
	 */
	public Integer getChongcishu() {
		return chongcishu;
	}
	/**
	 * 设置：最大重试次数
	 */
	public void setMaxChongcishu(Integer maxChongcishu) {
		this.maxChongcishu = maxChongcishu;
	}
	/**
	 * 获取：最大重试次数
	 */
	public Integer getMaxChongcishu() {
		return maxChongcishu;
	}
	/**
	 * 设置：失败原因
	 */
	public void setShibaiyuanyin(String shibaiyuanyin) {
		this.shibaiyuanyin = shibaiyuanyin;
	}
	/**
	 * 获取：失败原因
	 */
	public String getShibaiyuanyin() {
		return shibaiyuanyin;
	}
	/**
	 * 设置：下次重试时间
	 */
	public void setXiaciChongshishijian(Date xiaciChongshishijian) {
		this.xiaciChongshishijian = xiaciChongshishijian;
	}
	/**
	 * 获取：下次重试时间
	 */
	public Date getXiaciChongshishijian() {
		return xiaciChongshishijian;
	}
	/**
	 * 设置：最后发送时间
	 */
	public void setLastFasongshijian(Date lastFasongshijian) {
		this.lastFasongshijian = lastFasongshijian;
	}
	/**
	 * 获取：最后发送时间
	 */
	public Date getLastFasongshijian() {
		return lastFasongshijian;
	}
	/**
	 * 设置：预约ID
	 */
	public void setYuyueId(Long yuyueId) {
		this.yuyueId = yuyueId;
	}
	/**
	 * 获取：预约ID
	 */
	public Long getYuyueId() {
		return yuyueId;
	}
	/**
	 * 设置：是否已读
	 */
	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}
	/**
	 * 获取：是否已读
	 */
	public Integer getIsRead() {
		return isRead;
	}

}
