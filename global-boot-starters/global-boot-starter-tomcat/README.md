## 1. 组件介绍

此组件给应用添加嵌入式tomcat

## 2. 配置
必配参数: 
无

配置参考[com.yiji.boot.tomcat.TomcatProperties](src/main/java/com/yiji/boot/tomcat/TomcatProperties.java)

## 3. F.A.Q

### 3.1 关于yiji-boot对jsp的支持

#### 3.1.1. 如何配置

1. 增加依赖：

	     <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
        </dependency>
        
2. 启用配置

		yiji.tomcat.jsp.enable=true

3. 增加controller

		
		@RequestMapping("/jsp")
    	public String welcome1(Map<String, Object> model) {
        	model.put("time", new Date());
        	model.put("message", "xxxx");
        	return "welcome";
    	}
    	
4. 增加jsp页面

	页面需放到`src/main/resources/META-INF/resources/jsp/welcome.jsp`路径。


#### 3.1.2. 注意

参考官方的说法：

>>When running a Spring Boot application that uses an embedded servlet container (and is packaged as an executable archive), there are some limitations in the JSP support.

>>With Tomcat it should work if you use war packaging, i.e. an executable war will work, and will also be deployable to a standard container (not limited to, but including Tomcat). An executable jar will not work because of a hard coded file pattern in Tomcat.
Jetty does not currently work as an embedded container with JSPs.
Undertow does not support JSPs.

我们实现了：

1. 应用打成jar包仍然可以使用jsp
2. 可以同时使用vm和jsp

但是我们不会考虑性能、安全、开发模式支持。


### 3.2 `yiji-boot`对于https/http处理的说明

总体原则:应用不启用https，由nginx来卸载https.



`nginx`中`server`需要配置如下参数:

    proxy_set_header x-forwarded-proto $scheme;
    proxy_set_header x-forwarded-for $remote_addr;
    proxy_set_header Host $host;
	

yiji-boot会使用这些web标准http header信息来完成如需求：


1. `x-forwarded-proto`

	保证应用能获取到正确的`schema`，如果不配置，应用不知道具体的`schema`信息，在处理相对地址时会有问题。
	
2. `x-forwarded-for`

	保证应用能获取到客户端的地址，如果不配置，应用只能获取到`nginx`代理的地址。`yiji-boot`已经调整了tomcat,应用通过`request.getRemoteAddr()`能获取到正确的客户端地址，而且对于tomcat的访问日志也已调整，访问日志中会输出正确的客户端ip
	
3. `Host`

	保证应用能获取到客户端访问的域名，如果不配置，应用会获取到服务器的ip.应用可以通过`request.getServerName()`获取到当前应用正确的域名信息。(so,应用里面不需要配置自己应用的域名信息)
	
### 3.3 重定向时tomcat在url中加上session标识

打开浏览器，首次访问ppm应用，ppm重定向时，重定向地址加上session标识。

tomcat `org.apache.catalina.connector.Response#isEncodeable`的逻辑如下：

1. 响应时需要输出session标识
2. cookie中没有session标识
3. sessionTrackingModes支持url
4. 这个url是我们应用的地址

满足所有条件，符合servlet规范的容器就会`encode`url,在url中加入session标识。

可能有同学有以下疑问：

1. 访问首页干嘛要创建session？

	代码见`com.yjf.ppm.web.interceptor.PPMWebInterceptor`,对于所有请求,创建了`session`。P.S. 代码作者已经离职

2. tomcat为嘛要搞这个逻辑？

	如果浏览器不支持cookie，容器会通过重写url来实现`session track`.容器没有办法判断浏览器是否支持cookie，所以搞了这一套规则。
	
3. 为什么仅仅打开浏览器第一次访问会这样？

	后续的请求在cookie中已经有session标识了，
	

`yiji-boot`已经限制`sessionTrackingModes`仅支持cookie了，非`yiji-boot`应用请自行处理。