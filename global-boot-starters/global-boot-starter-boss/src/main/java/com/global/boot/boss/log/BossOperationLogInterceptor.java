/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved.
 */

/*
 * 修订记录：
 * 麦子（lvchen@yiji.com）  2016-04-25 创建
 */

package com.global.boot.boss.log;

import com.global.boot.boss.BOSSProperties;
import com.global.boot.boss.BossConstants;
import com.global.boot.boss.log.annotations.BossOperation;
import com.global.boot.boss.log.domain.BossOperationLogMessage;
import com.global.boot.boss.log.domain.LogParameter;
import com.google.common.collect.Lists;
import com.global.boot.core.Apps;
import com.global.boot.web.SpringHandlerInterceptor;
import com.yjf.common.concurrent.MonitoredThreadPool;
import com.yjf.common.id.OID;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;

/**
 * @author 麦子（lvchen@yiji.com）
 */
@SpringHandlerInterceptor(excludePatterns = { "/css/**", "/js/**", "/images/**", "/services/**", "/resources/**" },
		includePatterns = { "/boss/**" })
public class BossOperationLogInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(BossOperationLogInterceptor.class);
	
	@Resource(name = "bossLogMQTemplate")
	private RabbitTemplate bossLogMQTemplate;
	
	@Resource(name = "bossLogThreadPool")
	private MonitoredThreadPool bossLogThreadPool;
	
	@Autowired
	private BOSSProperties bossProperties;
	
	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (null != request && null != handler) {
			
			if (handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				
				try {
					BossOperation bossOperationAnno = handlerMethod.getMethod().getAnnotation(BossOperation.class);
					
					String methodName = handlerMethod.getMethod().getName();
					if (ignoreThisMethod(bossOperationAnno, methodName, request)) {
						logger.info("忽略方法{}", methodName);
						return true;
					}
					
					BossOperationLogMessage logMessage = genMessage(request, handlerMethod, bossOperationAnno);
					if (logMessage != null) {
						bossLogThreadPool.execute(() -> {
							bossLogMQTemplate.convertAndSend(logMessage);
							logger.debug("发送数据到日志收集队列成功，{}", logMessage);
						});
					}
				} catch (Exception e) {
					logger.error("发送操作日志数据到队列失败", e);
				}
			}
		}
		return true;
	}
	
	private boolean ignoreThisMethod(BossOperation bossOperationAnno, String methodName, HttpServletRequest request) {
		return bossProperties.getLogGlobalIgnoreMethodList().contains(methodName)
				|| (bossOperationAnno != null && bossOperationAnno.ignore())
				|| ServletFileUpload.isMultipartContent(request);
	}
	
	private BossOperationLogMessage genMessage(HttpServletRequest request, HandlerMethod handlerMethod,
												BossOperation bossOperationAnno) {
		BossOperationLogMessage message = null;
		HttpSession session = request.getSession();
		if (null == session) {
			logger.warn("没有得到session，组装操作日志失败");
			return null;
		}
		Assertion assertion = (Assertion) session.getAttribute(BossConstants.CONST_CAS_ASSERTION);
		if (null == assertion) {
			logger.warn("没有得到单点登录的assertion，组装操作日志失败");
			return null;
		}
		Map<String, Object> userInfo = assertion.getPrincipal().getAttributes();
		if (null == userInfo) {
			logger.warn("没有得到单点登录缓存的用户信息，组装操作日志失败");
			return null;
		}
		
		message = new BossOperationLogMessage();
		
		message.setLogMessageId(OID.newID("BS"));
		message.setSystemName(Apps.getAppName());
		message.setOperationTime(new Date());
		message.setOperationIp(request.getRemoteAddr());
		message.setRequestUri(request.getRequestURI());
		message.setMethodName(handlerMethod.getMethod().getName());
		
		message.setDescription(bossOperationAnno == null ? null : bossOperationAnno.description());
		message.setLogParameterList(retrieveLogParameters(request, bossOperationAnno));
		
		message.setUserId(getUserInfoByKey(userInfo, BossConstants.CACHED_USER_ID_KEY));
		message.setRealName(getUserInfoByKey(userInfo, BossConstants.CACHED_USER_REAL_NAME_KEY));
		message.setUserName(getUserInfoByKey(userInfo, BossConstants.CACHED_USER_NAME_KEY));
		
		message.check();
		
		return message;
	}
	
	private LogParameter[] retrieveLogParameters(HttpServletRequest request, BossOperation bossOperationAnno) {
		LogParameter[] parameters = null;
		
		if (bossOperationAnno == null || !bossOperationAnno.ignoreParams()) {
			parameters = convertRequestParameters(request, getIgnoreParamPatterns(bossOperationAnno),
				getMaskParamPatterns(bossOperationAnno));
		}
		
		return parameters;
	}
	
	private List<String> getMaskParamPatterns(BossOperation bossOperationAnno) {
		//全局掩码参数
		List<String> maskParamList = Lists.newArrayList(bossProperties.getLogGlobalMaskParamNamePatternList());
		
		if (bossOperationAnno != null) {
			//注解定义的掩码参数
			String[] ignoreParameterArray = bossOperationAnno.maskParameterList();
			
			if (ignoreParameterArray != null && ignoreParameterArray.length > 0) {
				maskParamList.addAll(Lists.newArrayList(ignoreParameterArray));
			}
		}
		return maskParamList;
	}
	
	private List<String> getIgnoreParamPatterns(BossOperation bossOperationAnno) {
		//全局忽略参数
		List<String> ignoreParamList = Lists.newArrayList(bossProperties.getLogGlobalIgnoreParamNamePatternList());
		
		//注解定义忽略参数
		if (bossOperationAnno != null) {
			String[] ignoreParameterArray = bossOperationAnno.ignoreParameterList();
			
			if (ignoreParameterArray != null && ignoreParameterArray.length > 0) {
				ignoreParamList.addAll(Lists.newArrayList(ignoreParameterArray));
			}
		}
		
		return ignoreParamList;
	}
	
	private String getUserInfoByKey(Map<String, Object> userInfo, String key) {
		return userInfo.get(key) != null ? (String) userInfo.get(key) : null;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
							ModelAndView modelAndView) throws Exception {
		return;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
																														throws Exception {
		return;
	}
	
	private LogParameter[] convertRequestParameters(HttpServletRequest request,
													List<String> ignoreParameterPatternList,
													List<String> maskParamPatterns) {
		
		Enumeration<String> names = request.getParameterNames();
		
		List<LogParameter> paramList = new ArrayList<LogParameter>();
		
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (!ignoreParams(ignoreParameterPatternList, name)) {
				paramList.add(new LogParameter(name, maskParam(name, request.getParameter(name), maskParamPatterns)));
			}
		}
		
		return paramList.toArray(new LogParameter[paramList.size()]);
		
	}
	
	private String maskParam(String paramName, String paramValue, List<String> maskParamPatterns) {
		if (StringUtils.isBlank(paramName)) {
			return paramValue;
		}
		for (String maskPattern : maskParamPatterns) {
			if (StringUtils.isBlank(maskPattern)) {
				continue;
			}
			if (new AntPathMatcher().match(maskPattern.toLowerCase(), paramName.toLowerCase())) {
				logger.info("根据配置{},给参数{}掩码", maskPattern, paramName);
				return com.yjf.common.util.StringUtils.mask(paramValue);
			}
		}
		return paramValue;
	}
	
	private boolean ignoreParams(List<String> ignoreParameterPatternList, String paramName) {
		if (StringUtils.isBlank(paramName)) {
			return false;
		}
		for (String ignorePattern : ignoreParameterPatternList) {
			if (StringUtils.isBlank(ignorePattern)) {
				continue;
			}
			if (new AntPathMatcher().match(ignorePattern.toLowerCase(), paramName.toLowerCase())) {
				logger.info("根据配置{},忽略参数{}", ignorePattern, paramName);
				return true;
			}
		}
		return false;
	}
	
}
