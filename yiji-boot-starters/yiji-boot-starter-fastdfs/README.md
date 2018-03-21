## 1. 组件介绍
此组件提供分布文件存储系统(fastdfs)的客户端组件，定义了FastdfsClient，提供文件的上传，下载、和删除功能。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-fastdfs</artifactId>
    </dependency>

2) 配置组件参数    

必配参数: 

|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|yiji.fastdfs.trackerAddress| 服务器地址和端口, 如: 127.0.0.1:22122|是|
|yiji.fastdfs.downloadHost| 文件资源下载域名，如fastdfs.yijifu.net|是|

**注意： 如果启用了hera配置管理系统进行配置，则通用配置项(上表第三列为"是"的配置项)，业务项目不用配置。**
**这些配置，将由hera配置管理系统的common项目提供，业务项目将自动获得common项目的配置**

配置参考[com.yiji.boot.fastdfs.FastdfsProperties](src/main/java/com/yiji/boot/fastdfs/FastdfsProperties.java)

*如果应用使用配置管理系统(hera)进行配置管理，则不需要对此组件进行参数配置。基础技术部会对该组件在配置管理系统中进行统一配置*

## 3. 使用 (参考测试用例)
         
参考测试用例
com.yiji.boot.test.fastdfs

## 4. F.A.Q

### 4.1 最大上传文件限制?

目前最大上传文件限制是服务器设置的,最大为16M