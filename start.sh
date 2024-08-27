#!/bin/sh

if [ -z "$JAVA_OPTS" ]
then
    JAVA_OPTS="-XX:-UseContainerSupport -Xms1G -Xmx1G"
else
    JAVA_OPTS="-XX:-UseContainerSupport $JAVA_OPTS"
fi

exec java $JAVA_OPTS -jar /app/ojt-minhyeok-authorization.jar
