package com.global.boot.shiro;

import com.google.common.collect.Maps;
import com.global.boot.yedis.YedisAutoConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.ReflectionBuilder;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.CachingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;

/**
 * @author husheng@yiji.com
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ ShiroProperties.class })
@ConditionalOnProperty(value = "yiji.shiro.enable", matchIfMissing = true)
@AutoConfigureAfter({ YedisAutoConfiguration.class, YedisAutoConfiguration.class })
public class ShiroAutoConfiguration {
	
	@Bean
	public CacheManager shiroCacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
		ShiroCacheManager shiroCacheManager = new ShiroCacheManager();
		shiroCacheManager.setRedisTemplate(redisTemplate);
		return shiroCacheManager;
	}
	
	@Bean
	public WebSecurityManager shiroSecurityManager(@Qualifier("shiroCacheManager") CacheManager shiroCacheManager) {
		
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setCacheManager(shiroCacheManager);
		return securityManager;
	}
	
	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(	@Qualifier("shiroSecurityManager") WebSecurityManager shiroSecurityManager,
															ShiroProperties shiroProperties) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(shiroSecurityManager);
		shiroFilter.setLoginUrl(shiroProperties.getLoginUrl());
		shiroFilter.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
		shiroFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
		shiroFilter.setFilters(buildFiltersMap(shiroProperties));
		shiroFilter.setFilterChainDefinitions(getFilterChainDefinitions(shiroProperties));
		
		return shiroFilter;
	}
	
	@Bean
	public Filter shiroFilter(@Qualifier("shiroFilterFactoryBean") ShiroFilterFactoryBean shiroFilterFactoryBean,
								@Qualifier("shiroRealm") Realm shiroRealm) throws Exception {
		//延迟加载shiroRealm。由于shiroFilterFactoryBean是FactoryBean会提前被加载。
		if (shiroRealm instanceof CachingRealm) {
			CachingRealm realm = (CachingRealm) shiroRealm;
			realm.setCachingEnabled(true);
		}
		if (shiroRealm instanceof AuthorizingRealm) {
			AuthorizingRealm realm = (AuthorizingRealm) shiroRealm;
			realm.setAuthorizationCachingEnabled(true);
			realm.setAuthorizationCacheName(ShiroCacheManager.KEY_AUTHZ);
		}
		if (shiroRealm instanceof AuthenticatingRealm) {
			AuthenticatingRealm realm = (AuthenticatingRealm) shiroRealm;
			realm.setAuthenticationCachingEnabled(true);
			realm.setAuthenticationCacheName(ShiroCacheManager.KEY_AUTHC);
		}
		customSecurityManager(shiroFilterFactoryBean.getSecurityManager(), shiroRealm);
		((DefaultWebSecurityManager) shiroFilterFactoryBean.getSecurityManager()).setRealm(shiroRealm);
		return (Filter) shiroFilterFactoryBean.getObject();
	}
	
	@Bean
	public FilterRegistrationBean shiroFilterRegistrationBean(@Qualifier("shiroFilter") Filter shiroFilter,
																ShiroProperties shiroProperties) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(shiroFilter);
		registration.addUrlPatterns(shiroProperties.getUrlPattern());
		registration.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
		registration.setName("shiroFilter");
		return registration;
	}
	
	@Bean
	@ConditionalOnMissingBean(LifecycleBeanPostProcessor.class)
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor shiroAuthorizationAttributeSourceAdvisor(WebSecurityManager shiroSecurityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(shiroSecurityManager);
		return advisor;
	}
	
	/**
	 * 延迟加载shiroRealm。由于shiroFilterFactoryBean是FactoryBean会提前被加载。
	 *
	 * @param shiroSecurityManager
	 * @param shiroRealm
	 */
	private void customSecurityManager(org.apache.shiro.mgt.SecurityManager shiroSecurityManager, Realm shiroRealm) {
		
		DefaultWebSecurityManager securityManager = ((DefaultWebSecurityManager) shiroSecurityManager);
		if (securityManager.getRealms() == null) {
			
			if (shiroRealm instanceof CachingRealm) {
				CachingRealm realm = (CachingRealm) shiroRealm;
				realm.setCachingEnabled(true);
			}
			if (shiroRealm instanceof AuthorizingRealm) {
				AuthorizingRealm realm = (AuthorizingRealm) shiroRealm;
				realm.setAuthorizationCachingEnabled(true);
				realm.setAuthorizationCacheName(ShiroCacheManager.KEY_AUTHZ);
			}
			if (shiroRealm instanceof AuthenticatingRealm) {
				AuthenticatingRealm realm = (AuthenticatingRealm) shiroRealm;
				realm.setAuthenticationCachingEnabled(true);
				realm.setAuthenticationCacheName(ShiroCacheManager.KEY_AUTHC);
			}
			securityManager.setRealm(shiroRealm);
		}
	}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor shiroAuthorizationAttributeSourceAdvisor(@Qualifier("shiroSecurityManager") WebSecurityManager shiroSecurityManager,
																						@Qualifier("shiroRealm") Realm shiroRealm) {
		customSecurityManager(shiroSecurityManager, shiroRealm);
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(shiroSecurityManager);
		return advisor;
	}
	
	private String getFilterChainDefinitions(ShiroProperties shiroProperties) {
		List<Map<String, String>> urls = shiroProperties.getUrls();
		if (CollectionUtils.isEmpty(urls)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map<String, String> url : urls) {
			if (MapUtils.isNotEmpty(url)) {
				for (Map.Entry<String, String> entry : url.entrySet()) {
					sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
				}
			}
		}
		return sb.toString();
	}
	
	private Map<String, Filter> buildFiltersMap(ShiroProperties shiroProperties) {
		Map<String, String> filters = shiroProperties.getFilters();
		if (MapUtils.isEmpty(filters)) {
			return Maps.newLinkedHashMap();
		}
		
		ReflectionBuilder builder = new ReflectionBuilder();
		Map<String, ?> built = builder.buildObjects(filters);
		return extractFilters(built);
	}
	
	private Map<String, Filter> extractFilters(Map<String, ?> objects) {
		if (CollectionUtils.isEmpty(objects)) {
			return Maps.newLinkedHashMap();
		}
		Map<String, Filter> filterMap = Maps.newLinkedHashMap();
		for (Map.Entry<String, ?> entry : objects.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Filter) {
				filterMap.put(key, (Filter) value);
			}
		}
		return filterMap;
	}
	
}
