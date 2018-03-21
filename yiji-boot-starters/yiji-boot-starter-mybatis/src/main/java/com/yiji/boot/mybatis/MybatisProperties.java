/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 09:20 创建
 *
 */
package com.yiji.boot.mybatis;

import com.google.common.collect.Maps;
import com.yiji.boot.core.Apps;
import lombok.Data;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
@Data
public class MybatisProperties implements InitializingBean {
	
	public static final String PREFIX = "yiji.mybatis";
	/**
	 * 必填：默认实体扫描包
	 */
	public static final String DEFAULT_ENTITY_SCAN_PACKAGES = Apps.getBasePackage() + ".dal.entity";
	/**
	 * 必填：默认mapper接口扫描包
	 */
	public static final String DEFAULT_MAPPER_SCAN_PACKAGES = Apps.getBasePackage() + ".dal.mapper";
	
	/**
	 * 是否启用此组件
	 */
	private boolean enable = true;
	
	/**
	 * 必填： mybatis mapper接口扫描路径，多个包用逗号隔开。
	 * <p>
	 * 如果不指定，扫描路径为{@link MybatisProperties#DEFAULT_MAPPER_SCAN_PACKAGES}
	 * <p>
	 * mybatis会扫描这些包下的mapper并注册为{@link MapperFactoryBean}
	 */
	private String mapperScanPackages = DEFAULT_MAPPER_SCAN_PACKAGES;
	/**
	 * 必填： mybatis 实体 扫描路径，多个包用逗号隔开。
	 * <p>
	 * 如果不指定，扫描路径为{@link MybatisProperties#DEFAULT_ENTITY_SCAN_PACKAGES}
	 * <p>
	 * mybatis会扫描此包下的所有的类，注册到alias中
	 */
	private String entityScanPackages = DEFAULT_ENTITY_SCAN_PACKAGES;
	
	/**
	 * 可选：mybatis-config.xml配置文件路径
	 */
	private String config;
	
	/**
	 * 可选: mybatis配置
	 * ,ref:http://www.mybatis.org/mybatis-3/configuration.html#settings
	 */
	private Map<String, String> settings;
	/**
	 * 可选：自定义类型处理器TypeHandler所在的包，多个包用逗号分隔
	 */
	private String typeHandlersPackage;
	
	/**
	 * 可选：执行模式，默认为SIMPLE
	 */
	private ExecutorType executorType = ExecutorType.SIMPLE;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (settings == null) {
			settings = Maps.newHashMap();
		}
		if (!settings.containsKey("localCacheScope")) {
			settings.put("localCacheScope", LocalCacheScope.STATEMENT.name());
		}
		settings.put("mapUnderscoreToCamelCase",Boolean.TRUE.toString());
	}
	
}
