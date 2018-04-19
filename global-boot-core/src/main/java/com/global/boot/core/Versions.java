/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-17 00:34 创建
 */
package com.global.boot.core;

import com.global.common.lang.util.DateUtil;
import com.global.common.spring.ApplicationContextHolder;

import java.net.JarURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
public class Versions {
	public static final String UNKOWN = "unkown";
	private static String version = null;
	private static String compileTime = null;
	
	/**
	 * 获取yiji-boot版本
	 */
	public static String getVersion() {
		if (version == null) {
			String tmp = Versions.class.getPackage().getImplementationVersion();
			if (tmp == null || tmp.length() == 0) {
				tmp = "unkown";
			}
			version = tmp;
		}
		return version;
	}
	
	/**
	 * 获取yiji-boot compile time
	 */
	public static String getComplieTime() {
		if (compileTime != null) {
			return compileTime;
		}
		try {
			String rn = Versions.class.getName().replace('.', '/') + ".class";
			JarURLConnection j = (JarURLConnection) ApplicationContextHolder.get().getClassLoader().getResource(rn)
				.openConnection();
			Date createDate = new Date(j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime());
			compileTime = new SimpleDateFormat(DateUtil.simple).format(createDate);
		} catch (Exception e) {
			compileTime = UNKOWN;
		}
		return compileTime;
	}
	
}
