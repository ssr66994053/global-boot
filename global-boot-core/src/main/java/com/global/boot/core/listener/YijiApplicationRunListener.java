/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-12 10:42 创建
 *
 */
package com.global.boot.core.listener;

import ch.qos.logback.classic.LoggerContext;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.global.boot.core.AppConfigException;
import com.global.boot.core.Apps;
import com.global.boot.core.EnvironmentHolder;
import com.global.boot.core.YijiBootApplication;
import com.global.boot.core.log.YijiLogbackLoggingSystem;
import com.global.boot.core.log.initializer.ConsoleLogInitializer;
import com.global.common.env.Env;
import com.global.common.id.CodeGenerator;
import com.global.common.lang.ip.IPUtil;
import com.global.common.log.UncaughtExceptionHandlerWrapper;
import com.global.common.spring.ApplicationContextHolder;
import com.global.common.util.ShutdownHooks;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationPid;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.PrintStream;
import java.util.List;

/**
 * @author qiubo@yiji.com
 */
public class YijiApplicationRunListener implements SpringApplicationRunListener, PriorityOrdered {
	
	private static List<String> disabledPackageName = Lists.newArrayList("", "com.global", "com.yjf", "com.global.boot");
	
	public YijiApplicationRunListener(SpringApplication application, String[] args) {
		application.setRegisterShutdownHook(false);
		checkAndSetPackage(application);
		checkVersions();
		jvmPropstuning();
		YijiBootApplication yijiBootApplication = findYijiBootApplication(application);
		initEnvVars(yijiBootApplication);
		setThreadName();
		application.setBanner(new AppBanner(yijiBootApplication));
	}
	
	private void checkVersions() {
		if (SpringVersion.getVersion().startsWith("3")) {
			throw new AppConfigException("请确保org.springframework:spring-*版本大于3.x");
		}
		if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
			throw new AppConfigException("请使用jdk1.8及以上版本");
		}
	}
	
	private void checkAndSetPackage(SpringApplication application) {
		Assert.isTrue(application.getSources().size() == 1, "yiji-boot应用应该只有一个source");
		application.getSources().forEach(o -> {
			Package pkg = ((Class) o).getPackage();
			if (pkg == null || disabledPackageName.contains(pkg.getName())) {
				throw new AppConfigException("请把main-class定义到应用包中，禁止定义到以下包中:" + disabledPackageName);
			}
			System.setProperty(Apps.YIJI_BASE_PACKAGE, pkg.getName());
		});
	}
	
	private void setThreadName() {
		//todo 升级为1.4时，修改为main
		Thread.currentThread().setName("Main");
	}
	
	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
	}
	
	@Override
	public void started() {
	}
	
	private void initEnvVars(YijiBootApplication yijiBootApplication) {
		String sysName = yijiBootApplication.sysName();
		Assert.hasLength(sysName, "系统名不能为空");
		System.setProperty(Apps.APP_NAME, sysName);
		// ref ContextIdApplicationContextInitializer
		System.setProperty("spring.application.name", sysName);
		//set servlet container display name
		System.setProperty("server.display-name", sysName);
		//set servelt container response header server
		System.setProperty("server.server-header", "YIJI");
		
		System.setProperty(Apps.HERA_ENABLE, Boolean.toString(yijiBootApplication.heraEnable()));
		String portStr=System.getProperty("server.port");
		if(Strings.isNullOrEmpty(portStr)){
			System.setProperty(Apps.HTTP_PORT, Integer.toString(yijiBootApplication.httpPort()));
		}else{
			System.setProperty(Apps.HTTP_PORT, portStr);
		}
		System.setProperty(Apps.PID, new ApplicationPid().toString());
		//for extends log
		System.setProperty(LoggingSystem.SYSTEM_PROPERTY, YijiLogbackLoggingSystem.class.getName());
		//spring aop use cglib
		System.setProperty("spring.aop.proxy-target-class", Boolean.TRUE.toString());
		String logPath = Apps.getLogPath();
		System.setProperty(Apps.LOG_PATH, logPath);
	}
	
	private YijiBootApplication findYijiBootApplication(SpringApplication application) {
		return (YijiBootApplication) application.getSources().stream().map(o1 -> (Class) o1)
			.filter(o1 -> o1.isAnnotationPresent(YijiBootApplication.class)).findFirst()
			.orElseThrow(() -> new AppConfigException("启动类必须标注" + YijiBootApplication.class.getSimpleName()))
			.getAnnotation(YijiBootApplication.class);
	}
	
	private void jvmPropstuning() {
		//优选ipv4网络
		System.setProperty("java.net.preferIPv4Stack", "true");
		//rmi gc 间隔
		System.setProperty("sun.rmi.dgc.client.gcInterval", "7200000");
		System.setProperty("sun.rmi.dgc.server.gcInterval", "7200000");
		//启用headless模式
		System.setProperty("java.awt.headless", "true");
		//fix http://gitlab.yiji/qzhanbo/yiji-boot/issues/45
		System.setProperty("java.security.egd", "file:/dev/./urandom");
	}
	
	/**
	 * 初始化EnvironmentHolder
	 *
	 * <p>
	 * YijiApplicationRunListener实现了PriorityOrdered接口，
	 * 在environmentPrepared阶段会优先执行，在此阶段初始化EnvironmentHolder，可以用于日志系统配置时获取环境。 在日志系统配置中使用此类时，获取的环境不包括hera PropertySource
	 * </p>
	 */
	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		new EnvironmentHolder().setEnvironment(environment);
		setProfileIfEnableActiveProfiles(environment);
	}

	private void setProfileIfEnableActiveProfiles(ConfigurableEnvironment environment) {
		if (Strings.isNullOrEmpty(System.getProperty(Apps.SPRING_PROFILE_ACTIVE))) {
			String profile = environment.getProperty(Apps.SPRING_PROFILE_ACTIVE);
			if (!Strings.isNullOrEmpty(profile)) {
				System.setProperty(Apps.SPRING_PROFILE_ACTIVE, profile);
			}
		}
	}

	@Override
	public void contextPrepared(final ConfigurableApplicationContext context) {
		new DevToolsDetector().apply(context.getEnvironment());
		new AppCodeInitializor().run();
	}
	
	@Override
	public void finished(ConfigurableApplicationContext context, Throwable exception) {
		if (exception == null) {
			//install UncaughtExceptionHandler
			UncaughtExceptionHandlerWrapper.install();
			//when system startup ,register shutdown hooks to clean resouces.
			new ShutdownThread().register();
			//log startup info
			LoggerFactory.getLogger(YijiApplicationRunListener.class).info("启动成功: http://{}:{}",
				IPUtil.getFirstNoLoopbackIPV4Address(), context.getEnvironment().getProperty(Apps.HTTP_PORT));
		} else {
			ConsoleLogInitializer.addConsoleAppender();
			ShutdownHooks.shutdownAll();
			shutdownLogSystem();
		}
	}
	
	private static void shutdownLogSystem() {
		//关闭日志，日志需要最后关闭,等所有资源清理后关闭日志
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1;
	}
	
	public static class AppBanner implements Banner {
		private static List<String> infos = Lists.newArrayList();
		private YijiBootApplication application;
		
		public AppBanner(YijiBootApplication application) {
			this.application = application;
		}
		
		@Override
		public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
			printAppInfo();
		}
		
		private void printAppInfo() {
			//don't init log system in object create phase
			Logger logger = LoggerFactory.getLogger(AppBanner.class);
			logger.info("************************************");
			logger.info("应用[{}]开始启动,env={},http port={},heraEnable={},basePackage={}", Apps.getAppName(), Env.getEnv(),
				application.httpPort(), application.heraEnable(),
				Preconditions.checkNotNull(Apps.getBasePackage(), "应用basePackage为空，请通知基础技术部检查"));
			logger.info("************************************");
			if (infos != null) {
				infos.forEach(logger::info);
				infos.clear();
			}
		}
		
		public static List<String> getInfos() {
			return infos;
		}
	}
	
	/**
	 * 检测是否为开发模式
	 */
	static class DevToolsDetector {
		public void apply(ConfigurableEnvironment environment) {
			if (environment.getPropertySources().contains("refresh")) {
				System.setProperty(Apps.DEV_MODE_KEY, Boolean.TRUE.toString());
			}
		}
	}
	
	static class AppCodeInitializor implements Runnable {
		
		@Override
		public void run() {
			Thread thread = new Thread(() -> {
				CodeGenerator.genNodeCode();
				CodeGenerator.genSystemCode();
			});
			thread.setName("code-generator-thread");
			thread.start();
		}
	}
	
	/**
	 * 关闭应用
	 */
	public static void shutdownApp() {
		Logger logger = LoggerFactory.getLogger(YijiApplicationRunListener.class);
		logger.info("应用[{}}]开始关闭", Apps.getAppName());
		//在应用关闭时打印console log,便于自动化发布系统查看日志
		ConsoleLogInitializer.addConsoleAppender();
		//run all shutdown hooks before spring container avoid service dependency
		ShutdownHooks.shutdownAll();
		//close spring container
		ApplicationContext applicationContext = ApplicationContextHolder.get();
		if (applicationContext instanceof ConfigurableApplicationContext) {
			if (!((ConfigurableApplicationContext) applicationContext).isActive()) {
				System.exit(0);
			} else {
				((ConfigurableApplicationContext) applicationContext).close();
			}
		}
		shutdownLogSystem();
	}
	
	static class ShutdownThread extends Thread {
		public ShutdownThread() {
			super("Shutdown");
		}
		
		@Override
		public void run() {
			shutdownApp();
		}
		
		public void register() {
			Runtime.getRuntime().addShutdownHook(this);
		}
	}
	
}
