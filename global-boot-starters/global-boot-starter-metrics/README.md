## 1. 组件介绍 ##
此组件提供metrics-spring监控能力。参考http://192.168.45.16/confluence/pages/viewpage.action?pageId=11962122。 监控数据可通过Jmx或者watcher查看。

## 2. 组件配置 ##
maven依赖

	<dependency>
		<groupId>com.yiji.boot</groupId>
		<artifactId>yiji-boot-starter-metrics</artifactId>
	</dependency>


必配参数: 
无

配置类com.yiji.boot.metrics.MetricsProperties。其中yiji.metrics.enable参数控制是否启用该组件；yiji.metrics.enableJmxReporter参数表示是否通过Jmx暴露监控数据。该组件提供的监控数据默认已通过watcher暴露。

## 3. 注意事项 ##
目前@Gauge和@CachedGauge两个注解有bug，暂时用不了，其他注解可以正常使用。