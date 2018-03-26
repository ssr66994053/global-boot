/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-01-21 14:37 创建
 *
 */
package com.global.boot.velocity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yjf.common.spring.ApplicationContextHolder;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Escape HTML是治疗xss风险的最好方式。 此类用于对特定的velocity reference做Escape处理。
 * @author qzhanbo@yiji.com
 */
public class HtmlEscapeReference implements ReferenceInsertionEventHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(HtmlEscapeReference.class);
	
	private List<String> skipEscapeRef = Lists.newArrayList();
	private boolean defaultHtmlEscape = true;
	
	/**
	 * 快速ref匹配cache， 如果ref匹配value=false，代表需要escape ref匹配value=true，表示可以跳过匹配
	 * <p>
	 * ref的个数有限，不会存在给gc造成压力。
	 */
	private static ConcurrentReferenceHashMap<String, Boolean> fastMatchedSkipedCache = new ConcurrentReferenceHashMap<>(
		50, ConcurrentReferenceHashMap.ReferenceType.WEAK);
	
	public HtmlEscapeReference() {
		YijiVelocityProperties yijiVelocityProperties = ApplicationContextHolder.get().getBean(
			YijiVelocityProperties.class);
		defaultHtmlEscape = yijiVelocityProperties.isHtmlEscapeEnable();
		if (defaultHtmlEscape) {
			skipEscapeRef.addAll(getDefaultSkipedEscapeRef());
			String velocityRefSkipEscape = yijiVelocityProperties.getRefSkipEscape();
			if (StringUtils.hasText(velocityRefSkipEscape)) {
				for (String ref : Splitter.on(',').trimResults().omitEmptyStrings().split(velocityRefSkipEscape)) {
					skipEscapeRef.add(ref);
				}
			}
			logger.info("velocity html escape启用：skipEscapeRef={}", skipEscapeRef);
		} else {
			logger.info("velocity html escape没有启用");
		}
	}
	
	@Override
	public Object referenceInsert(String reference, Object value) {
		if (defaultHtmlEscape) {
			if (value == null) {
				return null;
			}
			if (Strings.isNullOrEmpty(reference)) {
				return value;
			}
			//1.检查cache中是否有匹配结果
			Boolean cached = fastMatchedSkipedCache.get(reference);
			if (cached == null) {
				//1.1 没有匹配结果，需做条件匹配
				String refName = getRefName(reference);
				for (String skip : skipEscapeRef) {
					if (refName.startsWith(skip)) {
						//1.2 此ref可以不esacpe
						fastMatchedSkipedCache.put(reference, true);
						return value;
					}
				}
				//1.3 此ref需要escape
				fastMatchedSkipedCache.put(reference, false);
				//logger.debug("vm渲染时html escape ref:{}", reference);
				return HtmlUtils.htmlEscape(value.toString());
			} else if (cached) {
				//2. 不需要escape，忽略
				return value;
			} else {
				//3. 需要escape，do it
				//logger.debug("vm渲染时html escape ref:{}", reference);
				return HtmlUtils.htmlEscape(value.toString());
			}
			
		} else {
			return value;
		}
	}
	
	private List<String> getDefaultSkipedEscapeRef() {
		return Lists.newArrayList("screen_content", "crsfToken", "esc", "dateUtil", "dateTool",
			"springMacroRequestContext");
	}
	
	/**
	 *
	 * 获取ref name，ref只有下面三种形式： <li>$dateUtil <li>$!dateUtil <li>$!{dateUtil}
	 *
	 */
	@VisibleForTesting
	public String getRefName(String ref) {
		char[] chars = ref.toCharArray();
		int idx;
		if (chars[0] == '$') {
			if (chars[1] == '!') {
				if (chars[2] == '{') {
					idx = 3;
				} else {
					idx = 2;
				}
			} else if (chars[1] == '{') {
				idx = 2;
			} else {
				idx = 1;
			}
			
		} else {
			idx = 0;
		}
		return new String(chars, idx, chars.length - idx);
	}
	
}
