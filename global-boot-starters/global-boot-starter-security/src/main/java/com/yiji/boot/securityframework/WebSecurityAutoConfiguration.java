/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月7日 下午5:04:46 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.AutoConfigurationPropertyUtils;
import com.yiji.boot.securityframework.WebSecurityProperties.ConvertBindProperties;
import com.yiji.boot.securityframework.WebSecurityProperties.ProtectProperties;
import com.yiji.boot.securityframework.WebSecurityProperties.RedirectProperties;
import com.yiji.boot.securityframework.WebSecurityProperties.ValidSignatureProperties;
import com.yiji.common.security.SecurityConfigurationException;
import com.yiji.common.security.SecurityManager;
import com.yiji.common.security.annotation.*;
import com.yiji.common.security.bean.Syphon;
import com.yiji.common.security.spring.web.*;
import com.yiji.common.security.web.Redirect;
import com.yiji.common.security.web.ServletWebCryptographicService;
import com.yiji.common.security.web.WebCryptographicService;
import com.yiji.common.web.Scope;
import com.yiji.common.web.ServletWebScopeService;
import com.yiji.common.web.WebScopeService;
import com.yjf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.Assert;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Servlet;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 安全框架Web相关的功能自动配置。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@Configuration
@EnableConfigurationProperties({ SecurityProperties.class, WebSecurityProperties.class })
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class })
@ConditionalOnProperty(value = { SecurityProperties.PATH + ".enable", WebSecurityProperties.PATH + ".enable" },
		matchIfMissing = true)
@ConditionalOnBean({ SecurityManager.class })
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class WebSecurityAutoConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityAutoConfiguration.class);
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private WebSecurityProperties webSecurityProperties;
	
	private ProtectAnnotationAdvice protectAnnotationAdvice;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		ConvertBindProperties convertBindProperties = this.webSecurityProperties.getConvertBindProperties();
		if (convertBindProperties.isEnable()) {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能启用[{}]与[{}]注解的支持。", ConvertBind.class.getName(), InjectBind.class.getName());
			}
			ServletRequestConvertBindSecurityResolver resolver = new ServletRequestConvertBindSecurityResolver();
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver, "ciphertextName");
			if (StringUtils.isBlank(convertBindProperties.getDefaultResultProcessorName())) {
				if (infoEnabled) {
					LOGGER.info("没有配置必须的属性[{}]，设置默认的结果处理器名称'" + SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX
								+ "URLQueryStringResultProcessor'。",
						convertBindProperties.mergePropertyPath("defaultResultProcessorName"));
				}
				resolver.setDefaultResultProcessorName(SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX
														+ "URLQueryStringResultProcessor");
			} else {
				AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver,
					"defaultResultProcessorName");
			}
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver,
				"securityManagerRefName", "defaultSecurityManagerName");
			resolver.setLogOriginal(convertBindProperties.isLogOriginal());
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver, "name");
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver, "scope");
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver, "securityUserRef");
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(convertBindProperties, resolver, "signatureName");
			resolver.setBeanFactory(this.applicationContext);
			argumentResolvers.add(resolver);
		} else {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能未启用[{}]与[{}]注解的支持。", ConvertBind.class.getName(), InjectBind.class.getName());
			}
		}
	}
	
	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		RedirectProperties redirectProperties = this.webSecurityProperties.getRedirectProperties();
		if (redirectProperties.isEnable()) {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能启用[{}]作为[Controller]的返回值的支持。", Redirect.class.getName());
			}
			ServletRedirectMethodReturnValueHandler handler = new ServletRedirectMethodReturnValueHandler();
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(redirectProperties, handler, "ciphertextName");
			if (StringUtils.isBlank(redirectProperties.getWebCryptographicServiceRefName())) {
				handler.setWebCryptographicService(new ServletWebCryptographicService());
			} else {
				WebCryptographicService webCryptographicService = BeanUtils.getBean(this.applicationContext,
					redirectProperties.getWebCryptographicServiceRefName(), WebCryptographicService.class,
					redirectProperties, "webCryptographicServiceRefName");
				handler.setWebCryptographicService(webCryptographicService);
			}
			if (StringUtils.isBlank(redirectProperties.getWebScopeServiceRefName())) {
				handler.setWebScopeService(new ServletWebScopeService());
			} else {
				WebScopeService webScopeService = BeanUtils.getBean(this.applicationContext,
					redirectProperties.getWebScopeServiceRefName(), WebScopeService.class, redirectProperties,
					"webScopeServiceRefName");
				handler.setWebScopeService(webScopeService);
			}
			if (StringUtils.isBlank(redirectProperties.getSecurityManagerRefName())) {
				handler.setSecurityManager(BeanUtils.getBean(this.applicationContext, SecurityManager.class,
					redirectProperties, "securityManagerRefName"));
			} else {
				SecurityManager securityManager = BeanUtils.getBean(this.applicationContext,
					redirectProperties.getSecurityManagerRefName(), SecurityManager.class, redirectProperties,
					"securityManagerRefName");
				handler.setSecurityManager(securityManager);
			}
			if (StringUtils.isBlank(redirectProperties.getConversionServiceRefName())) {
				ConversionService conversionService;
				try {
					conversionService=BeanUtils.getBean(this.applicationContext, ConversionService.class,
                            redirectProperties, "conversionServiceRefName");
				} catch (Exception e) {
					conversionService=this.applicationContext.getBean("defaultConversionService",ConversionService.class);
				}
				Assert.notNull(conversionService);
				handler.setConversionService(conversionService);
			} else {
				ConversionService webScopeService = BeanUtils.getBean(this.applicationContext,
					redirectProperties.getConversionServiceRefName(), ConversionService.class, redirectProperties,
					"conversionServiceRefName");
				handler.setConversionService(webScopeService);
			}
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(redirectProperties, handler, "signatureName");
			String encodeCharset = redirectProperties.getEncodeCharset();
			if (StringUtils.isNotBlank(encodeCharset)) {
				Charset charset;
				try {
					charset = Charset.forName(encodeCharset);
				} catch (Exception e) {
					throw new SecurityConfigurationException("["
																+ redirectProperties.mergePropertyPath("encodeCharset")
																+ "]属性不是正确的编码类型。");
				}
				handler.setEncodeCharset(charset);
			}
			returnValueHandlers.add(handler);
		} else {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能未启用[{}]作为[Controller]的返回值的支持。", Redirect.class.getName());
			}
		}
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		ValidSignatureProperties validSignatureProperties = this.webSecurityProperties.getValidSignatureProperties();
		if (validSignatureProperties.isEnable()) {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能启用[{}]注解的支持。", ValidSignature.class.getName());
			}
			ServletValidSignatureAnnotationHandlerInterceptor interceptor = new ServletValidSignatureAnnotationHandlerInterceptor();
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(validSignatureProperties, interceptor,
				"securityManagerRefName", "defaultSecurityManagerName");
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(validSignatureProperties, interceptor,
				"securityUserRef");
			AutoConfigurationPropertyUtils.safeCopyNotNullProperty(validSignatureProperties, interceptor,
				"signatureName");
			interceptor.setValidThrowException(validSignatureProperties.isValidThrowException());
			if (StringUtils.isBlank(validSignatureProperties.getWebScopeServiceRefName())) {
				interceptor.setWebScopeService(new ServletWebScopeService());
			} else {
				WebScopeService webScopeService = BeanUtils.getBean(this.applicationContext,
					validSignatureProperties.getWebScopeServiceRefName(), WebScopeService.class,
					validSignatureProperties, "webScopeServiceRefName");
				interceptor.setWebScopeService(webScopeService);
			}
			String valueScope = validSignatureProperties.getValueScope();
			List<Scope> scopes = new ArrayList<>(3);
			if (StringUtils.isNotBlank(valueScope)) {
				List<String> valueScopes = StringUtils.stringSplitToList(valueScope, "\\,");
				for (String s : valueScopes) {
					Scope scope;
					try {
						scope = Scope.valueOf(s);
					} catch (Exception e) {
						throw new SecurityConfigurationException(
							"[" + validSignatureProperties.mergePropertyPath("valueScope") + "]属性必须为["
									+ Scope.class.getName() + "]类型存在的值。");
					}
					scopes.add(scope);
				}
			}
			if (!scopes.isEmpty()) {
				interceptor.setValueScope(scopes.toArray(new Scope[scopes.size()]));
			}
			interceptor.setBeanFactory(this.applicationContext);
			registry.addInterceptor(interceptor);
		} else {
			if (infoEnabled) {
				LOGGER.info("安全框架Web相关的功能未启用[{}]注解的支持。", ValidSignature.class.getName());
			}
		}
	}
	
	/**
	 * 创建一个名为 <code>yijiBoot_SF_URLQueryStringResultProcessor</code>的 {@link URLQueryStringResultProcessor} 到容器。
	 * @return 创建到容器的 {@link URLQueryStringResultProcessor} 的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "URLQueryStringResultProcessor")
	@ConditionalOnMissingBean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "URLQueryStringResultProcessor")
	public URLQueryStringResultProcessor createURLQueryStringResultProcessor() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("系统不存在[{}]的Bean，创建名为'" + SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX
						+ "URLQueryStringResultProcessor'，类型为'{}'的Bean到容器。", ResultProcessor.class.getName(),
				URLQueryStringResultProcessor.class.getName());
		}
		return new URLQueryStringResultProcessor();
	}
	
	/**
	 * 创建一个 FormattingConversionService 到容器。
	 * @return 创建到容器的 FormattingConversionService 的实例。
	 */
	@Bean
	@ConditionalOnMissingBean(ConversionService.class)
	public FactoryBean<FormattingConversionService> conversionService() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("系统不存在[{}]的Bean，创建类型为'{}'的Bean到容器。", ConversionService.class.getName(),
				FormattingConversionService.class.getName());
		}
		return new FormattingConversionServiceFactoryBean();
	}
	
	/**
	 * 创建一个 RequestContextFilter 到容器。
	 * @return 创建到容器的 RequestContextFilter 的实例。
	 */
	@Bean
	@ConditionalOnMissingBean(RequestContextFilter.class)
	public RequestContextFilter requestContextFilter() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("系统不存在[{}]的Bean，创建类型为'{}'的Bean到容器。", RequestContextFilter.class.getName(),
				RequestContextFilter.class.getName());
		}
		return new RequestContextFilter();
	}
	
	/**
	 * 创建一个 DefaultAdvisorAutoProxyCreator 到容器。
	 * @return 创建到容器的 DefaultAdvisorAutoProxyCreator 的实例。
	 */
	@Bean
	@ConditionalOnProperty(value = { ProtectProperties.PATH + ".enable" }, matchIfMissing = true)
	@ConditionalOnMissingBean(AspectJAwareAdvisorAutoProxyCreator.class)
	public AnnotationAwareAspectJAutoProxyCreator createDefaultAdvisorAutoProxyCreator() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("系统不存在[{}]的Bean，创建类型为'{}'的Bean到容器。", AbstractAdvisorAutoProxyCreator.class.getName(),
				AnnotationAwareAspectJAutoProxyCreator.class.getName());
		}
		return new AnnotationAwareAspectJAutoProxyCreator();
	}
	
	/**
	 * 创建一个名为<code>yijiBoot_SF_ProtectAnnotationAdvice</code>的 {@link ProtectAnnotationAdvice} 到容器。
	 * @return 创建到容器的 {@link ProtectAnnotationAdvice} 的实例。
	 */
	@Bean(name = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "ProtectAnnotationAdvice")
	@ConditionalOnProperty(value = { ProtectProperties.PATH + ".enable" }, matchIfMissing = true)
	@ConditionalOnBean(Syphon.class)
	public ProtectAnnotationAdvice createProtectAnnotationAdvice() {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		if (infoEnabled) {
			LOGGER.info("系统不存在[{}]的Bean，创建名为'{}'类型为'{}'的Bean到容器。", ProtectAnnotationAdvice.class.getName(),
				SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "ProtectAnnotationAdvice",
				ServletProtectAnnotationAdvice.class.getName());
		}
		ProtectProperties protectProperties = this.webSecurityProperties.getProtectProperties();
		ServletProtectAnnotationAdvice advice = new ServletProtectAnnotationAdvice();
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(protectProperties, advice, "defaultSecurityUser");
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(protectProperties, advice, "logOriginal");
		if (StringUtils.isBlank(protectProperties.getSecurityManagerRefName())) {
			advice.setSecurityManager(BeanUtils.getBean(this.applicationContext, SecurityManager.class,
				protectProperties, "securityManagerRefName"));
		} else {
			SecurityManager securityManager = BeanUtils.getBean(this.applicationContext,
				protectProperties.getSecurityManagerRefName(), SecurityManager.class, protectProperties,
				"securityManagerRefName");
			advice.setSecurityManager(securityManager);
		}
		AutoConfigurationPropertyUtils.safeCopyNotNullProperty(protectProperties, advice, "securityUserRef");
		if (StringUtils.isBlank(protectProperties.getSyphonRefName())) {
			advice.setSyphon(BeanUtils.getBean(this.applicationContext, Syphon.class, protectProperties,
				"syphonRefName"));
		} else {
			Syphon syphon = BeanUtils.getBean(this.applicationContext, protectProperties.getSyphonRefName(),
				Syphon.class, protectProperties, "syphonRefName");
			advice.setSyphon(syphon);
		}
		if (StringUtils.isBlank(protectProperties.getWebScopeServiceRefName())) {
			advice.setWebScopeService(new ServletWebScopeService());
		} else {
			WebScopeService webScopeService = BeanUtils.getBean(this.applicationContext,
				protectProperties.getWebScopeServiceRefName(), WebScopeService.class, protectProperties,
				"webScopeServiceRefName");
			advice.setWebScopeService(webScopeService);
		}
		this.protectAnnotationAdvice = advice;
		return advice;
	}
	
	/**
	 * 创建 ProtectAnnotationAdvice 的PointcutAdvisor到容器，该PointcutAdvisor为所有
	 * {@link org.springframework.stereotype.Controller} 注解。
	 * @return 创建的 PointcutAdvisor 。
	 */
	@Bean
	@DependsOn(SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "ProtectAnnotationAdvice")
	@ConditionalOnProperty(value = { ProtectProperties.PATH + ".protectAllController" }, matchIfMissing = true)
	@ConditionalOnBean(AspectJAwareAdvisorAutoProxyCreator.class)
	@ConditionalOnClass(name = "org.aspectj.weaver.tools.JoinPointMatch")
	public AspectJExpressionPointcutAdvisor createProtectAnnotationAdvicePointcutAdvisorByAnnotation() {
		String expression = "within(@org.springframework.stereotype.Controller * and !org.springframework.boot..*)";
		return createProtectAnnotationAdvicePointcutAdvisor(expression);
	}
	
	private AspectJExpressionPointcutAdvisor createProtectAnnotationAdvicePointcutAdvisor(String expression) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("创建保护相关的注解的Advice的PointcutAdvisor，类型为[{}]，AspectJ表达式为'{}'。",
				AspectJExpressionPointcutAdvisor.class, expression);
		}
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setAdvice(this.protectAnnotationAdvice);
		advisor.setExpression(expression);
		return advisor;
	}
	
}