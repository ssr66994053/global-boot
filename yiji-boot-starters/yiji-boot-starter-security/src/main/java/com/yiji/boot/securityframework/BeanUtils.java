/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月25日 下午7:49:54 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.common.security.SecurityConfigurationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * bean相关的工具。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public abstract class BeanUtils {
	
	/**
	 * 从<tt>beanFactory</tt>中获取一个<tt>bean</tt>。
	 * @param beanFactory bean工厂。
	 * @param name bean名称。
	 * @param beanType bean类型。
	 * @param properties 所属的自动配置属性。
	 * @param propertyName 相关的属性名称。
	 * @return 对应的bean。
	 * @throws SecurityConfigurationException 如果过程中发生错误。
	 */
	public static <T> T getBean(BeanFactory beanFactory, String name, Class<T> beanType,
								AutoConfigurationProperties properties, String propertyName) {
		T bean;
		try {
			bean = beanFactory.getBean(name, beanType);
		} catch (NoSuchBeanDefinitionException e) {
			throw new SecurityConfigurationException("没有找到[" + properties.mergePropertyPath(propertyName)
														+ "]属性对应的[beanName]为'" + name + "'，类型为[" + beanType.getName()
														+ "]的Bean。");
			
		} catch (BeanNotOfRequiredTypeException e) {
			throw new SecurityConfigurationException("找到[" + properties.mergePropertyPath(propertyName)
														+ "]属性对应的Bean不是[" + beanType.getName() + "]类型。");
		} catch (BeansException e) {
			throw new SecurityConfigurationException("查找[" + properties.mergePropertyPath(propertyName)
														+ "]属性对应的Bean时发生错误，错误信息为：" + e.getMessage());
		}
		return bean;
	}
	
	/**
	 * 从<tt>beanFactory</tt>中获取一个<tt>bean</tt>。
	 * @param beanFactory bean工厂。
	 * @param beanType bean类型。
	 * @param properties 所属的自动配置属性。
	 * @param propertyName 相关的属性名称。
	 * @return 对应的bean。
	 * @throws SecurityConfigurationException 如果过程中发生错误。
	 */
	public static <T> T getBean(BeanFactory beanFactory, Class<T> beanType, AutoConfigurationProperties properties,
								String propertyName) {
		T bean;
		try {
			bean = beanFactory.getBean(beanType);
		} catch (NoUniqueBeanDefinitionException e) {
			throw new SecurityConfigurationException("[" + properties.mergePropertyPath(propertyName) + "]属性处理出错：["
														+ beanType.getName() + "]类型的Bean存在多个匹配项。");
		} catch (NoSuchBeanDefinitionException e) {
			throw new SecurityConfigurationException("[" + properties.mergePropertyPath(propertyName) + "]属性处理出错：没有找到["
														+ beanType.getName() + "]类型的Bean。");
		}
		return bean;
	}
}
