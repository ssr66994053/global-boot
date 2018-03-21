## 1. 组件介绍

此组件提供spring data mongo相关的Bean。包含了mongo

## 2. 配置

1) 增加组件依赖

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>

 **该组件依赖于yiji-boot-starter-jdbc, 请确保该组件已经正确配置**


2) 配置组件参数    

必配参数: （在数据库字段命名不是标准的'_'分隔的情况下） 

|参数名|参数描述|是否通用配置|配置值|
|:---:|:------|:-----|:-----|
|spring.data.mongodb.uri| mongo连接地址|否|mongodb://silverbolt:123456@192.168.45.15:27017,192.168.45.15:27018,192.168.45.15:27019/yjf_silverbolt|


## 3. 基于java config的配置

以前基于spring xml的mongo示例配置

    <mongo:auditing/>
    <mongo:repositories base-package="com.yjf.core.silverbolt.domainrepository"/>
    <mongo:db-factory id="mongoDbFactory" dbname="${mongo.dbname}" mongo-ref="mongo" username="${mongo.user}"
                      password="${mongo.password}"/>
    <mongo:mongo id="mongo" replica-set="${mongo.replicationset}">
         	<mongo:options connections-per-host="200"
                   threads-allowed-to-block-for-connection-multiplier="4"/>
        </mongo:mongo>
    
    <bean id="marjoryWriteConcern" class="com.mongodb.WriteConcern">
          <constructor-arg index="0" type="int" value="1"/>
    </bean>
        <bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
            <constructor-arg name="mongoDbFactory" ref="mongoDbFactory"/>
            <property name="writeResultChecking" value="EXCEPTION"/>
            <property name="writeConcern" ref="marjoryWriteConcern"/>
        </bean>
    </beans>


转换后的配置示例为：

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, WriteConcern marjoryWriteConcern){
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        mongoTemplate.setWriteConcern(marjoryWriteConcern);
        return mongoTemplate;
    }

    @Bean(name= "marjoryWriteConcern")
    public WriteConcern marjoryWriteConcern1(){
        WriteConcern marjoryWriteConcern = new WriteConcern(0);
        return marjoryWriteConcern;
    }

    @Bean
    public MongoClientOptions options(){
        MongoClientOptions options = MongoClientOptions.builder().build();
        MongoClientOptions.Builder builder =options.builder();
        builder.connectionsPerHost(200);
        builder.threadsAllowedToBlockForConnectionMultiplier(5);
        return options;
    }