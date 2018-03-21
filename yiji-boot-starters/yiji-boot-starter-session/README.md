## 1. 组件介绍
此组件提供分布式session的能力，应用布署的不同结点可以共享同一个session。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-session</artifactId>
    </dependency>

2) 配置组件参数    
必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.session.host| session存储redis地址|是|
|yiji.session.port| session存储redis端口|是|


**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

配置参考[com.yiji.boot.session.SessionProperties](src/main/java/com/yiji/boot/session/SessionProperties.java)

## 3. 使用 (参考测试用例)
         
分布式session会对底层的session进行透明的替换，应用只需要像以前一样正常使用session即可。

需要注意的是：    
1) 分布式session不支持session listener
2) 存储在session里面的对象是序列化存储到分布式缓存中的，而不是在内存中存储的，所以想要更改session存储对象的数据时，需要重新setAttribute

    ValueObject valueObject;
    valueObject.xx = origin;
    //存储对象到session中
    session.setAttribute(key, valueObject);
    //更改对象的属性值
    valueObject.xx = modify;
    //获取session中存储的对象
    ValueObject value = session.getAttribute(key)
    //未使用分布式session时， value.xx 是等于 modify的
    assertThat(value.xx).isEqualTo(modify);
    //使用分布式session时，  value.xx 是等于 origin的
    assertThat(value.xx).isEqualTo(origin);

## 4. FAQ

### 4.1. 如何支持UrlBasedSession

由于safari的限制,我们提供iframe给用户时,不支持跨域保存cookie,需要在请求url中带用sessionid信息,默认的session机制(由spring-session提供)不支持从url中获取sessionid,现在提供通过yiji-boot实现从url中获取sessionid,使用步骤如下:

1. 启用特性

        yiji.session.enableUrlBasedSession=true
    
2. url中携带session参数

    session参数名字为`Apps.getAppSessionCookieName()`
    
    IF
        
        url=http://localhost/xxxx.html
        Apps.getAppSessionCookieName()=test-session
        sessionIdValue=xxxxxxx
      
     THEN
        
        请求url= http://localhost/xxxx.html?test-session=xxxxxxx

### 4.2 如何防止用户多次登录?
    
	@Autowired
	private LoginChecker loginChecker;
	
	@RequestMapping("/login")
    public String login(HttpServletRequest request) {
    	try {
    	    //do some login operations
    	    //if login success,then
    		loginChecker.checkUserHasLogin(request, username);
    	} catch (UserHasLoginException e) {
    	    // user has logined, throw a UserHasLoginException
    		return "userHasLogin";
    	}
    		return "loginSuccess";
    	}

`UserHasLoginException`包含如下信息:

    UserHasLoginException{ip='192.168.56.30', username='bohr', loginTime='1462271299591'}
    
注意: 用户已经登录,再次登录时,会`invalidate`新生成的`session`.页面关闭时,请调用注销会话。