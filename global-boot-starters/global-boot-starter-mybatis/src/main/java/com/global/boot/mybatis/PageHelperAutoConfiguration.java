/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-13 10:05 创建
 */
package com.global.boot.mybatis;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 当前spring版本不支持构造器注入，只能重写
 * @author qiubo@yiji.com
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@ConfigurationProperties
@EnableConfigurationProperties
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PageHelperAutoConfiguration {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	private Map<String, String> pagehelper = new LinkedHashMap<>();
	
	@PostConstruct
	public void addPageInterceptor() {
		PageInterceptor interceptor = new PageInterceptor();
		Properties properties = new Properties();
		properties.putAll(pagehelper);
		interceptor.setProperties(properties);
		sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
	}
	
	public Map<String, String> getPagehelper() {
		return pagehelper;
	}
	
	public void setPagehelper(Map<String, String> pagehelper) {
		this.pagehelper = pagehelper;
	}
}
