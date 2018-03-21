## 1. 组件介绍

此组件提供JPA相关的Bean。包含了entityManagerFactory,jpaVendorAdapter,transactionManager等。以及Entity和Repository自动扫描加载。

## 2. 配置

1) 增加组件依赖

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

 **该组件依赖于yiji-boot-starter-jdbc, 请确保该组件已经正确配置**


2) 配置组件参数    

必配参数: （在数据库字段命名不是标准的'_'分隔的情况下） 

|参数名|参数描述|是否通用配置|配置值|
|:---:|:------|:-----|:-----|
|spring.jpa.hibernate.naming-strategy| 数据库字段映射命名策略|否|org.hibernate.cfg.DefaultNamingStrategy|


**如果命名是标准的'_'分隔的结构，不需要配置这个配置项。