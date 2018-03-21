/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-03-16 17:33 创建
 */
package com.yiji.boot.test;

import com.yjf.common.net.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class HttpController implements InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(HttpController.class);
	
	CloseableHttpAsyncClient httpclient = null;
	
	@RequestMapping("/testSycnHttp")
	@ResponseBody
	public String testSycnHttp() throws Exception {
		serviceA();
		HttpUtil.HttpResult result = HttpUtil.getInstance().get(
			"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/blank_56a92bd4.png");
		HttpController.this.serviceB(result.getStatusCode());
		return "ok";
	}
	
	@RequestMapping("/testAsycnHttp")
	@ResponseBody
	public String testAsycnHttp() {
		serviceA();
		HttpGet request = new HttpGet(
			"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/blank_56a92bd4.png");
		httpclient.execute(request, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse result) {
				//此请求在io线程中执行,需要使用另外的业务线程池
				HttpController.this.serviceB(result.getStatusLine().getStatusCode());
			}
			
			@Override
			public void failed(Exception ex) {
			}
			
			@Override
			public void cancelled() {
			}
		});
		return "ok";
	}
	
	private void serviceA() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			//do nothing
		}
	}
	
	private void serviceB(int statusCode) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			//do nothing
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		httpclient = HttpAsyncClients.custom().setMaxConnTotal(200).setMaxConnPerRoute(50).build();
		httpclient.start();
		
	}
	
	@Override
	public void destroy() throws Exception {
		httpclient.close();
	}
	
}
