/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2016年4月25日 下午7:23:36 创建
 */
package com.yiji.boot.securityframework.index.ri;

import com.yiji.common.security.index.IndexManager;
import com.yiji.common.security.index.IndexModeFactory;
import com.yiji.common.security.index.mode.def.DefaultIndexBuilderFactory;
import com.yiji.common.security.index.referenceimplements.RIDefaultIndexModeFactory;
import com.yiji.common.security.index.referenceimplements.RIIndexManager;
import com.yiji.common.security.referenceimplements.SecurityConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>ri</code>环境创建 {@link IndexManager} 的工厂。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class RiIndexManagerFactory {
	
	/**
	 * 创建一个 {@link IndexManager} 的实例。
	 * @param securityConfigs 配置参数的印射。
	 * @return 创建的实例。
	 */
	public IndexManager createIndexManager(Map<String, SecurityConfig> securityConfigs) {
		RIIndexManager indexManager = new RIIndexManager();
		indexManager.setSecurityConfigMap(securityConfigs);
		// 支持的索引模式的工厂列表
		ConcurrentMap<String, IndexModeFactory> indexModeFactorys = new ConcurrentHashMap<>();
		// 默认索引模式
		RIDefaultIndexModeFactory riDefaultIndexModeFactory = new RIDefaultIndexModeFactory();
		riDefaultIndexModeFactory.setLogOriginal(true);
		riDefaultIndexModeFactory.setSecurityConfigMap(securityConfigs);
		indexModeFactorys.put(DefaultIndexBuilderFactory.MODE, riDefaultIndexModeFactory);
		
		indexManager.setIndexModeFactorys(indexModeFactorys);
		return indexManager;
	}
	
}
