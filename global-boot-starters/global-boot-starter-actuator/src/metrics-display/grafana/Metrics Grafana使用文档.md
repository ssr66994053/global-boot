# Metrics Grafana使用文档

## 部署位置
### 线下环境
部署在192.168.46.26上。   
日志目录: /var/log/grafana  
配置路径: /etc/grafana/grafana.ini    
自定义js脚本目录: /usr/share/grafana/public/dashboards/scripted.js   
启动命令: service grafana-server start 

### 相关文件说明
本文档同级目录下还包含以下文件:
  
+ grafana.ini 配置文件
+ scripted.js 自定义js脚本目录, 默认位于/usr/share/grafana/public/dashboards/
+ json_gen.py, MetricsTemplate.json json_gen.py是用来生成静态dashaboard json配置的脚本, 它根据metrics名字和MetricsTemplate.json
 模板文件来生成
 
## 访问与管理
web方式： http://192.168.46.26:3000/  

admin账号： admin/123456  

http api访问token：
`eyJrIjoidUViZ3FKWnd2MmpidVg0SFFUNlRxdFpDMDdxS0RvaEQiLCJuIjoiYWRtaW5fdG9rZW4iLCJpZCI6MX0=`

http api访问方式：
`curl -H "Authorization: Bearer your_key_above" http://your.grafana.com/api/dashboards/db/mydash`

http api授权文档: [http://docs.grafana.org/v2.6/reference/http_api/](http://docs.grafana.org/v2.6/reference/http_api/)


