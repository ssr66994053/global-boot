/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 15:43 创建
 */
package com.global.boot.appservice;

import com.global.boot.appservice.ex.ExceptionHandler;
import com.global.boot.core.Apps;
import com.yjf.common.lang.validator.Validators;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(AppServiceProperties.PREFIX)
public class AppServiceProperties {
	
	public static final String PREFIX = "yiji.appService";
	
	private static final String INTERNAL_EXCEPTION_HANDLER_PACKAGE = "com.global.boot.appservice.ex,"
																		+ Apps.getBasePackage();
	/**
	 * {@link AppService} annotation扫描路径
	 */
	@NotBlank
	private String appServiceScanPackage=Apps.getBasePackage();
	
	/**
	 * {@link ExceptionHandler} 实现类扫描路径
	 */
	private String exceptionHanderScanPackage;
	
	/**
	 * 默认参数校验实现类,可替换为其他实现类
	 */
	private String parameterCheckFilterImpl = "com.global.boot.appservice.filter.ParameterCheckFilter";
	
	public String getParameterCheckFilterImpl() {
		return parameterCheckFilterImpl;
	}
	
	public void setParameterCheckFilterImpl(String parameterCheckFilterImpl) {
		this.parameterCheckFilterImpl = parameterCheckFilterImpl;
	}
	
	public String getAppServiceScanPackage() {
		return appServiceScanPackage;
	}
	
	public void setAppServiceScanPackage(String appServiceScanPackage) {
		this.appServiceScanPackage = appServiceScanPackage;
	}
	
	public String getExceptionHanderScanPackage() {
		if (!StringUtils.isBlank(exceptionHanderScanPackage)) {
			return INTERNAL_EXCEPTION_HANDLER_PACKAGE + "," + exceptionHanderScanPackage.trim();
		}
		return INTERNAL_EXCEPTION_HANDLER_PACKAGE;
	}
	
	public void setExceptionHanderScanPackage(String exceptionHanderScanPackage) {
		this.exceptionHanderScanPackage = exceptionHanderScanPackage;
	}
	
	public void check() {
		Validators.checkJsr303(this);
	}
	
}
