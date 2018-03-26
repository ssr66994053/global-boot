/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-02-16 18:20 创建
 * qiubo@yiji.com 2016-02-22 18:06 去掉启动和关闭方法\修改监听事件,优化性能
 *
 */
package com.global.boot.rocketmq.producer;

//import com.global.framework.hera.client.core.Hera;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class MessageProducerLifeManager implements ApplicationListener<ContextRefreshedEvent>, BeanFactoryAware {
	
	private BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		MessageProducer messageProducer = this.beanFactory.getBean(MessageProducer.class);
		//注册为Hera的热切换对象，动态更改disableTopics，来禁止往指定的topic发送消息
//		Hera.registerHotSwapObject(messageProducer);
	}
}
