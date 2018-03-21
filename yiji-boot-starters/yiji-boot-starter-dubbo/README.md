## 1. 组件介绍

此组件给应用dubbo能力,并注册[DubboRemoteProxyFacotry bean](http://gitlab.yiji/qzhanbo/yjf-common-util/blob/master/src/main/java/com/yjf/common/dubbo/DubboRemoteProxyFacotry.java)

## 2. 配置

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.dubbo.owner| 应用负责人|否|
|yiji.dubbo.provider.port| 服务提供者端口(服务提供者才需要配置)|否|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

配置参考[com.yiji.boot.dubbo.DubboProperties](src/main/java/com/yiji/boot/dubbo/DubboProperties.java)

## 3. 使用说明

可以开启consumer访问服务端日志`yiji.dubbo.consumerLog`和provider提供服务日志`yiji.dubbo.providerLog`,此两种日志都默认关闭


此组件已经初始化了dubbo基本配置，如果要配置provider或者consumer，可以通过xml或者java config来配置：
    
### 3.1 java config配置

#### 3.1.1 provider

下面的代码暴露了DemoService服务，版本为1.5

	//使用dubbo提供的annotation
	import com.alibaba.dubbo.config.annotation.Service;
	//如果实现类只实现了一个接口，可以不指定interfaceClass，实现多个接口时，请指定要暴露的接口
	@Service(version = "1.5")
	public class DemoServiceImpl implements DemoService {
	
		@Override
		public String echo(String msg) {
			return msg;
		}
	}


#### 3.1.2 consumer：

下面的代码使用了UserService服务，版本为1.5

	@Reference(version = "1.5")
	private UserService userService;


### 3.2 xml配置(不建议使用)

通过下面的annotation来加载xml配置:

    @ImportResource({ "classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-consumer.xml" })

dubbo-provider.xml参考:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://code.alibabatech.com/schema/dubbo
    http://code.alibabatech.com/schema/dubbo/dubbo.xsd
    ">
    <!--ref为服务实现bean id-->
    <dubbo:service interface="com.yjf.cs.service.api.SmsService"
               ref="smsService" version="1.0"/>      
    </beans>
    
dubbo-comsumer.xml参考:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://code.alibabatech.com/schema/dubbo
            http://code.alibabatech.com/schema/dubbo/dubbo.xsd
    ">
    <dubbo:reference id="demoService"
                 interface="com.alibaba.dubbo.demo.DemoService" version="1.0" />
    </beans>
    
## 3 FAQ
### 3.1 dubbo多注册中心
yiji-boot-dubbo-starter组件默认已经定义了一个RegistryConfig bean，该注册中心地址使用 yiji.common.zkUrl 进行配置。如果还需要其他注册中心，可为每个额外的注册中心添加一个bean配置。示例：  
 
	@Bean
	public static RegistryConfig secondRegistry() {
    	RegistryConfig config = new RegistryConfig();
    	config.setProtocol("zookeeper");
		String zkUrlKey = "zkUrl2";
        String cacheFile = "/dubbo/dubbo.cache2";
    	EnvironmentHolder.RelaxedProperty relaxedProperty = new EnvironmentHolder.RelaxedProperty("yiji.common", zkUrlKey);
    	String zkUrl = relaxedProperty.getProperty();
    	if(Strings.isNullOrEmpty(zkUrl)) {
        	throw new AppConfigException("dubbo组件请配置zookeeper地址,key=" + relaxedProperty.getPropertyName());
    	} else {
        	logger.info("dubbo使用注册中心地址:{}", zkUrl);
        	config.setAddress(zkUrl);
        	config.setFile(Apps.getAppDataPath() + cacheFile);
        	return config;
    	}
	}
 
注意：上面的 `zkUrlKey` 变量是注册中心地址配置的key，示例中设为zkUrl2，这样就可以在hera或者application.properties配置文件中使用key `yiji.common.zkUrl2` 配置。`cacheFile` 是注册信息本地缓存文件路径后半部分，需要和默认注册中心中的 "/dubbo/dubbo.cache" 不同，可简单采用在后面加数字编号的方式进行命名。  
 
使用多个注册中心时，如果要指明某个Reference使用某个特定的注册中心，可以使用@Reference注解的registry属性指定注册中心bean的名字。比如`@Reference(registry = "secondRegistry")`

### 3.2 启动时并行注册`com.alibaba.dubbo.config.annotation.Reference`

建议把所有`Reference`放在抽象父类中.dubbo在初始化时,会对一个类中的所有`Reference`并行初始化,这样加快系统的启动速度.

### 3.3 使用`com.yiji.boot.dubbo.cache.DubboCache`

### 3.3 使用`@DubboCache`

`@DubboCache`提供dubbo消费者直接使用缓存的能力，当缓存不存在时，再访问远程dubbo服务。

#### 3.3.1 如何使用

对于dubbo服务提供者，只需要在dubbo接口上增加此注解。

	public interface CacheableService {
		String CACHE_NAME="test";
		@DubboCache(cacheName = CACHE_NAME,key = "#p0.playload")
		SingleValueResult<String> echo(SingleValueOrder<String> order);
	}

如上所示，`cacheName=test`,`key`为第一个参数的playload字段，缓存有效期默认5分钟。

上面的注解和`@org.springframework.cache.annotation.Cacheable(value = CACHE_NAME, key = "#p0.playload")`等价。

对于dubbo服务消费者，只需要跟新jar包即可。

#### 3.3.2 控制缓存

`@DubboCache`提供了消费者可优先使用缓存，**缓存的一致性由服务提供方负责**，当服务提供方使用此注解后，所有的服务消费者都会使用此缓存。

控制缓存分为两种情况：

1. 缓存一致性要求不高，可以通过`DubboCache#expire`设置过期时间，默认为5分钟。
2. 缓存一致性要求高，服务提供方通过`redisTemplate`或者`org.springframework.cache.annotation.CacheEvict`控制缓存。
	
	如上面的例子，如果是会员系统(系统名为`customer`)提供此服务，请求参数中`playload=Bohr`，在redis中缓存`key=cache_customer:test:Bohr`,此`redis key`由三部分组成：namespace(默认为`YedisProperties#namespace`，此由yedis提供)、cacheName,key. 
	
	在会员系统中通过如下代码清除缓存：
	
		redisTemplate.delete("test:Bohr");
	
	或者在修改模型的方法上加：
	
		@CacheEvict(value = CACHE_NAME, key = "#p0.playload")
	