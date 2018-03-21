/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-11-04 18:08 创建
 *
 */
package com.yiji.boot.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import com.yiji.boot.core.Apps;
import com.yiji.boot.core.EnvironmentHolder;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * 支持jsonPrettyFormat
 * @author qiubo@yiji.com
 */
public class YijiFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

	private volatile SerializerFeature[] featuresWithPrettyFormat = null;

	private SerializerFeature[] simpleModeFeatures = null;

	private boolean jacksonSupport = false;

	public YijiFastJsonHttpMessageConverter() {
		List<SerializerFeature> features = Lists.newArrayList();
		features.add(SerializerFeature.PrettyFormat);
		features.add(SerializerFeature.WriteDateUseDateFormat);
		features.add(SerializerFeature.DisableCircularReferenceDetect);
		simpleModeFeatures = features.toArray(new SerializerFeature[0]);
		jacksonSupport = Boolean.parseBoolean(EnvironmentHolder.get().getProperty("yiji.web.jacksonSupport"));
	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException,
																				HttpMessageNotWritableException {
		OutputStream out = outputMessage.getBody();
		if (outputMessage instanceof ServletServerHttpResponse) {
			ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) outputMessage;
			HttpServletResponse httpServletResponse = servletServerHttpResponse.getServletResponse();
			//如果响应头中格式化json，则格式化输出json
			if (httpServletResponse != null && Boolean.valueOf(httpServletResponse.getHeader(Apps.JSON_PRETTY_FORMAT))) {
				if (featuresWithPrettyFormat == null) {
					List<SerializerFeature> serializerFeatures = Lists.newArrayList(getFeatures());
					serializerFeatures.add(SerializerFeature.PrettyFormat);
					featuresWithPrettyFormat = serializerFeatures.toArray(new SerializerFeature[0]);
				}
				String text = toJSONString(obj, featuresWithPrettyFormat);
				byte[] bytes = text.getBytes(getCharset());
				out.write(bytes);
				return;
			}
			if (httpServletResponse != null && Boolean.valueOf(httpServletResponse.getHeader("simpleMode"))) {
				String text = toJSONString(obj, simpleModeFeatures);
				byte[] bytes = text.getBytes(getCharset());
				out.write(bytes);
				return;
			}
		}
		String text;
		if (jacksonSupport) {
			if (obj != null && obj.getClass().getName().startsWith("com.fasterxml.jackson.databind")) {
				//jackson格式
				text = obj.toString();
			} else if (obj != null && obj.getClass().equals(String.class)) {
				//原本就是json字符串
				text = String.valueOf(obj);
			}else{
				text = JSON.toJSONString(obj, getFeatures());
			}
		} else {
			text = JSON.toJSONString(obj, getFeatures());
		}
		byte[] bytes = text.getBytes(getCharset());
		out.write(bytes);
	}
}
