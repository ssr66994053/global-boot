/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-11-03 16:15 创建
 *
 */
package com.global.boot.test.actuator;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;
import com.github.kevinsawicki.http.HttpRequest;
import com.global.boot.core.Apps;
import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ShellProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author qiubo@yiji.com
 */
public class ActuatorTest extends TestBase {
	
	@Reference
	private RegistryService registry;
	@Autowired(required = false)
	private ConcurrentVisitCounterService visitCounterService;
	
	@Test
	public void testAclFailure() throws Exception {
		String ip = "113.204.226.234";
		String url = buildUrl("/mgt");
		String body = HttpRequest.get(url).header("X-Forwarded-For", ip).body();
		assertThat(body).isEqualTo("Forbidden");
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					visitCounterService.visit();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	@Test
	public void testIndexPage() throws Exception {
		assertGetBodyContains("/mgt/", "<center>actuator</center>");
	}
	
	@Test
	public void testInfo() throws Exception {
		String url = buildUrl("/mgt/info");
		String body = HttpRequest.get(url).body();
		JSONObject jsonObject = (JSONObject) JSON.parse(body);
		JSONObject app = (JSONObject) jsonObject.get("app");
		assertThat(app.get("name")).isEqualTo("yiji-boot-test");
		JSONObject build = (JSONObject) jsonObject.get("build");
		assertThat(build.get("artifact")).isEqualTo("yiji-boot-test");
		JSONObject dubbo = (JSONObject) jsonObject.get("dubbo");
		assertThat(dubbo.get("port")).isEqualTo("28881");
		
	}
	
	@Test
	public void testExport() {
		URL subscribeUrl = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "", Constants.INTERFACE_KEY,
			"yiji-boot-test", Constants.CATEGORY_KEY, "mgt");
		registry.subscribe(subscribeUrl, new NotifyListener() {
			@Override
			public void notify(List<URL> urls) {
				for (URL url : urls) {
					if (url.getProtocol().equals("http")) {
						assertThat(url.getPort()).isEqualTo(Apps.getHttpPort());
					}
				}
			}
		});
		
	}
	
	@Test
	public void testCrshDisabled() throws Exception {
		try {
			getApplicationContext().getBean(ShellProperties.class);
			fail("must be throw exception");
		} catch (BeansException e) {
			assertThat(e).isNotNull();
		}
		try {
			getApplicationContext().getBean(CrshAutoConfiguration.class);
			fail("must be throw exception");
		} catch (BeansException e) {
			assertThat(e).isNotNull();
		}
	}
	
	@Test
	public void testResponseHeader_XApplicationContext() throws Exception {
		String url = buildUrl("/mgt/info");
		String[] context = HttpRequest.get(url).headers("X-Application-Context");
		assertThat(context).isEmpty();
	}
}
