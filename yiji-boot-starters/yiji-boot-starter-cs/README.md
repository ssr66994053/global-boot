## 1. 组件介绍

此组件提供发送异步外部通知和发送邮件的功能。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-cs</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.cs.shuntMessageQueueName| shuntMessage队列名称|是|
|yiji.cs.emailQueueName| 邮件队列名称|是|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

## 3. 使用 (参考测试用例)
         
1) 直接注入使用

    // 发送异步通知
    @Autowired
     private ShuntMQClient shuntMQClient;

    // 发送邮件
    @Autowired
     private MailMQClient mailMQClient ;






