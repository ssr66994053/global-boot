/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-04-25 创建
 */
package com.global.boot.boss.log.domain;

import com.global.common.lang.result.Status;
import com.global.common.util.ToString;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author 麦子（lvchen@yiji.com）
 */
public class BossOperationLogMessage {
	
	/**
	 * 客户端生成一个ID
	 */
	private String logMessageId;
	
	/**
	 * 系统名
	 */
	private String systemName;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 操作描述
	 */
	private String description;
	
	/**
	 * 方法名
	 */
	private String methodName;
	
	/**
	 * 请求路径
	 */
	private String requestUri;
	
	/**
	 * 处理结果
	 */
	private Status status;
	
	/**
	 * 参数列表
	 */
	private LogParameter[] logParameterList;
	
	/**
	 * 操作备注
	 */
	private String remark;
	
	/**
	 * 操作时间
	 */
	private Date operationTime;
	
	/**
	 * 操作IP
	 */
	private String operationIp;
	
	public void check() {
		Assert.hasLength(logMessageId, "消息ID不能为空");
		Assert.hasLength(systemName, "系统名为空");
		Assert.hasLength(methodName, "用户操作方法名为空");
		Assert.notNull(operationTime, "操作时间不能为空");
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public Date getOperationTime() {
		return operationTime;
	}
	
	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}
	
	public String getSystemName() {
		return systemName;
	}
	
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public LogParameter[] getLogParameterList() {
		return logParameterList;
	}
	
	public void setLogParameterList(LogParameter[] logParameterList) {
		this.logParameterList = logParameterList;
	}
	
	public String getLogMessageId() {
		return logMessageId;
	}
	
	public void setLogMessageId(String logMessageId) {
		this.logMessageId = logMessageId;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getRequestUri() {
		return requestUri;
	}
	
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	public String getOperationIp() {
		return operationIp;
	}
	
	public void setOperationIp(String operationIp) {
		this.operationIp = operationIp;
	}
}
