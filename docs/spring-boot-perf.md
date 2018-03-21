## 性能测试:

测试tomcat embed和传统tomcat容器的性能，我们只考虑很简单的请求处理，对于耗时较长的请求，性能影响完全决定于编程模型和线程池调优。

### 1. 参数配置

我们使用jdk8和jvm调优参数如下：

	-server -Xms1024m -Xmx1024m -XX:SurvivorRatio=4 -XX:LargePageSizeInBytes=64m -verbosegc -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:./gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./oom.hprof -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:MaxTenuringThreshold=5 -XX:+CMSClassUnloadingEnabled -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600001

### 2. 测试传统tomcat

#### 2.1 500c

	ab -n 1000000 -c 500 http://192.168.45.15:10000/test.json

	Concurrency Level:      500
	Time taken for tests:   70.484 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    14187.60 [#/sec] (mean)
	Time per request:       35.242 [ms] (mean)
	Time per request:       0.070 [ms] (mean, across all concurrent requests)
	Transfer rate:          2660.18 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0   25 163.9      2    7014
	Processing:     0   10  14.7      8    2773
	Waiting:        0    9  12.6      8    2772
	Total:          0   35 166.0     10    7022

	Percentage of the requests served within a certain time (ms)
  		50%     10
  		66%     12
  		75%     13
  		80%     14
  		90%     17
  		95%     22
  		98%   1006
  		99%   1011
 		100%   7022 (longest request)
 		
#### 2.2 500c after minus thread pool

	ab -n 1000000 -c 500 http://192.168.45.15:10000/test.json
	
	Concurrency Level:      500
	Time taken for tests:   74.007 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    13512.26 [#/sec] (mean)
	Time per request:       37.003 [ms] (mean)
	Time per request:       0.074 [ms] (mean, across all concurrent requests)
	Transfer rate:          2533.55 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0   19  98.8     10    3013
	Processing:     1   18  12.9     18     672
	Waiting:        0   14  11.1     14     664
	Total:          1   37  99.9     29    3036

	Percentage of the requests served within a certain time (ms)
  		50%     29
  		66%     32
  		75%     34
  		80%     34
  		90%     37
  		95%     39
  		98%     44
  		99%     58
 		100%   3036 (longest request)
 		
#### 2.3 200c	
 	
 	ab -n 1000000 -c 200 http://192.168.45.15:10000/test.json

	Concurrency Level:      200
	Time taken for tests:   64.148 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    15588.90 [#/sec] (mean)
	Time per request:       12.830 [ms] (mean)
	Time per request:       0.064 [ms] (mean, across all concurrent requests)
	Transfer rate:          2922.92 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0    5  64.8      1    3004
	Processing:     0    8   7.7      7     677
	Waiting:        0    7   5.3      6     677
	Total:          0   13  65.3      8    3023

	Percentage of the requests served within a certain time (ms)
  		50%      8
  		66%      9
  		75%     10
  		80%     10
  		90%     12
 		95%     15
  		98%     18
  		99%     23
 		100%   3023 (longest request)
 
 
#### 2.4 200c	after minus thread pool

	ab -n 1000000 -c 200 http://192.168.45.15:10000/test.json

	Concurrency Level:      200
	Time taken for tests:   70.900 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    14104.28 [#/sec] (mean)
	Time per request:       14.180 [ms] (mean)
	Time per request:       0.071 [ms] (mean, across all concurrent requests)
	Transfer rate:          2644.55 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0    5  27.2      4    1007
	Processing:     0    9   6.2      9     315
	Waiting:        0    8   3.9      7     229
	Total:          1   14  27.7     13    1038

	Percentage of the requests served within a certain time (ms)
  		50%     13
  		66%     14
  		75%     14
  		80%     15
  		90%     17
  		95%     19
  		98%     22
  		99%     24
 		100%   1038 (longest request)

### 3. 测试tomcat-embed性能:
 
#### 3.1 500c

 	ab -n 1000000 -c 500 http://192.168.45.15:8081/test.json

	Concurrency Level:      500
	Time taken for tests:   65.874 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    15180.56 [#/sec] (mean)
	Time per request:       32.937 [ms] (mean)
	Time per request:       0.066 [ms] (mean, across all concurrent requests)
	Transfer rate:          2846.36 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0   26 156.8      1    3010
	Processing:     0    7   9.0      6    1950
	Waiting:        0    6   9.0      6    1950
	Total:          0   33 158.1      8    3029

	Percentage of the requests served within a certain time (ms)
  		50%      8
  		66%      9
  		75%     10
  		80%     10
  		90%     12
  		95%     16
  		98%   1004
  		99%   1008
 		100%   3029 (longest request)
 		
#### 3.2 200c

	ab -n 1000000 -c 200 http://192.168.45.15:8081/test.json

	Concurrency Level:      200
	Time taken for tests:   70.305 seconds
	Complete requests:      1000000
	Failed requests:        0
	Write errors:           0
	Total transferred:      192000000 bytes
	HTML transferred:       24000000 bytes
	Requests per second:    14223.71 [#/sec] (mean)
	Time per request:       14.061 [ms] (mean)
	Time per request:       0.070 [ms] (mean, across all concurrent requests)
	Transfer rate:          2666.95 [Kbytes/sec] received

	Connection Times (ms)
              min  mean[+/-sd] median   max
	Connect:        0    8 124.7      1    7016
	Processing:     0    6   6.8      5    1019
	Waiting:        0    6   6.7      5    1019
	Total:          0   14 125.2      6    7032

	Percentage of the requests served within a certain time (ms)
  		    50%      6
  		    66%      7
  		    75%      8
 		    80%      8
  		    90%     10
  		    95%     12
  		    98%     15
  		    99%     20
 		    100%   7032 (longest request)

### 4. 结论

性能差不多。如果我们把tomcat-embed中没用的模块去掉，性能会更好。
