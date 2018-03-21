
# 本脚本的作用是根据metricNames列表和MetricsTemplate.json模板文件生成Metrics.json配置文件.
# 生成的配置文件可以导入到grafana中生成dashboard

import json
f = open('MetricsTemplate.json', 'r')
metrics = json.load(f)

metricNames = ("redis.session.NumActive", "redis.cache.NumActive",
               "tomcat.connectionCount", "tomcat.currentThreadCount",
               "dubbo.Gauge.poolSize", "dubbo.Gauge.activeThreadCnt",
               "httpsessions.active",
               "RabbitContainerThread-.Gauge.poolSize", "RabbitContainerThread-.Gauge.activeThreadCnt",
               "heap.used", "threads",
               	"fastdfs.tracker.NumActive", "fastdfs.storage.NumActive")

curLen = len(metrics["rows"])
print(curLen)
while curLen < len(metricNames):
    metrics["rows"].append(metrics["rows"][0])
    curLen += 1

idx = 0
for metricName in metricNames:
    metrics["rows"][idx]['panels'][0]['targets'][0]['metric'] = metricName
    metrics["rows"][idx]['panels'][0]['title'] = metricName
    idx += 1

fout = open('Metrics.json', 'w')
json.dump(metrics, fout)