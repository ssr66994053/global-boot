#!/bin/bash
JAVA_HOME=/usr/local/jdk1.8.0_40

APP_NAME=$1
LOG_HOME="/var/log/webapps/${APP_NAME}"
PID=''

echoRed() { echo $'\e[0;31m'$1$'\e[0m'; }
echoGreen() { echo $'\e[0;32m'$1$'\e[0m'; }
echoYellow() { echo $'\e[0;33m'$1$'\e[0m'; }
log(){
    now=`date "+%Y-%m-%d %H:%M:%S"`
    echo "${now}    $@" >> CONSOLE_LOG
    echoGreen "$@"
}
check_owner_user() {
	pid=`ps -ef|grep -w "name=$APP_NAME "|grep -v grep|awk '{print $2}'`
	owner_user=$(ps -ef|grep "$pid" |grep -v grep|awk '{print $1}');
	tty_user=`whoami`;
	[ "$owner_user" == "$tty_user" ] || { echoRed "$tty_user is not running user $tty_user"; exit 1; }
}

if [ x$1 = x ]; then
      echoRed "Usage: $0 appName";
      exit 1;
fi

if [ ! -z "`ps -ef|grep -w "name=${APP_NAME} "|grep -v grep|awk '{print $2}'`" ]; then
    PID=`ps -ef|grep -w "name=$1 "|grep -v grep|awk '{print $2}'`
    PID_EXIST=`ps -f -p ${PID} | grep java`
    if [ -z "$PID_EXIST" ]; then
        echoRed "${APP_NAME} is not running"
        exit 0
    fi
else
    echoRed "${APP_NAME} is not running"
    exit 0
fi

check_owner_user
DUMP_ROOT=$LOG_HOME/dump
if [ ! -d $DUMP_ROOT ]; then
  mkdir $DUMP_ROOT
fi

DUMP_DATE=`date +%Y%m%d%H%M%S`
DUMP_DIR=$DUMP_ROOT/dump-$DUMP_DATE
if [ ! -d $DUMP_DIR ]; then
  mkdir $DUMP_DIR
fi


log  "Dumping  $APP_NAME to $DUMP_DIR"


$JAVA_HOME/bin/jstack $PID > $DUMP_DIR/jstack-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jinfo $PID > $DUMP_DIR/jinfo-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jstat -gcutil $PID > $DUMP_DIR/jstat-gcutil-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jstat -gccapacity $PID > $DUMP_DIR/jstat-gccapacity-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jmap $PID > $DUMP_DIR/jmap-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jmap -heap $PID > $DUMP_DIR/jmap-heap-$PID.txt 2>&1
echo -e ".\c"
$JAVA_HOME/bin/jmap -histo $PID > $DUMP_DIR/jmap-histo-$PID.txt 2>&1
echo -e ".\c"
if [ -r /usr/bin/lsof ]; then
	/usr/bin/lsof -p $PID > $DUMP_DIR/lsof-$PID.txt
	echo -e ".\c"
fi

if [ -r /usr/bin/sar ]; then
	/usr/bin/sar > $DUMP_DIR/sar.txt
	echo -e ".\c"
fi
if [ -r /usr/bin/uptime ]; then
	/usr/bin/uptime > $DUMP_DIR/uptime.txt
	echo -e ".\c"
fi
if [ -r /usr/bin/free ]; then
	/usr/bin/free -t > $DUMP_DIR/free.txt
	echo -e ".\c"
fi
if [ -r /usr/bin/vmstat ]; then
	/usr/bin/vmstat > $DUMP_DIR/vmstat.txt
	echo -e ".\c"
fi
if [ -r /usr/bin/mpstat ]; then
	/usr/bin/mpstat > $DUMP_DIR/mpstat.txt
	echo -e ".\c"
fi
if [ -r /usr/bin/iostat ]; then
	/usr/bin/iostat > $DUMP_DIR/iostat.txt
	echo -e ".\c"
fi

## dump heap
cd $DUMP_DIR
DUMP_HEAP_NAME=${APP_NAME}-${DUMP_DATE}.hprof
log "dump heap"
$JAVA_HOME/bin/jmap -dump:live,format=b,file=${DUMP_HEAP_NAME} $PID
log "compress heap dump file"
tar zcvf ${DUMP_HEAP_NAME}.tar.gz ${DUMP_HEAP_NAME}

rm ${DUMP_HEAP_NAME}
log  "dump  file:${DUMP_DIR}/${DUMP_HEAP_NAME}.tar.gz"

#if [ -r /bin/netstat ]; then
#	/bin/netstat > $DUMP_DIR/netstat.txt
#	echo -e ".\c"
#fi
log  "dump OK!"
