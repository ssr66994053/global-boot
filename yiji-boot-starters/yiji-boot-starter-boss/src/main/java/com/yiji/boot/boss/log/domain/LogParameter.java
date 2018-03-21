/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 *
 * 修订记录:
 * lvchen@yiji.com 2016-04-29 11:44 创建
 *
 */
package com.yiji.boot.boss.log.domain;

/**
 * @author 麦子（lvchen@yiji.com）
 */
public class LogParameter {
	
	/**
	 * 参数名
	 */
	private String parameterName;
	
	/**
	 * 参数值
	 */
	private String parameterValue;
	
	/**
	 * 参数描述
	 */
	private String parameterDesc;
	
	/**
	 * 是否忽略
	 */
	private boolean ignore;
	
	public LogParameter() {
	}
	
	public LogParameter(String parameterName, String paramterValue, String paramterDesc, boolean ignore) {
		this.ignore = ignore;
		this.parameterValue = paramterValue;
		this.parameterName = parameterName;
		this.parameterDesc = paramterDesc;
	}
	
	public LogParameter(String parameterName, String paramterValue) {
		
		this.parameterName = parameterName;
		this.parameterValue = paramterValue;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public String getParameterValue() {
		return parameterValue;
	}
	
	public String getParameterDesc() {
		return parameterDesc;
	}
	
	public void setParameterDesc(String parameterDesc) {
		this.parameterDesc = parameterDesc;
	}
	
	public boolean isIgnore() {
		return ignore;
	}
	
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
	@Override
	public String toString() {
		return "{" + parameterName + ":" + parameterValue + "}";
	}
}
