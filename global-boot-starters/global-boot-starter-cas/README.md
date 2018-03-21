## 1. 组件介绍

此组件提供集成单点登录authcenter的功能。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-cas</artifactId>
    </dependency>

**1.cas组件内置依赖于 yiji-boot-starter-session, yiji-boot-starter-web,yiji-boot-starter-yedis组件,请确保这些组件已经正确配置**

**2.cas组件内置shiro权限，需要去掉yiji-boot-starter-shiro**

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----:|
|yiji.cas.clientServerName|客户端应用服务地址|否|
|yiji.cas.loginUrl|登录地址|否|
|yiji.cas.successUrl|登录成功地址|否|
|yiji.cas.casServer|authchenter服务端地址|是|
|yiji.cas.newcaptchaUrl|登录验证码地址|是|



* **注意： 如果启用了hera配置管理系统进行配置，则通用配置的配置项，业务项目不用配置。**

* **通用配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

## 3. 使用

 1) 配置说明
 
 已cs应用为例:

     属性：
          1.添加配置： (是你本机调试的地址，线上请用域名）
          必选
          yiji.cas.clientServerName=http://127.0.0.1:8081
          yiji.cas.loginUrl=http://127.0.0.1:8081/login
          yiji.cas.successUrl=http://127.0.0.1:8081/hello

          通用配置，如果配置hera就可以不用配
          yiji.cas.casServer=http://127.0.0.1:8326
          yiji.cas.newcaptchaUrl=https://authcenter.yiji/getImgCode

          2.权限配置.
                  a.对应shiro.ini中的[urls]标签,从下标0开始,如:
                     yiji.cas.excludeUrls[0]./main.html=anon
                     yiji.cas.excludeUrls[1]./logout.html=anon
                  b.如果资源位于以下路径,不需要添加权限,默认添加xxx=anon：
                      /mgt/**
                      /boss/**
                      /login
                      /css/**
                      /js/**
                      /img/**
                      /images/**
                      /resources/**
                  c.yiji.cas.loginUrl也是默认排除掉的,不会经过权限验证,不然登录时候会循环重定向

   
   
   
   




   