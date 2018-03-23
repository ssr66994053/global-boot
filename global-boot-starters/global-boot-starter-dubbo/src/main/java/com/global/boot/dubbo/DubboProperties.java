/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-20 16:14 创建
 *
 */
package com.global.boot.dubbo;

import com.alibaba.dubbo.common.Constants;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(DubboProperties.PREFIX)
@Data
public class DubboProperties implements InitializingBean {
	public static final String PREFIX = "yiji.dubbo";
	
	/**
	 * 是否启用dubbo
	 */
	private boolean enable = true;
	
	/**
	 * 是否注册本应用的服务到注册中心(测试的时候可能需要本地服务不注册到注册中心)
	 */
	private boolean register = true;

	/**
	 * 扫描包，多个包之间用逗号分隔
	 */
	private String scanPackage;
	
	/**
	 * 只消费注册中心zk地址
	 */
	private String refOnlyZkUrl1 = "";
	private String refOnlyZkUrl2 = "";
	private String refOnlyZkUrl3 = "";
	
	/**
	 * 必填：应用负责人,请填写邮箱前缀
	 */
	private String owner;
	
	/**
	 *   选填：应用版本号，如果配置此版本号，服务可以不用指定版本号
	 */
	private String version;
	
	/**
	 * 是否启用dubbo consumer请求provider日志
	 */
	private boolean consumerLog = false;
	
	/**
	 * 是否启用dubbo provider提供服务被请求日志
	 */
	private boolean providerLog = false;
	
	private Provider provider = new Provider();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (enable) {
			Assert.hasText(owner, "dubbo应用负责人yiji.dubbo.owner不能为空");
		}
	}
	
	@Data
	public static class Provider {
		public static final int DEFAULT_THREAD = 400;
		public static final int DEFAULT_TIMEOUT = 60000;
		public static final boolean DEFAULT_REGISTER = true;
		/**
		 * 是否启用服务提供者
		 */
		private boolean enable = true;
		
		/**
		 * 必填：服务提供者端口
		 */
		private Integer port;
		
		/**
		 * 线程池最大线程数
		 */
		private Integer maxThreads = DEFAULT_THREAD;
		
		/**
		 * 初始化核心线程数
		 */
		private Integer corethreads = 200;
		
		/**
		 * 队列大小
		 */
		private Integer queue = maxThreads;
		
		/**
		 * 线程池线程保活时间，单位s
		 */
		private Integer keepAliveTime = 60 * 5;
		
		/**
		 * 服务超时时间，默认60s
		 */
		private Integer timeout = DEFAULT_TIMEOUT;
		
		/**
		 * 服务是否注册到zk
		 */
		private boolean register = DEFAULT_REGISTER;
		
		/**
		 * provider 序列化
		 */
		private String serialization = null;
		
		public Integer getQueue() {
			//如果当queue设置为0时,会使用SynchronousQueue,这个东东导致了任务线程执行"不均衡"
			//但是如果queue设置得太小,导致queue成为瓶颈,这个时候线程比较闲还出现请求被拒绝的问题
			if (queue != 0) {
				queue =  this.getMaxThreads() / 2;
			}
			return queue;
		}
		
	}
	
}
