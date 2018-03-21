/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-09-09 09:28 创建
 *
 */
package com.yiji.boot.mybatis;

import com.google.common.collect.Lists;
import com.yiji.boot.jdbc.JDBCAutoConfiguration;
import com.yjf.common.mybatis.MoneyTypeHandler;
import com.yjf.common.util.StringUtils;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * @author yanglie@yiji.com
 * @author qiubo@yiji.com
 */
@Configuration
@EnableConfigurationProperties({ MybatisProperties.class })
@ConditionalOnProperty(value = "yiji.mybatis.enable", matchIfMissing = true)
@Import({ MapperScannerRegistrar.class })
@AutoConfigureAfter(JDBCAutoConfiguration.class)
public class MybatisAutoConfiguration {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);
	
	private static final String MAPPER_RESOURCE_PATTERN =  ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +"/mybatis/**/*Mapper.xml";
	@Autowired(required = false)
	private Interceptor[] interceptors;
	static {
		VFS.USER_IMPLEMENTATIONS.add(SpringBootVFS.class);
	}
	@Autowired
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(	@Qualifier("dataSource") DataSource dataSource,
												MybatisProperties properties) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		if (org.springframework.util.StringUtils.hasText(properties.getConfig())) {
			factory.setConfigLocation(getConfigResource(properties.getConfig()));
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (org.springframework.util.StringUtils.hasLength(properties.getEntityScanPackages())) {
			factory.setTypeAliasesPackage(properties.getEntityScanPackages());
		}
		if (org.springframework.util.StringUtils.hasLength(properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
		}
		factory.setMapperLocations(getMapperResources());
		
		factory.setTypeHandlers(Lists.newArrayList(commonTypeHandlers()).toArray(new TypeHandler<?>[0]));
		
		SqlSessionFactory sqlSessionFactory = factory.getObject();
		customConfig(sqlSessionFactory.getConfiguration(), properties);
		return sqlSessionFactory;
	}
	
	private void customConfig(	org.apache.ibatis.session.Configuration configuration,
								MybatisProperties mybatisProperties) {
		BeanWrapperImpl wrapper = new BeanWrapperImpl(configuration);
		mybatisProperties.getSettings().entrySet().stream()
			.forEach(entry -> wrapper.setPropertyValue(entry.getKey(), entry.getValue()));
	}
	
	/***
	 * 通用TypeHandler列表
	 */
	private List<TypeHandler<?>> commonTypeHandlers() {
		MoneyTypeHandler moneyTypeHandler = new MoneyTypeHandler();
		return Lists.newArrayList(moneyTypeHandler);
	}
	
	@Bean(name = "sqlSessionTemplate", destroyMethod = "")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	private Resource[] getMapperResources() {
		String pattern =MAPPER_RESOURCE_PATTERN;
		Resource[] resources;
		try {
			resources = this.resourcePatternResolver.getResources(pattern);
		} catch (IOException e) {
			throw new RuntimeException("加载mapper资源文件失败", e);
		}
		return resources;
	}

	private Resource getConfigResource(String config) {
		Resource rs = null;
		if (StringUtils.isNotBlank(config)) {
			try {
				rs = resourcePatternResolver.getResource(config);
			} catch (Exception e) {
				throw new RuntimeException(String.format("加载mybatis配置文件%s失败", config), e);
			}
		}
		return rs;
	}
}