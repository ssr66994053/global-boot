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

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public Integer getMaxThreads() {
			return maxThreads;
		}

		public void setMaxThreads(Integer maxThreads) {
			this.maxThreads = maxThreads;
		}

		public Integer getCorethreads() {
			return corethreads;
		}

		public void setCorethreads(Integer corethreads) {
			this.corethreads = corethreads;
		}

		public Integer getKeepAliveTime() {
			return keepAliveTime;
		}

		public void setKeepAliveTime(Integer keepAliveTime) {
			this.keepAliveTime = keepAliveTime;
		}

		public Integer getTimeout() {
			return timeout;
		}

		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}

		public boolean isRegister() {
			return register;
		}

		public void setRegister(boolean register) {
			this.register = register;
		}

		public String getSerialization() {
			return serialization;
		}

		public void setSerialization(String serialization) {
			this.serialization = serialization;
		}

		public void setQueue(Integer queue) {
			this.queue = queue;
		}
		
		
		
		
		
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isRegister() {
		return register;
	}

	public void setRegister(boolean register) {
		this.register = register;
	}

	public String getScanPackage() {
		return scanPackage;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}

	public String getRefOnlyZkUrl1() {
		return refOnlyZkUrl1;
	}

	public void setRefOnlyZkUrl1(String refOnlyZkUrl1) {
		this.refOnlyZkUrl1 = refOnlyZkUrl1;
	}

	public String getRefOnlyZkUrl2() {
		return refOnlyZkUrl2;
	}

	public void setRefOnlyZkUrl2(String refOnlyZkUrl2) {
		this.refOnlyZkUrl2 = refOnlyZkUrl2;
	}

	public String getRefOnlyZkUrl3() {
		return refOnlyZkUrl3;
	}

	public void setRefOnlyZkUrl3(String refOnlyZkUrl3) {
		this.refOnlyZkUrl3 = refOnlyZkUrl3;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isConsumerLog() {
		return consumerLog;
	}

	public void setConsumerLog(boolean consumerLog) {
		this.consumerLog = consumerLog;
	}

	public boolean isProviderLog() {
		return providerLog;
	}

	public void setProviderLog(boolean providerLog) {
		this.providerLog = providerLog;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	
}
