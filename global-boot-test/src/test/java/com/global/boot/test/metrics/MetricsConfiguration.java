/**
 * Copyright (C) 2012 Ryan W Tenney (ryan@10e.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.global.boot.test.metrics;

import com.codahale.metrics.JmxReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.access.MBeanProxyFactoryBean;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

@Configuration
public class MetricsConfiguration {
	
	@Bean(name = "methodGauge")
	public MBeanProxyFactoryBean getMethodGauge() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.gaugedMethod"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxGaugeMBean.class);
		return factoryBean;
	}
	
	@Bean(name = "fieldGauge")
	public MBeanProxyFactoryBean getFieldGauge() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.gaugeValue"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxGaugeMBean.class);
		return factoryBean;
	}
	
	@Bean(name = "cachedMethodGauge")
	public MBeanProxyFactoryBean getCachedMethodGauge() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.cachedGaugeMethod"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxGaugeMBean.class);
		return factoryBean;
	}
	
	@Bean(name = "timer")
	public MBeanProxyFactoryBean getTimer() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.timedMethod"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxTimerMBean.class);
		return factoryBean;
	}
	
	@Bean
	public MBeanProxyFactoryBean getCounter() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.countedMethod"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxCounterMBean.class);
		return factoryBean;
	}
	
	@Bean(name = "meter")
	public MBeanProxyFactoryBean getMeter() {
		MBeanProxyFactoryBean factoryBean = new MBeanProxyFactoryBean();
		try {
			factoryBean.setObjectName(new ObjectName("metrics", "name",
				"com.yiji.boot.test.metrics.MetricServiceImpl.meteredMethod"));
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		factoryBean.setProxyInterface(JmxReporter.JmxMeterMBean.class);
		return factoryBean;
	}
}
