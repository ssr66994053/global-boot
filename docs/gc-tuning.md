
## 请求

	ab -n 100000 -c 100 http://127.0.0.1:9832/gc.json

## GC 算法

### 1. CMS:

#### 1.1 jvm参数
	
	-server -Xms4096m -Xmx4096m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:SurvivorRatio=4 -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseParNewGC -XX:MaxTenuringThreshold=5 -XX:+CMSClassUnloadingEnabled -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -XX:AutoBoxCacheMax=20000 -verbosegc  -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:/var/log/webapps/yiji-boot-test/gc.log
	
#### 1.2. 响应：
	
	Concurrency Level:      100
	Time taken for tests:   218.696 seconds
	Percentage of the requests served within a certain time (ms)
	  50%    106
	  66%    129
	  75%    148
	  80%    163
	  90%    213
	  95%    735
	  98%   1437
	  99%   2124
	 100%  21490 (longest request)
	 
#### 1.3 gc 总体时间

	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
	  0.00   0.63  67.07   2.09  97.83  96.91    135    4.316     0    0.000    4.316
 	 
### 2. G1:

#### 2.1 jvm参数
	-server -XX:+UseG1GC -XX:MaxGCPauseMillis=10 -Xms4096m -Xmx4096m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m  -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -XX:AutoBoxCacheMax=20000 -verbosegc  -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:/var/log/webapps/yiji-boot-test/gc.log
	
#### 2.2. 响应：
	
	Concurrency Level:      100
	Time taken for tests:   219.174 seconds
	Percentage of the requests served within a certain time (ms)
	  50%    105
	  66%    130
	  75%    152
	  80%    168
	  90%    298
	  95%    957
	  98%   1274
	  99%   1830
	 100%  14606 (longest request)
	 
#### 2.3 gc 总体时间	 
	 
	 jstat -gcutil 91674
	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
	  0.00 100.00  58.26   6.43  97.79  96.78     53    1.157     0    0.000    1.157
	 
	 
#### 2.1 jvm参数
	-server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -Xms4096m -Xmx4096m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m  -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -XX:AutoBoxCacheMax=20000 -verbosegc  -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:/var/log/webapps/yiji-boot-test/gc.log

	
#### 2.2. 响应：
	
	Concurrency Level:      100
	Time taken for tests:   219.949 seconds
	Percentage of the requests served within a certain time (ms)
	  50%    107
	  66%    134
	  75%    161
	  80%    180
	  90%    322
	  95%    846
	  98%   1322
	  99%   1597
	 100%  14243 (longest request)
	 
#### 2.3 gc 总体时间	 
	 
	jstat -gcutil 93610
	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
	  0.00 100.00  12.02   6.48  98.04  96.81     44    1.156     0    0.000    1.156
	  
	  
	  

	 
#### 2.1 jvm参数

	-server -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Xms4096m -Xmx4096m -XX:+ParallelRefProcEnabled -XX:+PrintAdaptiveSizePolicy -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m  -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -XX:AutoBoxCacheMax=20000 -verbosegc  -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:/var/log/webapps/yiji-boot-test/gc.log

	
#### 2.2. 响应：
	
	Concurrency Level:      100
	Time taken for tests:   237.103 seconds
	Percentage of the requests served within a certain time (ms)
	  50%    100
	  66%    114
	  75%    133
	  80%    149
	  90%    196
	  95%    519
	  98%    753
	  99%   1037
	 100%  20571 (longest request)
	 
#### 2.3 gc 总体时间	 
	 
	jstat -gcutil 1470
	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
	  0.00 100.00  22.96   6.54  98.09  96.81     42    1.594     0    0.000    1.594