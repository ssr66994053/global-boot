/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-09-22 10:50 创建
 */
package com.global.boot.appservice.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.global.boot.appservice.AppService;
import com.global.boot.filterchain.Filter;
import com.global.boot.filterchain.FilterChain;
import com.yjf.common.lang.exception.AppException;
import com.yjf.common.lang.exception.Exceptions;
import com.yjf.common.lang.validator.YJFValidatorFactory;
import com.yjf.common.service.OrderBase;
import com.yjf.common.service.OrderCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *
 * <li>1. 支持原orderBase校验逻辑</li>
 * <li>2. 支持jsr303分组校验</li>
 * <li>3. 支持请求对象和请求对象字段(需标注@javax.validation.Valid)类执行checkOnXxx()方法，比如ValidationGroup.value
 * =Update.class,执行校验方法为checkOnUpdate(OrderCheckException oce)</li>
 *
 * @author qiubo@yiji.com
 */
public class ParameterCheckFilter implements Filter<AppServiceContext> {
	private static final Logger logger = LoggerFactory.getLogger(ParameterCheckFilter.class);
	
	private Map<Method, CheckerHandler> handlerMap = Maps.newConcurrentMap();
	
	@Override
	public void doFilter(AppServiceContext context, FilterChain<AppServiceContext> filterChain) {
		//1.没有入参
		if (context.getMethodInvocation().getArguments().length == 0) {
			filterChain.doFilter(context);
			return;
		}
		//2. 请求参数不能为空
		for (int i = 0; i < context.getMethodInvocation().getArguments().length; i++) {
			if (context.getMethodInvocation().getArguments()[i] == null) {
				OrderCheckException exception = new OrderCheckException();
				exception.addError("order", "调用异常,第" + i + "个参数为null");
				throw exception;
			}
		}
		//3.ValidationGroup为空,走原来调用逻辑
		AppService.ValidationGroup validationGroup = context.getMethodInvocation().getMethod()
			.getAnnotation(AppService.ValidationGroup.class);
		if (validationGroup == null) {
			for (Object o : context.getMethodInvocation().getArguments()) {
				if (o instanceof OrderBase) {
					((OrderBase) o).check();
				}
			}
			filterChain.doFilter(context);
			return;
		}
		
		//4.ValidationGroup不为空，不会调用check()方法
		OrderCheckException exception = new OrderCheckException();
		for (Object request : context.getMethodInvocation().getArguments()) {
			checkJsr303WithGroup(validationGroup, exception, request);
			
			CheckerHandler checkerHandler = handlerMap.get(context.getMethodInvocation().getMethod());
			if (checkerHandler == null) {
				checkerHandler = buildCheckHandler(context, validationGroup, request);
			}
			
			if (checkerHandler != null) {
				checkerHandler.invokeCheckMethod(request, exception);
			}
		}
		
		if (!exception.getErrorMap().isEmpty()) {
			throw exception;
		}
		filterChain.doFilter(context);
	}
	
	private void checkJsr303WithGroup(AppService.ValidationGroup validationGroup, OrderCheckException exception,
										Object request) {
		Set<ConstraintViolation<Object>> constraintViolations;
		Class<?>[] groups;
		if (validationGroup.checkDefaultGroup()) {
			if (validationGroup.value() != AppService.ValidationGroup.Null.class) {
				groups = new Class[] { Default.class, validationGroup.value() };
			} else {
				groups = new Class[] { Default.class };
			}
		} else {
			if (validationGroup.value() != AppService.ValidationGroup.Null.class) {
				groups = new Class[] { validationGroup.value() };
			} else {
				groups = null;
			}
		}
		if (groups != null) {
			constraintViolations = YJFValidatorFactory.INSTANCE.getValidator().validate(request, groups);
			if (constraintViolations != null && !constraintViolations.isEmpty()) {
				for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
					exception.addError(constraintViolation.getPropertyPath().toString(),
						constraintViolation.getMessage());
				}
			}
		}
	}
	
	private CheckerHandler buildCheckHandler(AppServiceContext context, AppService.ValidationGroup validationGroups,
												Object request) {
		if (request == null) {
			return null;
		}
		CheckerHandlers checkerHandlers = new CheckerHandlers();
		
		findRequestObjectChecker(validationGroups, request, checkerHandlers);
		try {
			findRequestFieldChecker(validationGroups, request, checkerHandlers);
		} catch (AppException e) {
			return null;
		}
		
		handlerMap.put(context.getMethodInvocation().getMethod(), checkerHandlers);
		return checkerHandlers;
	}
	
	private void findRequestFieldChecker(AppService.ValidationGroup validationGroups, Object request,
											CheckerHandlers checkerHandlers) {
		
		Method method;
		for (Field field : request.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(Valid.class)) {
				continue;
			}
			Type type = field.getGenericType();
			field.setAccessible(true);
			//泛型参数
			if (type instanceof TypeVariable) {
				try {
					Object o = field.get(request);
					if (o == null) {
						//当范型字段值为null时，不能发现范型类型，需要等待字段值不为空时再执行
						throw Exceptions.newRuntimeExceptionWithoutStackTrace("当范型字段值为null时，不能发现范型类型，需要等待字段值不为空时再执行");
					} else {
						method = findCheckerMethod(validationGroups, o.getClass());
						if (method != null) {
							checkerHandlers.addCheckerHandler(new FieldCheckerHandler(field, method));
						}
					}
					//do nothing
				} catch (Exception e) {
					logger.error("", e);
				}
			} else if (type instanceof Class) {
				method = findCheckerMethod(validationGroups, ((Class<?>) type));
				if (method != null) {
					checkerHandlers.addCheckerHandler(new FieldCheckerHandler(field, method));
				}
			}
			
		}
	}
	
	private void findRequestObjectChecker(AppService.ValidationGroup validationGroups, Object request,
											CheckerHandlers checkerHandlers) {
		Method method = findCheckerMethod(validationGroups, request.getClass());
		if (method != null) {
			checkerHandlers.addCheckerHandler(new RequestCheckerHandler(method));
		}
	}
	
	private Method findCheckerMethod(AppService.ValidationGroup validationGroups, Class<?> clazz) {
		try {
			return clazz.getDeclaredMethod("checkOn" + validationGroups.value().getSimpleName(),
				OrderCheckException.class);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	public interface CheckerHandler {
		void invokeCheckMethod(Object request, OrderCheckException e);
	}
	
	public class RequestCheckerHandler implements CheckerHandler {
		private Method validateMethod;
		
		public RequestCheckerHandler(Method validateMethod) {
			this.validateMethod = validateMethod;
		}
		
		@Override
		public void invokeCheckMethod(Object request, OrderCheckException e) {
			try {
				validateMethod.invoke(request, e);
			} catch (Exception e1) {
				logger.error("", e);
			}
		}
	}
	
	public class FieldCheckerHandler implements CheckerHandler {
		private Field requestField;
		private Method validateMethod;
		
		public FieldCheckerHandler(Field requestField, Method validateMethod) {
			this.requestField = requestField;
			this.validateMethod = validateMethod;
		}
		
		public void invokeCheckMethod(Object request, OrderCheckException e) {
			try {
				validateMethod.invoke(requestField.get(request), e);
			} catch (Exception e1) {
				logger.error("", e);
			}
			
		}
	}
	
	public class CheckerHandlers implements CheckerHandler {
		
		private List<CheckerHandler> handlers = null;
		
		public void addCheckerHandler(CheckerHandler checkerHandler) {
			if (handlers == null) {
				handlers = Lists.newArrayList();
			}
			handlers.add(checkerHandler);
		}
		
		@Override
		public void invokeCheckMethod(Object request, OrderCheckException e) {
			if (handlers != null) {
				for (CheckerHandler handler : handlers) {
					handler.invokeCheckMethod(request, e);
				}
			}
		}
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 2;
	}
}
