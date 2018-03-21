Spring Java-based容器配置
==========

多年以来，Spring大量的XML配置及复杂的依赖管理饱受非议。为了实现免XML的开发体验，Spring添加了新的配置注解以支持Java Config开发模式，其中最重要的注解就是@Configuration和@Bean。

### 基本概念：@Bean和@Configuration

在Spring新的Java-configuration支持中，最核心的部分就是使用@Configuration注解的类和使用@Bean注解的类。

@Bean注解用于指示一个方法实例化，配置，初始化一个新的被Spring容器管理的对象。对于熟悉Spring `<beans/>` XML配置的人来说，@Bean注解跟`<bean/>`元素作用相同。你可以在任何Spring @Component中使用@Bean注解的方法，不过，它们通常和@Configuration注解的beans一块使用。

使用@Configuration注解一个类意味着它的主要目的是作为bean定义的来源。此外，@Configuration类允许bean之间（inter-bean）的依赖，你只需简单地调用该类中其他的@Bean方法。
示例：
```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

}
```
上面的AppConfig类等价于下面的Spring `<bean/>` XML：
```xml
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```
* Full @Configuration VS 'lite' @Beans模式

当@Bean方法声明在没有被@Conguration注解的类里，这就是所谓的以'精简'模式处理。例如，在一个@Component中，甚至在一个普通的类中声明的bean方法都会以'精简'处理。
跟完整@Configuration不同的是，精简@Bean方法难以声明bean之间的依赖。通常，在精简模式中操作时，不应该在一个@Bean方法中调用另一个@Bean方法。

一种推荐的方式是只在@Configuration类中使用@Bean方法，这样可以确保总是使用'完整'模式，避免@Bean方法意外地被调用多次，减少那些在精简模式下产生的很难跟踪的微妙bugs。

### 使用AnnotationConfigApplicationContext实例化Spring容器

AnnotationConfigApplicationContext是在Spring 3.0中新增的。这个多功能的ApplicationContext实现即可接收@Configuration类作为输入，也可接收普通的@Component类，及使用JSR-330元数据注解的类。

当将@Configuration类作为输入时，@Configuration类本身被注册为一个bean定义，并且该类中所有声明的@Bean方法也被注册为bean定义。

当将@Component和JSR-330类作为输入时，它们被注册为bean定义，并且在需要的地方使用DI元数据，比如@Autowired或@Inject。

* 构造器实例化

跟实例化一个ClassPathXmlApplicationContext时将Spring XML文件用作输入类似，在实例化一个AnnotationConfigApplicationContext时可以使用@Configuration类作为输入。这就允许Spring容器完全零XML配置：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
如上所述，AnnotationConfigApplicationContext不局限于只使用@Configuration类。任何@Component或JSR-330注解的类都可以作为AnnotationConfigApplicationContext构造器的输入。例如：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```
上面假设MyServiceImpl，Dependency1和Dependency2使用Spring依赖注入注解，比如@Autowired。

* register(Class<?>…​)实例化

可以使用无参的构造器实例化AnnotationConfigApplicationContext，然后使用`register()`方法对容器进行配置。这种方式在以编程方式构造一个AnnotationConfigApplicationContext时非常有用。
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

* 启用scan(String…​)的组件扫描

想要启用组件扫描，只需按如下方式注解你的@Configuration类：
```java
@Configuration
@ComponentScan(basePackages = "com.acme")
public class AppConfig  {
    ...
}
```
**注：** 有经验的Spring用户会比较熟悉来自Spring `context:`命名空间的等效XML声明：
```xml
<beans>
    <context:component-scan base-package="com.acme"/>
</beans>
```
在上面的示例中，将会扫描`com.acme`包，查找任何被@Component注解的类，并且这些类将被注册为容器里的Spring bean定义。AnnotationConfigApplicationContext暴露`scan(String…​)`方法来实现相同的容器扫描功能：
```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}
```
**注：** 记着@Configuration类是被@Component元注解的，所以它们也是组件扫描的候选者！在上面的示例中，假设AppConfig定义在`com.acme`包（或任何下层包）中，在调用`scan()`期间它也会被扫描，当`refresh()`时它的所有@Bean方法将被处理，并注册为容器里的bean定义。

* 使用AnnotationConfigWebApplicationContext支持web应用

AnnotationConfigWebApplicationContext是AnnotationConfigApplicationContext的WebApplicationContext变种，当配置Spring ContextLoaderListener servlet监听器，Spring MVC DispatcherServlet等会用到。下面的web.xml片段配置了一个典型的Spring MVC web应用。注意contextClass的context-param和init-param的使用：
```xml
<web-app>
    <!-- 配置ContextLoaderListener使用 AnnotationConfigWebApplicationContext代替默认的XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration位置必须包含一个或多个逗号或空格分隔的全限定 @Configuration类.组件扫描中要指定该全限定包.-->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- 像平常那样使用ContextLoaderListener启动根应用上下文 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!--像平常那样声明一个Spring MVC DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 配置DispatcherServlet使用AnnotationConfigWebApplicationContext代替默认的XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- 再次，配置路径必须包含一个或多个逗号，或空格分隔的全限定@Configuration类 -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- 将/app/*的所有请求映射到该dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>
```
### @Bean注解用法

@Bean是一个方法级别的注解，XML`<bean/>`元素的等价物。该注解提供一些`<bean/>`提供的元素，例如[init-method](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-lifecycle-initializingbean), [destroy-method](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-lifecycle-disposablebean), [autowiring](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-autowire)和`name`。
你可以在一个@Configuration注解或@Component注解的类中使用@Bean注解。

* 声明一个bean

想要声明一个bean，只需简单的使用@Bean注解一个方法。你可以使用该方法注册一个bean定义到ApplicationContext中，类型通过方法的返回值指定。默认情况下，bean的名称和方法名一样。下面是一个简单的@Bean方法声明示例：
```java
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl();
    }

}
```
上述的配置等价于下面的Spring XML：
```xml
<beans>
    <bean id="transferService" class="com.acme.TransferServiceImpl"/>
</beans>
```
两个声明都在ApplicationContext中创建了一个名称为transferService 的bean，该bean都绑定到一个TransferServiceImpl类型的对象实例：`transferService -> com.acme.TransferServiceImpl`。

* Bean依赖

一个@Bean注解的方法可以有任意数量的参数来描述构建该bean所需的依赖。举例来说，如果我们的TransferService需要一个AccountRepository，我们可以通过一个方法参数提供该依赖：
```java
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }

}
```
这种处理机制对于基于构造器的依赖注入非常重要，具体参考[相关章节](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-constructor-injection)。

* 接收生命周期回调

每个使用@Bean注解定义的类都支持常规的生命周期回调，并可以使用来自JSR-250的@PostConstruct和@PreDestroy注解，具体参考[JSR-250注解](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-postconstruct-and-predestroy-annotations)，常规的Spring生命周期回调也全支持。如果一个bean实现InitializingBean，DisposableBean或Lifecycle，它们相应的方法会被容器调用。

标准的`*Aware`接口集合，比如[BeanFactoryAware](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-beanfactory), [BeanNameAware](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-aware), [MessageSourceAware](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#context-functionality-messagesource), [ApplicationContextAware](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-aware)等也都全部支持。@Bean注解支持指定任意数量的初始化和销毁回调方法，类似于Spring XML `bean`元素的`init-method`和`destroy-method`属性：
```java
public class Foo {
    public void init() {
        // initialization logic
    }
}

public class Bar {
    public void cleanup() {
        // destruction logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public Foo foo() {
        return new Foo();
    }

    @Bean(destroyMethod = "cleanup")
    public Bar bar() {
        return new Bar();
    }

}
```
**注：** 默认情况下，使用Java config定义的beans有一个public的`close`或`shutdown`方法被自动注册为销毁回调。如果你有一个public的`close`或`shutdown`方法，并且不想在容器关闭时调用它，你只需简单地将`@Bean(destroyMethod="")`添加到bean定义以此禁用默认的推断（inferred）模式。对于一个通过JNDI获取的资源来说，由于它的生命周期是由服务器管理，而不是应用，所以你可能想默认就这样做，特别是针对DataSource这样的资源：
```java
@Bean(destroyMethod="")
public DataSource dataSource() throws NamingException {
    return (DataSource) jndiTemplate.lookup("MyDS");
}
```
当然，在上面的Foo示例中，直接在构造器中调用`init()`方法也是有效的：
```java
@Configuration
public class AppConfig {
    @Bean
    public Foo foo() {
        Foo foo = new Foo();
        foo.init();
        return foo;
    }

    // ...

}
```
**注：** 当直接使用Java时，你可以对你的对象做任何想做的事，而不总是需要依赖于容器的生命周期。

* 指定bean作用域

1.使用@Scope注解

你可以为通过@Bean注解定义的bean指定一个特定的作用域。你可以使用[Bean Scopes](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-scopes)作用域中定义的任何标准作用域。默认作用域为singleton，但你可以使用@Scope作用域覆盖它：
```java
@Configuration
public class MyConfiguration {

    @Bean
    @Scope("prototype")
    public Encryptor encryptor() {
        // ...
    }

}
```
2.@Scope和scoped-proxy

Spring提供一个方便的方式来通过[scoped proxies](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-scopes-other-injection)处理作用域的依赖。最简单的方式是创建一个这样的proxy，在使用XML配置时相应配置为`<aop:scoped-proxy/>`元素。在Java中通过@Scope注解和proxyMode属性可以达到相同的功能。默认没有proxy（ScopedProxyMode.NO），但你可以指定为ScopedProxyMode.TARGET_CLASS或ScopedProxyMode.INTERFACES。
如果你想从XML参考文档的scoped proxy示例过渡到使用Java的@Bean，它看起来可能如下所示：
```java
// an HTTP Session-scoped bean exposed as a proxy
@Bean
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public UserPreferences userPreferences() {
    return new UserPreferences();
}

@Bean
public Service userService() {
    UserService service = new SimpleUserService();
    // a reference to the proxied userPreferences bean
    service.setUserPreferences(userPreferences());
    return service;
}
```
3.自定义bean名称

默认情况下，配置类使用@Bean方法名作为结果bean的名称。该功能可以被`name`属性覆盖。
```java
@Configuration
public class AppConfig {

    @Bean(name = "myFoo")
    public Foo foo() {
        return new Foo();
    }

}
```
4.Bean别名

有时候为单个bean起多个名称是有必要的，@Bean注解的name属性可以接收一个String数组来达到这个目的。
```java
@Configuration
public class AppConfig {

    @Bean(name = { "dataSource", "subsystemA-dataSource", "subsystemB-dataSource" })
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }

}
```
5.Bean描述

有时候为一个bean提供详细的文本描述是很有帮助的，特别是当beans被暴露（可能通过JMX）用于监控目的时。

可以使用[@Description](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Description.html)注解为一个@Bean添加描述：
```java
@Configuration
public class AppConfig {

    @Bean
    @Description("Provides a basic example of a bean")
    public Foo foo() {
        return new Foo();
    }

}
```

### @Configuration注解用法

@Configuration注解是一个类级别的注解，它意味着该对象是一个bean定义的来源。@Configuration类通过public的@Bean注解的方法来声明beans。调用@Configuration类上的@Bean方法可用于定义bean之间的依赖。

* bean的依赖注入

当@Beans依赖其他bean时，只需要在一个bean方法中调用另一个bean即可表达这种依赖关系：
```java
@Configuration
public class AppConfig {

    @Bean
    public Foo foo() {
        return new Foo(bar());
    }

    @Bean
    public Bar bar() {
        return new Bar();
    }

}
```
在上面的示例中，foo bean通过构造器注入获取到一个指向bar的引用。

**注：** 声明bean之间依赖的方法只在@Configuration类内部的@Bean方法有效，你不能使用普通的@Component类声明bean之间的引用。

* Lookup方法注入

正如前面提到的，[lookup method injection](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-factory-method-injection)是一个高级的特性，你应该少用。当一个singleton作用域的bean依赖一个prototype作用域的bean时该方法很有用。使用Java配置方式对这种类型的配置提供了一种自然的手段来实现该模式。
```java
public abstract class CommandManager {
    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();

        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
    return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}
```
使用Java配置支持，你可以创建一个CommandManager的子类，在这里抽象的createCommand()方法以查找一个新的（prototype）命令对象来覆盖：
```java
@Bean
@Scope("prototype")
public AsyncCommand asyncCommand() {
    AsyncCommand command = new AsyncCommand();
    // inject dependencies here as required
    return command;
}

@Bean
public CommandManager commandManager() {
    // return new anonymous implementation of CommandManager with command() overridden
    // to return a new prototype Command object
    return new CommandManager() {
        protected Command createCommand() {
            return asyncCommand();
        }
    }
}
```

* Java-based配置内容如何工作

下面的示例展示了一个@Bean注解的方法被调用了两次：
```java
@Configuration
public class AppConfig {

    @Bean
    public ClientService clientService1() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientService clientService2() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientDao clientDao() {
        return new ClientDaoImpl();
    }

}
```
`clientDao()`在`clientService1()`中调用了一次，然后在`clientService2()`中调用了一次。由于这个方法创建了一个新的ClientDaoImpl实例，然后返回它，你可以期望有两个实例（每个service都有一个实例）。这绝对会出问题：在Spring中，实例化的beans默认情况下作用域为singleton。这就是魔法产生的地方：所有的@Configuration类在启动期间都是被CGLIB子类化过的（代理）。在子类中，子类的方法首先检查容器是否缓存（scoped）相应的beans，如果没有缓存才会调用父类的方法，创建一个新的实例。注意在Spring3.2之后，已经不需要添加CGLIB的依赖，因为CGLIB被重新打包到org.springframework下，并直接包含在spring-core JAR中。

**注：** 该行为依赖于bean的作用域，此处我们讨论的是单例。由于CGLIB动态代理的特性这里有一些限制：配置类不能为final的，它们应该有一个无参构造器。

### 组装Java-based的配置

* 使用@Import注解

跟在Spring XML文件中使用`<import>`元素添加模块化的配置类似，@Import注解允许你加载其他配置类中的@Bean定义：
```java
@Configuration
public class ConfigA {

     @Bean
    public A a() {
        return new A();
    }

}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }

}
```
现在，当实例化上下文时，你只需要显式的指定ConfigB，而不需要既提供ConfigA.class，又提供ConfigB.class：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}
```
这种方式简化了容器的初始化，因为只需要处理一个类，而不是让开发者记住构造期间的大量@Configuration类。

* 导入@Bean的依赖注入

上面的示例可以工作，但太简单。在大多数实际的场景中，beans会依赖另一个跨配置类的bean。当使用XML时，这不是问题，因为不涉及到编译，其中一个bean只需要声明`ref="someBean"`，剩下的交给Spring在容器初始化期间处理即可。当然，当使用@Configuration类时，Java编译器对配置模式产生一些限制，对其他beans的引用必须是合法的java语法。

幸运的是，解决该问题是很容易的。正如我们[已经讨论](http://docs.spring.io/spring/docs/4.2.0.RC1/spring-framework-reference/htmlsingle/#beans-java-dependencies)的，@Bean可以有任意多个用来描述bean依赖的参数。让我们探讨一个更现实的场景，在这里将使用一些彼此依赖的@Configuration类：
```java
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }

}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }

}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
这里有另外的方法可以达到相同的效果。记住，@Configuration根本上只是容器中的另一个bean-这意味着它们可以像其他bean那样充分利用@Autowired注入元数据。
**注：** 确保以这种方式注入的都是简单类型的。@Configuration类在容器初始化时被处理的相当早，用这种方式强制注入依赖可能导致无法预料地过早初始化问题。只要有可能就采用上面示例中基于参数的注入方式。

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }

}

@Configuration
public class RepositoryConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
在上面的示例中，使用@Autowired工作的很好，并且提供了想要的模块化，但要确切地指明自动注入的bean定义在哪声明依旧有点模糊。例如，一个开发者正在查看ServiceConfig，那你怎么准确地知道`@Autowired AccountRepository` bean在哪声明的？在代码中并不明确，不过有时候这样就行。记着[Spring Tool Suite](https://spring.io/tools/sts)可以提供渲染图的工具，这些图展示了Spring Bean之间是怎么连起来的-这可能是你需要的。同时，你的Java IDE可以轻松的找到所有声明和使用AccountRepository类型的bean，并为你快速展现返回该类型的@Bean方法位置。

如果你不能接受这种模糊性，并希望在你的IDE中可以从一个@Configuration类导航到另一个，那就考虑注入配置类本身：
```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        // navigate 'through' the config class to the @Bean method!
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }

}
```
在上面的解决方案中，我们可以很明确地知道AccountRepository定义的地方。然而，ServiceConfig现在紧紧地跟RepositoryConfig耦合了。这就是权衡。紧耦合在某种程度上可以通过使用基于接口或抽象类的@Configuration类来减轻。考虑以下内容：
```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}

@Configuration
public interface RepositoryConfig {

    @Bean
    AccountRepository accountRepository();

}

@Configuration
public class DefaultRepositoryConfig implements RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(...);
    }

}

@Configuration
@Import({ServiceConfig.class, DefaultRepositoryConfig.class}) // import the concrete config!
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```
现在，ServiceConfig跟具体的DefaultRepositoryConfig类是松耦合的关系，并且内嵌的IDE工具依旧有用：它很容易为开发者获取RepositoryConfig实现的类型层次。采用这种方式，导航@Configuration和它们的依赖就变得跟平常处理基于接口的代码导航没区别了。

* 有条件的包含@Configuration类或@Beans

基于任意的系统状态，有条件地禁用一个完整的@Configuration类，甚至单独的@Bean方法通常是很有用的。一个常见的示例是，当一个特定的profile在Spring Environment中启用时，使用@Profile注解激活beans。

@Profile注解实际上实现了一个非常灵活的注解：[@Conditional](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Conditional.html)。@Conditional注解意味着在注册@Bean之前，必须先咨询指定的`org.springframework.context.annotation.Condition`实现。

Condition接口的实现者只需简单地提供一个返回true或false的`matches(…​)`方法。例如，下面是@Profile注解采用的Condition实现：
```java
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    if (context.getEnvironment() != null) {
        // Read the @Profile annotation attributes
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                    return true;
                }
            }
            return false;
        }
    }
    return true;
}
```
具体参考[@Conditional javadocs](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Conditional.html)。

* 结合Java和XML配置

Spring @Configuration类支持目的不是想要100%的替换Spring XML。一些设施，比如Spring XML命名空间仍旧是配置容器的完美方式。在XML很方便或必须的情况下，你有个选择：采用"XML为中心"的方式实例化容器，比如ClassPathXmlApplicationContext，或使用AnnotationConfigApplicationContext以"Java为中心"的方式，并使用@ImportResource注解导入需要的XML。

* 在以"XML为中心"的情况下使用@Configuration类

从XML启动Spring容器，以特设模式包含@Configuration类可能是个更可选的方式。例如，在一个已经存在的使用Spring XML的大型代码库中，遵循按需原则创建@Configuration，并从现有的XML文件中包括它们是非常容易的。下面你将找到在这样的"XML为中心"的解决方案中使用@Configuration类的可选项。

记着@Configuration类本质上只是容器中的bean定义。在下面的示例中，我们创建了一个名称为AppConfig的@Configuration类，并将它作为`<bean/>`定义包含到`system-test-config.xml`中。因为`<context:annotation-config/>`是开启的，容器将会识别@Configuration注解，并正确地处理AppConfig中声明的@Bean方法。
```java
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }

}
```
system-test-config.xml如下：
```xml
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```
jdbc.properties如下：
```properties
jdbc.properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```
main方法如下：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```
**注：** 在上面的`system-test-config.xml`中，`AppConfig<bean/>`没有声明一个`id`元素。如果没有bean引用它，那就没有必要指定`id`元素，否则就要通过name从容器获取bean（name对应bean定义中声明的id）。DataSource也一样-它只是通过类型自动注入（autowired by type），所以并不需要显式的分配一个bean id。

由于@Configuration被@Component元注解了（被注解注解，很拗口），所以被@Configuration注解的类自动成为组件扫描（component scanning）的候选者。同样使用上面的场景，我们可以重新定义`system-test-config.xml`来充分利用组件扫描。注意在这个示例中，我们不需要明确声明`<context:annotation-config/>`，因为`<context:component-scan/>`启用了同样的功能。

system-test-config.xml如下：
```xml
<beans>
    <!-- picks up and registers AppConfig as a bean definition -->
    <context:component-scan base-package="com.acme"/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```
* 在@Configuration"类为中心"的情况下使用@ImportResourcedaoru导入XML

在将@Configuration类作为配置容器的主要机制的应用中，仍旧存在对XML的需求。在那些场景中，可以使用@ImportResource，并定义所需的XML。这样做可以实现以"Java为中心"的方式配置容器，并保留最低限度的XML。
```java
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }

}
```
properties-config.xml如下：
```xml
<beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>
```
jdbc.properties如下：
```properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```
main方法如下：
```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```
