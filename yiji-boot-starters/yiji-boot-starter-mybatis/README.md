## 1. 组件介绍

此组件提供mybatis的sqlSessionFactory，sqlSessionTemplate, mapperScannerConfigurer定义，启用了[mybatis分页插件](https://github.com/abel533/Mapper)

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-mybatis</artifactId>
    </dependency>

 **该组件依赖于yiji-boot-starter-jdbc, 请确保该组件已经正确配置**

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.mybatis.mapperScanPackages| mapper接口扫描路径，多个包用逗号隔开|否|
|yiji.mybatis.entityScanPackages| 实体类扫描路径，多个包用逗号隔开|否|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**


了解更多详细参数配置，请参考组件配置类：    
[com.yiji.boot.mybatis.MybatisProperties](src/main/java/com/yiji/boot/mybatis/MybatisProperties.java)    

## 3. 使用 (参考测试用例)
         
使用方法请参考测试用例，需要注意点为：

1. 项目定义的mapper接口必须放在`yiji.mybatis.mapperScanPackages`指定的包路径，多个包用逗号隔开,如果不指定，默认包为`com.yiji.boot.mybatis.MybatisProperties.DEFAULT_MAPPER_SCAN_PACKAGES`
2. 项目的SQL mapper xml文件需放在`classpath:/mybatis/`目录及其子目录，且必须以Mapper.xml结尾，如(CityMapper.xml)
3. 项目定义的实体类必须放在`yiji.mybatis.entityScanPackages`指定的包路径,多个包用逗号隔开,如果不指定，默认包为`com.yiji.boot.mybatis.MybatisProperties.DEFAULT_ENTITY_SCAN_PACKAGES`
4. 默认注册`TypeHandler`为`com.yjf.common.mybatis.MoneyTypeHandler`


相关资源文档:
[mybatis官网](http://mybatis.github.io/mybatis-3/index.html)
[mybatis-spring](http://mybatis.github.io/spring/mappers.html)
[mybatis资源汇总](http://mybatis.tk/)
[mybatis分页插件](http://git.oschina.net/free/Mybatis_PageHelper/blob/master/wikis/HowToUse.markdown)

## 4. FAQ

### 4.1 关于mybatis的缓存

mybatis中有一级缓存和二级缓存，一级缓存默认打开(sqlSession中的缓存)，二级缓存默认关闭(应用单独配置，比如用分布式缓存)。

一级缓存会造成一些问题，比如在一个事务中，应用两次调用同样的查询，这两次调用都会使用同一个sqlSession，第二次调用不会访问数据库，直接从sqlSession的缓存中返回结果。

合理使用缓存能提高程序性能，考虑到大多数场景，现把一级缓存关闭。如果需要打开一级缓存，请如下配置：

	yiji.mybatis.settings.localCacheScope=SESSION

### 4.2 ibatis2 项目转换为mybatis项目

参考[Ibatis2mybatis](Ibatis2mybatis.md)

### 4.3 使用 Mybatis Common Mapper
 
Mybatis增加单表增删改查通用能力，不用写一行sql语句，单表的操作能力全覆盖。

组件会在系统启动阶段组装`org.apache.ibatis.mapping.SqlSource`。相对于`mybatis generator`，不用手动生成代码，更不用当schema变动时重新生成、合并`Mapper.xml`文件.

在`Mapper.xml`文件中仍然可以自定义语句(`id`不能和`BaseMapper`中的方法同名).

使用例子如下：

#### 4.3.1. 定义DO

	public class City {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		
		private String name;
	
		private Integer age;
	}
	
上面使用到了jpa annotation添加元数据。非数据库字段请添加`transient`关键字。


#### 4.3.2. 定义Mapper接口

	import com.yiji.boot.mybatis.BaseMapper;

	public interface CityMapper extends BaseMapper<City> {
	}

注意：Mapper接口需要继承`com.yiji.boot.mybatis.BaseMapper`

#### 4.3.3. 使用mapper

	
	//插入所有属性
	cityMapper.insert(city);
	//插入所有非null属性
	cityMapper.insertSelective(city);
	//主键查询
	assertThat(cityMapper.selectByPrimaryKey(city.getId())).isNotNull();
	//主键删除
	cityMapper.deleteByPrimaryKey(city.getId());
	//查询所有
	assertThat(cityMapper.selectAll()).isEmpty();
	
	//通过Example对象组装查询条件
	//SELECT id,name,age FROM city WHERE ( age > ? and id <= ? ) or( name like ? )
	Example example = new Example(City.class);
	example.createCriteria().andGreaterThan("age", 21).andLessThanOrEqualTo("age", 22);
	example.or().andLike("name", "abe");
	assertThat(cityMapper.selectByExample(example)).hasSize(2);
	
	//通过Example对象组装查询条件
	//SELECT id,name,age FROM city WHERE ( name like ? )
	Example example1 = new Example(City.class);
	example1.createCriteria().andLike("name", "ab%");
	assertThat(cityMapper.selectByExample(example1)).hasSize(2);
	
	//通过不为空的属性查询
	City sc1=new City();
	sc1.setAge(22);
	sc1.setName("ac");
	//SELECT id,name,age FROM city WHERE name = ? AND age = ?
	assertThat(cityMapper.select(sc1)).hasSize(1);
	
	//分页查询
	//SELECT id,name,age FROM city WHERE name = ? AND age = ? LIMIT 10
	assertThat(cityMapper.selectByRowBounds(sc1,new RowBounds(0, 10))).hasSize(1);
	
	//分页查询
	//SELECT id,name,age FROM city LIMIT 10
	PageHelper.startPage(1, 10);
	assertThat(cityMapper.selectAll()).hasSize(3);
	
更多能力参考`com.yiji.boot.mybatis.BaseMapper`接口。

#### 4.3.4 More

 相关使用文档参考:https://github.com/abel533/Mapper
 
 
 如何扩展通用接口，参考`com.yiji.boot.mybatis.mapper.DeleteAllMapper`,建议大家提`issue`或者`pull request`到`yiji-boot`。
 
 
