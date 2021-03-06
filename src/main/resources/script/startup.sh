#!/bin/sh

##  Skin Service Bootstrap Script ##
MEM_MAX=1303m
MEM_MIN=1303m
active=test


SKIN_HOME=$(cd `dirname $0`;cd .. ;pwd)
SKIN_CLASSPATH="${SKIN_HOME}/lib/WEB-INF/classes/:${SKIN_HOME}/lib/WEB-INF/lib/*"
pidcount=`ps -ef|grep java|grep ${SKIN_HOME}|wc -l`
if [ "$pidcount" -le 0 ]; then
	nohup java -Xms${MEM_MAX} -Xmx${MEM_MIN} -XX:MaxPermSize=256m -Dspring.profiles.active=${active}  -classpath ${SKIN_CLASSPATH} org.es.skinservice.SkinServiceApp >/dev/null 2>&1 &
	sleep 2
	pid=`ps -ef|grep java|grep ${SKIN_HOME}|awk '{print $2}'`
	if [ -z "$pid" ]; then
		echo "${SKIN_HOME}-$1 (pid:$pid ) fails to bootstrap!"
	else
		echo "${SKIN_HOME}-$1 (pid:$pid ) bootstrap successfully!"
	fi

else
	echo "${SKIN_HOME} has already started!"
fi



