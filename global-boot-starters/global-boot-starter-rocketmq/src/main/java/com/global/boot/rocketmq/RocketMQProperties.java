/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-29 13:41 创建
 *
 */
package com.global.boot.rocketmq;

import com.global.boot.core.Apps;
import com.yjf.common.env.Env;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(prefix = "yiji.rocketmq")
public class RocketMQProperties {
	private boolean enable = true;
	/**
	 * 必填：服务连接地址
	 */
	private String nameSrvAddr;
	
	/**
	 * 选填：禁止使用的topic，项目不用管这个配置
	 */
	private String disableTopics;
	/**
	 * 生产端必填：生产者相关配置
	 */
	private Producer producer = new Producer();
	/**
	 * 消费端必填：消费端相关配置
	 */
	private Consumer consumer = new Consumer();
	
	public String getNameSrvAddr() {
		return nameSrvAddr;
	}
	
	public void setNameSrvAddr(String nameSrvAddr) {
		this.nameSrvAddr = nameSrvAddr;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public Producer getProducer() {
		return producer;
	}
	
	public void setProducer(Producer producer) {
		this.producer = producer;
	}
	
	public Consumer getConsumer() {
		return consumer;
	}
	
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
	
	public String getDisableTopics() {
		return disableTopics;
	}
	
	public void setDisableTopics(String disableTopics) {
		this.disableTopics = disableTopics;
	}
	
	public static class Producer {
		
		private static final String PROVIDER_PREFIX = "Providers_";
		/**
		 * 生产端的group名字，默认为: PROVIDER_PREFIX + Apps.getAppName()
		 */
		private String group = PROVIDER_PREFIX + Apps.getAppName();
		/**
		 * 是否启用生产端，默认为不启用
		 */
		private boolean enable = false;
		
		/**
		 * 是否启用生产者日志
		 */
		private boolean logEnable = true;
		
		public String getGroup() {
			return group;
		}
		
		public void setGroup(String group) {
			this.group = group;
		}
		
		public boolean isEnable() {
			return enable;
		}
		
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		public boolean isLogEnable() {
			return logEnable;
		}
		
		public void setLogEnable(boolean logEnable) {
			this.logEnable = logEnable;
		}
	}
	
	public static class Consumer {
		
		private static final String CONSUMER_PREFIX = "Consumers_";
		/**
		 * 消费端的group名字，默认为: CONSUMER_PREFIX + Apps.getAppName()
		 */
		private String group = CONSUMER_PREFIX + Apps.getAppName() + "_" + Env.getEnv();
		/**
		 * 是否启用消费端，默认为不启用
		 */
		private boolean enable = false;
		/**
		 * 每次从服务端获取多少条消费到本地消费，不建议修改
		 * <p>
		 * 如果值大于1,每个批量中有一条处理错误,会导致整个批量重发
		 */
		private int consumeMessageBatchMaxSize = 1;
		/**
		 * 消费端线程池最小为多少
		 */
		private int consumeThreadMin = 10;
		/**
		 * 消费端线程池最大为多少
		 */
		private int consumeThreadMax = 50;
		
		public String getGroup() {
			return group;
		}
		
		public void setGroup(String group) {
			this.group = group;
		}
		
		public int getConsumeMessageBatchMaxSize() {
			return consumeMessageBatchMaxSize;
		}
		
		public void setConsumeMessageBatchMaxSize(int consumeMessageBatchMaxSize) {
			this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
		}
		
		public int getConsumeThreadMin() {
			return consumeThreadMin;
		}
		
		public void setConsumeThreadMin(int consumeThreadMin) {
			this.consumeThreadMin = consumeThreadMin;
		}
		
		public int getConsumeThreadMax() {
			return consumeThreadMax;
		}
		
		public void setConsumeThreadMax(int consumeThreadMax) {
			this.consumeThreadMax = consumeThreadMax;
		}
		
		public boolean isEnable() {
			return enable;
		}
		
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}
}
