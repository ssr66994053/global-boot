# OpenTsdb使用文档

## 部署位置
### 线下环境
部署在192.168.46.26上。  
配置路径: /etc/opentsdb   
日志目录: /home/opentsdb   
启动命令: /home/opentsdb/start_tsd.sh  
日志文件: /home/opentsdb/tsd.log  
     
## 常用命令
### 删除metrics数据
`tsdb scan 2014/05/01 sum <metric-name> tag_name1=tag_value1 tag_name1=tag_value1 --delete` 
### 删除metric名字
`tsdb uid delete metrics metric-name`
### 删除没用的metrics名字
`tsdb uid fsck fix delete_unknown`