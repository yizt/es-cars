#!/bin/sh

SKIN_HOME=$(cd `dirname $0`;cd .. ;pwd)
SKIN_CLASSPATH="${SKIN_HOME}/lib/WEB-INF/classes/:${SKIN_HOME}/lib/WEB-INF/lib/*"
pidcount=`ps -ef|grep java|grep ${SKIN_HOME}|wc -l`
if [ "$pidcount" -le 0 ]; then
	echo "${SKIN_HOME} has not started!"
else
	pid=`ps -ef|grep java|grep ${SKIN_HOME}|awk '{print $2}'`
	kill -9 pid
	echo "${SKIN_HOME} has stopped."
fi
