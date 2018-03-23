/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-06 11:55 创建
 *
 */
package com.global.boot.core.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.Metric;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * @author daidai@yiji.com
 */
public abstract class AbstractJmxMetrics implements JmxMetrics {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ObjectName objectName;
	private String displayPrefix;
	private Set<String> attributes;
	
	public AbstractJmxMetrics(ObjectName objectName, String displayPrefix) {
		this.objectName = objectName;
		this.displayPrefix = displayPrefix;
		this.attributes = new HashSet<>();
	}
	
	public AbstractJmxMetrics() {
	}
	
	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}
	
	@Override
	public ObjectName getObjectName() {
		return objectName;
	}
	
	@Override
	public String displayPrefix() {
		return this.displayPrefix;
	}
	
	@Override
	public abstract Collection<String> getAttributeKeys();
	
	private void addOneAttribute(List<Metric<?>> metrics, MBeanServer mBeanServer, ObjectName name, String displayName,
									String attributeKey) throws AttributeNotFoundException, MBeanException,
														ReflectionException, InstanceNotFoundException {
		final Object attribute = mBeanServer.getAttribute(name, attributeKey);
		if (attribute instanceof Number) {
			metrics.add(new Metric<Number>(displayName, (Number) attribute));
		} else if (attribute instanceof Boolean) {
			metrics.add(new Metric<Number>(displayName, (Boolean) attribute ? 1 : 0));
		} else if (attribute instanceof TabularDataSupport) {
			try {
				final TabularDataSupport map = (TabularDataSupport) attribute;
				final Set<Map.Entry<Object, Object>> entries = map.entrySet();
				for (Map.Entry<Object, Object> entry : entries) {
					CompositeDataSupport value = (CompositeDataSupport) entry.getValue();
					String key = (String) value.get("key");
					metrics.add(new Metric<Number>(displayName + "." + key.replace(':', '_'), (Integer) value
						.get("value")));
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		} else {
			logger.error("Unsupported JMX attribute type: " + attribute.getClass().getCanonicalName());
		}
	}
	
	@Override
	public Collection<Metric<?>> metrics() {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		final LinkedList<Metric<?>> metrics = new LinkedList<>();
		for (String attribute : getAttributeKeys()) {
			try {
				addOneAttribute(metrics, mBeanServer, objectName, displayPrefix + "." + attribute, attribute);
			} catch (Exception e) {
				logger.error("Exception occurs while parsing attribute {} for ObjectName={}", attribute,
					objectName.toString(), e);
			}
		}
		return metrics;
	}
}
