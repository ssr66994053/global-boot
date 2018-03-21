## 1. 组件介绍

此组件提供`Production-ready features`。

## 2. 特性说明

### 2.1 应用内部数据暴露

`yiji-boot`应用会通过`/mgt/*`路径暴露内部应用状态.`/mgt*`路径已经做了权限控制,只能内网访问.

**请应用在做权限时排除`/mgt/*`路径.**

### 2.2 健康检查

如何判断应用是否健康呢?如果应用所需要的\依赖的资源都ok,我们认为应用是健康的.(此时还有问题,这算是bug了,bug只有靠测试同学来hold住).

所以应用是否健康是由一个一个资源型组件和系统资源决定的.

健康检查特性会定时调用所有健康检查组件,如果发现健康检查失败,会调用监控报警系统通知应用负责人及其主管.

#### 2.2.1 如何开发健康检查组件

1. 组件开发参考:[com.yiji.boot.actuator.health.check.SystemLoadHealthIndicator](src/main/java/com/yiji/boot/actuator/healthcheck/SystemLoadHealthIndicator.java)
2. 组件注册:

        @Bean
    	public SystemLoadHealthIndicator systemLoadHealthIndicator() {
    		return new SystemLoadHealthIndicator();
    	}

    

### 2.3 应用Metrics指标

#### 2.3.1 如何自定义Metrics指标

##### 2.3.1.1 使用CounterService或GaugeService

spring-boot已经预先定义了一个`org.springframework.boot.actuate.metrics.CounterService`和一个
`org.springframework.boot.actuate.metrics.GaugeService`类型的Bean, 开发人员可以直接在自己的Bean中引用这两个Bean来收集metrics, 例如:

    @Component
    public class ConcurrentVisitCounterService  {
        @Autowired
        private CounterService counterService;
        @Autowired
        private GaugeService gaugeService;
        private Random random = new Random();
    
        public void visit() {
            counterService.increment("visitCount");
            gaugeService.submit("visitGauge", random.nextDouble());
        }
    }
   
##### 2.3.1.2 实现PublicMetrics

开发人员也可以实现`org.springframework.boot.actuate.endpoint.PublicMetrics`, 并将实现类注册为一个spring管理的Bean.指标开发参考
[com.yiji.boot.tomcat.TomcatMetrics](../yiji-boot-starter-tomcat/src/main/java/com/yiji/boot/tomcat/TomcatMetrics.java)

指标注册示例:

        @Bean
        //依赖的Bean, 可选
        @DependsOn("yijiEmbeddedServletContainerCustomizer")
        public TomcatMetrics tomcatMetrics() {
    	    return new TomcatMetrics(abstractProtocol);
        }
    
#### 2.3.2 自定义Metrics指标暴露与收集

通过上面两种方式自定义的Metrics指标会自动暴露到应用的"/mgt/metrics"页面上, 并会定期发往`opentsdb`(15s采样一次), 并通过图表展示出来. 


## 2. FAQ

### 2.1. 为什么`/mgt/info`中`build.revision=unkown`?

`build.revision`中存储`scm revision`信息.
    
* 使用git的项目,取`revision`前五位，如：`"revision":"20dbc"`
* 使用svn项目,如：`"revision":"86989"`

当程序在IDE中运行时，`build.revision`始终为`unkown`。
	
考虑大多数项目使用`svn`，`yiji-parent`默认配置只能获取`svn`信息。使用`git`的项目，请在`Main`类所在的module `pom.xml`中加入如下代码：
	
		<!--for generate git revision Implementation-Revision and save in /META-INFO/MANIFEST.MF -->
	    <scm>
	        <connection>scm:git:https://none</connection>
	        <url>scm:git:https://none</url>
	        <developerConnection>scm:git:https://none</developerConnection>
	    </scm>

