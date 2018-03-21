## 1. 组件介绍
此组件提供druid的数据源，并对慢查询和大数据集sql打印日志到`sql-10dt.log`文件

## 2. 配置

必配参数: 

|参数名|参数描述|是否通用配置|示例配置|
|:---:|:------|:-----|:-----|
|yiji.ds.url| 数据库连接url|否|jdbc:mysql://192.168.45.85:3306/yjf_scheduler_snet|
|yiji.ds.username| 数据库访问用户名|否|xxxx|
|yiji.ds.password| 数据库访问密码|否|xxxx|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**


了解更多详细参数配置，请参考组件配置类：    
[com.yiji.boot.jdbc.DruidProperties](src/main/java/com/yiji/boot/jdbc/DruidProperties.java)

## 3. 提供的bean

    @Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private PlatformTransactionManager platformTransactionManager;
	@Autowired
    private TransactionTemplate transactionTemplate;
	
## 4. FAQ

### 4.1 如何创建多个数据源

此组件会创建一个数据源(使用配置前缀为`yiji.ds`)，我们的应用程序有使用到多个数据源的情况，可以通过此组件创建一个数据源，然后在通过如下的代码创建其他数据源：

        @Bean
        //DruidDataSource的bean name=xxDataSource，读取环境配置前缀yiji.ds1
    	public DruidDataSource xxDataSource() {
            return DruidProperties.buildFromEnv("yiji.ds1");
    	}
    	 @Bean
         //DruidDataSource的bean name=yyDataSource，读取环境配置前缀yiji.ds2
         public DruidDataSource yyDataSource() {
            return DruidProperties.buildFromEnv("yiji.ds2");
         }
