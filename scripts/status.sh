#!/bin/bash
cd `dirname $0`
cdir=`pwd`
echoRed() { echo $'\e[0;31m'$1$'\e[0m'; }
echoGreen() { echo $'\e[0;32m'$1$'\e[0m'; }
echoYellow() { echo $'\e[0;33m'$1$'\e[0m'; }

checkIsRunning(){
    if [ ! -z "`ps -ef|grep -w "name=${APP_NAME} "|grep -v grep|awk '{print $2}'`" ]; then
        pid=`ps -ef|grep -w "name=$1 "|grep -v grep|awk '{print $2}'`
        log "$APP_NAME Already running pid=$pid";
        exit 0;
    fi
}

if [ x$1 = x ]; then
      echoRed "Usage: $0 appName";
      exit 1;
fi

APP_NAME=$1
LOGS_DIR="/var/log/webapps/$1"
if [ ! -z "`ps -ef|grep -w "name=${APP_NAME} "|grep -v grep|awk '{print $2}'`" ]; then
    PID=`ps -ef|grep -w "name=$1 "|grep -v grep|awk '{print $2}'`
    if [ ! -z "${PID}" ]; then
      echoGreen "${APP_NAME} is running @ $PID"
    else
        echoRed "${APP_NAME} is not running"
    fi
else
    echoRed "${APP_NAME} is not running"
fi
