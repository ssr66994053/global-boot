## 1. 组件介绍

此组件提供了基于rocketmq的统一异步通知组件。提供了messageProducer,启用了RocketListener注解支持，内置消息序列化功能，使用kyro实现。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-rocketmq</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|备注|
|:---:|:------|:-----|:-----|
|yiji.rocketmq.nameSrvAddr| rocketmq服务地址|是|如rocketmq.yiji.me:9876|
|yiji.rocketmq.producer.enable| 是否启用消息生产端|否|默认为false|
|yiji.rocketmq.consumer.enable| 是否启用消息生产端|否|默认为false|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

更多详细配置参考[com.yiji.boot.rocketmq.RocketMQProperties](src/main/java/com/yiji/boot/rocketmq/RocketMQProperties.java)    

使用rocketmq前建议阅读官方文档[RocketMQ原理简介](http://gitlab.yiji/yanglie/rocketmq/blob/master/docs/RocketMQ_%E5%8E%9F%E7%90%86%E7%AE%80%E4%BB%8B.pdf)


## 3. 使用 (参考测试用例)
         
1) 消息生产端
     
     //消息发送器
     @Autowired
     MessageProducer messageProducer;
    
     //发送消息 
     public void sendMessage() { 
           //(谨慎使用，会影响并发性) 发送顺序消息，消费端需要按照发送顺序进行处理
           OrderedNotifyMessage message = new OrderedNotifyMessage();
           //发送普通消息为，NotifyMessage message = new NotifyMessage();
           
           //仅针对顺序消息，设置消息的groupId，属于同一个groupId的多个消息，消费端按发送顺序进行处理。
           //比如一个订单有多个状态变化，需要分别进行通知，那这个订单产生的多个状态变化通知消息需要设置相同的groupId，可以使用订单号。
           message.setGroupId("order_001");
           //设置消息发送到哪个topic,一般来说一个应用对应一个topic
           message.setTopic("TopicTest");
           //设置消息的事件类型，主要用来区分不同应用的不同业务类型消息
           message.setEvent("event_trade");
           //设置消息接收系统, 如果是想广播消息，则不需要设置接收系统，默认会为ALL
           message.setToSystem("yiji-boot-test");
           //设置消息的唯一id，不同的消息应该保证id唯一，方便进行消息的追踪
           message.setId("trade_no_201602161400_" + i);
           //设置消息的gid，对应统一订单号的gid
           message.setGid("gid00000000000000000000000000000");
           //设置消息内容，key,value的map
           Map<Object, Object> data = new HashMap();
           message.setData(data);
           try {
               MessageResult result = messageProducer.send(message);
               if(!result.isSuccess())){
                   //已经发送到服务端，但未完全成功，不能保证消息百分百不丢失
                   //对数据可靠性要求高的系统，需要进行重发，但可能会导致消息重发，消费端需做好幂等校验
               }
           }catch (Exception e) {
               //消息未发送到服务端，发送失败，异常处理
           }
     }    


2) 消息消费端    

    @Component
    public class RocketConsumer {
    	
    	/**
    	 * 定义一个顺序消息的监听处理方法，会接收到topic上的消息，通过messageFilter对消息进行过滤
    	 * @param message
    	 */
    	@RocketListener(topic = "TopicTest", ordered = true,
    			messageFilter = @MessageFilter(fromSystem = "accountant", event = "event_trade")
    	public void processOrderedMessage(OrderedNotifyMessage message) {
    		System.out.println(ToString.toString(message));
    	}
    	
        /**
         * 定义一个消息的监听处理方法，会接收到topic上的消息，并进行处理
         * @param message
         */
        @RocketListener(topic = "TopicTest",
                messageFilter = @MessageFilter(fromSystem = "accountant", event = "event_trade")
        public void processCommonMessage(NotifyMessage message) {
            System.out.println(ToString.toString(message));
        }
        
        /**
         * 接收accountant发送的广播消息
         * @param message
         */
        @RocketListener(topic = "TopicTest",
                messageFilter = @MessageFilter(fromSystem = "accountant", toSystem = "ALL", event = "event_trade")
        public void processBroadcastMessage(NotifyMessage message) {
            System.out.println(ToString.toString(message));
        }
        
        /**
         * messageFilter的每个属性支持多条件，以OR的方式组合起来
         * 下面的filter表示，消息发送方是account或trade，接收方为cashier或common-service, 
         * event为event-trade或event-deposit，环境为stest或snet
         * @param message
         */
        @RocketListener(topic = "TopicTest",
                messageFilter = @MessageFilter(fromSystem = { "accountant", "trade" },
                        toSystem = { "cashier", "common-service" }, event = { "event-trade", "event-deposit" },
                        env = { "stest", "snet" }) )
        public void processBroadcastMessage(NotifyMessage message) {
            System.out.println(ToString.toString(message));
        }
    }

3) 注意点

3.1 一个topic只能有一个RocketListener。     
3.2 尽量不要使用顺序消息，除非实在有必要。     
3.3 监听消息的方法，如果抛出异常，服务端会重发消息。    
3.4 异常情况下，消费端可能会接收到重复的消息，消费端需要做好幂等校验。     
3.5 顺序消息在服务器宕机或扩容的情况下，可能会有短暂的消息乱序。
3.6 topic的创建原则上按照系统来创建，即每个系统对应自己的topic，名字为topic_${系统名}。    
3.7 如果是一个系统需要往不同的系统群发送消息，那topic由发送系统创建。    
3.8 如果是一个系统需要接收多个系统的消息来做数据汇集，那topic由消费系统创建。

4) 上线准备    
线上关闭了rocketmq的topic, producerGroup和consumerGroup的自动创建功能，需要运维人员手工创建。     
所以上线前需要确保，项目需要使用到的topic已经创建完成了。    
如果系统需要接收消息，需要确保consumerGroup已经创建好，名字为：Consumers_${系统名}__common 和 Consumers_${系统名}_ordered    

5) 创建topic所需参数: topic名字， topic的可读队列数，可写对列数(默认均为8, 如果该topic的吞吐大，可适当考虑加大)    
6) 创建consumerGroup所需参数：consumerGroup的名字
