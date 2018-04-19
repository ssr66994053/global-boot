/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 19:23 创建
 *
 */
package com.global.boot.core;

import com.global.boot.core.listener.YijiApplicationRunListener;
import com.global.common.id.CodeGenerator;
import com.global.common.spring.ApplicationContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author qiubo@yiji.com
 */
public class Apps {
	/**
	 * 应用名称
	 */
	public static final String APP_NAME = "yiji.appName";
	public static final String HERA_ENABLE = "yiji.hera.enable";
	public static final String HERA_ZK_URL = "yiji.hera.zk.url";
	
	public static final String DEV_MODE_KEY = "yiji.devMode";
	/**
	 * 日志路径
	 */
	public static final String LOG_PATH = "yiji.log.path";
	
	public static final String HTTP_PORT = "server.port";
	/**
	 * 进程id
	 */
	public static final String PID = "yiji.pid";
	
	public static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";
	
	public static final String YIJI_BASE_PACKAGE = "yiji.basePackage";
	
	public static final String JSON_PRETTY_FORMAT = "jsonPrettyFormat";
	
	private static String logPath = null;
	
	private static String dataPath = null;
	
	public static String getAppName() {
		String name = System.getProperty(APP_NAME);
		if (name == null) {
			throw new AppConfigException("没有设置应用名称,请设置系统变量" + APP_NAME);
		}
		return name;
	}
	
	/**
	 * 获取日志路径
	 */
	public static String getLogPath() {
		if (logPath != null) {
			return logPath;
		}
		logPath = "/var/log/webapps/" + getAppName() + "/";
		return logPath;
	}
	
	/**
	 * 获取应用数据目录，组件如果要存放临时数据，请放在此目录
	 */
	public static String getAppDataPath() {
		if (dataPath != null) {
			return dataPath;
		}
		dataPath = System.getProperty("user.home") + "/appdata/" + getAppName() + "/";
		return dataPath;
	}
	
	/**
	 * 标注有{@link YijiBootApplication}注解所在的包
	 * @return
	 */
	public static String getBasePackage() {
		return System.getProperty(YIJI_BASE_PACKAGE);
	}
	
	public static String getAppSessionCookieName() {
		return Apps.getAppName() + "-session";
	}
	
	public static int getHttpPort() {
		return Integer.parseInt(System.getProperty(HTTP_PORT));
	}
	
	public static int getPid() {
		return Integer.parseInt(System.getProperty(PID));
	}
	
	/**
	 * 暴露info信息，可以通过 actuator info endpoint获取
	 * @param key key
	 * @param value value
	 */
	public static void exposeInfo(String key, Object value) {
		String infoKey = "info." + key;
		System.setProperty(infoKey, String.valueOf(value));
	}
	
	/**
	 * 当系统参数中没有{@link Apps#SPRING_PROFILE_ACTIVE}时，设置应用运行环境
	 */
	public static void setProfileIfNotExists(String profile) {
		if (!StringUtils.hasLength(System.getProperty(SPRING_PROFILE_ACTIVE))
			&& !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
			System.setProperty(SPRING_PROFILE_ACTIVE, profile);
		}
	}
	
	/**
	 * 是否是开发模式
	 */
	public static boolean isDevMode() {
		return Boolean.valueOf(System.getProperty(DEV_MODE_KEY));
	}
	
	/**
	 * @see CodeGenerator#getNodeCode()
	 */
	public static String getNodeCode() {
		return CodeGenerator.getNodeCode();
	}
	
	/**
	 * @see CodeGenerator#getSystemCode()
	 */
	public static String getSystemCode() {
		return CodeGenerator.getSystemCode();
	}

	/**
	 * 关闭应用
     */
	public static void shutdown(){
		YijiApplicationRunListener.shutdownApp();
	}

	public static Environment getEnvironment(){
		return EnvironmentHolder.get();
	}

	public static ApplicationContext getApplicationContext(){
		return ApplicationContextHolder.get();
	}

	public static <T> T buildProperties(Class<T> clazz){
		return EnvironmentHolder.buildProperties(clazz);
	}

	public static void buildProperties(Object target) {
		EnvironmentHolder.buildProperties(target);
	}
	
}
