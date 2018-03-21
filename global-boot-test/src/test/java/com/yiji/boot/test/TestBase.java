/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 15:02 创建
 *
 */
package com.yiji.boot.test;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.core.Apps;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.web.servlet.DispatcherServlet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
@SpringApplicationConfiguration(classes = Main.class)
public abstract class TestBase extends AppWebTestBase {
	protected static final String PROFILE = "stest";
	
	static {
		Apps.setProfileIfNotExists(PROFILE);
	}
	private boolean dispatcherServletInit = false;
	
	public DispatcherServlet getDispatcherServlet() {
		DispatcherServlet dispatcherServlet = getApplicationContext().getBean("dispatcherServlet",
			DispatcherServlet.class);
		if (!dispatcherServletInit) {
			//init DispatcherServlet
			HttpRequest.head(buildUrl("favicon.ico")).code();
			dispatcherServletInit = true;
		}
		return dispatcherServlet;
	}

	public static void main(String[] args) {
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println(format.format(new Date()));
	}
}
