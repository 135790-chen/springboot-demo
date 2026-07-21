#!/bin/sh
# Maven Wrapper for Git Bash

MVNW_VERBOSE=false

PRG="$0"
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")/"$link"
  fi
done

SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

CLASSPATH=$APP_HOME/.mvn/wrapper/maven-wrapper.jar
MAVEN_OPTS="${MAVEN_OPTS:--Xmx1024m}"

if [ -n "$JAVA_HOME" ]; then
  JAVA_EXEC="$JAVA_HOME/bin/java"
else
  JAVA_EXEC="java"
fi

# Java 25+ requires these flags
JAVA_OPTS="--enable-native-access=ALL-UNNAMED -Dmaven.multiModuleProjectDirectory=$APP_HOME"
exec "$JAVA_EXEC" $JAVA_OPTS -cp "$CLASSPATH" org.apache.maven.wrapper.MavenWrapperMain "$@"