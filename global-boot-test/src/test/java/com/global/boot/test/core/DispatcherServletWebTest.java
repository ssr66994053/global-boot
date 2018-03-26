/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-17 10:24 创建
 *
 */
package com.global.boot.test.core;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.global.boot.test.TestBase;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author qiubo@yiji.com
 */
@SuppressWarnings("all")
public class DispatcherServletWebTest extends TestBase {
	
	@Test
	public void testListAllLoadBean() throws Exception {
		String[] str = getApplicationContext().getBeanDefinitionNames();
		System.out.println(Arrays.toString(str));
	}
	
	/**
	 * make sure view resolver only register one
	 * @throws Exception
	 */
	@Test
	public void testViewResolver() throws Exception {
		DispatcherServlet dispatcherServlet = getDispatcherServlet();
		List<ViewResolver> viewResolvers = (List<ViewResolver>) ReflectionTestUtils.getField(dispatcherServlet,
			"viewResolvers");
		//		assertThat(viewResolvers).hasSize(1);
		assertThat(viewResolvers.get(0)).isInstanceOf(VelocityViewResolver.class);
	}
	
	@Test
	public void testResponseBodyConverters() throws Exception {
		DispatcherServlet dispatcherServlet = getDispatcherServlet();
		List<HandlerAdapter> handlerAdapters = (List<HandlerAdapter>) ReflectionTestUtils.getField(dispatcherServlet,
			"handlerAdapters");
		assertThat(
			handlerAdapters.stream().filter((handlerAdapter) -> handlerAdapter instanceof RequestMappingHandlerAdapter)
				.count()).isEqualTo(1);
		RequestMappingHandlerAdapter requestMappingHandlerAdapter = (RequestMappingHandlerAdapter) handlerAdapters
			.stream().filter((handlerAdapter) -> handlerAdapter instanceof RequestMappingHandlerAdapter).findFirst()
			.get();
		assertThat(
			requestMappingHandlerAdapter.getMessageConverters().stream()
				.filter((converter) -> converter instanceof FastJsonHttpMessageConverter).count()).isEqualTo(1);
		
	}
	
}
