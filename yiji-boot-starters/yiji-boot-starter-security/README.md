## 1. 组件介绍
此组件自动集成[yiji-security-framework](http://gitlab.yiji/agraellee/yiji-security-framework)。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-security</artifactId>
    </dependency>

2) 配置组件参数    

加密、解密、签名、验证等功能配置参考[com.yiji.boot.securityframework.SecurityProperties](src/main/java/com/yiji/boot/securityframework/SecurityProperties.java)与[com.yiji.boot.securityframework.WebSecurityProperties](src/main/java/com/yiji/boot/securityframework/WebSecurityProperties.java)

[SecurityManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/SecurityManager.java) 的来源环境默认支持如下环境： 

`local`：使用本地环境提供 [SecurityConfig](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-ri/src/main/java/com/yiji/common/security/referenceimplements/SecurityConfig.java) 来完成。需要在容器中提供 [SecurityConfigRepository](src/main/java/com/yiji/boot/securityframework/environment/local/SecurityConfigRepository.java) 的实例来提供 [SecurityConfig](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-ri/src/main/java/com/yiji/common/security/referenceimplements/SecurityConfig.java) 。如果容器中没有 [SecurityConfigRepository](src/main/java/com/yiji/boot/securityframework/environment/local/SecurityConfigRepository.java) 的实例则会报错。 

`ri`：使用参考实现来提供 [SecurityManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/SecurityManager.java) （`beanName`为 `yijiBoot_SF_SecurityManager`），通过使用 `beanName`： `yijiBoot_SF_SecurityConfigs`从容器中获得对应的 `java.util.Map` 配置相关的安全用户信息。这也是默认的配置 

`ref`：直接引用环境中已经存在或者已经配置的[SecurityManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/SecurityManager.java) 实例。如果为该环境，则需要指定 [SecurityProperties.setSecurityManagerRefName(String)](src/main/java/com/yiji/boot/securityframework/SecurityProperties.java) 。


索引、反向索引等功能配置参考[com.yiji.boot.securityframework.IndexProperties](src/main/java/com/yiji/boot/securityframework/IndexProperties.java)
[IndexManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/index/IndexManager.java) 的来源环境默认支持如下环境： 

`domain`：使用安全域环境提供 [IndexManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/index/IndexManager.java) 来完成。如果为该环境，则需要指定 [IndexProperties.setIndexManagerRefName(String)](src/main/java/com/yiji/boot/securityframework/IndexProperties.java)，且需要在容器中提供`org.springframework.data.redis.core.RedisTemplate`的实例，默认推荐使用yedis。 

`ri`：使用参考实现来提供  [IndexManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/index/IndexManager.java) （`beanName`为 `yijiBoot_SF_I_IndexManager`），通过使用 `beanName`： `yijiBoot_SF_I_SecurityConfigs`从容器中获得对应的 `java.util.Map` 配置相关的安全用户信息。这也是默认的配置 

`ref`：直接引用环境中已经存在或者已经配置的 [IndexManager](http://gitlab.yiji/agraellee/yiji-security-framework/blob/master/yiji-security-framework-core/src/main/java/com/yiji/common/security/index/IndexManager.java) 实例。如果为该环境，则需要指定 [IndexProperties.setIndexManagerRefName(String)](src/main/java/com/yiji/boot/securityframework/IndexProperties.java) 。


## 3. 使用 (参考测试用例)
         
引入即可使用（安全管理器默认为`ri`环境，可根据需要该为`local`或者`ref`环境，索引管理器默认为`ri`环境，可根据需要该为`domain`或者`ref`环境。）。具体使用细节请参考[yiji-security-framework](http://gitlab.yiji/agraellee/yiji-security-framework)项目的[WIKI](http://gitlab.yiji/agraellee/yiji-security-framework/wikis/home)。
