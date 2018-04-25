## 1. 组件介绍 ##
该组件提供Postman组件功能。参考http://192.168.45.16/confluence/pages/viewpage.action?pageId=10584156

## 2. 组件配置 ##
1) maven依赖

	<dependency>
    	<groupId>com.yiji.boot</groupId>
        <artifactId>yiji-boot-starter-postman</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.postman.appkey|信息中心分配的appkey|否|
|yiji.postman.appsecret| 信息中心分配的应用appsecret|否|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

了解更多详细参数配置，请参考组件配置类：    
配置类com.yiji.boot.postman.PostmanProperties。