/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.yiji.boot.test.boss;

import com.github.kevinsawicki.http.HttpRequest;
import com.yiji.boot.boss.log.domain.BossOperationLogMessage;
import com.yiji.boot.boss.log.manager.BossOperationLogManager;
import com.yiji.boot.core.Apps;
import com.yiji.boot.test.TestBase;
import com.yjf.marmot.MarmotFilterProxyBean;
import com.yjf.marmot.user.MarmotUser;
import com.yjf.marmot.user.Menu;
import com.yjf.marmot.user.UserAuthInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhouxi@yiji.com
 */
public class BossTest extends TestBase {
	
	@Autowired(required = false)
	private MarmotUser marmotUser;
	
	@Autowired(required = false)
	private MarmotFilterProxyBean marmotFilterProxyBean;
	
	@Autowired(required = false)
	private BossLogConsumer bossLogConsumer;
	
	@Autowired(required = false)
	private BossOperationLogManager bossOperationLogManager;
	
	@Test
	public void testAnno() throws IOException {
		assertGetBodyIs("css/anno", "anno");
	}
	
	@Test
	public void testisFilterEnable() throws IOException {
		assertGetBodyIs("/testFilter", "noBoss");
	}
	
	@Test
	public void testBossForward() throws IOException {
		String url = buildUrl("/boss/" + Apps.getAppName() + "/testPerm");
		HttpRequest request = HttpRequest.get(url).followRedirects(true);
		int code = request.code();
		String host = request.getConnection().getURL().getHost();
		assertThat(code).isEqualTo(HttpStatus.OK.value());
		assertThat(host).isEqualTo(PROFILE + ".yiji");
	}
	
	@Test
	public void testPermission() {
		UserAuthInfo info = marmotUser.getMarmotUserAuthInfo("PERM2016010500000401", Apps.getAppName());
		assertThat(info).isNotNull();
		Menu menu = marmotUser.getMenus("PERM2016010500000401");
		assertThat(menu).isNotNull();
	}
	
	@Test
	public void testEnableDevModeAndEnablePermission() {
		assertThat(marmotFilterProxyBean.isRedirect()).isEqualTo(false);
		assertThat(marmotFilterProxyBean.getFilters().get("permissionFilter")).isEqualTo(null);
	}
	
	//由于boss的操作需要登陆，但是目前无法模拟登陆，只能屏蔽测试用例。
	//需要使用的话，测试方式是，启动测试用例，访问地址，登陆一次，之后再访问该地址，测试用例可以通过...
	//http://localhost:9832/boss/yiji-boot-test/testLog?userId=1234567890&payPassword=hahahaha&paypassword=xxx&passwd=xxx&pwd=ssssss&mobile=111&sensitive=important
	@Test
	@Ignore
	public void testLog() throws InterruptedException {
		
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (bossLogConsumer.isReady()) {
				break;
			}
		}
		BossOperationLogMessage lastReceiveMessage = bossLogConsumer.getLastReceiveMessage();
		System.out.println(">>>>>" + lastReceiveMessage);
		assertThat(lastReceiveMessage).isNotNull();
		assertThat(lastReceiveMessage.getSystemName()).isEqualTo(Apps.getAppName());
		assertThat(lastReceiveMessage.getLogMessageId()).hasSize(20);
		assertThat(lastReceiveMessage.getDescription()).isEqualTo("测试日志收集");
		assertThat(lastReceiveMessage.getLogParameterList()).hasSize(2);
		assertThat(lastReceiveMessage.getLogParameterList()[1].getParameterValue()).contains("*");
		
		assertThat(lastReceiveMessage.getMethodName()).isEqualTo("testLog");
		bossLogConsumer.setLastReceiveMessage(null);
		bossLogConsumer.setReady(false);
	}
	
	@Test
	@Ignore
	//	http://localhost:9832/boss/yiji-boot-test/testLogIgnore?userId=1234567890
	public void testLogIgnore() throws InterruptedException {
		
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (bossLogConsumer.isReady()) {
				break;
			}
		}
		BossOperationLogMessage lastReceiveMessage = bossLogConsumer.getLastReceiveMessage();
		System.out.println(">>>>>" + lastReceiveMessage);
		assertThat(lastReceiveMessage).isNotNull();
		assertThat(lastReceiveMessage.getSystemName()).isEqualTo(Apps.getAppName());
		assertThat(lastReceiveMessage.getLogMessageId()).hasSize(20);
		assertThat(lastReceiveMessage.getDescription()).isEqualTo("测试日志收集2");
		assertThat(lastReceiveMessage.getLogParameterList()).isNull();
		assertThat(lastReceiveMessage.getMethodName()).isEqualTo("testLogIgnore");
		bossLogConsumer.setLastReceiveMessage(null);
		bossLogConsumer.setReady(false);
		
	}
	
	@Test
	@Ignore
	//	http://localhost:9832/boss/yiji-boot-test/testLogNoAnnotation?userId=123456&paypassword=xxxx&mycarNo=ssssss
	public void testLogNoAnnotation() throws InterruptedException {
		
		int i = 0;
		while (i < 3) {
			Thread.sleep(500);
			if (bossLogConsumer.isReady()) {
				break;
			}
		}
		BossOperationLogMessage lastReceiveMessage = bossLogConsumer.getLastReceiveMessage();
		System.out.println(">>>>>" + lastReceiveMessage);
		assertThat(lastReceiveMessage).isNotNull();
		assertThat(lastReceiveMessage.getSystemName()).isEqualTo(Apps.getAppName());
		assertThat(lastReceiveMessage.getLogMessageId()).hasSize(20);
		assertThat(lastReceiveMessage.getDescription()).isNull();
		assertThat(lastReceiveMessage.getLogParameterList()).isNotNull();
		assertThat(lastReceiveMessage.getMethodName()).isNotNull();
		bossLogConsumer.setLastReceiveMessage(null);
		bossLogConsumer.setReady(false);
		
	}
	
	//testGlobalIgnoreMethod
	//	http://localhost:9832/boss/yiji-boot-test/testLogGolbalIgnoreMethod?userId=1234567890



	//	testXss ： http://localhost:9832/boss/yiji-boot-test//testXss?contentWithHtml=<'x'>

	@Test
	public void testNonBossXss(){
		assertGetBodyIs("testXss2?contentWithHtml=<'x'>", "<'x'>");
	}
}
