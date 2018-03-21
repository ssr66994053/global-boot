/* global _ */

/*
 * Complex scripted dashboard
 * This script generates a dashboard object that Grafana can load. It also takes a number of user
 * supplied URL parameters (in the ARGS variable)
 *
 * Return a dashboard object, or a function
 *
 * For async scripts, return a function, this function must take a single callback function as argument,
 * call this callback function with the dashboard object (look at scripted_async.js for an example)
 */

'use strict';

// accessible variables in this scope
var window, document, ARGS, $, jQuery, moment, kbn;

// Setup some variables
var dashboard;

// All url parameters are available via the ARGS object
var ARGS;

// Intialize a skeleton with nothing but a rows array and service object
dashboard = {
  rows : [],
};

// Set a title

// Set default time
// time can be overriden in the url using from/to parameters, but this is
// handled automatically in grafana core during dashboard initialization
dashboard.time = {
  from: "now-6h",
  to: "now"
};

/*
 调用后台服务的accessToken, 参考http://docs.grafana.org/v2.6/reference/http_api/
*/
var accessToken = 'eyJrIjoidUViZ3FKWnd2MmpidVg0SFFUNlRxdFpDMDdxS0RvaEQiLCJuIjoiYWRtaW5fdG9rZW4iLCJpZCI6MX0=';
var dataSourceQueryUrl = '/api/datasources';
var metricsQueryUrl = 'api/suggest?type=metrics&q=&max=200';

var rows = 1
var seriesName = '*';
var appName = '*';
var host = '*';
var dataSource;
var metrics;

if(!_.isUndefined(ARGS.appName)) {
  appName = ARGS.appName;
}

if(!_.isUndefined(ARGS.host)) {
  host = ARGS.host;
}

if(!_.isUndefined(ARGS.metricName)) {
  seriesName = ARGS.metricName;
}

function getDataSource() {
  $.ajax({
    type: "GET",
    url: dataSourceQueryUrl,
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Bearer " + accessToken);
    },
    async: false,
    success: function(result) {
      dataSource = result[0].url;
    }
  });
}


function alertObj(obj){
  var output = "";
  for(var i in obj){
    var property=obj[i];
    output+=i+" = "+property+"\n";
  }
  alert(output);
}

function getMetrics() {
  $.ajax({
    type: "GET",
    url: dataSource + metricsQueryUrl,
    async: false,
    success: function(result) {
      metrics = result;
    },
    error: function(result) {
      alertObj(result);
    }
  });
}

function  genTarget(metricName) {
  return {
    "aggregator": "avg",
    "downsampleAggregator": "avg",
    "errors": {},
    "metric": metricName,
    "tags": {
      "host": host,
      "appName": appName
    }
  };
}

function genPanel(rowTitle, metricName, width) {
  var metricTarget = [];
  metricTarget[0] = genTarget(metricName);
  return {
    title: metricName,
    type: 'graph',
    span: width,
    fill: 1,
    "legend": {
      "avg": false,
      "current": true,
      "max": true,
      "min": true,
      "show": true,
      "total": false,
      "values": false
    },
    linewidth: 2,
    grid: {max: null, min: 0},
    stack: true,
    targets: metricTarget,
    seriesOverrides: [
      {
        alias: '/random/',
        yaxis: 2,
        fill: 0,
        linewidth: 5
      }
    ],
    "decimals": 0,
    tooltip: {
      shared: true
    }
  };
}

function hasData(appName, metricName) {
  if (appName == '*' || metricName == '*')
    return true;
  var lastDataQueryUrl = 'api/query/last?timeseries=' + metricName + '{appName='+ appName + '}&resolve=false';
  var hasData = false;
  $.ajax({
    type: "GET",
    url: dataSource + lastDataQueryUrl,
    async: false,
    success: function(result) {
      if (result.length > 0) {
        hasData = true;
      }
    },
    error: function(result) {
      alertObj(result);
    }
  });
  return hasData;
}

function addRow(rowTitle, metricArr) {
  var metricPanels = [];
  var count = 1;
  if (metricArr.length >= 5) {
    count = 3;
  } else if (metricArr.length >= 2) {
    count = 2;
  }
  var j = 0;
  for (var i = 0; i < metricArr.length; ++i) {
    if (hasData(appName, metricArr[i])) {
      metricPanels[j++] = genPanel(rowTitle, metricArr[i], 12 / count);
    }
  }
  if (metricPanels.length > 0) {
    var collaps = true;
    if (firstRow == 1) {
      collaps = false;
      firstRow = 0;
    }

    dashboard.rows.push({
      title: rowTitle,
      height: '300px',
      collapse: collaps,
      panels: metricPanels
    });
  }
}

dashboard.title = seriesName;
dashboard.refresh = '30s';

if (seriesName == '*') {
  getDataSource();
  getMetrics();
} else {
  metrics = [];
  metrics[0] = seriesName;
}

/* 按前缀分组, 相同前缀的metrics, 放在同一行中 */
metrics.sort();
var pre = undefined;
var curArr = [];
var i = 0;
var prefix;
var firstRow = 1;
for (var j = 0; j < metrics.length; ++j) {
  var key = metrics[j];

  var idx = key.indexOf(".");
  if (idx > 0) {
    prefix = key.slice(0, idx);
  } else {
    prefix = key;
  }

  if (prefix == pre) {
    curArr[i++] = key;
  } else {
    if (curArr.length > 0) {
      addRow(pre, curArr);
      curArr = [];
      i = 0;
    }
    curArr[i++] = key;
  }
  pre = prefix;
}

if (curArr.length > 0) {
  addRow(pre, curArr);
}

return dashboard;