## yiji-boot 组件开发指南

`yiji-boot`是基于`spring-boot`的，她在`spring-boot`的基础上按照公司规范定制而成。`yiji-boot`组件也是一个纯`spring-boot`的组件，可以按照`spring-boot`官方文档([ Creating your own auto-configuration](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/boot-features-developing-auto-configuration.html))来开发组件。

### 1. 原理

#### 1.1 组件加载原理

`spring-boot`应用在启动时，会通过`EnableAutoConfigurationImportSelector#selectImports`来加载类路径下所有的`META-INF/spring.factories`文件中定义的`EnableAutoConfiguration`.找到这些`Configuration`类后，会根据条件判断是否启用此类，最后根据条件把bean加载到spring容器中。

#### 1.2  例子
这里以`yiji-boot-starter-jdbc`为例：

* 加载`META-INF/spring.factories`，读取到`EnableAutoConfiguration=com.yiji.boot.jdbc.JDBCAutoConfiguration`
* 条件判断`JDBCAutoConfiguration`是否需要启用，这里通过配置参数`yiji.ds.enable`来判断是否需要启用
* 执行`JDBCAutoConfiguration#dataSource`方法获取`DataSource` bean实例注册到spring容器中。

### 2. 组件开发

#### 2.1. 开发流程：

这里以组件名为`demo`的组件为例：

* 组件开发者从[gitlab](http://gitlab.yiji/qzhanbo/yiji-boot) fork yiji-boot项目
	
* 创建组件module，命名为`yiji-boot-starter-demo`，路径为`yiji-boot/yiji-boot-starters/yiji-boot-starter-demo`
	
* 组件包名为`com.yiji.boot.demo`，配置类名称为`DemoAutoConfiguration`
	
* 配置`META-INF/spring.factories`，加入下列配置

		org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.yiji.boot.demo.DemoAutoConfiguration
	
* 更新`yiji-boot-starter-parent`,添加组件依赖到`dependencyManagement`
* 编写组件`ConfigurationProperties`,`prefix`为`yiji.demo`。
* 组件都应该提供配置开关。(开发者依赖了此组件jar，但是还是可以通过配置来关闭此组件)命名规则为`yiji.demo.enable`。
* 编写组件代码
* 资源型组件需要添加:关键配置动态生效,metric ,healthy check ,dtrace埋点,参考tomcat组件
* 编写组件测试代码，测试代码在`yiji-boot-test`module中，按包路径区分不同的组件测试,demo组件包为`com.yiji.boot.test.demo`
* 编写`README.md`文件，包括组件基本介绍和组件配置介绍。
* 测试用例通过后，pull request it

#### 2.2 开发要求

1. 按照开发流程编写组件
2. 组件配置参考`DruidProperties`,应该包括必填参数检查，默认参数设置。组件配置属性必须添加详细的注释，便于开发者能获得IDE提示信息。


### 3. 组件开发FAQ

#### 3.1 如何配置`filter`、`servlet`

参考：

WatcherAutoConfiguration#watcherServlet(通过`ServletRegistrationBean`来定义web组件注册信息)

CsrfAutoConfiguration#csrfFilter(此处不需要添加映射信息，直接返回一个filter对象)

#### 3.2 如何配置组件日志

某些组件可能会添加独立的日志文件，或者对某些日志进行简单的调整。

参考：com.yiji.boot.jdbc.DruidLogInitializer，启用此类在`META-INF/spring.factories`配置为：

	com.yiji.boot.core.log.initializer.LogInitializer=\
	com.yiji.boot.jdbc.DruidLogInitializer
	
#### 3.3 web组件

编写一个web组件，此组件包含静态资源、模板文件和controller，怎么办？

注册Controller和普通bean没有任何区别：参考 `ErrorMvcAutoConfiguration#basicErrorController`

静态资源放在`src/main/resources/static`目录，`vm`模板放在`src/main/resources/templates`目录。

为了便于隔离，建议web组件定义独立的顶级路径。比如名字为`xxx`的web组件，`controller` RequestMapping为/xxx,静态资源放在`src/main/resources/static/xxx`目录，模板文件放在`src/main/resources/templates/xxx`目录。

#### 3.4 如何获取`Environment`、`ApplicationContext`、`ClassLoader`

可以在AutoConfiguration类中，实现`EnvironmentAware`、`ApplicationContextAware`、`BeanClassLoaderAware`接口，容器会注入这些类。

#### 3.5 如何配置`ServletContext`启动参数

	/**
	* add servlet content init parameter
	*/
	@Bean
	public ServletContextInitializer addServletContextInitializer() {
    	return servletContext -> {
        	servletContext.setInitParameter("xxx","xxx");
    	};
	}
	
#### 3.6 组件提供的bean请设置前缀

比如security组件创建`SecurityManager`，boss组件也提供了`SecurityManager`,如果beanName相同，就会冲突，请对创建的bean设置合适的前缀。

比如在Postman组件中，需要创建线程池，beanName=postmanThreadPool `com.yiji.boot.postman.PostmanAutoConfiguration#postmanThreadPool()`

#### 3.7 组件如何提供健康检查

参考yiji-boot-starter-yedis组件提供的yedis健康检查:

1. 添加依赖`yiji-boot-starter-actuator`
2. 创建`YedisHealthIndicator`
3. 注册bean `com.yiji.boot.yedis.YedisAutoConfiguration.yedisHealthIndicator`


#### 3.8 如何添加资源依赖启动时检查

我们应用和组件有很多非标准化的依赖，比如依赖一个外部特殊的服务，比如依赖jdk提供某方面的能力。我们需要在系统启动阶段就能检查这样的能力，如果系统不提供这样的能力，应该提前发现问题，让系统启动报错。

下面以检查应用声明的http端口是否被占用为例：

1. 新增检查类`com.yiji.xxx.PortDenpendencyChecker`

		public class PortDenpendencyChecker implements DependencyChecker {
			@Override
			public void check(Environment environment) {
				if (isPortUsing(Apps.getHttpPort())) {
					throw new AppConfigException("port:" + Apps.getHttpPort() + " is using.");
				}
			}
		}

2. 启用此检查

在`META-INF/spring.factories`文件中添加如下代码：

		com.yjf.common.dependency.DependencyChecker=\
		com.yiji.xxx.PortDenpendencyChecker

如果端口被占用，在启动时就会报错。

