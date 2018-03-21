/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.yiji.boot.boss;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author zhouxi@yiji.com
 */
@ConfigurationProperties("yiji.boss")
public class BOSSProperties {
	
	/**
	 * 默认不进行过滤的地址
	 */
	public static final String FILTER_CHAIN_DEF = "    /css/**           = anon\n" + "    /js/**            = anon\n"
													+ "    /images/**        = anon\n"
													+ "    /resources/**     = anon\n"
													+ "    /services/**      = anon\n"
													+ "    /**               = permissionFilter";
	
	/**
	 * 默认不进行过滤的地址不使用权限
	 */
	public static final String FILTER_CHAIN_DEF_NO_PERMISSION = "    /css/**           = anon\n"
																+ "    /js/**            = anon\n"
																+ "    /images/**        = anon\n"
																+ "    /resources/**     = anon\n"
																+ "    /services/**      = anon";
	
	/**
	 * 是否启用此组件
	 */
	private boolean enable = true;
	
	/**
	 * boss子系统的地址
	 */
	private String serverAddress;
	
	/**
	 * 启用uri权限过滤
	 */
	private boolean enablePermission = true;
	
	/**
	 * 是否为boss管理系统,一般情况下都不是
	 */
	private boolean isManageServer = false;
	
	/**
	 * 日志收集的消息队列
	 */
	private String logMqName = BossConstants.DEFAULT_MQ_NAME;
	
	/**
	 * 日志收集全局忽略的方法名,逗号分隔,支持ant路径格式
	 * @see org.springframework.util.AntPathMatcher
	 */
	private String logGlobalIgnoreMethods = "myHomePage,loadPermissionMenuInfo,getMenuInfoByMenuName";
	
	/**
	 * 日志收集全局忽略的参数名，便于忽略全局定义的一些不用传递的参数，比如，敏感参数，分页参数。逗号分隔,支持ant路径格式
	 */
	private String logGlobalIgnoreParamNamePatterns = "*password*,*pass*,*passwd*,*pwd*";
	
	/**
	 * 日志收集全局掩码的参数名字规则，敏感信息简单掩码处理。逗号分隔，部分匹配（如，配置password,会处理payPassword、loginPassword等）,支持ant路径格式
	 */
	private String logGlobalMaskParamNamePatterns = "*cardNo*,*idcard*,*mobileNo*,*userName*,*tel*,*path*,*certno*,*mobile*,*phone*,*email*,*image*,*name*,*pic*,*card*,*bank*,*qq*,*contact*";
	
	@PostConstruct
	public void check() {
		Assert.hasText(serverAddress, "boss子系统的地址必须设置");
	}
	
	public boolean isManageServer() {
		return isManageServer;
	}
	
	public void setManageServer(boolean isManageServer) {
		this.isManageServer = isManageServer;
	}
	
	public boolean isEnablePermission() {
		return enablePermission;
	}
	
	public void setEnablePermission(boolean enablePermission) {
		this.enablePermission = enablePermission;
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getLogMqName() {
		return logMqName;
	}
	
	public String getLogGlobalIgnoreMethods() {
		return logGlobalIgnoreMethods;
	}
	
	public void setLogGlobalIgnoreMethods(String logGlobalIgnoreMethods) {
		this.logGlobalIgnoreMethods = logGlobalIgnoreMethods;
	}
	
	public String getLogGlobalIgnoreParamNamePatterns() {
		return logGlobalIgnoreParamNamePatterns;
	}
	
	public void setLogGlobalIgnoreParamNamePatterns(String logGlobalIgnoreParamNamePatterns) {
		this.logGlobalIgnoreParamNamePatterns = logGlobalIgnoreParamNamePatterns;
	}
	
	public String getLogGlobalMaskParamNamePatterns() {
		return logGlobalMaskParamNamePatterns;
	}
	
	public void setLogGlobalMaskParamNamePatterns(String logGlobalMaskParamNamePatterns) {
		this.logGlobalMaskParamNamePatterns = logGlobalMaskParamNamePatterns;
	}
	
	public List<String> getLogGlobalIgnoreMethodList() {
		return splitToList(logGlobalIgnoreMethods);
	}
	
	public List<String> getLogGlobalIgnoreParamNamePatternList() {
		return splitToList(logGlobalIgnoreParamNamePatterns);
	}
	
	public List<String> getLogGlobalMaskParamNamePatternList() {
		return splitToList(logGlobalMaskParamNamePatterns);
	}
	
	private List<String> splitToList(String str) {
		List<String> list = Lists.newArrayList();
		if (str != null) {
			list = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
		}
		return list;
	}
	
}
