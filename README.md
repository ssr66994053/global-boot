yiji-boot项目是基于spring-boot定制、优化后的版本。


### 新人入门基础文档
* [必看] 配置管理系统 [link](http://wiki.yiji.dev/pages/viewpage.action?pageId=3310540)
* [必看] Spring Java-based容器配置 [link](docs/java_config.md)
* [必看] Spring Boot Reference Guide中文翻译 [online](http://qbgbook.gitbooks.io/spring-boot-reference-guide-zh/content/) [local](docs/spring-boot-reference-guide-zh.pdf)
* [必看] yiji-boot 新手笔记 [link](http://gitlab.yiji/qzhanbo/photowall/tree/master)
* [选看] spring-boot example [link](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples)
* [选看] 组件开发指南 [link](yiji-boot-starters/README.md)

----

### 目前提供的组件
* [rabbitmq](yiji-boot-starters/yiji-boot-starter-amqp/README.md)     异步消息
* [cs](yiji-boot-starters/yiji-boot-starter-cs/README.md)             邮件，外部通知
* [dubbo](yiji-boot-starters/yiji-boot-starter-dubbo/README.md)       Java RPC
* [fastdfs](yiji-boot-starters/yiji-boot-starter-fastdfs/README.md)   分布式存储
* [jdbc](yiji-boot-starters/yiji-boot-starter-jdbc/README.md)         druid数据源
* [mybatis](yiji-boot-starters/yiji-boot-starter-mybatis/README.md)   数据库持久化层
* [jpa](yiji-boot-starters/spring-boot-starters/spring-boot-jpa.md)   数据库持久化层
* [mongodb](yiji-boot-starters/spring-boot-starters/spring-boot-mongo.md) mongo持久化层
* [postman](yiji-boot-starters/yiji-boot-starter-postman/README.md)   发送短信
* [security](yiji-boot-starters/yiji-boot-starter-security/README.md) 加密解密相关
* [session](yiji-boot-starters/yiji-boot-starter-session/README.md)   分布式session
* [velocity](yiji-boot-starters/yiji-boot-starter-velocity/README.md) java模板引擎
* [yedis](yiji-boot-starters/yiji-boot-starter-yedis/README.md)       redis缓存
* [web](yiji-boot-starters/yiji-boot-starter-web/README.md)           webMVC组件
* [boss](yiji-boot-starters/yiji-boot-starter-boss/README.md)         boss系统接入
* [metrics](yiji-boot-starters/yiji-boot-starter-metrics/README.md)   app度量统计
* [xss](yiji-boot-starters/yiji-boot-starter-xss/README.md)           xss过滤
* [csrf](yiji-boot-starters/yiji-boot-starter-csrf/README.md)         csrf攻击防范
* [filterchain](yiji-boot-starters/yiji-boot-starter-filterchain/README.md)    通用过滤器
* [offlinelocks](yiji-boot-starters/yiji-boot-starter-offlinelocks/README.md)    离线锁
* [rocketmq](yiji-boot-starters/yiji-boot-starter-rocketmq/README.md)    rocketmq
* [appservice](yiji-boot-starters/yiji-boot-starter-appservice/README.md)    appservice(提供服务通用相关处理能力)
* [cas](yiji-boot-starters/yiji-boot-starter-cas/README.md)    cas






注意：

* 除JDBC外需要加入数据库相关的驱动包之外，其余组件均可以单独使用，不需要添加额外的依赖。
* yiji-boot和spring-boot是兼容的，可以直接使用spring-boot的组件
* 如果提供的组件不能满足你的需求，请联系基础技术部。

----

### 目录介绍

* script: 脚本目录
* doc: 相关文档
* yiji-boot-archtype: yiji-boot应用archtype
* yiji-boot-core: 核心代码模块
* yiji-boot-starters: 组件代码模块
* yiji-boot-test: 测试代码模块

----

# FAQ

## 1. 如何修改日志配置

`yiji-boot`已经定义了基础日志，您可以自定义日志。

### 1.1 自定义日志文件

请在classpath下添加`logback-spring.xml`文件,在文件中加入logback日志配置。比如增加日志输出文件
	
	 <appender class="ch.qos.logback.core.rolling.RollingFileAppender" name="xxx">
        <file>${yiji.log.path}/xxx.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${yiji.log.path}/xxx.log.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>1024MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder>
            <pattern>${yiji.log.pattern.str}</pattern>
        </encoder>
    </appender>
    <appender class="com.yjf.common.log.LogbackAsyncAppender" name="async-xxx">
        <appender-ref ref="xxx"/>
    </appender>
    <logger name="com.yiji">
        <appender-ref ref="async-xxx"/>
    </logger>
	
`yiji.log.path`为日志输出路径，`yiji.log.pattern.str`日志输出pattern，这个变量由`yiji-boot`设置

### 1.2 自定义日志级别

	#设置ROOT日志级别为debug
	yiji.log.level.root=debug
	#设置com.yiji日志级别为warn
	yiji.log.level.com.yiji=warn

### 1.3 修改日志pattern

`yiji-boot`定义了三种日志pattern,`com.yiji.boot.core.configuration.LogAutoConfiguration.LogProperties.Pattern`,如下的配置使用MDC中有gid参数的pattern。

	yiji.log.pattern=MDC_WITH_GID

默认的日志pattern为`com.yiji.boot.core.configuration.LogAutoConfiguration.LogProperties.Pattern#COMMON`

**注意**:

日志pattern修改必须在配置文件中.因为日志是系统启动第一步做的事情,包括hera启动也会依赖日志.如要要让日志配置可以在hera中使用,那么需要在hera启动时把hera自己启动中的日志缓存住,等hera启动完后,再初始化日志系统,把缓存的日志打印出来.这样做必要性不是很大.


### 1.4 如何通过编程的方式增加日志配置

参考`META-INF/spring.factories`配置的`LogInitializer`

### 1.5 如何增加业务监控

您需要使用com.yjf.common.log.BusinessLogger记录下业务日志就ok了。我们会提供自助式报表展示平台。
    
使用：
    
    BusinessLogger.BusinessDO bo = new BusinessLogger.BusinessDO();
    bo.setLogType("xxxx");
    bo.addContent("xx", 2).addContent("key", "value");
    BusinessLogger.log(bo);

或者：
    
    BusinessLogger.log("xx", "key", "value", "k", "v");

## 2. 如何获取配置

    @Autowired
    private Environment env;
    
    String value= env.getProperty("key");

## 3. 配置读取顺序(PropertySource order)

配置读取顺序如下,从一个PropertySource读到后不会继续往下读:


    Command line arguments
    JNDI attributes from java:comp/env
    Java System properties (System.getProperties())
    OS environment variables
    RandomValuePropertySource that only has properties in random.*.
    HeraPropertySource [name='hera']
    application-{profile}.properties(yml) – outside jar
        - inside jar application.properties(yml) - outside jar
        - inside jar
    @PropertySource annotations on your @Configuration classes
    Default properties (specified using SpringApplication.setDefaultProperties)
    
## 4. 如何自定义hera地址

默认yiji-boot会根据大家使用的环境来设置hera地址.

* profile=online时,使用zookeeper.yiji.me:2181.
* profile!=online时,使用snet.yiji:2181.

可以通过下面的方式来配置hera地址:

* 在java启动参数中通过`-Dyiji.hera.zk.url`设置
* 在操作系统中添加环境变量`YIJI_HERA_ZK_URL`设置

## 5. 如何开启开发者模式

开发者模式主要提供的能力如下:

1. 设置默认配置(关闭模板引擎缓存)
2. 自动重启(建议还是使用`spring-loaded`)

更多参考[DevTools in Spring Boot](https://spring.io/blog/2015/06/17/devtools-in-spring-boot-1-3)

### 5.1 关闭模板引擎缓存(修改模板文件后不用重启)

在主类所在的`module`加入如下依赖:
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
    
使用`archtype`生成的项目已配置.在项目打包时,此依赖会被排除掉,不会影响线上.

### 5.2 启用spring-loaded(修改类后不用重启)

在`run configration`里启用spring-loaded,在`VM options`中加入

    -noverify  -javaagent:/Users/bohr/.m2/repository/org/springframework/springloaded/1.2.4.RELEASE/springloaded-1.2.4.RELEASE.jar
    
上面的jar包路径根据您的情况修改

### 5.3 如何在编译打包阶段过滤某些资源

以收银台为例,收银台需要在编译打包时,按照当前时间生成版本号.
    
`cashier-assemble/src/main/resources/templates/layout/version.vm`中配置如下:

    <span id="code_version" style="display:none;">v@webapp.build.version@</span>
    
在`cashier-assemble/pom.xml`中启用资源过滤`version.vm`

     <resources>
                 <resource>
                     <directory>${basedir}/src/main/resources/</directory>
                     <filtering>true</filtering>
                     <includes>
                         <include>templates/layout/version.vm</include>
                         <include>**/application.properties</include>
                     </includes>
                 </resource>
                 <resource>
                     <directory>${basedir}/src/main/resources/</directory>
                 </resource>
     </resources>
     
定义参数:

    <properties>
            <webapp.build.version>${maven.build.timestamp}</webapp.build.version>
            <maven.build.timestamp.format>yyyyMMddHHmm</maven.build.timestamp.format>
    </properties>
    
在编译打包阶段,vm中的`@webapp.build.version@`被替换为`201602040617`