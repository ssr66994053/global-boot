#!/bin/bash
cd `dirname $0`

JAVA_HOME=/usr/local/jdk1.8.0_40
APP_NAME="$1"
JAR_FILE="$2"
JAVA_HEAP_SIZE="$3"
JMXport="$4"

LOGS_DIR="/var/log/webapps/${APP_NAME}"
STDOUT_FILE=${LOGS_DIR}/stdout.log
START_DATE=`date +%Y%m%d%H%M%S`

echoRed() { echo $'\e[0;31m'$1$'\e[0m'; }
echoGreen() { echo $'\e[0;32m'$1$'\e[0m'; }
echoYellow() { echo $'\e[0;33m'$1$'\e[0m'; }
log(){
    now=`date "+%Y-%m-%d %H:%M:%S"`
    echo "${now}    $@" >> CONSOLE_LOG
    echoGreen "$@"
}

# 检查java版本
checkJavaVersion(){
    JAVA="$JAVA_HOME/bin/java"
    if [[ ! -r "$JAVA" ]]; then
        JAVA='java'
    fi

    JAVA_EXIST=`${JAVA} -version 2>&1 |grep 1.8`
    if [ ! -n "$JAVA_EXIST" ]; then
            log "JDK version is not 8"
            ${JAVA} -version
            exit 0
    fi
}


isRunning() {
  ps -p $1 &> /dev/null
}

checkIsRunning(){
    if [ ! -z "`ps -ef|grep -w "name=${APP_NAME} "|grep -v grep|awk '{print $2}'`" ]; then
        pid=`ps -ef|grep -w "name=$1 "|grep -v grep|awk '{print $2}'`
        log "$APP_NAME Already running pid=$pid";
        exit 0;
    fi
}

checkLogDir(){
    if [ ! -d ${LOGS_DIR} ]; then
        mkdir ${LOGS_DIR}
    fi
}

checkOptionvlue () {
	if [ "x$1" == "x" ];then
		log "$2 Option vlue is empty"
		exit 0;
	fi
}

checkBasepath () {
    log $#
	if [ $# -ne 4 ];then
		log "check option APP_NAME JAR_FILE JAVA_HEAP_SIZE JMXport"
		exit 0
	fi
	checkOptionvlue "${APP_NAME}" "APP_NAME"
	checkOptionvlue "${JAR_FILE}" "JAR_FILE"
	checkOptionvlue "${JAVA_HEAP_SIZE}" "JAVA_HEAP_SIZE"
	checkOptionvlue "${JMXport}" "JMXport"
	test ! -f ${JAR_FILE}  && log "JAR_FILE is not fund" && exit 0
}

log "Starting the $APP_NAME ..."

checkBasepath $*
checkJavaVersion
checkIsRunning
checkLogDir
JAVA_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Xms${JAVA_HEAP_SIZE}m -Xmx${JAVA_HEAP_SIZE}m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:LargePageSizeInBytes=128m -XX:+ParallelRefProcEnabled -XX:+PrintAdaptiveSizePolicy -XX:+UseFastAccessorMethods -XX:+TieredCompilation -XX:+ExplicitGCInvokesConcurrent -XX:AutoBoxCacheMax=20000 $JAVA_OPTS"
JAVA_GC_LOG="-verbosegc  -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:${LOGS_DIR}/gc.log"
JAVA_OOM_DUMP="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGS_DIR}/oom-${START_DATE}.hprof"
JAVA_JMX=" -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${JMXport} -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=true"
JAVA_OPTS="$JAVA_OPTS $JAVA_GC_LOG $JAVA_OOM_DUMP $JAVA_JMX -Dsys.name=$APP_NAME"



log "JAVA_OPTS=${JAVA_OPTS}"
nohup ${JAVA} ${JAVA_OPTS} -jar ${JAR_FILE}  > ${STDOUT_FILE} 2>&1 &
sleep 2
