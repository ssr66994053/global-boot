## 1. 组件介绍

此组件提供FilterChain,应用可以此组建快速组装FilterChain。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-filterchain</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 无




## 3. 使用 (参考测试用例)
         
1) 定义Context

	public class TestContext extends Context {}

2）定义FilterChain

	public class TestFilterChain extends FilterChainBase<TestContext> {}

3）定义Filter

	public class TestFilter implements Filter<TestContext> {
		private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);
		
		@Override
		public void doFilter(TestContext context, FilterChain<TestContext> filterChain) {
			logger.info("in");
			filterChain.doFilter(context);
			logger.info("out");
		}
		
		@PostConstruct
		public void init() {
			logger.info("init");
		}
	}

4) 使用

	@Resource(name = "testFilterChain")
	private FilterChain<TestContext> filter;
	
	TestContext testContext = new TestContext();
	filter.doFilter(testContext);

## 4. FAQ

### 4.1. 如何配置这些组件？

不需要使用者配置组件，装配组件。filterchain会扫描`yiji.filterchain.scanPackage`定义的包，`Filter`和`FilterChain`会注册到spring容器。

### 4.2. 如何定义filter顺序

filter可以重写`com.yiji.boot.filterchain.Filter#getOrder`方法

### 4.3. 如何实现filter重入

	TestContext testContext = new TestContex();
	//调用filter链
	filter.doFilter(testContext);
	//设置Context对象可以重入
	testContext.reentry();
	//调用filter链
	filter.doFilter(testContext);
