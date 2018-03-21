/*
* www.yiji.com Inc.
* Copyright (c) 2014 All Rights Reserved
*/

package com.yiji.boot.core.hera;

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-10 14:36 创建
 *
 */

import com.yiji.framework.hera.client.core.Hera;
import com.yiji.framework.hera.client.core.HeraLauncher;
import com.yiji.framework.hera.client.exception.HeraException;
import com.yiji.framework.hera.client.listener.Event;
import com.yiji.framework.hera.client.listener.ValueTrigger;
import com.yiji.framework.hera.client.support.annotation.AutoConfig;
import com.yiji.framework.hera.client.util.HeraUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于处理spring bean中的@AutoConfig
 * @author qiubo@yiji.com
 */
public class HeraConfigPostProcessor extends AutowiredAnnotationBeanPostProcessor implements BeanFactoryAware,
																					EnvironmentAware, InitializingBean,
																					ValueTrigger, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(HeraConfigPostProcessor.class);
	
	private BeanFactory beanFactory;
	private Environment environment;
	protected Map<String, Map<String, String>> configBeanHolder = new ConcurrentHashMap<>();
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ValueTrigger) {
			Hera.addValueTrigger((ValueTrigger) bean);
		}
		return bean;
	}
	
	@Override
	public void onChange(Event event) throws HeraException {
		Map<String, Object> rMap = event.getChangedPorpMapWithStatus();
		if (null != rMap) {
			for (Map.Entry<String, Object> entrys : rMap.entrySet()) {
				String key = entrys.getKey();
				String rawKey = key.substring(key.indexOf('^') + 1, key.length());
				for (Map.Entry<String, Map<String, String>> entry : configBeanHolder.entrySet()) {
					String beanName = entry.getKey();
					Map<String, String> propMap = entry.getValue();
					Object bean = beanFactory.getBean(beanName);
					if (null == bean) {
						continue;
					}
					for (Map.Entry<String, String> entry1 : propMap.entrySet()) {
						String pKey = entry1.getKey();
						if (rawKey.equals(pKey)) {
							if (key.indexOf('^') != -1) {
								String propName = propMap.get(pKey);
								Object propValue = rMap.get(key);
								try {
									setValue(key, beanName, pKey, bean, propName, propValue);
								} catch (Exception e) {
									logger.error("property hotSwap for bean={} key={} failed ", beanName, pKey, e);
								}
							}
						}
					}
				}
				//处理非spring bean
				for (Map.Entry<Object, Map<String, String>> entry : Hera.getHostSwapBean().entrySet()) {
					Object bean = entry.getKey();
					if (null == bean) {
						continue;
					}
					Map<String, String> propMap = entry.getValue();
					for (Map.Entry<String, String> entry0 : propMap.entrySet()) {
						String pKey = entry0.getKey();
						if (rawKey.equals(pKey)) {
							if (key.indexOf('^') != -1) {
								String propName = propMap.get(pKey);
								Object propValue = rMap.get(key);
								try {
									setValue(key, bean.getClass().getName(), pKey, bean, propName, propValue);
								} catch (Exception e) {
									logger.error("property hotSwap for bean [{}] failed ,cause by:", bean.getClass()
										.getName(), e);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void setValue(String key, String beanName, String pKey, Object bean, String propName, Object propValue) {
		String opt = key.substring(0, key.indexOf('^'));
		if (opt.equals(HeraUtil.OPT[1])) {
			BeanWrapper wrapper = new BeanWrapperImpl(bean);
			wrapper.setPropertyValue(propName, propValue);
			logger.info("hotswap UPDATE for beanName={},property={},value={}.", beanName, propName, propValue);
		} else if (opt.equals(HeraUtil.OPT[2])) {
			BeanWrapper wrapper = new BeanWrapperImpl(bean);
			wrapper.setPropertyValue(propName, null);
			logger.info("hotswap REMOVE for beanName={},property={}.", beanName, propName);
		} else if (opt.equals(HeraUtil.OPT[0])) {
			logger.debug("we don't support for added property to hotSwap, just ignore.beanName={},key={}", beanName,
				pKey);
		} else {
			logger.error("unsupported operation type={},key={}", opt, key);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Hera.addValueTrigger(this);
	}
	
	@Override
	public void destroy() throws Exception {
		HeraLauncher.getInstance().stop();
	}
	
	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
													String beanName) throws BeansException {
		if (pvs instanceof MutablePropertyValues) {
			Field[] fields = bean.getClass().getDeclaredFields();
			if (null != fields) {
				for (Field f : fields) {
					if (f.isAnnotationPresent(AutoConfig.class)) {
						String key = f.getAnnotation(AutoConfig.class).value();
						if (null != key) {
							if (!Modifier.isVolatile(f.getModifiers())) {
								logger.warn("字段:{} 标注了@AutoConfig注解,会被hera动态修改,为了保证线程修改可见性,请增加字段volatile修饰符", f);
							}
							PropertyValue pv = new PropertyValue(f.getName(), environment.getProperty(key));
							((MutablePropertyValues) pvs).addPropertyValue(pv);
							if (null == configBeanHolder.get(beanName)) {
								Map<String, String> props = new HashMap<>();
								props.put(key, f.getName());
								configBeanHolder.put(beanName, props);
							} else {
								configBeanHolder.get(beanName).put(key, f.getName());
							}
						}
					}
				}
				
			}
		}
		return pvs;
	}
}