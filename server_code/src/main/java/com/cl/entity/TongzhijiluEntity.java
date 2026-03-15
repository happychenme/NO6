package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知记录
 * 用于跟踪和管理就诊通知的发送状态
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("tongzhijilu")
public class TongzhijiluEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;

	/**
	 * 预约编号
	 */
	private String yuyuebianhao;

	/**
	 * 医生账号
	 */
	private String yishengzhanghao;

	/**
	 * 用户账号
	 */
	private String zhanghao;

	/**
	 * 用户类型(1-用户, 2-医生)
	 */
	private Integer yonghuleixing;

	/**
	 * 通知类型(1-预约成功通知, 2-就诊前1天提醒, 3-就诊前2小时提醒, 4-就诊前15分钟提醒)
	 */
	private Integer tongzhileixing;

	/**
	 * 通知内容
	 */
	private String tongzhineirong;

	/**
	 * 发送状态(0-待发送, 1-发送成功, 2-发送失败, 3-发送中)
	 */
	private Integer fasongzhuangtai;

	/**
	 * 计划发送时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date jihuafasongshijian;

	/**
	 * 实际发送时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date shijifasongshijian;

	/**
	 * 失败原因
	 */
	private String shibaiyuanyin;

	/**
	 * 重试次数
	 */
	private Integer chongshicishu;

	/**
	 * 下次重试时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date xiacichongshishijian;

	/**
	 * 接收人手机号
	 */
	private String jieshourenshouji;

	/**
	 * 处理状态(0-未处理, 1-已处理, 2-已忽略)
	 */
	private Integer chulizhuangtai;

	/**
	 * 处理人
	 */
	private String chuliren;

	/**
	 * 处理时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date chulishijian;

	/**
	 * 处理备注
	 */
	private String chulibeizhu;

	/**
	 * 就诊时间(用于计算提醒时间)
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date jiuzhenshijian;

	/**
	 * 创建时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getYuyuebianhao() {
		return yuyuebianhao;
	}

	public void setYuyuebianhao(String yuyuebianhao) {
		this.yuyuebianhao = yuyuebianhao;
	}

	public String getYishengzhanghao() {
		return yishengzhanghao;
	}

	public void setYishengzhanghao(String yishengzhanghao) {
		this.yishengzhanghao = yishengzhanghao;
	}

	public String getZhanghao() {
		return zhanghao;
	}

	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}

	public Integer getYonghuleixing() {
		return yonghuleixing;
	}

	public void setYonghuleixing(Integer yonghuleixing) {
		this.yonghuleixing = yonghuleixing;
	}

	public Integer getTongzhileixing() {
		return tongzhileixing;
	}

	public void setTongzhileixing(Integer tongzhileixing) {
		this.tongzhileixing = tongzhileixing;
	}

	public String getTongzhineirong() {
		return tongzhineirong;
	}

	public void setTongzhineirong(String tongzhineirong) {
		this.tongzhineirong = tongzhineirong;
	}

	public Integer getFasongzhuangtai() {
		return fasongzhuangtai;
	}

	public void setFasongzhuangtai(Integer fasongzhuangtai) {
		this.fasongzhuangtai = fasongzhuangtai;
	}

	public Date getJihuafasongshijian() {
		return jihuafasongshijian;
	}

	public void setJihuafasongshijian(Date jihuafasongshijian) {
		this.jihuafasongshijian = jihuafasongshijian;
	}

	public Date getShijifasongshijian() {
		return shijifasongshijian;
	}

	public void setShijifasongshijian(Date shijifasongshijian) {
		this.shijifasongshijian = shijifasongshijian;
	}

	public String getShibaiyuanyin() {
		return shibaiyuanyin;
	}

	public void setShibaiyuanyin(String shibaiyuanyin) {
		this.shibaiyuanyin = shibaiyuanyin;
	}

	public Integer getChongshicishu() {
		return chongshicishu;
	}

	public void setChongshicishu(Integer chongshicishu) {
		this.chongshicishu = chongshicishu;
	}

	public Date getXiacichongshishijian() {
		return xiacichongshishijian;
	}

	public void setXiacichongshishijian(Date xiacichongshishijian) {
		this.xiacichongshishijian = xiacichongshishijian;
	}

	public String getJieshourenshouji() {
		return jieshourenshouji;
	}

	public void setJieshourenshouji(String jieshourenshouji) {
		this.jieshourenshouji = jieshourenshouji;
	}

	public Integer getChulizhuangtai() {
		return chulizhuangtai;
	}

	public void setChulizhuangtai(Integer chulizhuangtai) {
		this.chulizhuangtai = chulizhuangtai;
	}

	public String getChuliren() {
		return chuliren;
	}

	public void setChuliren(String chuliren) {
		this.chuliren = chuliren;
	}

	public Date getChulishijian() {
		return chulishijian;
	}

	public void setChulishijian(Date chulishijian) {
		this.chulishijian = chulishijian;
	}

	public String getChulibeizhu() {
		return chulibeizhu;
	}

	public void setChulibeizhu(String chulibeizhu) {
		this.chulibeizhu = chulibeizhu;
	}

	public Date getJiuzhenshijian() {
		return jiuzhenshijian;
	}

	public void setJiuzhenshijian(Date jiuzhenshijian) {
		this.jiuzhenshijian = jiuzhenshijian;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}
