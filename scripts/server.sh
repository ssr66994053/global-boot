#!/bin/bash
cd `dirname $0`
cdir=`pwd`
echoRed() { echo $'\e[0;31m'$1$'\e[0m'; }
if [ x$1 = x ]; then
      echoRed "ERROR: Please input argument: [start|stop|restart|dump|status]";
      exit 1;
fi

sh "$cdir/$1.sh" $2 $3 $4