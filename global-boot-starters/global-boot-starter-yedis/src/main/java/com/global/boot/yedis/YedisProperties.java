/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * yanglie@yiji.com 2015-08-20 14:08 创建
 *
 */
package com.global.boot.yedis;

import com.global.boot.core.Apps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglie@yiji.com
 */
@ConfigurationProperties(prefix = "yiji.yedis")
@Data
public class YedisProperties {
	
	private boolean enable = true;
	/**
	 * 必填：访问地址
	 */
	private String host = "localhost";
	/**
	 * 必填：访问端口
	 */
	private int port = 6379;
	/**
	 * 可选：应用缓存空间名，必须注意，为避免与其它应用发生冲突，建议采用默认名，自定义命名空间一定要特别注意。
	 */
	private String namespace = buidCacheNameSpace(Apps.getAppName());
	/**
	 * 可选：创建socket连接的超时时间
	 */
	private int timeOut = 2000;
	
	/**
	 * 可选：基于注解的Spring CacheManager，设置缓存的过期时间。默认为0，即不会过期
	 * 如果是使用RedisTemplate来显示读写缓存的，需要自己调用expire方法设置每个key的过期时间 单位秒
	 */
	private int expireTime = 0;
	/**
	 * 可选：连接池配置
	 */
	private Pool pool = new Pool();
	
	public static String buidCacheNameSpace(String appName) {
		return "cache_" + appName + ":";
	}
	
	@Data
	public static class Pool {
		/**
		 * 最大连接数
		 */
		private int maxTotal = 500;
		/**
		 * 最大空闲连接
		 */
		private int maxIdle = 8;
		/**
		 * 最大等待连接时间
		 */
		private int maxWait = 2000;
		
	}
}
