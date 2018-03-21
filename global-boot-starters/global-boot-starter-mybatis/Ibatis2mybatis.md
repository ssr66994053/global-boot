# Ibatis2 转换到 Mybatis3（support for yiji-boot） 


 1.初步转换包含sqlmap的xml
 ---------


* 下载转换工具
		
		git clone https://github.com/mybatis/ibatis2mybatis
		
* 下载ant构建工具，选择自己平台适合的版本,设置好环境变量，就像设置maven一样
		
		http://ant.apache.org/bindownload.cgi	
		
* 把你原ibatis所有的sqlmap配置文件放入刚才下好的工具的`source`目录中
		
		└── ibatis2mybatis
    		├── README.md
    		├── build.xml
    		├── destination
    		│   └── Get_your_new_mybatis3_mapper_files_from_here.txt
   			├── migrate.xslt
    		├── mybatis-3-config.dtd
    		├── mybatis-3-mapper.dtd
    		└── source
        		└── Put_Your_old_ibatis2_sqlmap_files_here.txt		
* 修改dtd
		
    找到原来每个slqmap文件 dtd 
		
		<!DOCTYPE sqlMap PUBLIC "-//iBATIS.com//DTD SQL Map 2.0//EN" "http://www.ibatis.com/dtd/sql-map-2.dtd">    
	
    修改为:	
	 
	  	<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">	 
	     		
* 在工具根目录执行转换命令
		
		ant
* 在工具的destination目录将会得到从Ibatis2到Mybatis3初步的sqlmap文件



2.工具能力有限，无法修改原Ibatis`<dynamic/>`标签中的内容，需要人工继续修改转换的sqlmap文件
----
* 在开始这一步之前请删掉所有关于ibatis的依赖斌添加以下依赖：
	    
             <dependency>
                <groupId>com.yiji.boot</groupId>
                <artifactId>yiji-boot-starter-mybatis</artifactId>
            </dependency>    

* 开始人工修改

如：
	原ibatis的sqlmap-dynamic 标签：
		
		<dynamic prepend="set">
					<isNotEmpty property="paraValue" prepend=",">
						para_value=#paraValue#
					</isNotEmpty>
					<isNotEmpty property="remark" prepend=",">
						remark=#remark#
					</isNotEmpty>
					<isNotEmpty property="encrypted" prepend=",">
						encrypted=#encrypted#
					</isNotEmpty>
		</dynamic>
		

   在转换后的sqlmap中修改为:
   		
   		 <set>
            <if test="paraValue != null">
                para_value=#{paraValue},
            </if>
            <if test="remark != null">
                remark=#{remark},
            </if>
            <if test="encrypted != null">
                encrypted=#{encrypted}
            </if>
        </set>
		
		
例1:
	ibatis:
	
	   <dynamic prepend="set">
            <isNotEmpty property="gid" prepend=",">
                gid=#gid#
            </isNotEmpty>
            <isNotEmpty property="messageType" prepend=",">
                message_type=#messageType#
            </isNotEmpty>
            <isNotEmpty property="messageContent" prepend=",">
                message_content=#messageContent#
            </isNotEmpty>
            <isNotEmpty property="url" prepend=",">
                url=#url#
            </isNotEmpty>
            <isNotEmpty property="charset" prepend=",">
                charset=#charset#
            </isNotEmpty>
            <isNotEmpty property="securityCheckCode" prepend=",">
                security_check_code=#securityCheckCode#
            </isNotEmpty>
            <isNotEmpty property="signType" prepend=",">
                sign_type=#signType#
            </isNotEmpty>
            <isNotEmpty property="clientIp" prepend=",">
                client_ip=#clientIp#
            </isNotEmpty>
            <isNotEmpty property="nextExecuteTime" prepend=",">
                next_execute_time=#nextExecuteTime#
            </isNotEmpty>
            <isGreaterThan property="executeNum" compareValue="0" prepend=",">
                execute_num=#executeNum#
            </isGreaterThan>
            <isNotEmpty property="status" prepend=",">
                status=#status#
            </isNotEmpty>
            <isNotEmpty property="description" prepend=",">
                description=#description#
            </isNotEmpty>
        </dynamic>
        
  
  mybatis:
  		
  		 <set>
            <if test="gid != null">
                gid=#{gid},
            </if>
            <if test="messageType != null">
                message_type=#{messageType},
            </if>
            <if test="messageContent != null">
                message_content=#{messageContent},
            </if>
            <if test="url != null">
                url=#{url},
            </if>
            <if test="charset != null">
                charset=#{charset},
            </if>
            <if test="securityCheckCode != null">
                security_check_code=#{securityCheckCode},
            </if>
            <if test="signType != null">
                sign_type=#{signType},
            </if>
            <if test="clientIp != null">
                client_ip=#{clientIp},
            </if>
            <if test="nextExecuteTime != null">
                next_execute_time=#{nextExecuteTime},
            </if>
            <if test="executeNum &gt; 0" >
                execute_num=#{executeNum},
            </if>
            <if test="status !=null">
                status=#{status},
            </if>
            <if test="exeStatus !=null">
                exe_status=#{exeStatus},
            </if>
            <if test="description !=null">
                description=#{description}
            </if>
        </set>
        
           	
例2：
	 
   ibais： 
   
   		<dynamic prepend=" where ">
			<isNotEmpty property="name" prepend=" and ">
				name like '%$name$%'
			</isNotEmpty>
			<isNotEmpty property="subject" prepend=" and ">
				subject like '%$subject$%'
			</isNotEmpty>
		</dynamic>
		

   mybatis：
   		
   		<where>
			<if test="name != null">
				name like '%${name}%'
			</if>
			<if test="subject != null">
				and subject like '%${subject}%'
			</if>
		</where>
		

注意：

* 变量替换方式 老:`#var#` ，新:`#{var}`, 老: `$var$`,新:`${var}`
* mybatis动态标签参考：http://mybatis.org/mybatis-3/
	
3.修改resultMap中不兼容的javaType 对应的 jdbcType
----
例如:

	ibatis:
	
	<result property="content" column="content" javaType="java.lang.String" jdbcType="TEXT"/>
	<result property="executeNum" column="execute_num" javaType="int" jdbcType="INT" nullValue="0"/>
	
	mybatis:
	
	  <result property="content" column="content" javaType="java.lang.String" jdbcType="LONGVARCHAR"/>
	  <result property="executeNum" column="execute_num" javaType="int" jdbcType="INTEGER"/>
	  
	ibatis  
注意：

* 如果启动报`jdbcType`相关错误请参考：`org.apache.ibatis.type#JdbcType` 或邮件联系zhouxi@yiji.com

4.自定义类型转换 typeHandler
----
#### 全局

* （编写代码）
		
		/**
		 * @author Clinton Begin
		 */
		public class DateTypeHandler extends BaseTypeHandler<Date> {

 		 @Override
 		 public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType 		jdbcType)
      		throws SQLException {
    		ps.setTimestamp(i, new Timestamp((parameter).getTime()));
  			}

 		 @Override
  		public Date getNullableResult(ResultSet rs, String columnName)
      		throws SQLException {
    		Timestamp sqlTimestamp = rs.getTimestamp(columnName);
    		if (sqlTimestamp != null) {
     			 return new Date(sqlTimestamp.getTime());
    		}
    			return null;
  			}

  			@Override
  		public Date getNullableResult(ResultSet rs, int columnIndex)
      		throws SQLException {
   		 Timestamp sqlTimestamp = rs.getTimestamp(columnIndex);
    		if (sqlTimestamp != null) {
      		return new Date(sqlTimestamp.getTime());
   		 }
    		return null;
  		}

  			@Override
 		 public Date getNullableResult(CallableStatement cs, int columnIndex)
      		throws SQLException {
    		Timestamp sqlTimestamp = cs.getTimestamp(columnIndex);
    		if (sqlTimestamp != null) {
     		 return new Date(sqlTimestamp.getTime());
    		}
    		return null;
 		 }	
		}
	
		

* xml
			
		<configuration>
    		<typeHandlers>
              <typeHandler handler="org.apache.ibatis.type.DateTypeHandler"   javaType="java.util.Date" jdbcType="VARCHAR" />
      </typeHandlers>
      <mappers>
          <mapper resource="sqlmap/Extra-sqlmap-mapping.xml"/>
      </mappers>
     </configuration>
     
    
#### 局部
	<resultMap id="RM-CONFIG" type="com.yjf.cs.dal.dataobject.ConfigDO">
               <result property="rawAddTime" column="raw_add_time" typeHandler="org.apache.ibatis.type.DateTypeHandler" javaType="java.util.Date" jdbcType="VARCHAR"/>
         </resultMap>

 



5.修改baseSqlMapClientDAO
----
   ibatis：
   		
   		 <bean id="baseSqlMapClientDAO" abstract="true">
          <property name="dataSource">
            <ref bean="dataSource"/>
          </property>
          <property name="sqlMapClient">
            <ref bean="sqlMapClient"/>
          </property>
   		 </bean>
   		 
   mybatis：
   		
   		<bean id="baseSqlMapClientDAO" abstract="true">
       	 <property name="sqlSessionTemplate">
            <ref bean="sqlSessionTemplate"/>
         </property>
        </bean>

注意:

* 如果你不是采用baseDao的方式又不知道如何修改，请邮件联系zhouxi@yiji.com

5.修改你的Dao层
----

 1. 修改extends

 		ibatis：
 			
 		 public class IbatisConfigDAO extends SqlMapClientDaoSupport implements ConfigDAO
 		 
        mybatis：
       	
       	 public class IbatisConfigDAO extends SqlSessionDaoSupport implements ConfigDAO 
 
 2.修改template方法:

        ibatis:
        	
        	getSqlMapClientTemplate().xxx()
        	 
        mybatis:
        	
        	getSqlSession().xxx()
        
例：
		ibatis:
			
			 getSqlMapClientTemplate().insert("MS-CONFIG-PERSIST", config);
			 getSqlMapClientTemplate().update("MS-CONFIG-UPDATE", config);
			 getSqlMapClientTemplate().queryForList("MS-CONFIG-FIND-ALL-CONFIG", null);
			 getSqlMapClientTemplate().queryForObject("MS-CONFIG-FIND-CONFIG-BY-KEY", paraKey)
			 getSqlMapClientTemplate().delete("MS-SMS-VERIFY-DELETE");      
			 
	    mybatis:
	    	
	    	 getSqlSession().insert("MS-CONFIG-PERSIST", config);
	    	 getSqlSession().update("MS-CONFIG-UPDATE", config);
	    	 getSqlSession().selectList("MS-CONFIG-FIND-ALL-CONFIG", null);
	    	 getSqlSession().selectOne("MS-CONFIG-FIND-CONFIG-BY-KEY", paraKey)
	    	 getSqlSession().delete("MS-SMS-VERIFY-DELETE");

注意：

* getSqlMapClientTemplate() 和	 getSqlSession() curd的方法名字上有变动，请根据实际情况对应。如果有不清楚的请邮件联系zhouxi@yiji.com		 

6.配置yiji-boot
----
  	

* 添加配置：
		
		yiji.mybatis.config=sqlmap/sqlmap.xml 
		
注意：

* yiji-boot-starter-mybatis具体用法可参考http://gitlab.yiji/qzhanbo/yiji-boot/tree/master/yiji-boot-starters/yiji-boot-starter-mybatis
* sqlmap/sqlmap.xml 为你项目从resources目录开始的路径。
* 例子：
		
      resources
        └── sqlmap
             ├── Config-sqlmap-mapping.xml
             ├── Download-sqlmap-mapping.xml
             ├── EmailAttachment-sqlmap-mapping.xml
             ├── EmailInfo-sqlmap-mapping.xml
             ├── EmailTemplate-sqlmap-mapping.xml
             ├── Extra-sqlmap-mapping.xml
             └── sqlmap.xml
             

  sqlmap.xml一应该是一个包含其他所有*sqlmap-mapping.xml的文件：
  			
  		<?xml version="1.0" encoding="UTF-8" ?>
		<!DOCTYPE configuration 
		 PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
		
		<configuration>
    		<mappers>
       		 <mapper resource="sqlmap/Config-sqlmap-mapping.xml"/>
        	<mapper resource="sqlmap/Download-sqlmap-mapping.xml"/>
   		 </mappers>
		</configuration> 
		
	
	
   	yiji.mybatis.config配置路径为:
   		
   		yiji.mybatis.config=sqlmap/sqlmap.xml 	
   		

7.更多的问题 
----
#### Email：zhouxi@yiji.com