/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-12 11:15 创建
 */
package com.yiji.boot.mybatis;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.yiji.boot.core.EnvironmentHolder;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author qiubo@yiji.com
 */
@Slf4j
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
	
	private ResourceLoader resourceLoader;
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
		EnvironmentHolder.RelaxedProperty relaxedProperty = new EnvironmentHolder.RelaxedProperty(
			MybatisProperties.PREFIX, "mapperScanPackages");
		String mapperScanPackages = relaxedProperty.getProperty();
		if (Strings.isNullOrEmpty(mapperScanPackages)) {
			mapperScanPackages = MybatisProperties.DEFAULT_MAPPER_SCAN_PACKAGES;
		}
		try {
			if (this.resourceLoader != null) {
				scanner.setResourceLoader(this.resourceLoader);
			}
			scanner.registerFilters();
			scanner.doScan(Splitter.on(",").splitToList(mapperScanPackages).toArray(new String[0]));
		} catch (IllegalStateException ex) {
			log.debug("Could not determine auto-configuration " + "package, automatic mapper scanning disabled.");
		}
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
