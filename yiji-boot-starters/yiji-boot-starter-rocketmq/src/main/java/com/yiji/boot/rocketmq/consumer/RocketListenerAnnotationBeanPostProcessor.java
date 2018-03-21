/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2016-01-31 10:26 创建
 *
 */
package com.yiji.boot.rocketmq.consumer;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.yiji.boot.core.Apps;
import com.yiji.boot.rocketmq.message.NotifyMessage;
import com.yiji.boot.rocketmq.message.OrderedNotifyMessage;
import com.yiji.boot.rocketmq.support.KyroMessageConverter;
import com.yjf.common.env.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
public class RocketListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware {
	private static final Logger logger = LoggerFactory.getLogger(RocketListenerAnnotationBeanPostProcessor.class);
	private static final String OR = " || ";
	private BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (!isMatchPackage(targetClass)) {
			return bean;
		}
		ReflectionUtils.doWithMethods(targetClass, (Method method) -> {
			RocketListener rocketListener = AnnotationUtils.getAnnotation(method, RocketListener.class);
			if (rocketListener != null) {
				logger.info("{}-{}找到RocketListener: {}", beanName, method.getName(), rocketListener.toString());
				try {
					processRocketListener(rocketListener, method, bean, beanName);
				} catch (MQClientException e) {
					throw new BeanInitializationException(e.getMessage(), e);
				}
			}
		});
		return bean;
	}
	
	private boolean isMatchPackage(Class clazz) {
		return clazz.getName().startsWith(Apps.getBasePackage());
	}
	
	protected void processRocketListener(RocketListener rocketListener, Method method, Object bean, String beanName)
																													throws MQClientException {
		checkMethodParameter(method, rocketListener);
		RocketMQConsumer rocketMQConsumer = this.beanFactory.getBean(RocketMQConsumer.class);
		TopicMessageListener topicMessageListener = generateTopicListener(rocketListener, method, bean);
		if (rocketListener.ordered()) {
			rocketMQConsumer.addOrderTopicListener(topicMessageListener);
			logger.info("增加顺序topicListener:" + topicMessageListener);
		} else {
			rocketMQConsumer.addTopicListener(topicMessageListener);
			logger.info("增加普通topicListener:" + topicMessageListener);
		}
	}
	
	protected void checkMethodParameter(Method method, RocketListener rocketListener) {
		if (method.getParameterCount() == 1) {
			if (rocketListener.ordered()) {
				if (!method.getParameterTypes()[0].equals(OrderedNotifyMessage.class)) {
					throw new BeanInitializationException("顺序消息监听方法，只能有一个参数，参数类型为OrderedNotifyMessage");
				}
			} else {
				if (!method.getParameterTypes()[0].equals(NotifyMessage.class)) {
					throw new BeanInitializationException("普通消息监听方法，只能有一个参数，参数类型为NotifyMessage");
				}
			}
		} else {
			throw new BeanInitializationException("消息监听方法，只能有一个参数，参数类型为NotifyMessage或者OrderedNotifyMessage");
		}
	}
	
	protected TopicMessageListener generateTopicListener(RocketListener rocketListener, Method method, Object bean) {
		TopicMessageListener topicMessageListener = new TopicMessageListener();
		topicMessageListener.setTopic(rocketListener.topic());
		topicMessageListener.setTags(generateTags(rocketListener));
		HandlerMethod handlerMethod = new HandlerMethod(bean, method);
		topicMessageListener.setHandlerMethod(handlerMethod);
		topicMessageListener.setMessageConverter(new KyroMessageConverter());
		topicMessageListener.setEnableLog(rocketListener.enableLog());
		return topicMessageListener;
	}
	
	protected String generateTags(RocketListener rocketListener) {
		StringBuilder stringBuilder = new StringBuilder();
		MessageFilter messageFilter = rocketListener.messageFilter();
		String[] fromSystems = messageFilter.fromSystem();
		if (fromSystems == null || fromSystems.length == 0) {
			throw new BeanInitializationException("RocketListener必须设置fromSystem");
		}
		//如果没有设置接收系统，则默认设置为当前系统
		String[] toSystems = messageFilter.toSystem();
		if (toSystems == null || toSystems.length == 0) {
			toSystems = new String[1];
			toSystems[0] = Apps.getAppName();
		}
		String[] events = messageFilter.event();
		if (events == null || events.length == 0) {
			throw new BeanInitializationException("RocketListener必须设置event");
		}
		//如果没有设置环境，则默认设置为当前环境
		String[] envs = messageFilter.env();
		if (envs == null || envs.length == 0) {
			envs = new String[1];
			envs[0] = Env.getEnv();
		}
		for (String fromSystem : fromSystems) {
			for (String toSystem : toSystems) {
				for (String event : events) {
					for (String env : envs) {
						stringBuilder.append(fromSystem);
						stringBuilder.append(NotifyMessage.SEPARATOR);
						stringBuilder.append(toSystem);
						stringBuilder.append(NotifyMessage.SEPARATOR);
						stringBuilder.append(event);
						stringBuilder.append(NotifyMessage.SEPARATOR);
						stringBuilder.append(env);
						stringBuilder.append(OR);
					}
				}
			}
		}
		String tags = stringBuilder.toString();
		if (tags.lastIndexOf(OR) != -1) {
			tags = tags.substring(0, tags.lastIndexOf(OR));
		}
		return tags;
	}
	
	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}
}
