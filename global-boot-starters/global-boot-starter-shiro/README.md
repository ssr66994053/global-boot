## 1. 组件介绍

此组件给应用[Shiro](http://shiro.apache.org)能力，实现对url的权限控制

## 2. 配置

配置参考[com.yiji.boot.shiro.ShiroProperties](src/main/java/com/yiji/boot/shiro/ShiroProperties.java)

## 3. 使用说明
1) 实现自定义的Realm实现，bean设置为shiroRealm，如：
	
	@Component("shiroRealm")
	public class ShiroDBAuthorizingRealm extends AuthorizingRealm {
	}
2) 配置链接，实现对url的权限控制，**注意顺序**

	yiji.shiro.urls[0]./login.html = anon
	yiji.shiro.urls[1]./logout.html = anon
	yiji.shiro.urls[2]./unauthorized.html = anon
	
	yiji.shiro.urls[3]./css/** = anon
	yiji.shiro.urls[4]./js/** = anon
	yiji.shiro.urls[5]./images/** = anon
	yiji.shiro.urls[6]./swf/** = anon
	
	yiji.shiro.urls[7]./** = authc
3) 如需特殊的filter配置，参考如下：

	yiji.shiro.filters.authc=com.yiji.neverstopfront.web.shiro.CaptchaFormAuthenticationFilter
	yiji.shiro.filters.admin=com.yiji.neverstopfront.web.shiro.AdminAuthorizationFilter
	yiji.shiro.filters.houseProperty=com.yiji.neverstopfront.web.shiro.ServiceTypeAuthorizationFilter
	yiji.shiro.filters.houseProperty.serviceType=HOUSE_PROPERTY
	yiji.shiro.filters.installment=com.yiji.neverstopfront.web.shiro.ServiceTypeAuthorizationFilter
	yiji.shiro.filters.installment.serviceType=INSTALLMENT

	

## 4. 参考
- [Apache Shiro](http://shiro.apache.org)
- [Apache Shiro Web Support](http://shiro.apache.org/web.html)
- [Integrating Apache Shiro into Spring-based Applications](http://shiro.apache.org/spring.html)]
