## 1. 组件介绍

此组件提供RabbitMQ的ConnectionFactory，admin, listener container定义，启用了rabbit注解支持，使用了kyro做为序列化实现，设置listener container的thread pool 

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-amqp</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|spring.rabbitmq.host| 服务地址|是|
|spring.rabbitmq.port| 服务端口|是|
|spring.rabbitmq.username| 登陆用户名|是|
|spring.rabbitmq.password| 登陆密码|是|
    

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

了解更多详细参数配置，请参考组件配置类：    
[com.yiji.boot.amqp.RabbitExtensionProperties](src/main/java/com/yiji/boot/amqp/RabbitExtensionProperties.java)    
[com.yiji.boot.amqp.RabbitThreadPoolProperties](src/main/java/com/yiji/boot/amqp/RabbitThreadPoolProperties.java)
[org.springframework.boot.autoconfigure.amqp.RabbitProperties]


*如果应用使用配置管理系统(hera)进行配置管理，则不需要对此组件进行参数配置。基础技术部会对该组件在配置管理系统中进行统一配置*

## 3. FAQ

### 3.1 如何调整`@RabbitListener`线程数

默认每个`@RabbitListener`标注的方法只有一个线程,yiji-boot已经修改默认为5个线程,可以通过`yiji.rabbitmq.listenerConcurrency`参数调整线程个数

## 4. 使用 (参考测试用例)
         
使用此组件时，请先明确自己的使用场景。请阅读 [rabbitmq项目实践系列](http://doc.yiji.dev/confluence/pages/viewpage.action?pageId=11961418)  

值得注意的是，请不要再使用项目实践系列基于spring xml配置的方式。推荐使用更简单炫酷的基于java config和注解的使用方式。    

使用方法请参考组件的测试用例:  

工作队列: com.yiji.boot.test.amqp.wqueue     
发布订阅: com.yiji.boot.test.amqp.pubsub    
消息路由: com.yiji.boot.test.amqp.routing    
主题分发: com.yiji.boot.test.amqp.topic   

如果生产者没有使用spring-boot，而是使用的以前的方式发送消息，那消费端的使用方式需要参考测试用例：
兼容方式：com.yiji.boot.test.amqp.compatible    

[深入了解spring-amqp使用方式](http://docs.spring.io/spring-amqp/reference/htmlsingle/)



## yiji-boot切换rabbitmq的正确姿势

对于全新的使用rabbitmq的系统(包含生产者和消费都都是新的系统)，推荐参考组件提供的测试用例的方式来使用rabbitmq发送和接收消息。

但是对于要发送或接收已经存在的老系统的消息，基于yiji-boot的官方推荐玩法如下：

### 引用yiji-boot rabbitmq组件
    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-amqp</artifactId>
    </dependency>

### 服务端      
   
1. 不需要在spring xml里面再定义ConnectionFactory和rabbitAdmin由组件提供     
2. 定义跟queue或exchange绑定的rabbitTemplate Bean (组件提供了template bean名字为"rabbitTemplate"， 业务定义的名字不能与组件重复)   


        @Bean
	    public RabbitTemplate amqpTemplate(	@Qualifier("rabbitConnectionFactory") CachingConnectionFactory rabbitConnectionFactory) {
		    RabbitTemplate ampqTemplate = new RabbitTemplate  (rabbitConnectionFactory);
            ampqTemplate.setQueue("my.sample.queue");
		    return ampqTemplate;
	    }

       
         public class Producer extends AbstractMQClient<Message> {
             @Resource(name="amqpTemplate")
             protected AmqpTemplate amqpTemplate;
             public void sendMessage(Message message){
                this.doSend(message,amqpTemplate);
             }
         }

    
### 消费端
1.  不需要在spring xml里面再定义ConnectionFactory和rabbitAdmin由组件提供
2.  也不需要在spring xml里面再配置listener-container, rabbit-listener, consumer bean等等
3.  采用注解的方式来接收处理消息

         @Component
         import com.yjf.common.kryo.Kryos;
         import org.springframework.amqp.core.Message;
         import org.springframework.amqp.rabbit.annotation.RabbitListener;
         import org.springframework.stereotype.Component;
         public class CompatibleConsumer {
	        @RabbitListener(queues = ""my.sample.queue"", containerFactory = "compatibleRabbitListenerContainerFactory")
	        public void process(Message message) {
	         //根据服务端的序列化方式，从rabbitmq的原始Message对象中反序列化出业务消息对象
		     MyMessage message = Kryos.readObject(message.getBody(), MyMessage.class);
	     	//开始处理消息
	        }
        }
        
## 服务端定义或创建queue, exchange, binding例子
        @Configuration
        public class xxxConfiguration {
            final static String queueNameOne = "spring-boot-topic-one";
            final static String queueNametwo = "spring-boot-topic-two";
            final static String exchangeName = "spring-boot-topic-exchange";
            final static String routingKeyOne = "*.orange.*";
            final static String routingKeyTwo = "*.*.rabbit";
            @Autowired
            RabbitTemplate rabbitTemplate;
            
            @Bean
            Queue queueOne() {
                return new Queue(queueNameOne);
            }
            
            @Bean
            Queue queueTwo() {
                //可通过其它构造函数，传递不同的构建参数, 如durable, autoDelete等
                return new Queue(queueNametwo);
            }
            
            @Bean
            TopicExchange topicExchange() {
                //可通过其它构造函数，传递不同的构建参数，如durable, autoDelete等
                return new TopicExchange(exchangeName);
            }
            
            @Bean
            Binding bindingOne(Queue queueOne, TopicExchange topicExchange) {
                return BindingBuilder.bind(queueOne).to(topicExchange).with(routingKeyOne);
            }
            
            @Bean
            Binding bindingTwo(Queue queueTwo, TopicExchange topicExchange) {
                return BindingBuilder.bind(queueTwo).to(topicExchange).with(routingKeyTwo);
            }
            
## 客户端定义或创建queue, exchange, binding例子
		
            @Component
            public class CompatibleConsumer {
                private final static String queueName = "spring-boot-compatible";
                private volatile boolean ready = false;
                private MyMessage lastReceiveMessage;
                
                /**
                 * 定义一个消息的监听处理方法，会接收到queueName的上的消息，并进行处理
                 * @param message
                 */
                @RabbitListener(
                        			bindings = @QueueBinding(value = @Queue(value = "ewpay.trade.queue", durable = "true") ,
                        					exchange = @Exchange(value = "trade.asynmessage", durable = "true", type = ExchangeTypes.FANOUT) ) ,
                        			containerFactory = "compatibleRabbitListenerContainerFactory", admin = "rabbitAdmin")
                public void process(Message message) {
                }
            }

