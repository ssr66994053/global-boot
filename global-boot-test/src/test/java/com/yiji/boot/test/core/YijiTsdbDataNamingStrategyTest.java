/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-13 08:51 创建
 *
 */
package com.yiji.boot.test.core;

import com.yiji.boot.actuator.metrics.opentsdb.YijiTsdbNamingStrategy;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbName;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author daidai@yiji.com
 */
public class YijiTsdbDataNamingStrategyTest {
	
	@Test
	public void test1() {
		final YijiTsdbNamingStrategy namingStrategy = new YijiTsdbNamingStrategy();
		final OpenTsdbName testMetric = namingStrategy.getName("testMetric");
		assertThat(testMetric.getMetric()).isEqualTo("testMetric");
		assertThat(testMetric.getTags()).hasSize(0);
		
		OpenTsdbName name = namingStrategy.getName("testMetric tag1=value1");
		assertThat(name.getMetric()).isEqualTo("testMetric");
		assertThat(name.getTags()).hasSize(1);
		assertThat(name.getTags()).contains(new Map.Entry<String, String>() {
			@Override
			public String getKey() {
				return "tag1";
			}
			
			@Override
			public String getValue() {
				return "value1";
			}
			
			@Override
			public String setValue(String value) {
				return null;
			}
		});
		
		name = namingStrategy.getName("testMetric tag1=value1,tag2=value2");
		assertThat(name.getMetric()).isEqualTo("testMetric");
		assertThat(name.getTags()).hasSize(2);
		assertThat(name.getTags()).contains(new Map.Entry<String, String>() {
			@Override
			public String getKey() {
				return "tag1";
			}
			
			@Override
			public String getValue() {
				return "value1";
			}
			
			@Override
			public String setValue(String value) {
				return null;
			}
		});
		assertThat(name.getTags()).contains(new Map.Entry<String, String>() {
			@Override
			public String getKey() {
				return "tag2";
			}
			
			@Override
			public String getValue() {
				return "value2";
			}
			
			@Override
			public String setValue(String value) {
				return null;
			}
		});
	}
}
