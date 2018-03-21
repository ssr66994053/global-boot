## 1. 组件介绍

此组件提供分布式缓存的能力(主要用于替换memcached), 底层存储实现是基于codis(一个分布式 Redis 集群解决方案)。

对于spring 声明式缓存，加入如下能力：
1. 根据codis特性优化性能
2. 响应结果为null时不缓存数据，需要缓存null时，建议用空对象代替(我们的使用场景很少会对结果为null时缓存)
3. 当缓存操作异常时不抛出异常
4. 缓存key格式为:namespace+cacheName+":"+param,(cacheName为@Cacheable上的cacheName参数)

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-yedis</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.yedis.host| 缓存redis服务地址|是|
|yiji.yedis.port| 缓存redis端口|是|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

更多详细配置参考[com.yiji.boot.yedis.YedisProperties](src/main/java/com/yiji/boot/yedis/YedisProperties.java)


## 3. 使用 (参考测试用例)
         
1) 基于spring注解使用缓存能力

    // 先从缓存取 user 
    @Cacheable(value = "cacheName",key = "#id")   
     public Object findUser( long id ) {    
       return user;    
     }    

    // 先更新缓存 user    
    @CachePut(value = "cacheName" ,key = "#user.id")        
    public Object updateUser(Object user){     
      return  user ;      
    }      

    // 从缓存中删除 user 的key   
    @CacheEvict(value = "cachaName" ,key = "#id")   
    public void deleteUser(long id){    
      return ;   
    }

2) 基于redisTemplate使用底层缓存存取    

    // 获取redisTemplate bean实例 
    @Autowired
    private RedisTemplate redisTemplate;
    
    // 对简单的 key-value 的封装
    ValueOperations va = redisTemplate.opsForValue() ;
    va.set("test_key","test_value");
    Object cachValue = va.get("test_key");
    // 对hash表操作的封装 
    HashOperations hash =  redisTemplate.opsForHash() ;
    // 对链表操作的封装 
    ListOperations list = redisTemplate.opsForList() ;
    // 对集合操作的封装 
    SetOperations set = redisTemplate.opsForSet() 

