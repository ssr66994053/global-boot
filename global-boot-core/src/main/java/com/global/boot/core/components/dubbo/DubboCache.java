/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-16 15:46 创建
 */
package com.global.boot.core.components.dubbo;

import java.lang.annotation.*;

/**
 * dubbo 消费者缓存注解。
 * <p>
 * 在dubbo服务接口方法上标注此注解，消费者会优先从缓存中读取数据。
 *
 * <pre class="code">
 * {@code
 *
 * 缓存key=namespace+cacheName +':'+spring el
 * namespace默认为："cache_" + Apps.getAppName()，如果服务提供者没有修改yedis默认namespace配置，此项不用设置。
 *
 * }
 * </pre>
 * @author qiubo@yiji.com
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DubboCache {

	/**
	 * cache name
	 */
	String cacheName() default "";
	
	/**
	 * 为了防止缓存key冲突，我们在yedis上加了namespace的概念，默认namespace和yedis一致,都为{@link com.yiji.boot.yedis.YedisProperties#namespace}。
	 * <p>
	 * 如果服务提供者修改了应用的<code>yiji.yedis.namespace</code>配置，请设置此值和配置相同。如果没有修改配置，则不需要填写此值。
	 * <p>
	 *
	 *
	 */
	String namespace() default "";

	/**
	 * spring el 表达式，第一个参数用p0表示。
	 * <p>比如第一个参数gid作为key，表达式为#p0.gid
	 * <p>比如第一个参数name和age作为key，表达式为#p0.name+#p0.age
	 * <p>比如第一个参数name和age，中间加.作为key，表达式为#p0.name+'.'+#p0.age
	 */
	String key();
	
	/**
	 * 缓存过期时间，单位秒
	 */
	int expire() default 5 * 60;
}
