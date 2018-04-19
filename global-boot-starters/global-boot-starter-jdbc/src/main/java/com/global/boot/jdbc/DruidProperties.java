/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-24 17:36 创建
 *
 */
package com.global.boot.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.global.boot.core.AppConfigException;
import com.global.boot.core.AutoConfigurationProperties;
import com.global.boot.core.EnvironmentHolder;
//import com.global.framework.hera.client.core.Hera;
import com.global.common.env.Env;
import com.global.common.metrics.MetricsHolder;
import com.global.common.portrait.model.DBEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties(prefix = DruidProperties.PREFIX)
public class DruidProperties implements BeanClassLoaderAware, AutoConfigurationProperties {
	
	public static final String PREFIX = "yiji.ds";
	
	public static final int DEFAULT_SLOW_SQL_THRESHOLD = 1000;
	
	private static final Logger logger = LoggerFactory.getLogger(DruidProperties.class);
	
	private static final int ORACLE_MAX_ACTIVE = 200;
	private static final int MYSQL_MAX_ACTIVE = 100;
	
	private String prefix = PREFIX;
	/**
	 * 是否启用此组件
	 */
	private boolean enable = true;
	
	/**
	 * 必填：jdbc url
	 */
	//@NotBlank(message = "数据库连接不能为空")
	//用jsr303信息展示太不直观
	private String url;
	
	/**
	 * 必填：数据库用户名
	 */
	private String username;
	
	/**
	 * 必填：数据库密码
	 */
	private String password;
	
	/**
	 * 初始连接数
	 */
	private Integer initialSize = 5;
	
	/**
	 * 最小空闲连接数
	 */
	private Integer minIdle = 20;
	
	/**
	 * 最大连接数,支持hera动态修改
	 */
	private Integer maxActive = 100;
	
	/**
	 * 获取连接等待超时的时间
	 */
	private Integer maxWait = 10000;
	
	/**
	 * 慢sql日志阈值，超过此值则打印日志
	 */
	private Integer slowSqlThreshold = DEFAULT_SLOW_SQL_THRESHOLD;
	
	/**
	 * 大结果集阈值，超过此值则打印日志
	 */
	private Integer maxResultThreshold = 1000;
	
	/**
	 * 是否在非线上环境开启打印sql，默认开启
	 */
	private boolean showSql = true;
	
	private ClassLoader beanClassLoader;
	
	public void check() {
		if (enable) {
			Assert.hasText(url, "数据库连接yiji.ds.url不能为空");
			Assert.hasText(username, "数据库用户名yiji.ds.username不能为空");
			Assert.hasText(password, "数据库密码yiji.ds.password不能为空");
		}
	}
	
	public String getUrl() {
		return normalizeUrl(this.url);
	}
	
	public static String normalizeUrl(String url) {
		if (isMysql(url) && !url.contains("?")) {
			return url
					+ "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false";
		}
		return url;
	}
	
	public static boolean isMysql(String url) {
		return url.toLowerCase().startsWith("jdbc:mysql");
	}
	
	public boolean mysql() {
		return isMysql(this.url);
	}
	
	public ClassLoader getBeanClassLoader() {
		return beanClassLoader;
	}
	
	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public Integer getInitialSize() {
		return initialSize;
	}
	
	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}
	
	public Integer getMaxActive() {
		return maxActive;
	}
	
	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	
	public Integer getMaxResultThreshold() {
		return maxResultThreshold;
	}
	
	public void setMaxResultThreshold(Integer maxResultThreshold) {
		this.maxResultThreshold = maxResultThreshold;
	}
	
	public Integer getMaxWait() {
		return maxWait;
	}
	
	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}
	
	public Integer getMinIdle() {
		return minIdle;
	}
	
	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Integer getSlowSqlThreshold() {
		return slowSqlThreshold;
	}
	
	public void setSlowSqlThreshold(Integer slowSqlThreshold) {
		this.slowSqlThreshold = slowSqlThreshold;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isShowSql() {
		return showSql;
	}
	
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	
	/**
	 * 从环境配置中构建数据源
	 * @param prefix 配置前缀
	 */
	public static DruidDataSource buildFromEnv(String prefix) {
		DruidProperties druidProperties = new DruidProperties();
		EnvironmentHolder.buildProperties(druidProperties, prefix);
		return druidProperties.build();
	}
	
	/**
	 * 通过当前配置创建datasource
	 */
	DruidDataSource build() {
		this.check();
		if (this.beanClassLoader == null) {
			this.beanClassLoader = ClassUtils.getDefaultClassLoader();
		}
		DruidDataSource dataSource = new DruidDataSource();
		// 基本配置
		dataSource.setDriverClassLoader(this.getBeanClassLoader());
		dataSource.setUrl(this.getUrl());
		dataSource.setUsername(this.getUsername());
		dataSource.setPassword(this.getPassword());
		//应用程序可以自定义的参数
		dataSource.setInitialSize(this.getInitialSize());
		dataSource.setMinIdle(this.getMinIdle());
		
		if (mysql()) {
			maxActive = Math.max(maxActive, MYSQL_MAX_ACTIVE);
		} else {
			maxActive = Math.max(maxActive, ORACLE_MAX_ACTIVE);
		}
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(this.getMaxWait());
		//dataSource.setConnectionProperties(druidProperties.getConnectionProperties());
		//检测需要关闭的空闲连接间隔，单位是毫秒
		dataSource.setTimeBetweenEvictionRunsMillis(60000);
		//连接在池中最小生存的时间
		dataSource.setMinEvictableIdleTimeMillis(60000);
		dataSource.setTestWhileIdle(true);
		//从连接池中获取连接时不测试
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.setValidationQueryTimeout(5);
		
		//		dataSource.setRemoveAbandoned(true);
		//		dataSource.setRemoveAbandonedTimeout(1800);
		//		dataSource.setLogAbandoned(true);
		if (this.mysql()) {
			System.setProperty("druid.mysql.usePingMethod", "true");
			dataSource.setValidConnectionChecker(new MySqlValidConnectionChecker());
		} else {
			System.setProperty("druid.oracle.pingTimeout", "5");
			dataSource.setValidConnectionChecker(new OracleValidConnectionChecker());
		}
		//fixme 开启ps cache
		//dataSource.setPoolPreparedStatements(!druidProperties.mysql());
		Properties properties = new Properties();
		if (this.isShowSql()) {
			properties.put("yiji.ds.logForHumanRead", Boolean.TRUE.toString());
		}
		if (!Env.isOnline()) {
			//线下测试时，执行时间超过100ms就打印sql，用户可以设置为0，每条sql语句都打印
			properties.put("yiji.ds.slowSqlMillis",
				Integer.toString(Math.min(this.getSlowSqlThreshold(), DEFAULT_SLOW_SQL_THRESHOLD)));
		} else {
			//线上运行时，阈值选最大值。有可能线下测试设置为0，方便调试
			properties.put("yiji.ds.slowSqlMillis",
				Integer.toString(Math.max(this.getSlowSqlThreshold(), DEFAULT_SLOW_SQL_THRESHOLD)));
		}
		properties.put("yiji.ds.maxResult", Integer.toString(this.getMaxResultThreshold()));
		dataSource.setConnectProperties(properties);
		try {
			dataSource.init();
			registerMetrics(dataSource);
		} catch (SQLException e) {
			throw new AppConfigException("druid连接池初始化失败", e);
		}
		
		
//		Hera.addValueTrigger(event -> {
//			event.ifPresent(this.mergePropertyPath("maxActive"), v -> {
//				try {
//					logger.info("修改数据源:id={},url={},maxActive={}", dataSource.getID(), dataSource.getUrl(), v);
//					dataSource.setMaxActive(Integer.parseInt(v.toString()));
//				} catch (Exception e) {
//					logger.error("动态修改数据源" + dataSource + "配置失败", e);
//				}
//			});
//			event.ifPresent(this.mergePropertyPath("password"), v -> {
//				logger.info("修改数据源:id={},url={}密码", dataSource.getID(), dataSource.getUrl());
//				dataSource.setPassword(v.trim());
//			});
//			//支持主节点宕机后,切换到其他节点
//			event.ifPresent(this.mergePropertyPath("url"), v -> {
//				v = v.trim();
//				DBEndpoint oldDE = parseJDBCUrl(DruidProperties.this.getUrl());
//				DBEndpoint newDE = parseJDBCUrl(v);
//				if (!newDE.getIp().equals(oldDE.getIp())) {
//					String newUrl = normalizeUrl(v);
//					logger.info("修改数据源:id={},url={},新url={}", dataSource.getID(), dataSource.getUrl(), newUrl);
//					//fixme: close时设置inited=false
//					dataSource.close();
//					try {
//						dataSource.restart();
//					} catch (SQLException e) {
//						logger.error("修改数据源连接失败", e);
//					}
//					dataSource.setUrl(newUrl);
//				}
//			});
//		});
//		
		
		
		return dataSource;
	}
	
	private void registerMetrics(DruidDataSource dataSource) {
		Long id = dataSource.getID();
		MetricRegistry metricRegistry = MetricsHolder.metricRegistry();
		metricRegistry.register(MetricRegistry.name("druid", "activeCount", "id" + id.toString()),
			(Gauge<Integer>) dataSource::getActiveCount);
		metricRegistry.register(MetricRegistry.name("druid", "maxActive", "id" + id.toString()),
			(Gauge<Integer>) dataSource::getMaxActive);
		metricRegistry.register(MetricRegistry.name("druid", "full", "id" + id.toString()), (Gauge<Integer>) () -> {
			if (dataSource.isFull()) {
				return 1;
			} else {
				return 0;
			}
		});
	}
	
	public static DBEndpoint parseJDBCUrl(String url) {
		DBEndpoint dbEndpoint = new DBEndpoint();
		try {
			if (url.startsWith("jdbc:oracle")) {
				//oracle  jdbc:oracle:thin:@192.168.0.121:1522:yjfcdb
				String dbNmae = url.substring(url.lastIndexOf(':') + 1);
				dbEndpoint.setDbName(dbNmae);
				url = url.substring(url.indexOf(":@") + 2, url.lastIndexOf(':'));
				String ip = url.substring(0, url.indexOf(':'));
				String port = url.substring(url.indexOf(':') + 1);
				dbEndpoint.setIp(ip);
				dbEndpoint.setPort(Integer.parseInt(port));
				dbEndpoint.setName("oracle");
			} else if (url.startsWith("jdbc:mysql")) {
				//mysql jdbc:mysql://dbmaster51.cqmydb.yiji.me:3306/yjf_bresgw?xxxxxx
				String dbName = url.substring(url.lastIndexOf('/') + 1);
				int idx = dbName.indexOf('?');
				if (idx != -1) {
					dbName = dbName.substring(0, idx);
				}
				dbEndpoint.setDbName(dbName);
				url = url.substring(url.indexOf("://") + 3, url.lastIndexOf('/'));
				String ip = url.substring(0, url.indexOf(':'));
				String port = url.substring(url.indexOf(':') + 1);
				dbEndpoint.setIp(ip);
				dbEndpoint.setPort(Integer.parseInt(port));
				dbEndpoint.setName("mysql");
			} else {
				dbEndpoint.setDesc("unkown url:" + url);
			}
		} catch (Exception e) {
			dbEndpoint.setDesc("url parse error:" + e.getMessage());
		}
		return dbEndpoint;
	}
	
	@Override
	public String getPath() {
		return prefix;
	}
	
	@Override
	public void setPath(String path) {
		this.prefix = path;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}