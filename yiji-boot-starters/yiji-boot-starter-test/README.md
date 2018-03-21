## 1. 组件介绍
此组件提供测试基类，并提供测试相关依赖

## 2. 配置

无需配置

## 3. 使用

### 3.1 依赖
    
     <dependency>
        <groupId>com.yiji.boot</groupId>
        <artifactId>yiji-boot-starter-test</artifactId>
        <scope>test</scope>
     </dependency>
            
### 3.2 编写项目测试父类

TestBase.java：

    import com.yiji.boot.core.Apps;
    import com.yiji.boot.test.AppWebTestBase;
    //import 启动类
    import com.yiji.boot.xxx.Main;
    import org.springframework.boot.test.SpringApplicationConfiguration;
    
    /**
     * @author qiubo@yiji.com
     */
     //设置启动类
    @SpringApplicationConfiguration(classes = Main.class)
    public abstract class WebTestBase extends AppWebTestBase {
    	protected static final String PROFILE = "sdev";
    	static {
    	    //设置测试环境
    		Apps.setProfileIfNotExists(PROFILE);
    	}
    }
    
其他测试用例类继承此类

### 3.3 参数化测试


    @RunWith(JUnitParamsRunner.class)
    @Slf4j
    public class XXTest {

    	@Test
    	//读取csv文件，csv文件第一行为头信息
    	@CsvParameter(value = "test.csv")
    	@TestCaseName("id={0}")
    	public void test1(int id, int age, String name) {
    		log.info("id={},age={},name={}", id, age, name);
    	}

	    @Test
    	//读取csv文件，csv文件第一行为头信息，当文件不存在时，会自动创建csv文件(这里使用了jdk8特性，请给java compiler加上-parameters参数,保证参数名正确)
    	@CsvParameter(value = "test.csv")
    	//测试命名，方便测试用例失败时，IDE可视化展示
    	@TestCaseName("id={0}")
    	//参数自动转换
    	public void test2(ParamDTO dto) {
    		log.info("{}", dto);
    	}

    	@Data
    	public static class ParamDTO {
    		private Long id;
    		private int age;
    		private String name;
    	}
    }


 csv 文件如下：

    id,age,name
    1,30,bohr
    2,28,na
    3,,


### 3.4 集成测试

集成测试继承`IntegrationTestBase`,集成测试场景下，不会启动tomcat/web,并支持参数化测试。

例子：


    @Slf4j
    public class XXXTest extends IntegrationTestBase {
    	public static final String PROFILE = "sdev";
        //设置环境
    	static {
    		Apps.setProfileIfNotExists(PROFILE);
    	}
    	//注入dao
    	@Autowired
    	private AppDao appDao;
    	@Autowired
    	private CityDao cityDao;

    	@Test
    	//读取csv文件，csv文件第一行为头信息，当文件不存在时，会自动创建csv文件(这里使用了jdk8特性，请给java compiler加上-parameters参数,保证参数名正确)
    	@CsvParameter(value = "test.csv")
    	//测试命名，方便测试用例失败时，IDE可视化展示
    	@TestCaseName("id={0}")
    	//参数自动转换,我们可以充分利用代码生成器生成的entity
    	public void test2(ParamDTO dto) {
    		log.info("{}", dto);
    	}

    	@Data
    	public static class ParamDTO {
    		private Long id;
    		private int age;
    		private String name;
    	}
    }

  csv 文件如下：

    id,age,name
    1,30,bohr
    2,28,na
    3,,