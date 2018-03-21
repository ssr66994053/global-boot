## 1. 组件介绍

此组件提供集成boss的功能。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-boss</artifactId>
    </dependency>

**BOSS组件内置依赖于 yiji-boot-starter-session, yiji-boot-starter-web ，yiji-boot-starter-yedis, yiji-boot-starter-dubbo 组件， 请确保这些组件已经正确配置**

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.boss.serverAddress|BOSS子系统的地址|否|

* **注意： 如果启用了hera配置管理系统进行配置，则通用配置的配置项，业务项目不用配置。**

* **这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

* **如果你想关闭默认uri权限验证,用于测试,请使用`yiji.boss.enablePermission=false`,但是如果是线上环境这个选项是没有用的,强制进行uri权限认证**

* **如果你处于开发阶段不想每次登陆后都跳转到boss管理系统页面,请使用`yiji.boss.dev-mode=true`,但是如果是线上环境这个选项是没有用的,强制进行跳转**

## 3. 使用 (参考测试用例)

 1) 配置说明(使用boss子系统，访问uri必须是/boss/${appName}/**下才会生效，如:/boss/cs/xxx)
 
 已cs应用为例:

     属性：
          1.添加： (是你本机调试的地址，线上请用域名）
          yiji.boss.serverAddress=http://localhost:8081

          2.权限配置，请在权限系统里面配置此子系统的权限.

                  a.如：/boss/cs

                     可以在权限系统里面指定通配符:
                      REGEXP@/boss/cs/*RealName*
                      REGEXP@/boss/cs/cache/*
                      REGEXP@/boss/cs/mail*

                   b.如果资源位于以下路径不需要添加权限：
                      /css/**
                      /js/**
                      /images/**
                      /resources/**
                      /services/**

 2) 操作日志收集能力
 
   提供统一的记录用户操作的能力，用于后台审计。

   目前boss-starter通过spring HandlerInterceptor的拦截方式捕获/boss/*的所有请求，并组装成操作信息发送到MQ，由主Boss系统消费消息入库。
   
   子boss不做任何修改即可使用该能力。以下场景下考虑使用`@BossOperation`注解：
   
   1. 当参数存在敏感信息时，需要通过的`ignoreParameterList`来指定忽略的参数，或者使用`ignoreParams=true`来忽略**所有**参数的传递 
   2. 当参数需要做掩码处理时，需要通过的`maskParameterList`来指定忽略的参数
   3. 某个方法不需要收集操作日志，可以通过注解的`ignore`属性来忽略此操作。
   4. 需要中文描述以便于更好的做后台审计
   
   
   
   