/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-22 18:02 创建
 */
package com.global.boot.actuator.endpoint;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(prefix = "endpoints.jar")
public class JarEndpoint extends AbstractEndpoint<List<JarEndpoint.Jar>> {
	public static final String JAR = ".jar";
	private static final Logger logger = LoggerFactory.getLogger(JarEndpoint.class);

	private volatile List<Jar> jarList = null;
	
	public JarEndpoint() {
		super("jar");
	}
	
	@Override
	public synchronized List<Jar> invoke() {
		if (jarList != null) {
			return jarList;
		}
		List<Jar> jars = Lists.newArrayList();
		try {
			ProtectionDomain protectionDomain = getClass().getProtectionDomain();
			CodeSource codeSource = protectionDomain.getCodeSource();
			URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
			String path = (location == null ? null : location.getSchemeSpecificPart());
			if (path == null) {
				throw new IllegalStateException("Unable to determine code source archive");
			}
			if (isStartInIDE(path)) {
				parseInIDE(jars);
			} else {
				parseInJar(jars, path);
			}
		} catch (Exception e) {
			logger.error("parse jar error", e);
		}
		Collections.sort(jars, (o1, o2) -> o1.getName().compareTo(o2.getName()));
		jarList = jars;
		return jarList;
	}
	
	private void parseInJar(List<Jar> jars, String pathstr) throws IOException {
		String path=pathstr.replace("file:", "");
		path = path.substring(0, path.indexOf('!'));
		File root = new File(path);
		if (!root.exists()) {
			throw new IllegalStateException("Unable to determine code source archive from " + root);
		}
		try (JarFile jarFile = new JarFile(root)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			boolean inJarDir = false;
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (isJar(entry)) {
					inJarDir = true;
					String jarName = entry.getName().replace("lib/", "").replace("BOOT-INF/lib/", "").replace(JAR, "");
					Jar jar = new Jar();
					jar.parseByJarName(jarName);
					jar.setTime(entry.getTime());
					jars.add(jar);
				} else {
					if (inJarDir) {
						break;
					}
				}
			}
		}
	}
	
	private void parseInIDE(List<Jar> jars) {
		URL[] urls = ((URLClassLoader) ClassUtils.getDefaultClassLoader()).getURLs();
		if (urls == null || urls.length == 0) {
			urls = ((URLClassLoader) JarEndpoint.class.getClassLoader()).getURLs();
		}
		for (URL url : urls) {
			String fileName = url.getFile();
			if (isNotJvmJar(fileName)) {
				String jarName;
				int idx = fileName.lastIndexOf('/');
				if (idx > -1) {
					jarName = fileName.substring(idx + 1).replace(JAR, "");
				} else {
					jarName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1).replace(JAR, "");
				}
				Jar jar = new Jar();
				jar.parseByJarName(jarName);
				jar.setTime(new File(fileName).lastModified());
				jars.add(jar);
			}
		}
	}
	
	private boolean isNotJvmJar(String fileName) {
		return !fileName.contains("jre") && fileName.endsWith(JAR) && !fileName.contains("JavaVirtualMachines")
				&& !fileName.endsWith("idea_rt.jar");
	}
	
	private boolean isStartInIDE(String path) {
		return !path.contains("!");
	}
	
	protected boolean isJar(JarEntry entry) {
		String name = entry.getName();
		return name != null && (name.startsWith("lib/") || name.startsWith("BOOT-INF/lib/")) && name.endsWith(JAR);
	}
	
	public static class Jar {
		private String name;
		private String version;
		private long time;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getVersion() {
			return version;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public long getTime() {
			return time;
		}
		
		public void setTime(long time) {
			this.time = time;
		}
		
		public void parseByJarName(String jarName) {
			try {
				StringBuilder depName = new StringBuilder();
				StringBuilder version = new StringBuilder();
				boolean versionBegin = false;
				for (String split : Splitter.on('-').split(jarName)) {
					if (isDigital(split.charAt(0)) || versionBegin) {
						versionBegin = true;
						version.append(split).append('-');
					} else {
						depName.append(split).append('-');
					}
				}
				this.setName(depName.deleteCharAt(depName.length() - 1).toString());
				this.setVersion(version.deleteCharAt(version.length() - 1).toString());
			} catch (Exception e) {
				this.setName(jarName);
				logger.warn("解析jar信息失败,jarName={},ex={}", jarName, e.getMessage());
			}
		}
		
		private boolean isDigital(char ch) {
			return ch >= '0' && ch <= '9';
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Jar{");
			sb.append("name='").append(name).append('\'');
			sb.append(", version='").append(version).append('\'');
			sb.append(", time=").append(time);
			sb.append('}');
			return sb.toString();
		}
	}
}
