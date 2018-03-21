/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-10-27 12:24 创建
 *
 */
package com.yiji.boot.actuator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.Versions;
import com.yiji.boot.core.init.ComponentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.AuditAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceWebFilterAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author qiubo@yiji.com
 */
public class ActuatorComponentInitializer implements ComponentInitializer {
	private static final Logger logger = LoggerFactory.getLogger(ActuatorComponentInitializer.class);
	public static final String FALSE = "false";
	
	private ConfigurableApplicationContext configurableApplicationContext;
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		configurableApplicationContext = applicationContext;
		//设置actuator mvc请求前缀
		System.setProperty("management.contextPath", "/mgt");
		//设置info信息
		setInfo();
		// disable https://github.com/spring-projects/spring-boot/issues/4320
		System.setProperty("management.addApplicationContextHeader", FALSE);
		//关闭默认redis健康检查，因为yedis不支持info命令
		System.setProperty("management.health.redis.enabled", FALSE);
		//设置磁盘文件检查路径为日志文件所在路径
		if (Strings.isNullOrEmpty(System.getProperty("management.health.diskspace.path"))) {
			System.setProperty("management.health.diskspace.path", Apps.getLogPath());
		}
		//设置磁盘监控检查空闲值为500M
		System.setProperty("management.health.diskspace.threshold", Integer.toString(500 * 1024 * 1024));
		System.setProperty("endpoints.health.timeToLive", "5000");
		//https://github.com/spring-projects/spring-boot/issues/4365
		System.setProperty("endpoints.metrics.filter.enabled", FALSE);
		
		//disable Jolokia
		System.setProperty("endpoints.jolokia.enabled", FALSE);
		//disbale shutdown
		System.setProperty("endpoints.shutdown.enabled", FALSE);
	}
	
	private void setInfo() {
		Apps.exposeInfo("app.name", Apps.getAppName());
		Apps.exposeInfo("app.pid", Apps.getPid());
		Apps.exposeInfo("yijiboot.version", Versions.getVersion());
		Apps.exposeInfo("yijiboot.compileTime", Versions.getComplieTime());
		
		runInIDE();
	}
	
	private void runInIDE() {
		try {
			ProtectionDomain protectionDomain = getClass().getProtectionDomain();
			CodeSource codeSource = protectionDomain.getCodeSource();
			URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
			String path = (location == null ? null : location.getSchemeSpecificPart());
			if (path != null) {
				boolean isRunInIDE = !path.contains("!");
				setInfoIfNotExists("isRunInIDE", isRunInIDE);
				if (!isRunInIDE) {
					path = path.replace("file:", "");
					path = path.substring(0, path.indexOf('!'));
					File root = new File(path);
					if (!root.exists()) {
						throw new IllegalStateException("Unable to determine code source archive from " + root);
					}
					parseRevision(root);
				}
			}
		} catch (Exception e) {
			logger.warn("actuator init failure:{}", e.getMessage());
		}
		setInfoIfNotExists("isRunInIDE", true);
		setInfoIfNotExists("build.revision", "unkown");
	}
	
	private void parseRevision(File root) throws IOException {
		try (JarFile jarFile = new JarFile(root)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().equals("META-INF/MANIFEST.MF")) {
					try (InputStream bis = jarFile.getInputStream(entry)) {
						List<String> contents = CharStreams.readLines(new InputStreamReader(bis, "utf-8"));
						for (String content : contents) {
							if (content.contains("Implementation-Revision:")) {
								String revision = content.split(":")[1];
								Apps.exposeInfo("build.revision", revision.trim());
								return;
							}
						}
					}
				}
			}
		}
	}
	
	public static void setInfoIfNotExists(String key, Object value) {
		String infoKey = "info." + key;
		if (System.getProperty(infoKey) == null) {
			System.setProperty(infoKey, String.valueOf(value));
		}
	}
	
	@Override
	public List<String> excludeAutoconfigClassNames() {
		return Lists.newArrayList(TraceWebFilterAutoConfiguration.class.getName(),
			TraceRepositoryAutoConfiguration.class.getName(), CrshAutoConfiguration.class.getName(),
			AuditAutoConfiguration.class.getName());
	}
}
