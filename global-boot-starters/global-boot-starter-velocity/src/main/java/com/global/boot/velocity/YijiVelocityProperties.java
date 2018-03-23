/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-08-25 16:15 创建
 *
 */
package com.yiji.boot.velocity;

import com.google.common.base.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * @author qiubo@yiji.com
 */
@ConfigurationProperties("yiji.velocity")
public class YijiVelocityProperties {
	
	/**
	 * 是否启用velocity
	 */
	private boolean enable = true;
	/**
	 * 可选：启用html escape 默认值
	 */
	public static final boolean HTML_ESCAPE_ENABLE = true;
	/**
	 * 可选：vm模板渲染阶段是否启用Escape html
	 */
	private boolean htmlEscapeEnable = HTML_ESCAPE_ENABLE;
	/**
	 * 可选：公共宏配置文件路径
	 */
	private static final String COMMON_MACRO = "macro/yiji-macros.vm";
	
	/**
	 * 默认toolbox配置
	 */
	private static final String DEFAULT_TOOL_BOX = "classpath:com/yiji/boot/velocity/velocity-toolbox.xml";
	
	/**
	 * 可选：toolbox配置路径,默认使用yiji-boot提供的配置{@link YijiVelocityProperties#DEFAULT_TOOL_BOX}
	 */
	private String toolboxConfigLocation = DEFAULT_TOOL_BOX;
	
	/**
	 * 可选： vm模板渲染阶段需要跳过Escape的ref,匹配时采用前缀匹配策略，比如传入obj，那么ref=obj，ref=obj1，ref=obj. name 都会跳过，多个参数请用用逗号分隔。
	 *
	 */
	private String refSkipEscape;
	
	/**
	 * 可选： velocity 是否启用layout
	 * <p>
	 * 启用layout，使用{@link VelocityLayoutViewResolver}解析，不启用时使用 {@link VelocityViewResolver}解析
	 * </p>
	 */
	private boolean layoutEnable = true;
	
	/**
	 * 可选： 自定义宏路径，多个文件用逗号隔开，比如:macro/custom.vm,文件需放在classpath:/templates/路径下
	 *
	 */
	private String macroLocations;
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public boolean isHtmlEscapeEnable() {
		return htmlEscapeEnable;
	}
	
	public void setHtmlEscapeEnable(boolean htmlEscapeEnable) {
		this.htmlEscapeEnable = htmlEscapeEnable;
	}
	
	public boolean isLayoutEnable() {
		return layoutEnable;
	}
	
	public void setLayoutEnable(boolean layoutEnable) {
		this.layoutEnable = layoutEnable;
	}
	
	public String getRefSkipEscape() {
		return refSkipEscape;
	}
	
	public void setRefSkipEscape(String refSkipEscape) {
		this.refSkipEscape = refSkipEscape;
	}
	
	public String getMacroLocations() {
		return macroLocations;
	}
	
	public void setMacroLocations(String macroLocations) {
		this.macroLocations = macroLocations;
	}
	
	public String buildMacroLocs() {
		if (Strings.isNullOrEmpty(macroLocations)) {
			return COMMON_MACRO;
		} else {
			return macroLocations + "," + COMMON_MACRO;
		}
	}
	
	public String getToolboxConfigLocation() {
		return toolboxConfigLocation;
	}
	
	public void setToolboxConfigLocation(String toolboxConfigLocation) {
		this.toolboxConfigLocation = toolboxConfigLocation;
	}
}