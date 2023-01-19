#!/bin/bash

SCRIPTPATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

java -jar $SCRIPTPATH/target/spring-project-1.0.0-exec.jar

