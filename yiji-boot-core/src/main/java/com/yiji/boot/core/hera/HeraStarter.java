/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-06 13:46 创建
 */
package com.yiji.boot.core.hera;

import com.google.common.collect.Lists;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.EnvironmentHolder;
import com.yiji.framework.hera.client.core.HeraLauncher;
import com.yjf.common.env.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * 初始化hera(配置管理系统)，如果您没有在系统变量或者环境变量中配置YIJI_HERA_ZK_URL ,根据环境自动配置。
 *
 *
 * <p>
 * <b>自定义hera zk地址</b> hera地址可以在java启动参数中通过-Dyiji.hera.zk.url
 * 设置,也可以在操作系统中添加环境变量YIJI_HERA_ZK_URL设置
 * <p>
 * hera 初始化应该在environmentPrepared最后阶段(没有办法控制顺序，
 * {@link org.springframework.boot.context.event.EventPublishingRunListener}
 * 默认执行顺序最低 )，并且在contextPrepared阶段之前初始化,
 * 这里通过ApplicationContextInitializer来实现初始化hera.
 *
 * <p>
 * 使用{@link org.springframework.boot.env.EnvironmentPostProcessor}
 * 也可以实现配置环境，但是此时日志系统还没初始化，会导致看不到hera启动的日志。
 * <p>
 * 在{@link org.springframework.context.ApplicationContextInitializer}
 * 阶段来初始化hera，也会有副作用，在logback的配置中，不能使用hera配置。不过，我们可以忽略此特性。
 *
 * 主要功能如下:
 * <ul>
 * <li>1.如果启用hera，则添加hera到环境proptertySources中</li>
 * <li>2.hera PropertySource为:
 *
 * <pre>
 *    MapPropertySource [name='systemProperties']
 *    SystemEnvironmentPropertySource [name='systemEnvironment']
 *    RandomValuePropertySource [name='random']
 *    HeraPropertySource [name='hera']
 *    PropertiesPropertySource [name='applicationConfig: [classpath:/application.properties]']
 * </pre> hera环境配置优先级比PropertiesPropertySource高,在hera中的配置可以覆盖在配置文件中的配置。</li>
 * </ul>
 *
 * 更多参考
 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features
 * -external-config.html
 *
 * @author qiubo@yiji.com
 */
public class HeraStarter {
	private static final Logger logger = LoggerFactory.getLogger(HeraStarter.class);
	
	private static CompletableFuture<List<String>> future;
	private static List<String> logs = Lists.newArrayList();
	
	public static Boolean isHeraEnable() {
		return Boolean.valueOf(System.getProperty(Apps.HERA_ENABLE));
	}
	
	public static void start() {
		if (isHeraEnable()) {
			future = CompletableFuture.supplyAsync(() -> {
				try {
					HeraLauncher heraLauncher = HeraLauncher.getInstance();
					String heraZkUrl = System.getProperty(Apps.HERA_ZK_URL);
					if (heraZkUrl == null) {
						heraZkUrl = System.getenv("YIJI_HERA_ZK_URL");
						if (heraZkUrl != null) {
							logs.add("hera:从操作系统环境变量中读取hera地址配置为:" + heraZkUrl);
							System.setProperty(Apps.HERA_ZK_URL, heraZkUrl);
						} else {
							if (Env.isOnline()) {
								heraZkUrl = "zookeeper.yiji.me:2181";
							} else {
								heraZkUrl = "snet.yiji:2181";
							}
							logs.add("hera:根据您程序的运行环境,地址自动配置为:" + heraZkUrl);
							System.setProperty(Apps.HERA_ZK_URL, heraZkUrl);
						}
					} else {
						logs.add("hera:从java系统变量中读取hera地址配置为:" + heraZkUrl);
					}
					heraLauncher.launch();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return logs;
			});
		}
	}
	
	public static void handleResult() {
		if (isHeraEnable()) {
			try {
				future.join().stream().forEach(msg -> logger.warn("{}", msg));
			} catch (Exception e) {
				logger.warn("hera启动失败,使用本地配置", e);
			}
			MutablePropertySources sources = ((ConfigurableEnvironment) EnvironmentHolder.get()).getPropertySources();
			sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new HeraPropertySource());
			
		}
	}
	
}
