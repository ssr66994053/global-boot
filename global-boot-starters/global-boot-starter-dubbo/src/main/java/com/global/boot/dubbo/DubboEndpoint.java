/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-04-23 18:21 创建
 */
package com.yiji.boot.dubbo;

import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qiubo@yiji.com
 */
public class DubboEndpoint extends AbstractEndpoint<Map<String, Set<DubboEndpoint.DubboService>>> {
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private DubboProperties dubboProperties;
	@Autowired
	private AnnotationBean annotationBean;
	
	public DubboEndpoint() {
		super("dubbo");
	}
	
	@Override
	public Map<String, Set<DubboEndpoint.DubboService>> invoke() {
		Map<String, Set<DubboEndpoint.DubboService>> result = Maps.newHashMap();
		if (dubboProperties.getProvider().isEnable()) {
			Set<DubboService> dubboServices = Sets.newHashSet();
			Map<String, ServiceBean> beansOfType = applicationContext.getBeansOfType(ServiceBean.class);
			List<ServiceBean> serviceBeans = Lists.newArrayList(beansOfType.values());
			serviceBeans.addAll(annotationBean.getServiceConfigs());
			for (ServiceBean serviceBean : serviceBeans) {
				DubboService dubboService = new DubboService();
				dubboService.setInterfaceClass(serviceBean.getInterface());
				dubboService.setVersion(serviceBean.getVersion());
				dubboService.setGroup(serviceBean.getGroup());
				dubboServices.add(dubboService);
			}
			result.put("provider", dubboServices);
		}
		List<ReferenceBean> referenceBeans = Lists.newArrayList(applicationContext.getBeansOfType(ReferenceBean.class)
			.values());
		referenceBeans.addAll(annotationBean.getReferenceConfigs().values());
		Set<DubboService> dubboServices = Sets.newHashSet();
		for (ReferenceBean referenceBean : referenceBeans) {
			DubboService dubboService = new DubboService();
			dubboService.setInterfaceClass(referenceBean.getInterface());
			dubboService.setGroup(referenceBean.getGroup());
			dubboService.setVersion(referenceBean.getVersion());
			dubboServices.add(dubboService);
		}
		result.put("consumer", dubboServices);
		
		return result;
	}
	
	public static class DubboService {
		private String interfaceClass;
		private String version;
		private String group = "";
		
		public String getGroup() {
			return group;
		}
		
		public void setGroup(String group) {
			this.group = group;
		}
		
		public String getInterfaceClass() {
			return interfaceClass;
		}
		
		public void setInterfaceClass(String interfaceClass) {
			this.interfaceClass = interfaceClass;
		}
		
		public String getVersion() {
			return version;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof DubboService))
				return false;
			
			DubboService that = (DubboService) o;
			
			if (interfaceClass != null ? !interfaceClass.equals(that.interfaceClass) : that.interfaceClass != null)
				return false;
			if (version != null ? !version.equals(that.version) : that.version != null)
				return false;
			return group != null ? group.equals(that.group) : that.group == null;
			
		}
		
		@Override
		public int hashCode() {
			int result = interfaceClass != null ? interfaceClass.hashCode() : 0;
			result = 31 * result + (version != null ? version.hashCode() : 0);
			result = 31 * result + (group != null ? group.hashCode() : 0);
			return result;
		}
	}
}
